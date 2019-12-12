package com.scholanova.projectstore.repositories;

import com.scholanova.projectstore.exceptions.ModelNotFoundException;
import com.scholanova.projectstore.exceptions.StoreNotFoundException;
import com.scholanova.projectstore.models.Store;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StoreRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public StoreRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Store getById(Integer id) throws ModelNotFoundException {
        String query = "SELECT ID as id, " +
                "NAME AS name " +
                "FROM STORES " +
                "WHERE ID = :id";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);

        return jdbcTemplate.query(query,
                parameters,
                new BeanPropertyRowMapper<>(Store.class))
                .stream()
                .findFirst()
                .orElseThrow(ModelNotFoundException::new);
    }

    public Store create(Store storeToCreate) {
        KeyHolder holder = new GeneratedKeyHolder();

        String query = "INSERT INTO STORES " +
                "(NAME) VALUES " +
                "(:name)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("name", storeToCreate.getName());

        jdbcTemplate.update(query, parameters, holder);

        Integer newlyCreatedId = (Integer) holder.getKeys().get("ID");
        try {
            return this.getById(newlyCreatedId);
        } catch (ModelNotFoundException e) {
            return null;
        }
    }

    public void deleteById(Integer id) throws StoreNotFoundException {

        String query = "DELETE FROM STORES " +
                "WHERE ID = :id ";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);

        int totalAffectedRows = jdbcTemplate.update(query, parameters);
        if (totalAffectedRows == 0){
            throw new StoreNotFoundException();
        }
    }
}
