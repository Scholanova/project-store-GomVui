package com.scholanova.projectstore.controllers;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotValidException;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.models.Stock;
import com.scholanova.projectstore.services.StockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StockControllerTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate template = new TestRestTemplate();

    @MockBean
    private StockService stockService;

    @Captor
    ArgumentCaptor<Stock> createStockArgumentCaptor;

    @AfterEach
    public void reset_mocks() {
        Mockito.reset(stockService);
    }

    @Captor
    ArgumentCaptor<Integer> storeIdArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> stockIdArgumentCaptor;

    @Nested
    class Test_createStock {

        @Test
        void givenCorrectBody_whenCalled_createsStock() throws Exception, StockNotValidException {
            // given
            String url = "http://localhost:{port}/stores/1/stocks";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestJson = "{" +
                    "\"name\":\"Flat Nail\"," +
                    "\"type\":\"Nail\"," +
                    "\"value\":100," +
                    "\"storeId\":1" +
                    "}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            Stock createdStock = new Stock(1, "Flat Nail", "Nail", 100, 1);
            when(stockService.create(storeIdArgumentCaptor.capture(),createStockArgumentCaptor.capture())).thenReturn(createdStock);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"id\":1," +
                            "\"name\":\"Flat Nail\"," +
                            "\"type\":\"Nail\"," +
                            "\"value\":100," +
                            "\"storeId\":1" +
                            "}"
            );
            Stock stockToCreate = createStockArgumentCaptor.getValue();
            assertThat(stockToCreate.getName()).isEqualTo("Flat Nail");
            assertThat(storeIdArgumentCaptor.getValue()).isEqualTo(1);
        }

        @Test
        void givenEmptyName_whenCalled_createsStore() throws Exception, StockNotValidException {
            // given
            String url = "http://localhost:{port}/stores/1/stocks";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestJson = "{" +
                    "\"name\":\"\"," +
                    "\"type\":\"Nail\"," +
                    "\"value\":100," +
                    "\"storeId\":1" +
                    "}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            when(stockService.create(storeIdArgumentCaptor.capture(),createStockArgumentCaptor.capture())).thenThrow(StockNotValidException.class);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"msg\":\"Invalid stock\"" +
                            "}"
            );
            Stock stockToCreate = createStockArgumentCaptor.getValue();
            assertThat(stockToCreate.getName()).isEqualTo("");
        }

        @Test
        void givenZeroToValue_whenCalled_createsStore() throws Exception, StockNotValidException {
            // given
            String url = "http://localhost:{port}/stores/1/stocks";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestJson = "{" +
                    "\"name\":\"salut\"," +
                    "\"type\":\"Nail\"," +
                    "\"value\":0," +
                    "\"storeId\":1" +
                    "}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            when(stockService.create(storeIdArgumentCaptor.capture(),createStockArgumentCaptor.capture())).thenThrow(StockNotValidException.class);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"msg\":\"Invalid stock\"" +
                            "}"
            );
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);

            Stock stockToCreate = createStockArgumentCaptor.getValue();
            assertThat(stockToCreate.getName()).isEqualTo("salut");
            assertThat(stockToCreate.getValue()).isEqualTo(0);
            assertThat(storeIdArgumentCaptor.getValue()).isEqualTo(1);
        }

        @Test
        void givenBadType_whenCalled_createsStore() throws Exception, StockNotValidException {
            // given
            String url = "http://localhost:{port}/stores/1/stocks";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestJson = "{" +
                    "\"name\":\"salut\"," +
                    "\"type\":\"Fruits\"," +
                    "\"value\":10," +
                    "\"storeId\":1" +
                    "}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            when(stockService.create(storeIdArgumentCaptor.capture(),createStockArgumentCaptor.capture())).thenThrow(StockNotValidException.class);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"msg\":\"Invalid stock\"" +
                            "}"
            );
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);

            Stock stockToCreate = createStockArgumentCaptor.getValue();
            assertThat(stockToCreate.getType()).isEqualTo("Fruits");
        }
    }

    @Nested
    class Test_listStock {

        @Test
        void givenCorrectStoreId_whenCalled_getStock() throws Exception {
            // given
            String url = "http://localhost:{port}/stores/1/stocks";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            Stock createdStock = new Stock(1, "Flat Nail", "Nail", 100, 1);
            Stock createdStock2 = new Stock(1, "Not Flat Nail", "Nail", 100, 1);
            List<Stock> stockList = new ArrayList<>();
            stockList.add(createdStock);
            stockList.add(createdStock2);

            when(stockService.listStock(storeIdArgumentCaptor.capture())).thenReturn(stockList);

            // When
            ResponseEntity<String> responseEntity = template.exchange(url,
                    HttpMethod.GET,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "[" +
                        "{" +
                            "\"id\":1," +
                            "\"name\":\"Flat Nail\"," +
                            "\"type\":\"Nail\"," +
                            "\"value\":100," +
                            "\"storeId\":1" +
                        "}," +
                        "{" +
                            "\"id\":1," +
                            "\"name\":\"Not Flat Nail\"," +
                            "\"type\":\"Nail\"," +
                            "\"value\":100," +
                            "\"storeId\":1" +
                        "}" +
                    "]"
            );
            assertThat(storeIdArgumentCaptor.getValue()).isEqualTo(1);
        }

        @Test
        void givenNonExistantStoreId_whenCalled_getException() throws Exception {
            // given
            String url = "http://localhost:{port}/stores/1/stocks";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            when(stockService.listStock(1)).thenThrow(ModelNotFoundException.class);

            // When
            ResponseEntity<String> responseEntity = template.exchange(url,
                    HttpMethod.GET,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"msg\":\"Store not found\"" +
                    "}"
            );
            verify(stockService).listStock(1);
        }
    }

    @Nested
    class Test_deleteStock {

        @Test
        void givenExistingStockId_whenCalled_deleteStock() throws Exception {
            // given
            String url = "http://localhost:{port}/stocks/1";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            doNothing().when(stockService).deleteStockById(stockIdArgumentCaptor.capture());

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.DELETE,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);
            assertThat(responseEntity.getBody()).isEqualTo(null);
            int stockIdDeleted = stockIdArgumentCaptor.getValue();
            assertThat(stockIdDeleted).isEqualTo(1);
        }

        @Test
        void givenNonExistantStockId_whenCalled_getException() throws Exception {
            // given
            String url = "http://localhost:{port}/stocks/13";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            doThrow(new StockNotFoundException()).when(stockService).deleteStockById(13);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.DELETE,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"msg\":\"stock not found\"" +
                            "}"
            );
            verify(stockService).deleteStockById(13);
        }
    }
}
