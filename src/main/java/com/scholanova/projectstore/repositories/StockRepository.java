package com.scholanova.projectstore.repositories;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StockNotValidException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Stock;
import com.scholanova.projectstore.models.Store;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StockRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StockRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Stock getById(Integer id) throws ModelNotFoundException {
        String query = "SELECT ID as id, " +
                "NAME AS name, " +
                "TYPE AS type, " +
                "VALUE AS value, " +
                "STOREID AS storeId " +
                "FROM STOCK " +
                "WHERE ID = :id";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);

        return jdbcTemplate.query(query,
                parameters,
                new BeanPropertyRowMapper<>(Stock.class))
                .stream()
                .findFirst()
                .orElseThrow(ModelNotFoundException::new);
    }

    public List<Stock> listStocksByStoreId(Integer storeId) throws ModelNotFoundException {
        String query = "SELECT ID as id, " +
                "NAME AS name, " +
                "TYPE AS type, " +
                "VALUE AS value, " +
                "STOREID AS storeId " +
                "FROM STOCK " +
                "WHERE storeId = :storeId";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("storeId", storeId);

        List<Stock> stocks = jdbcTemplate.query(query,
                parameters,
                new BeanPropertyRowMapper<>(Stock.class));

        if (stocks.isEmpty()) {
            throw new ModelNotFoundException();
        }
        return jdbcTemplate.query(query,
                parameters,
                new BeanPropertyRowMapper<>(Stock.class));
    }

    public Stock getStockByStockIdAndStoreId(Integer storeId, Integer stockId) throws ModelNotFoundException {
        String query = "SELECT ID as id, " +
                "NAME AS name, " +
                "TYPE AS type, " +
                "VALUE AS value, " +
                "STOREID AS storeId " +
                "FROM STOCK " +
                "WHERE storeId = :storeId AND " +
                "id = :stockId";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("storeId", storeId);
        parameters.put("stockId", stockId);

        return jdbcTemplate.query(query,
                parameters,
                new BeanPropertyRowMapper<>(Stock.class))
                .stream()
                .findFirst()
                .orElseThrow(ModelNotFoundException::new);
    }
    
    public Stock addStockByStoreId(Integer storeId, Stock stock) throws ModelNotFoundException {
        KeyHolder holder = new GeneratedKeyHolder();

        String query = "INSERT INTO STOCK " +
                "(NAME, TYPE, VALUE, STOREID) VALUES " +
                "(:name, :type, :value, :storeId)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("storeId", storeId)
                .addValue("value", stock.getValue())
                .addValue("type", stock.getType())
                .addValue("name", stock.getName());

        jdbcTemplate.update(query, parameters, holder);

        Integer newlyCreatedId = (Integer) holder.getKeys().get("ID");
        try {
            return this.getById(newlyCreatedId);
        } catch (ModelNotFoundException e) {
            return null;
        }
    }

}
