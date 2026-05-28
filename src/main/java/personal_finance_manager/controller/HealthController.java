package personal_finance_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    /**
     * Public health-check endpoint for service monitoring (e.g. UptimeRobot, Render liveness checks).
     * GET /api/health
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "Personal Finance Manager API is running smoothly."
        ));
    }
}
