package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.entity.Transaction;
import personal_finance_manager.entity.User;
import personal_finance_manager.repository.TransactionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    /**
     * Generate monthly report for specified year/month.
     */
    public Map<String, Object> monthlyReport(int year, int month) {
        if (month < 1 || month > 12) {
            throw new personal_finance_manager.exception.BadRequestException("Invalid month: " + month);
        }
        User user = authService.getCurrentUser();

        String monthStr = String.format("%02d", month);
        String startDate = year + "-" + monthStr + "-01";

        // Calculate end of month
        int lastDay;
        switch (month) {
            case 2:
                lastDay = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 29 : 28;
                break;
            case 4: case 6: case 9: case 11:
                lastDay = 30;
                break;
            default:
                lastDay = 31;
                break;
        }
        String endDate = year + "-" + monthStr + "-" + String.format("%02d", lastDay);

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateBetweenOrderByDateDescIdDesc(user, startDate, endDate);

        return buildReport(transactions, year, month);
    }

    /**
     * Generate yearly report for specified year.
     */
    public Map<String, Object> yearlyReport(int year) {
        User user = authService.getCurrentUser();

        String startDate = year + "-01-01";
        String endDate = year + "-12-31";

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateBetweenOrderByDateDescIdDesc(user, startDate, endDate);

        return buildYearlyReport(transactions, year);
    }

    private Map<String, Object> buildReport(List<Transaction> transactions, int year, int month) {
        // Group income by category
        Map<String, Double> totalIncome = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Group expenses by category
        Map<String, Double> totalExpenses = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        double incomeSum = totalIncome.values().stream().mapToDouble(Double::doubleValue).sum();
        double expenseSum = totalExpenses.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("month", month);
        report.put("year", year);
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("netSavings", incomeSum - expenseSum);
        return report;
    }

    private Map<String, Object> buildYearlyReport(List<Transaction> transactions, int year) {
        // Group income by category
        Map<String, Double> totalIncome = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        // Group expenses by category
        Map<String, Double> totalExpenses = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        double incomeSum = totalIncome.values().stream().mapToDouble(Double::doubleValue).sum();
        double expenseSum = totalExpenses.values().stream().mapToDouble(Double::doubleValue).sum();

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("year", year);
        report.put("totalIncome", totalIncome);
        report.put("totalExpenses", totalExpenses);
        report.put("netSavings", incomeSum - expenseSum);
        return report;
    }
}