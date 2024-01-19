package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAllMpa() {
        SqlRowSet mpaSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa");
        List<Mpa> mpaList = new ArrayList<>();
        while (mpaSet.next()) {
            mpaList.add(new Mpa(mpaSet.getInt("mpa_id"), mpaSet.getString("mpa_name")));
        }
        return mpaList;
    }

    @Override
    public Mpa getMpaById(int id) {
        SqlRowSet mpaSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id = ?", id);
        if (!mpaSet.next()) {
            throw new NotFoundException(String.format("Mpa with id = %d not found", id));
        }
        return new Mpa(mpaSet.getInt("mpa_id"), mpaSet.getString("mpa_name"));
    }
}
