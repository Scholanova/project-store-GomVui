package com.scholanova.projectstore.repositories;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Stock;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.models.StoreWithTotalValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringJUnitConfig(StoreRepository.class)
@JdbcTest
class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "STOCK", "STORES");
    }

    @Nested
    class Test_getById {

        @Test
        void whenNoStoresWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer id = 1000;

            // When & Then
            assertThrows(ModelNotFoundException.class, () -> {
                storeRepository.getById(id);
            });
        }

        @Test
        void whenStoreExists_thenReturnsTheStore() throws Exception {
            // Given
            Integer id = 1;
            Store store = new Store(id, "Carrefour");
            insertStore(store);

            // When
            Store extractedStore = storeRepository.getById(id);

            // Then
            assertThat(extractedStore).isEqualToComparingFieldByField(store);
        }
    }

    @Nested
    class Test_create {

        @Test
        void whenCreateStore_thenStoreIsInDatabaseWithId() {
            // Given
            String storeName = "Auchan";
            Store storeToCreate = new Store(null, storeName);

            // When
            Store createdStore = storeRepository.create(storeToCreate);

            // Then
            assertThat(createdStore.getId()).isNotNull();
            assertThat(createdStore.getName()).isEqualTo(storeName);
        }
    }

    @Nested
    class Test_deleteById {

        @Test
        void whenNoStoresWithThatId_thenThrowsException() throws Exception {
            // Given
            Integer id = 1000;

            // When & Then
            assertThrows(StoreNotFoundException.class, () -> {
                storeRepository.deleteById(id);
            });
        }

        @Test
        void whenStoreExists_thenDeleteTheStore() throws Exception {
            // Given
            Integer id = 1;
            Store store = new Store(id, "Carrefour");
            insertStore(store);

            // When
            storeRepository.deleteById(id);

            // Then
            assertThrows(ModelNotFoundException.class, () -> {
                storeRepository.getById(id);
            });
        }
    }

    @Nested
    class Test_getListStoreByMinimumTotalStockValue {

        @Test
        void whenCorrectCalled_getListOfStores() throws ModelNotFoundException {
            //Given
            int minimumStockTotalValue = 100;
            Store store1 = new Store(1, "carrefour");
            Store store2 = new Store(2, "auchan");
            insertStore(store1);
            insertStore(store2);

            Stock mockedStock1 = new Stock(1, "Poire", "Fruit", 40, 1 );
            Stock mockedStock2 = new Stock(2, "Pomme", "Fruit", 90, 1 );
            Stock mockedStock3 = new Stock(3, "PATATE", "Fruit", 60, 2 );
            insertStock(mockedStock1);
            insertStock(mockedStock2);
            insertStock(mockedStock3);

            //WHEN
            List<StoreWithTotalValue> storeList = storeRepository.getStoreWithMinimumStockValue(minimumStockTotalValue);

            //THEN
            assertThat(storeList.get(0).getName()).isEqualTo(store1.getName());
            assertThat(storeList.get(0).getId()).isEqualTo(store1.getId());
            assertThat(storeList.size()).isEqualTo(1);
        }

        @Test
        void whenCorrectCalled2_getListOfStores() throws ModelNotFoundException {
            //Given
            int minimumStockTotalValue = 100;
            Store store1 = new Store(1, "carrefour");
            Store store2 = new Store(2, "auchan");
            insertStore(store1);
            insertStore(store2);

            Stock mockedStock1 = new Stock(1, "Poire", "Fruit", 40, 1 );
            Stock mockedStock2 = new Stock(2, "Pomme", "Fruit", 90, 1 );
            Stock mockedStock3 = new Stock(3, "PATATE", "Fruit", 110, 2 );
            insertStock(mockedStock1);
            insertStock(mockedStock2);
            insertStock(mockedStock3);

            //WHEN
            List<StoreWithTotalValue> storeList = storeRepository.getStoreWithMinimumStockValue(minimumStockTotalValue);

            //THEN
            assertThat(storeList.get(0).getName()).isEqualTo(store1.getName());
            assertThat(storeList.get(1).getName()).isEqualTo(store2.getName());
            assertThat(storeList.get(0).getId()).isEqualTo(store1.getId());
            assertThat(storeList.get(1).getId()).isEqualTo(store2.getId());
            assertThat(storeList.size()).isEqualTo(2);
        }

        @Test
        void whenCorrectCalled_givenTooHighMinimum_getEmptyListOfStores() throws ModelNotFoundException {
            //Given
            int minimumStockTotalValue = 200;
            Store store1 = new Store(1, "carrefour");
            Store store2 = new Store(2, "auchan");
            insertStore(store1);
            insertStore(store2);

            Stock mockedStock1 = new Stock(1, "Poire", "Fruit", 40, 1 );
            Stock mockedStock2 = new Stock(2, "Pomme", "Fruit", 90, 1 );
            Stock mockedStock3 = new Stock(3, "PATATE", "Fruit", 110, 2 );
            insertStock(mockedStock1);
            insertStock(mockedStock2);
            insertStock(mockedStock3);

            //WHEN
            List<StoreWithTotalValue> storeList = storeRepository.getStoreWithMinimumStockValue(minimumStockTotalValue);

            //THEN
            assertThat(storeList.size()).isEqualTo(0);
        }
    }

    private void insertStore(Store store) {
        String query = "INSERT INTO STORES " +
                "(ID, NAME) " +
                "VALUES ('%d', '%s')";
        jdbcTemplate.execute(
                String.format(query, store.getId(), store.getName()));
    }

    private void insertStock(Stock stock) {
        String query = "INSERT INTO STOCK " +
                "(ID, NAME, TYPE, VALUE, STOREID) " +
                "VALUES ('%d', '%s', '%s', '%d', '%d')";
        jdbcTemplate.execute(
                String.format(query, stock.getId(), stock.getName(),  stock.getType(),  stock.getValue(),  stock.getStoreId()));
    }
}