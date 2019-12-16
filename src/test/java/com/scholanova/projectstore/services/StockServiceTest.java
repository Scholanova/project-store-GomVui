package com.scholanova.projectstore.services;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotValidException;
import com.scholanova.projectstore.exceptions.StoreNameCannotBeEmptyException;
import com.scholanova.projectstore.models.Stock;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.repositories.StockRepository;
import com.scholanova.projectstore.repositories.StoreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@JdbcTest
public class StockServiceTest {

    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockRepository);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "STORES");
    }

    @Test
    void givenNoStockName_whenCreated_failsWithStockNotValidError() throws ModelNotFoundException {
        // GIVEN

        Store store = new Store(1, "Carrefour");
        insertStore(store);
        Stock emptyNameStock = new Stock(null, "", "Nail", 20, 1);

        // WHEN
        assertThrows(StockNotValidException.class, () -> {
            stockService.create(1, emptyNameStock);
        });

        // THEN
        verify(stockRepository, never()).addStockByStoreId(1, emptyNameStock);
    }

    @Test
    void givenZeroValue_whenCreated_failsWithStockNotValidError() throws ModelNotFoundException {
        // GIVEN

        Store store = new Store(1, "Carrefour");
        insertStore(store);
        Stock emptyNameStock = new Stock(null, "Torsadé", "Nail", 0, 1);

        // WHEN
        assertThrows(StockNotValidException.class, () -> {
            stockService.create(1, emptyNameStock);
        });

        // THEN
        verify(stockRepository, never()).addStockByStoreId(1, emptyNameStock);
    }

    @Test
    void givenNegativeValue_whenCreated_failsWithStockNotValidError() throws ModelNotFoundException {
        // GIVEN

        Store store = new Store(1, "Carrefour");
        insertStore(store);
        Stock emptyNameStock = new Stock(null, "Torsadé", "Nail", -1, 1);

        // WHEN
        assertThrows(StockNotValidException.class, () -> {
            stockService.create(1, emptyNameStock);
        });

        // THEN
        verify(stockRepository, never()).addStockByStoreId(1, emptyNameStock);
    }

    @Test
    void givenNotValidType_whenCreated_failsWithStockNotValidError() throws ModelNotFoundException {
        // GIVEN

        Store store = new Store(1, "Carrefour");
        insertStore(store);
        Stock emptyNameStock = new Stock(null, "Torsadé", "Nails", 50, 1);

        // WHEN
        assertThrows(StockNotValidException.class, () -> {
            stockService.create(1, emptyNameStock);
        });

        // THEN
        verify(stockRepository, never()).addStockByStoreId(1, emptyNameStock);
    }

    @Test
    void givenValidStock_whenCreated_stockCreated() throws ModelNotFoundException, StockNotValidException {
        // GIVEN

        Store store = new Store(1, "Carrefour");
        insertStore(store);
        Stock validStock = new Stock(null, "ds", "Nail", 20, 1);

        // WHEN
        stockService.create(1, validStock);

        // THEN
        verify(stockRepository).addStockByStoreId(1, validStock);
    }

    @Test
    void givenExistingStore_returnListOfStock() throws ModelNotFoundException {
        //GIVEN

        Store mockedStore = new Store(1, "carrefour");
        insertStore(mockedStore);

        //WHEN
        List listStock = stockService.listStock(1);

        //THEN
        verify(stockRepository).listStocksByStoreId(1);
    }

    private void insertStore(Store store) {
        String query = "INSERT INTO STORES " +
                "(ID, NAME) " +
                "VALUES ('%d', '%s')";
        jdbcTemplate.execute(
                String.format(query, store.getId(), store.getName()));
    }
}
