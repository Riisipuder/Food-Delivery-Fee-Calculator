package ee.fujitsu.fooddeliveryfeecalculator.infrastructure.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DatabaseHealthRepository {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHealthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isReachable() {
        Integer result = jdbcTemplate.queryForObject("select 1", Integer.class);
        return Integer.valueOf(1).equals(result);
    }
}
