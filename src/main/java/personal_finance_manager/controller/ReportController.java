package personal_finance_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.entity.Transaction;
import personal_finance_manager.service.ReportService;

import java.util.List;

@RestController
@RequestMapping(
        "/api/reports"
)

@RequiredArgsConstructor

public class ReportController {

    private final
    ReportService reportService;



    @GetMapping(
            "/monthly"
    )

    public List<Transaction>
    monthly() {

        return reportService
                .monthlyReport();

    }



    @GetMapping(
            "/yearly"
    )

    public List<Transaction>
    yearly() {

        return reportService
                .yearlyReport();

    }

}