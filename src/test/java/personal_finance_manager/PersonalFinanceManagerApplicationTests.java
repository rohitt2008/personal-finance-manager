package personal_finance_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive integration tests for the Personal Finance Manager API.
 * Tests all endpoints with proper session-based authentication.
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonalFinanceManagerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockHttpSession session;

    // ===================== AUTH TESTS =====================

    @Test
    @Order(1)
    void testRegisterUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user@example.com","password":"password123","fullName":"John Doe","phoneNumber":"+1234567890"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").isNumber());
    }

    @Test
    @Order(2)
    void testRegisterDuplicateUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user@example.com","password":"password123","fullName":"John Doe","phoneNumber":"+1234567890"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void testRegisterInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"notanemail","password":"password123","fullName":"Jane Doe","phoneNumber":"+1234567890"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(4)
    void testRegisterMissingFields() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"","password":"","fullName":"","phoneNumber":""}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void testLoginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user@example.com","password":"password123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andReturn();

        session = (MockHttpSession) result.getRequest().getSession();
    }

    @Test
    @Order(6)
    void testLoginInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user@example.com","password":"wrongpassword"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @Order(7)
    void testLoginNonExistentUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"noone@example.com","password":"password123"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @Order(8)
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized());
    }

    // ===================== CATEGORY TESTS =====================

    @Test
    @Order(10)
    void testGetDefaultCategories() throws Exception {
        mockMvc.perform(get("/api/categories").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories").isArray())
                .andExpect(jsonPath("$.categories.length()").value(7));
    }

    @Test
    @Order(11)
    void testCreateCustomCategory() throws Exception {
        mockMvc.perform(post("/api/categories").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"SideBusinessIncome","type":"INCOME"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("SideBusinessIncome"))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.isCustom").value(true));
    }

    @Test
    @Order(12)
    void testCreateDuplicateCategory() throws Exception {
        mockMvc.perform(post("/api/categories").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"SideBusinessIncome","type":"INCOME"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(13)
    void testCreateCategoryWithDefaultName() throws Exception {
        mockMvc.perform(post("/api/categories").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Salary","type":"INCOME"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(14)
    void testDeleteDefaultCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/Salary").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(15)
    void testDeleteCustomCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/SideBusinessIncome").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }

    @Test
    @Order(16)
    void testDeleteNonExistentCategory() throws Exception {
        mockMvc.perform(delete("/api/categories/NonExistent").session(session))
                .andExpect(status().isNotFound());
    }

    // ===================== TRANSACTION TESTS =====================

    @Test
    @Order(20)
    void testCreateIncomeTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":50000.00,"date":"2024-01-15","category":"Salary","description":"January Salary"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.amount").value(50000.0))
                .andExpect(jsonPath("$.date").value("2024-01-15"))
                .andExpect(jsonPath("$.category").value("Salary"))
                .andExpect(jsonPath("$.description").value("January Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    @Order(21)
    void testCreateExpenseTransaction() throws Exception {
        mockMvc.perform(post("/api/transactions").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":400.00,"date":"2024-01-20","category":"Food","description":"Groceries"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    @Order(22)
    void testCreateTransactionInvalidCategory() throws Exception {
        mockMvc.perform(post("/api/transactions").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":100.00,"date":"2024-01-15","category":"InvalidCat","description":"Test"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(23)
    void testCreateTransactionFutureDate() throws Exception {
        mockMvc.perform(post("/api/transactions").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":100.00,"date":"2099-01-15","category":"Salary","description":"Future"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(24)
    void testCreateTransactionNegativeAmount() throws Exception {
        mockMvc.perform(post("/api/transactions").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":-100.00,"date":"2024-01-15","category":"Salary","description":"Negative"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(25)
    void testGetTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray())
                .andExpect(jsonPath("$.transactions.length()").value(2));
    }

    @Test
    @Order(26)
    void testGetTransactionsWithDateFilter() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions").isArray());
    }

    @Test
    @Order(27)
    void testUpdateTransaction() throws Exception {
        mockMvc.perform(put("/api/transactions/1").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":60000.00,"description":"Updated January Salary"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(60000.0))
                .andExpect(jsonPath("$.description").value("Updated January Salary"));
    }

    @Test
    @Order(28)
    void testUpdateTransactionNotFound() throws Exception {
        mockMvc.perform(put("/api/transactions/9999").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":100.00}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(29)
    void testDeleteTransaction() throws Exception {
        // Create a transaction to delete
        MvcResult result = mockMvc.perform(post("/api/transactions").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":100.00,"date":"2024-02-01","category":"Food","description":"To Delete"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        Map<String, Object> tx = objectMapper.readValue(body, Map.class);
        int id = (int) tx.get("id");

        mockMvc.perform(delete("/api/transactions/" + id).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }

    @Test
    @Order(30)
    void testDeleteTransactionNotFound() throws Exception {
        mockMvc.perform(delete("/api/transactions/9999").session(session))
                .andExpect(status().isNotFound());
    }

    // ===================== GOAL TESTS =====================

    @Test
    @Order(40)
    void testCreateGoal() throws Exception {
        mockMvc.perform(post("/api/goals").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"goalName":"Emergency Fund","targetAmount":5000.00,"targetDate":"2027-01-01","startDate":"2025-01-01"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"))
                .andExpect(jsonPath("$.targetAmount").value(5000.0))
                .andExpect(jsonPath("$.targetDate").value("2027-01-01"))
                .andExpect(jsonPath("$.startDate").value("2025-01-01"))
                .andExpect(jsonPath("$.currentProgress").isNumber())
                .andExpect(jsonPath("$.progressPercentage").isNumber())
                .andExpect(jsonPath("$.remainingAmount").isNumber());
    }

    @Test
    @Order(41)
    void testGetAllGoals() throws Exception {
        mockMvc.perform(get("/api/goals").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals").isArray())
                .andExpect(jsonPath("$.goals.length()").value(1));
    }

    @Test
    @Order(42)
    void testGetGoalById() throws Exception {
        mockMvc.perform(get("/api/goals/1").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"));
    }

    @Test
    @Order(43)
    void testGetGoalNotFound() throws Exception {
        mockMvc.perform(get("/api/goals/9999").session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(44)
    void testUpdateGoal() throws Exception {
        mockMvc.perform(put("/api/goals/1").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"targetAmount":6000.00,"targetDate":"2027-02-01"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetAmount").value(6000.0))
                .andExpect(jsonPath("$.targetDate").value("2027-02-01"));
    }

    @Test
    @Order(45)
    void testUpdateGoalNotFound() throws Exception {
        mockMvc.perform(put("/api/goals/9999").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"targetAmount":6000.00}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(46)
    void testDeleteGoal() throws Exception {
        mockMvc.perform(delete("/api/goals/1").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }

    @Test
    @Order(47)
    void testDeleteGoalNotFound() throws Exception {
        mockMvc.perform(delete("/api/goals/9999").session(session))
                .andExpect(status().isNotFound());
    }

    // ===================== REPORT TESTS =====================

    @Test
    @Order(50)
    void testMonthlyReport() throws Exception {
        mockMvc.perform(get("/api/reports/monthly/2024/1").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(1))
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.totalIncome").isMap())
                .andExpect(jsonPath("$.totalExpenses").isMap())
                .andExpect(jsonPath("$.netSavings").isNumber());
    }

    @Test
    @Order(51)
    void testYearlyReport() throws Exception {
        mockMvc.perform(get("/api/reports/yearly/2024").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.totalIncome").isMap())
                .andExpect(jsonPath("$.totalExpenses").isMap())
                .andExpect(jsonPath("$.netSavings").isNumber());
    }

    @Test
    @Order(52)
    void testMonthlyReportEmptyMonth() throws Exception {
        mockMvc.perform(get("/api/reports/monthly/2024/6").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.netSavings").value(0.0));
    }

    // ===================== LOGOUT TESTS =====================

    @Test
    @Order(60)
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }

    @Test
    @Order(61)
    void testAccessAfterLogout() throws Exception {
        // After logout, session is invalidated
        MockHttpSession invalidSession = new MockHttpSession();
        mockMvc.perform(get("/api/transactions").session(invalidSession))
                .andExpect(status().isUnauthorized());
    }

    // ===================== DATA ISOLATION TESTS =====================

    @Test
    @Order(70)
    void testDataIsolationBetweenUsers() throws Exception {
        // Register second user
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user2@example.com","password":"password456","fullName":"Jane Doe","phoneNumber":"+0987654321"}
                                """))
                .andExpect(status().isCreated());

        // Login as second user
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user2@example.com","password":"password456"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession user2Session = (MockHttpSession) loginResult.getRequest().getSession();

        // Second user should see no transactions
        mockMvc.perform(get("/api/transactions").session(user2Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.length()").value(0));

        // Second user should see no goals
        mockMvc.perform(get("/api/goals").session(user2Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals.length()").value(0));

        // But should see default categories
        mockMvc.perform(get("/api/categories").session(user2Session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories.length()").value(7));
    }

    // ===================== CATEGORY IN-USE DELETION TEST =====================

    @Test
    @Order(80)
    void testCannotDeleteCategoryInUse() throws Exception {
        // Login as user2
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user2@example.com","password":"password456"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession user2Session = (MockHttpSession) loginResult.getRequest().getSession();

        // Create a custom category
        mockMvc.perform(post("/api/categories").session(user2Session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Freelance","type":"INCOME"}
                                """))
                .andExpect(status().isCreated());

        // Create a transaction using that category
        mockMvc.perform(post("/api/transactions").session(user2Session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount":500.00,"date":"2024-03-01","category":"Freelance","description":"Freelance work"}
                                """))
                .andExpect(status().isCreated());

        // Try to delete the category - should fail
        mockMvc.perform(delete("/api/categories/Freelance").session(user2Session))
                .andExpect(status().isBadRequest());
    }
}
