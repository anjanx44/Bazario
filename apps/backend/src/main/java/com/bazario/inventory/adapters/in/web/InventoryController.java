package com.bazario.inventory.adapters.in.web;

import com.bazario.inventory.adapters.in.web.dto.InventoryDtos;
import com.bazario.inventory.adapters.in.web.dto.InventoryWebMapper;
import com.bazario.inventory.domain.model.Inventory;
import com.bazario.inventory.ports.in.InventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryUseCase inventoryUseCase;
    private final InventoryWebMapper webMapper;

    @PostMapping("/initialize")
    public ResponseEntity<InventoryDtos.InventoryResponse> initializeInventory(
            @RequestBody InventoryDtos.InitializeInventoryRequest request) {
        Inventory inventory = inventoryUseCase.initializeInventory(request.productId(), request.initialStock(), request.lowStockThreshold());
        return ResponseEntity.ok(webMapper.toResponse(inventory));
    }

    @PatchMapping("/{productId}/stock")
    public ResponseEntity<InventoryDtos.InventoryResponse> updateStock(
            @PathVariable UUID productId,
            @RequestBody InventoryDtos.UpdateStockRequest request) {
        inventoryUseCase.updateStock(productId, request.quantityChange());
        return inventoryUseCase.getInventoryByProductId(productId)
                .map(webMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryDtos.InventoryResponse> getInventory(@PathVariable UUID productId) {
        return inventoryUseCase.getInventoryByProductId(productId)
                .map(webMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
