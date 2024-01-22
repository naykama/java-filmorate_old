package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Map<Integer, Mpa> mpas = new HashMap<>();

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        SqlRowSet mpaSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        while (mpaSet.next()) {
            mpas.put(mpaSet.getInt("mpa_id"),
                    new Mpa(mpaSet.getInt("mpa_id"), mpaSet.getString("mpa_name")));
        }
    }

    @Override
    public List<Mpa> getAllMpa() {
        return new ArrayList<>(mpas.values());
    }

    @Override
    public Mpa getMpaById(int id) {
        if (!mpas.containsKey(id)) {
            throw new NotFoundException(String.format("Mpa with id = %d not found", id));
        }
        return mpas.get(id);
    }
}
