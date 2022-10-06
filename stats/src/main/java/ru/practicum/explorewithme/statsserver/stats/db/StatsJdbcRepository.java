package ru.practicum.explorewithme.statsserver.stats.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.statsserver.stats.dto.HitsDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.join;

@Repository
@RequiredArgsConstructor
public class StatsJdbcRepository implements StatsRepository {

    public static final String TABLE_NAME = "stats";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Stats save(Stats stats) {
        if (stats.getCreated() == null) {
            stats.setCreated(LocalDateTime.now());
        }
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(TABLE_NAME).usingGeneratedKeyColumns("id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("app", stats.getApp())
                .addValue("uri", stats.getUri())
                .addValue("ip", stats.getIp())
                .addValue("created", stats.getCreated());
        Number id = jdbcInsert.executeAndReturnKey(parameters);
        stats.setId(id.longValue());
        return stats;
    }

    @Override
    public List<HitsDto> getHits(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end, String appName) {
        String sql = format("SELECT uri, COUNT(%s) count FROM %s WHERE created>? AND created<? AND app=? %s GROUP BY uri",
                unique ? "DISTINCT ip" : "*",
                TABLE_NAME,
                uris == null ? "" : "AND uri IN(?)"
        );

        if (uris == null) {
            return jdbcTemplate.query(sql, this::mapRowToStatsOutputDto, start, end, appName);
        }
        return jdbcTemplate.query(sql, this::mapRowToStatsOutputDto, start, end, appName, join(", ", uris));
    }

    private HitsDto mapRowToStatsOutputDto(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) return null;
        return HitsDto.builder()
                .uri(rs.getString("uri"))
                .hits(rs.getLong("count"))
                .build();
    }
}
