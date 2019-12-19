package com.scholanova.projectstore.repositories;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotValidException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.models.Stock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(StockRepository.class)
@JdbcTest
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "STOCK", "STORES");
    }

    @Nested
    class Test_getById {

        @Test
        void whenNoStockWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer id = 1000;

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.getById(id);
            });
        }

        @Test
        void whenStockExists_thenReturnsTheStock() throws Exception {
            // Given
            int storeId = 1;
            int stockId = 1;

            Store store = new Store(storeId, "Carrefour");
            insertStore(store);
            Stock stock = new Stock(stockId, "Poire", "Fruit", 50, storeId);
            insertStock(stock);

            // When
            Stock extractedStock = stockRepository.getById(stockId);

            // Then
            assertThat(extractedStock).isEqualToComparingFieldByField(stock);
        }
    }

    @Nested
    class Test_listStocksByStoreId {

        @Test
        void whenNoStoreWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer storeId = 1000;

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.listStocksByStoreId(storeId);
            });
        }

        @Test
        void whenStoreExists_thenReturnsTheStock() throws Exception {
            // Given
            int storeId = 1;
            int stockId = 1;
            int stock2Id = 2;

            Store store = new Store(storeId, "Carrefour");
            insertStore(store);
            Stock stock = new Stock(stockId, "Poire", "Fruit", 50, storeId);
            insertStock(stock);
            Stock stock2 = new Stock(stock2Id, "Pomme", "Fruit", 49, storeId);
            insertStock(stock2);

            // When
            List<Stock> extractedStocks = stockRepository.listStocksByStoreId(storeId);

            // Then
            assertThat(extractedStocks).isNotEmpty();
            assertThat(extractedStocks.size()).isEqualTo(2);
            assertThat(extractedStocks.contains(stock));
            assertThat(extractedStocks.contains(stock2));
        }
    }

    @Nested
    class Test_getStockByStockIdAndStoreId {

        @Test
        void whenNoStoreNoStockWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer storeId = 1000;
            Integer stockId = 1000;

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.getStockByStockIdAndStoreId(storeId, stockId);
            });
        }

        @Test
        void whenNoStoreWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer existingStoreId = 1000;
            Integer notExistingStoreCalled = 999;
            Integer stockId = 1000;

            Store store = new Store(existingStoreId, "Carrefour");
            insertStore(store);
            Stock stock = new Stock(stockId, "Poire", "Fruit", 50, existingStoreId);
            insertStock(stock);

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.getStockByStockIdAndStoreId(notExistingStoreCalled, stockId);
            });
        }

        @Test
        void whenNoStockWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer storeId = 1000;
            Integer notExistingStockCalled = 999;
            Integer existingStockId = 1000;

            Store store = new Store(storeId, "Carrefour");
            insertStore(store);
            Stock stock = new Stock(existingStockId, "Poire", "Fruit", 50, storeId);
            insertStock(stock);

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.getStockByStockIdAndStoreId(storeId, notExistingStockCalled);
            });
        }

        @Test
        void whenStoreAndStockExist_thenReturnsTheStock() throws Exception {
            // Given
            int storeId = 1;
            int stockId = 1;

            Store store = new Store(storeId, "Carrefour");
            insertStore(store);
            Stock stock = new Stock(stockId, "Poire", "Fruit", 50, storeId);
            insertStock(stock);
            Stock stock2 = new Stock(2, "Pomme", "Fruit", 49, storeId);
            insertStock(stock2);

            // When
            Stock extractedStock = stockRepository.getStockByStockIdAndStoreId(storeId, stockId);

            // Then
            assertThat(extractedStock).isEqualToComparingFieldByField(stock);
        }
    }

    @Nested
    class Test_addStockByStoreId {

        @Test
        void whenCreateStock_thenStoreIsInDatabaseWithId() throws ModelNotFoundException {
            // Given
            int mockedStoreId = 5;
            Store mockedStore = new Store(mockedStoreId, "Auchan");
            insertStore(mockedStore);
            Stock stock = new Stock(null, "Poire", "Fruit", 50, 5);

            // When
            Stock createdStock = stockRepository.addStockByStoreId(mockedStoreId, stock);

            // Then
            assertThat(createdStock.getId()).isNotNull();
            assertThat(createdStock.getName()).isEqualTo(stock.getName());
        }
    }

    @Nested
    class Test_deleteById {

        @Test
        void whenNoStockWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer id = 1000;

            // When & Then
            assertThrows(StockNotFoundException.class, () -> {
                stockRepository.deleteById(id);
            });
        }

        @Test
        void whenStockExists_thenDeleteTheStock() throws Exception {
            // Given
            Store store = new Store(1, "Carrefour");
            insertStore(store);
            int stockId = 1;
            Stock stock = new Stock(stockId, "Poire", "Fruit", 50, 1);
            insertStock(stock);

            // When
            stockRepository.deleteById(stockId);

            // Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.getById(stockId);
            });
        }
    }

    @Nested
    class Test_getStoreTotalValue {

        @Test
        void whenGivenExistingStoreWithStock_thenReturnStockTotalValue() throws ModelNotFoundException {
            // Given
            int mockedStoreId = 5;
            Store mockedStore = new Store(mockedStoreId, "Auchan");
            insertStore(mockedStore);
            Stock mockedStock = new Stock(1, "Poire", "Fruit", 50, 5);
            insertStock(mockedStock);
            Stock mockedStock2 = new Stock(2, "Pomme", "Fruit", 55, 5);
            insertStock(mockedStock2);

            // When
            int totalStoreValue = stockRepository.getStoreTotalValue(mockedStoreId);

            // Then
            assertThat(totalStoreValue).isEqualTo(105);
        }

        @Test
        void whenGivenExistingStoreWithoutStock_thenReturnStockTotalValue() throws ModelNotFoundException {
            // Given
            int mockedStoreId = 5;
            Store mockedStore = new Store(mockedStoreId, "Auchan");
            insertStore(mockedStore);

            // When
            Integer totalStoreValue = stockRepository.getStoreTotalValue(mockedStoreId);

            // Then
            assertThat(totalStoreValue).isEqualTo(0);
        }
    }

    @Nested
    class Test_getStoreStockByType {

        @Test
        void whenNoStoreWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer storeId = 1000;
            String requestedType = "Fruit";

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                stockRepository.getStoreStockByType(storeId, requestedType);
            });
        }

        @Test
        void whenStoreExists_thenReturnsTheStock() throws Exception {
            // Given
            int storeId = 1;
            int stockId = 1;
            int stock2Id = 2;
            int stock3Id = 3;

            Store store = new Store(storeId, "Carrefour");
            insertStore(store);
            Stock stock = new Stock(stockId, "Poire", "Fruit", 50, storeId);
            insertStock(stock);
            Stock stock2 = new Stock(stock2Id, "Pomme", "Fruit", 49, storeId);
            insertStock(stock2);
            Stock stock3 = new Stock(stock3Id, "Pomme", "Nail", 49, storeId);
            insertStock(stock3);

            String requestedType = "Fruit";

            // When
            List<Stock> extractedStocks = stockRepository.getStoreStockByType(storeId, requestedType);

            // Then
            assertThat(extractedStocks).isNotEmpty();
            assertThat(extractedStocks.size()).isEqualTo(2);
            assertThat(extractedStocks.contains(stock));
            assertThat(extractedStocks.contains(stock2));
        }
    }

    private void insertStock(Stock stock) {
        String query = "INSERT INTO STOCK " +
                "(ID, NAME, TYPE, VALUE, STOREID) " +
                "VALUES ('%d', '%s', '%s', '%d', '%d')";
        jdbcTemplate.execute(
                String.format(query, stock.getId(), stock.getName(),  stock.getType(),  stock.getValue(),  stock.getStoreId()));
    }

    private void insertStore(Store store) {
        String query = "INSERT INTO STORES " +
                "(ID, NAME) " +
                "VALUES ('%d', '%s')";
        jdbcTemplate.execute(
                String.format(query, store.getId(), store.getName()));
    }
}
