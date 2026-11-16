package in.project.gatekeeper.management.Services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class HealthService {
    private final JdbcTemplate jdbcTemplate;
    public HealthService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getDbStatus(){
        String dbStatus = "DOWN";
        try{
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if(result != null && result == 1){
                dbStatus = "UP";
            }
        } catch (Exception e) {
            System.out.println("My Sql Health Check Failed" + e.getMessage());
        }
        return dbStatus;
    }
}
