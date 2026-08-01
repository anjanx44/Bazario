import React from 'react';
import Link from 'next/link';
import { ChevronRight } from 'lucide-react';
import { Header } from '@/components/layout/Header';
import { Footer } from '@/components/layout/Footer';
import { ProductGallery } from '@/components/catalog/ProductGallery';
import { ProductInfo } from '@/components/catalog/ProductInfo';
import { ProductTabs } from '@/components/catalog/ProductTabs';
import FrequentlyBoughtTogether from '@/components/catalog/FrequentlyBoughtTogether';

export default function ProductPage() {
  const productData = {
    name: "AeroSound Elite 5 - Premium Noise-Cancelling Headphones",
    price: 299.00,
    originalPrice: 350.00,
    rating: 4.5,
    reviewsCount: 120,
    images: [
      "https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM",
      "https://lh3.googleusercontent.com/aida-public/AB6AXuCV54dkcMWwkMVBAqreY0UrmEIpcahUQn8gKhFq4YmBIi4IsuZvma6rp2juT2UBotGnR2bX7_u8zP2FDxaLUwc4WBSnNgDHoLm_rifzJ-hJLjTUDDHOoGQT6hlV4vtEkqXEIvy11YGAtekJum2QlBnV45yTqFMKfNFohLW92ldqFxoBgcHejEEDkbCH5_U3e6zwPCLdB1u1ixu0bGDWrRe_GTQrWRPuoNGazGKuTNpw1DVX1_oDQNeHu3pdUYPfnMMxq-PqaDxS7qU",
      "https://lh3.googleusercontent.com/aida-public/AB6AXuCewHoZeMQQEdBscS5Zz0-7GLBG8fQZMP2ZGmFLyvYUw1YgrzKcE6OiDGaEH6o9hhSCt_XXasINKhOTS8cPjMJ8-QNEHsd3c0vCpIju56rXwrbSI652k_rK7E39mckVRLonUZVWVDDV51BVWSYf62ka3qrpflQc7A-mFC60Y3TKJZlonHSbVnjI_mQy2vGSY3hN7PrTszbariSuKn28_yZjAWObv79ge_PfhdcOudpon6RKu1vFHzRzcaFQixJNSq9GJ_RCTE30t-E",
      "https://lh3.googleusercontent.com/aida-public/AB6AXuDq6oW5d1F19ryK9868DOkAl1Lh7S8-pioZ-BODmGL9NPriFi-S4byu-lqEbODjMgayTVLmrNqUKIWuaTXkr9_wPdx1t-zcfyUlrBKU8gxMSX3mkx-STnOHzj2QFrvkK6OKjjnBUx_l-b0nN0OOn4MMPQZuG--oNdc0v1uLai42mBzCxY-JzsgSiDhxto6IS2IGm_PIV9YiCpfF76aSiNegGVkP2wdwTkWRxe4-sZu6Oupov85z-efG7sdOEN6qi_kzEsg_b-5RYqI"
    ],
    colors: [
      { name: "Matte Black", value: "#1A1A1A" },
      { name: "Navy Blue", value: "#1B2A4E" },
      { name: "Silver Grey", value: "#D1D5DB" }
    ],
    bundle: {
      suggestedItems: [
        {
          id: "2",
          name: "Premium Aluminum Headphone Stand",
          price: 45.00,
          imageUrl: "https://lh3.googleusercontent.com/aida-public/AB6AXuDd5jFOGlYISa1ZBE_vK1v5adB3sGxypq7NSag8-JP1ZTE52cWvYjOqubO5pvcRf2vW802h4DufYfImB6BKmQpyLYtXE3XW_YA_bFplx2DVw8MgxvaPOtHpX38B_iKo17YJl3U7Lk4s-ipE5EHQngUjunxGEoZMnzgPqKcEYnitpRqGDioJTYXNRhV7rN9czuWO32mfmPSED26N8BwsLkMaEhX0QWk8psUMBl5WpqbbwS3kSrOZBIa0kOWK8zuLj0YfyIw4sk-bPgM"
        },
        {
          id: "3",
          name: "Hardshell Travel Protection Case",
          price: 29.00,
          imageUrl: "https://lh3.googleusercontent.com/aida-public/AB6AXuDcF_uGQZAUTB1cYE7N6-IcBOGNUZyXg-S5yDifpnq60Pp8mmoqfAz523XN-mc_DFNBlPFFD_Dpsv5PI7MfGXprdnrWtAW7o1JiXUbQWvVlE60Tu5WArEcGZ3SW-mlx6xKxOvWXdn3RIRD_o3taFTOS_6ELs0S4YqCyCscD3XhoVKKPGS1qIhk54EZrlIkkbpwdwjI8mbE0dR1yCheybYd1nPrA7nA_D-32UwCehkJfSd9uuDQSFf2u4WvniskYgKdbD0saZrdg-LA"
        }
      ]
    }
  };

  return (
    <div className="flex flex-col min-h-screen bg-surface">
      <Header />
      
      <main className="max-w-7xl mx-auto px-4 sm:px-12 py-8 flex-grow w-full">
        {/* Breadcrumbs */}
        <nav className="mb-8">
          <ol className="flex items-center gap-2 text-sm font-semibold text-gray-500">
            <li><Link href="/" className="hover:text-primary">Home</Link></li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" />
              <Link href="/electronics" className="hover:text-primary">Electronics</Link>
            </li>
            <li className="flex items-center gap-2">
              <ChevronRight className="w-4 h-4" />
              <Link href="/audio" className="hover:text-primary">Audio</Link>
            </li>
            <li className="flex items-center gap-2 text-on-surface">
              <ChevronRight className="w-4 h-4" />
              <span>AeroSound Elite 5</span>
            </li>
          </ol>
        </nav>

        {/* Product Core */}
        <section className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
          <ProductGallery images={productData.images} />
          <ProductInfo 
            name={productData.name}
            price={productData.price}
            originalPrice={productData.originalPrice}
            rating={productData.rating}
            reviewsCount={productData.reviewsCount}
            colors={productData.colors}
          />
        </section>

        {/* Tabs Section */}
        <ProductTabs />

        {/* Frequently Bought Together */}
        <FrequentlyBoughtTogether 
          currentProduct={{
            id: "1",
            name: productData.name,
            price: productData.price,
            imageUrl: productData.images[0]
          }}
          suggestedItems={productData.bundle.suggestedItems}
        />
      </main>

      <Footer />
    </div>
  );
}
