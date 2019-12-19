package com.scholanova.projectstore.services;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StoreNameCannotBeEmptyException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.models.StoreWithTotalValue;
import com.scholanova.projectstore.repositories.StockRepository;
import com.scholanova.projectstore.repositories.StoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class StoreService {

    private StoreRepository storeRepository;
    private StockRepository stockRepository;

    public StoreService(StoreRepository storeRepository, StockRepository stockRepository) {
        this.storeRepository = storeRepository;
        this.stockRepository = stockRepository;
    }

    public Store create(Store store) throws StoreNameCannotBeEmptyException {

        if (isNameMissing(store)) {
            throw new StoreNameCannotBeEmptyException();
        }

        return storeRepository.create(store);
    }

    private boolean isNameMissing(Store store) {
        return store.getName() == null ||
                store.getName().trim().length() == 0;
    }

    public StoreWithTotalValue getStore(int id) throws ModelNotFoundException {
        Integer totalStoreValue = stockRepository.getStoreTotalValue(id);
        Store store = storeRepository.getById(id);

        return new StoreWithTotalValue(store.getId(), store.getName(), totalStoreValue);
    }

    public void deleteStoreById(int id) throws StoreNotFoundException {
        storeRepository.deleteById(id);
    }
}
