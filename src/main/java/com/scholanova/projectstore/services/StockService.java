package com.scholanova.projectstore.services;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotValidException;
import com.scholanova.projectstore.models.Stock;
import com.scholanova.projectstore.repositories.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock create(int storeId, Stock stock) throws StockNotValidException, ModelNotFoundException {
        verifyStock(stock);

        return stockRepository.addStockByStoreId(storeId, stock);
    }

    public List<Stock> listStock(int storeId) throws ModelNotFoundException {
        return stockRepository.listStocksByStoreId(storeId);
    }

    private void verifyStock(Stock stock) throws StockNotValidException {
        if (!stock.getType().equals("Fruit") && !stock.getType().equals("Nail")) {
            throw new StockNotValidException();
        }

        if (stock.getName() == null || stock.getName().trim().length() == 0) {
            throw new StockNotValidException();
        }

        if (stock.getValue() <= 0) {
            throw new StockNotValidException();
        }
    }
}
