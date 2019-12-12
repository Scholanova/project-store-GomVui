package com.scholanova.projectstore.controllers;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StoreNameCannotBeEmptyException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.services.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping(path = "/stores/{id}")
    public ResponseEntity<?> getStation(@PathVariable int id) throws ModelNotFoundException {
        try {
            return ResponseEntity.ok()
                    .body(storeService.getStore(id));
        }catch (ModelNotFoundException ex) {
            return ResponseEntity.status(400).body("store not found");
        }
    }

    @DeleteMapping(path = "/stores/{id}")
    public ResponseEntity<?> deleteStore(@PathVariable int id) {
        try {
            storeService.deleteStoreById(id);
            return ResponseEntity.status(204).body(null);
        }catch (StoreNotFoundException ex) {
            Map<String, String> erroMsg = new HashMap<>();
            erroMsg.put("msg", "store not found");
            return ResponseEntity.status(400).body(erroMsg);
        }
    }

    @PostMapping(path = "/stores")
    public ResponseEntity<?> createStore(@RequestBody Store store) throws StoreNameCannotBeEmptyException {
        try{
            Store createdStore = storeService.create(store);
            return ResponseEntity.ok()
                    .body(createdStore);
        }catch (StoreNameCannotBeEmptyException ex) {
            Map<String, String> erroMsg = new HashMap<>();
            erroMsg.put("msg", "name cannot be empty");
            return ResponseEntity.status(400).body(erroMsg);
        }
    }
}
