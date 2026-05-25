package personal_finance_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.service.ReportService;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Get monthly report.
     * GET /api/reports/monthly/{year}/{month}
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<Map<String, Object>> monthly(@PathVariable int year,
                                                       @PathVariable int month) {
        Map<String, Object> response = reportService.monthlyReport(year, month);
        return ResponseEntity.ok(response);
    }

    /**
     * Get yearly report.
     * GET /api/reports/yearly/{year}
     */
    @GetMapping("/yearly/{year}")
    public ResponseEntity<Map<String, Object>> yearly(@PathVariable int year) {
        Map<String, Object> response = reportService.yearlyReport(year);
        return ResponseEntity.ok(response);
    }
}