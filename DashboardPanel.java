import java.text.NumberFormat;
import java.util.*;

public class DashboardPanel {

    // =========================================================================
    // 1. Integration Interfaces & Models
    // =========================================================================
    public interface GroupServiceBridge {
        double getTotalGroupExpenditure(String groupId);
        Map<String, Double> getCategoryBreakdown(String groupId);
    }

    public interface BalanceServiceBridge {
        Map<String, Double> getMemberBalances(String groupId);
    }

    public interface ExpenseServiceBridge {
        List<ExpenseRecord> getGroupExpenses(String groupId);
    }

    public static class ExpenseRecord {
        private final String date;
        private final String description;
        private final String paidBy;
        private final String category;
        private final double amount;

        public ExpenseRecord(String date, String description, String paidBy, String category, double amount) {
            this.date = date;
            this.description = description;
            this.paidBy = paidBy;
            this.category = category;
            this.amount = amount;
        }

        public String getDate() { return date; }
        public String getDescription() { return description; }
        public String getPaidBy() { return paidBy; }
        public String getCategory() { return category; }
        public double getAmount() { return amount; }
    }

    // =========================================================================
    // 2. Dashboard Panel Core Implementation
    // =========================================================================
    private final GroupServiceBridge groupService;
    private final ExpenseServiceBridge expenseService;
    private final BalanceServiceBridge balanceService;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public DashboardPanel(GroupServiceBridge groupService, ExpenseServiceBridge expenseService, BalanceServiceBridge balanceService) {
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.balanceService = balanceService;
    }

    /**
     * Display the full console dashboard for a given group ID.
     */
    public void displayDashboard(String groupId) {
        if (groupId == null || groupId.trim().isEmpty()) {
            System.out.println("Error: Invalid Group ID provided.");
            return;
        }

        printHeader("SMART EXPENSE SPLITTER - GROUP DASHBOARD");

        // 1. Total Group Expenditure
        double total = groupService.getTotalGroupExpenditure(groupId);
        System.out.printf(" TOTAL GROUP EXPENDITURE : %s%n", currencyFormatter.format(total));
        printLine('-');

        // 2. Member Balances
        System.out.println("\n[ MEMBER BALANCES ]");
        System.out.printf("  %-20s | %-15s | %-10s%n", "Member", "Net Balance", "Status");
        printLine('-');
        Map<String, Double> balances = balanceService.getMemberBalances(groupId);
        if (balances != null && !balances.isEmpty()) {
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                double balance = entry.getValue();
                String status = (balance < 0) ? "Owes" : (balance > 0) ? "Is Owed" : "Settled";
                System.out.printf("  %-20s | %-15s | %-10s%n",
                        entry.getKey(),
                        currencyFormatter.format(balance),
                        status);
            }
        } else {
            System.out.println("  No balance records available.");
        }

        // 3. Category Summary
        System.out.println("\n[ CATEGORY SUMMARY ]");
        System.out.printf("  %-20s | %-15s%n", "Category", "Total Spent");
        printLine('-');
        Map<String, Double> categories = groupService.getCategoryBreakdown(groupId);
        if (categories != null && !categories.isEmpty()) {
            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                System.out.printf("  %-20s | %-15s%n",
                        entry.getKey(),
                        currencyFormatter.format(entry.getValue()));
            }
        } else {
            System.out.println("  No category data available.");
        }

        // 4. Transaction History
        System.out.println("\n[ TRANSACTION HISTORY ]");
        System.out.printf("  %-12s | %-22s | %-12s | %-16s | %-10s%n", "Date", "Description", "Paid By", "Category", "Amount");
        printLine('-');
        List<ExpenseRecord> expenses = expenseService.getGroupExpenses(groupId);
        if (expenses != null && !expenses.isEmpty()) {
            for (ExpenseRecord e : expenses) {
                System.out.printf("  %-12s | %-22s | %-12s | %-16s | %-10s%n",
                        e.getDate(),
                        truncate(e.getDescription(), 22),
                        truncate(e.getPaidBy(), 12),
                        truncate(e.getCategory(), 16),
                        currencyFormatter.format(e.getAmount()));
            }
        } else {
            System.out.println("  No transactions recorded.");
        }

        printLine('=');
        System.out.println();
    }

    // --- Utility Formatting Helpers ---
    private void printHeader(String title) {
        printLine('=');
        System.out.println(" " + title);
        printLine('=');
    }

    private void printLine(char ch) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 85; i++) {
            sb.append(ch);
        }
        System.out.println(sb.toString());
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return (text.length() > maxLen) ? text.substring(0, maxLen - 3) + "..." : text;
    }

    // =========================================================================
    // 3. Standalone Execution (Runs without teammates' modules)
    // =========================================================================
    public static void main(String[] args) {
        // Mock GroupService implementation
        GroupServiceBridge mockGroupService = new GroupServiceBridge() {
            @Override
            public double getTotalGroupExpenditure(String groupId) {
                return 450.00;
            }

            @Override
            public Map<String, Double> getCategoryBreakdown(String groupId) {
                Map<String, Double> map = new LinkedHashMap<>();
                map.put("Food & Dining", 200.00);
                map.put("Utilities", 150.00);
                map.put("Entertainment", 100.00);
                return map;
            }
        };

        // Mock BalanceService implementation
        BalanceServiceBridge mockBalanceService = new BalanceServiceBridge() {
            @Override
            public Map<String, Double> getMemberBalances(String groupId) {
                Map<String, Double> map = new LinkedHashMap<>();
                map.put("Alice", 62.50);
                map.put("Bob", -37.50);
                map.put("Charlie", -25.00);
                return map;
            }
        };

        // Mock ExpenseService implementation
        ExpenseServiceBridge mockExpenseService = new ExpenseServiceBridge() {
            @Override
            public List<ExpenseRecord> getGroupExpenses(String groupId) {
                List<ExpenseRecord> list = new ArrayList<>();
                list.add(new ExpenseRecord("2026-11-01", "Dinner at Bistro", "Alice", "Food & Dining", 200.00));
                list.add(new ExpenseRecord("2026-11-03", "Electricity Bill", "Bob", "Utilities", 150.00));
                list.add(new ExpenseRecord("2026-11-05", "Movie Tickets", "Alice", "Entertainment", 100.00));
                return list;
            }
        };

        // Run standalone test directly
        DashboardPanel dashboard = new DashboardPanel(mockGroupService, mockExpenseService, mockBalanceService);
        dashboard.displayDashboard("GROUP_TEST_01");
    }
}
