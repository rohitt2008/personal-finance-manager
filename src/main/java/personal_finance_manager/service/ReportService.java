package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.entity.Transaction;
import personal_finance_manager.repository.TransactionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class ReportService {

    private final
    TransactionRepository transactionRepository;



    public List<Transaction>
    monthlyReport() {

        return transactionRepository
                .findAll();

    }



    public List<Transaction>
    yearlyReport() {

        return transactionRepository
                .findAll();

    }

}