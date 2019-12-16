package com.scholanova.projectstore.controllers;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotValidException;

import com.scholanova.projectstore.models.Stock;
import com.scholanova.projectstore.services.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping(path = "/stores/{storeId}/stocks")
    public ResponseEntity<?> createStock(@PathVariable int storeId, @RequestBody Stock stock) throws ModelNotFoundException {
        try{
            Stock createdStock = stockService.create(storeId, stock);
            return ResponseEntity.ok()
                    .body(createdStock);
        }catch (StockNotValidException ex) {
            Map<String, String> erroMsg = new HashMap<>();
            erroMsg.put("msg", "Invalid stock");
            return ResponseEntity.status(400).body(erroMsg);
        }
    }

    @GetMapping(path = "/stores/{storeId}/stocks")
    public ResponseEntity<?> listStock(@PathVariable int storeId) {
        try {
            return ResponseEntity.ok()
                    .body(stockService.listStock(storeId));
        }catch (ModelNotFoundException ex) {
            Map<String, String> erroMsg = new HashMap<>();
            erroMsg.put("msg", "Store not found");
            return ResponseEntity.status(400).body(erroMsg);
        }
    }

}
