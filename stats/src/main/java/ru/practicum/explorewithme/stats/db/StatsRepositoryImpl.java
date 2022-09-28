package ru.practicum.explorewithme.stats.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.explorewithme.stats.dto.HitsDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {

    public static final String TABLE_NAME = "stats";

    public static final String DEFAULT_APP = "ewm";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Stats save(Stats stats) {
        if(stats.getCreated() == null){
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
    public List<HitsDto> getHits(List<String> uris, Boolean unique, LocalDateTime start, LocalDateTime end) {
        String sql = String.format(
                "SELECT uri, COUNT(%s) count FROM %s WHERE created>? AND created<? %s GROUP BY uri",
                unique ? "DISTINCT ip" : "*",
                TABLE_NAME,
                uris == null ? "" : "AND uri IN(?)"
        );

        if (uris == null) {
            return jdbcTemplate.query(sql, this::mapRowToStatsOutputDto, start, end);
        }
        return jdbcTemplate.query(sql, this::mapRowToStatsOutputDto, start, end, String.join(", ", uris));
    }

    private HitsDto mapRowToStatsOutputDto(ResultSet rs, int rowNum) throws SQLException {
        if (rs.getRow() == 0) return null;
        return HitsDto.builder()
                .app(DEFAULT_APP)
                .uri(rs.getString("uri"))
                .hits(rs.getLong("count"))
                .build();
    }
}
