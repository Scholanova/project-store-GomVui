package com.scholanova.projectstore.controllers;

import com.scholanova.projectstore.exceptions.StoreNameCannotBeEmptyException;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.services.StoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping(path = "/stores/{id}")
    public Store getStation() {
        return null;
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
