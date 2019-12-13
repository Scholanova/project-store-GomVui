package com.scholanova.projectstore.controllers;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StoreNameCannotBeEmptyException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Store;
import com.scholanova.projectstore.services.StoreService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StoreControllerTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate template = new TestRestTemplate();

    @MockBean
    private StoreService storeService;

    @Captor
    ArgumentCaptor<Store> createStoreArgumentCaptor;

    @Captor
    ArgumentCaptor<Integer> getStoreArgumentCaptor;

    @Nested
    class Test_createStore {

        @Test
        void givenCorrectBody_whenCalled_createsStore() throws Exception {
            // given
            String url = "http://localhost:{port}/stores";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestJson = "{" +
                    "\"name\":\"Boulangerie\"" +
                    "}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            Store createdStore = new Store(123, "Boulangerie");
            when(storeService.create(createStoreArgumentCaptor.capture())).thenReturn(createdStore);

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
                            "\"id\":123," +
                            "\"name\":\"Boulangerie\"" +
                            "}"
            );
            Store storeToCreate = createStoreArgumentCaptor.getValue();
            assertThat(storeToCreate.getName()).isEqualTo("Boulangerie");
        }

        @Test
        void givenEmptyBody_whenCalled_createsStore() throws Exception {
            // given
            String url = "http://localhost:{port}/stores";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestJson = "{" +
                    "\"name\":\"\"" +
                    "}";
            HttpEntity<String> httpEntity = new HttpEntity<>(requestJson, headers);

            when(storeService.create(createStoreArgumentCaptor.capture())).thenThrow(StoreNameCannotBeEmptyException.class);

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
                        "\"msg\":\"name cannot be empty\"" +
                    "}"
            );
            Store storeNotCreate = createStoreArgumentCaptor.getValue();
            assertThat(storeNotCreate == null);
        }
    }

    @Nested
    class Test_getStore {

        @Test
        void givenExistingStoreId_whenCalled_getStore() throws Exception {
            // given
            String url = "http://localhost:{port}/stores/12";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            Store returnedStore = new Store(12, "boulangerie");
            when(storeService.getStore(getStoreArgumentCaptor.capture())).thenReturn(returnedStore);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.GET,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"id\":12," +
                            "\"name\":\"boulangerie\"" +
                            "}"
            );
            Integer storeToGet = getStoreArgumentCaptor.getValue();
            assertThat(storeToGet).isEqualTo(12);
        }

        @Test
        void givenNonExistingStoreId_whenCalled_getStore() throws Exception {
            // given
            String url = "http://localhost:{port}/stores/13";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            doThrow(new ModelNotFoundException()).when(storeService).getStore(13);

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.GET,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(BAD_REQUEST);
            assertThat(responseEntity.getBody()).isEqualTo(
                    "{" +
                            "\"msg\":\"store not found\"" +
                            "}"
            );
            verify(storeService).getStore(13);
        }
    }

    @Nested
    class Test_deleteStore {

        @Test
        void givenExistingStoreId_whenCalled_deleteStore() throws Exception {
            // given
            String url = "http://localhost:{port}/stores/12";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            doNothing().when(storeService).deleteStoreById(getStoreArgumentCaptor.capture());

            // When
            ResponseEntity responseEntity = template.exchange(url,
                    HttpMethod.DELETE,
                    httpEntity,
                    String.class,
                    urlVariables);

            // Then
            assertThat(responseEntity.getStatusCode()).isEqualTo(NO_CONTENT);

            Integer storeIdToDelete = getStoreArgumentCaptor.getValue();
            assertThat(storeIdToDelete).isEqualTo(12);
        }

        @Test
        void givenNonExistingStoreId_whenCalled_deleteStore() throws Exception {
            // given
            String url = "http://localhost:{port}/stores/13";

            Map<String, String> urlVariables = new HashMap<>();
            urlVariables.put("port", String.valueOf(port));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> httpEntity = new HttpEntity<>(headers);

            doThrow(new StoreNotFoundException()).when(storeService).deleteStoreById(13);

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
                        "\"msg\":\"store not found\"" +
                    "}"
            );
            verify(storeService).deleteStoreById(13);
        }
    }
}