package com.bazario.catalog.adapters.out.persistence;

import com.bazario.AbstractPostgresTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository-level tests for the catalog module.
 *
 * Slice test (@DataJpaTest): only JPA + repositories + Flyway are booted,
 * no controllers, no security, no service layer -> fast and focused.
 *
 * replace = NONE is REQUIRED: otherwise Spring replaces our Testcontainers
 * datasource with an in-memory H2, and the Postgres-specific migrations
 * (uuid-ossp, pg_trgm) would fail to run.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpringDataProductRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private SpringDataProductRepository productRepository;

    @Autowired
    private SpringDataCategoryRepository categoryRepository;

    private CategoryJpaEntity phones;

    @BeforeEach
    void setUp() {
        // Flyway created the tables; we only insert data.
        phones = categoryRepository.save(CategoryJpaEntity.builder()
                .name("Phones")
                .slug("phones")
                .build());
    }

    @Test
    @DisplayName("save + findBySku / findBySlug round-trip")
    void savesAndFindsProductBySkuAndSlug() {
        ProductJpaEntity saved = productRepository.save(newProduct("iPhone 15", "iphone-15", "SKU-001",
                new BigDecimal("999.00"), true, null));

        assertThat(productRepository.findBySku("SKU-001"))
                .isPresent()
                .get()
                .extracting(ProductJpaEntity::getId)
                .isEqualTo(saved.getId());

        assertThat(productRepository.findBySlug("iphone-15")).isPresent();
    }

    @Test
    @DisplayName("findBySku returns empty for unknown sku")
    void findBySkuReturnsEmptyForUnknownSku() {
        assertThat(productRepository.findBySku("NOPE")).isEmpty();
    }

    @Test
    @DisplayName("deactivateById flips the active flag without touching other rows")
    void deactivateByIdFlipsActiveFlag() {
        ProductJpaEntity keep = productRepository.save(newProduct("Pixel 8", "pixel-8", "SKU-002",
                new BigDecimal("699.00"), true, null));
        ProductJpaEntity kill = productRepository.save(newProduct("Nokia 3310", "nokia-3310", "SKU-003",
                new BigDecimal("99.00"), true, null));

        productRepository.deactivateById(kill.getId());

        // @Modifying queries bypass the persistence context, so flush+clear first.
        productRepository.flush();

        assertThat(productRepository.findById(kill.getId())).get()
                .extracting(ProductJpaEntity::getActive)
                .isEqualTo(false);
        assertThat(productRepository.findById(keep.getId())).get()
                .extracting(ProductJpaEntity::getActive)
                .isEqualTo(true);
    }

    @Test
    @DisplayName("findActiveProducts hides inactive products")
    void findActiveProductsHidesInactiveProducts() {
        productRepository.save(newProduct("Active One", "active-one", "SKU-A1",
                new BigDecimal("100.00"), true, null));
        productRepository.save(newProduct("Hidden One", "hidden-one", "SKU-H1",
                new BigDecimal("100.00"), false, null));

        Page<ProductJpaEntity> page = productRepository.findActiveProducts(
                null, null, null, null, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .extracting(ProductJpaEntity::getSku)
                .containsExactly("SKU-A1");
    }

    @Test
    @DisplayName("findActiveProducts filters by category slug, search text and price range")
    void findActiveProductsAppliesFilters() {
        CategoryJpaEntity laptops = categoryRepository.save(CategoryJpaEntity.builder()
                .name("Laptops")
                .slug("laptops")
                .build());

        productRepository.save(newProduct("Cheap Phone", "cheap-phone", "SKU-C1",
                new BigDecimal("200.00"), true, phones));
        productRepository.save(newProduct("Flagship Phone", "flagship-phone", "SKU-F1",
                new BigDecimal("1200.00"), true, phones));
        productRepository.save(newProduct("Budget Laptop", "budget-laptop", "SKU-L1",
                new BigDecimal("800.00"), true, laptops));

        // category filter
        assertThat(productRepository.findActiveProducts("phones", null, null, null, PageRequest.of(0, 10))
                .getContent())
                .extracting(ProductJpaEntity::getSku)
                .containsExactlyInAnyOrder("SKU-C1", "SKU-F1");

        // price range filter
        assertThat(productRepository.findActiveProducts(null, null,
                        new BigDecimal("300.00"), new BigDecimal("1000.00"), PageRequest.of(0, 10))
                .getContent())
                .extracting(ProductJpaEntity::getSku)
                .containsExactly("SKU-L1");

        // case-insensitive name search
        assertThat(productRepository.findActiveProducts(null, "FLAGSHIP", null, null, PageRequest.of(0, 10))
                .getContent())
                .extracting(ProductJpaEntity::getSku)
                .containsExactly("SKU-F1");
    }

    private ProductJpaEntity newProduct(String name, String slug, String sku,
                                        BigDecimal basePrice, boolean active,
                                        CategoryJpaEntity category) {
        // Do NOT set id here: @GeneratedValue(AUTO) expects a null id on insert.
        // Assigning one makes Hibernate treat the entity as detached and issue an UPDATE
        // on a row that does not exist yet -> StaleObjectStateException.
        return ProductJpaEntity.builder()
                .name(name)
                .slug(slug)
                .sku(sku)
                .basePrice(basePrice)
                .active(active)
                .category(category)
                .build();
    }
}
