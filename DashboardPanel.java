import java.text.NumberFormat;
import java.util.*;

public class DashboardPanel {

    // =========================================================================
    // 1. Data Models & Bridge Interfaces (Decouples your code from teammates')
    // =========================================================================
    public interface GroupServiceBridge {
        double getTotalGroupExpenditure(String groupId);
        Map<String, Double> getCategoryBreakdown(String groupId);
    }

    public interface BalanceServiceBridge {
        Map<String, Double> getMemberBalances(String groupId);
    }

    public interface ExpenseServiceBridge {
        List<ExpenseItem> getGroupExpenses(String groupId);
    }

    public static class ExpenseItem {
        private final String date;
        private final String description;
        private final String paidBy;
        private final String category;
        private final double amount;

        public ExpenseItem(String date, String description, String paidBy, String category, double amount) {
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
    // 2. Console Dashboard Logic
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
     * Primary entry point to print the entire dashboard to the terminal.
     * Call this from your main menu loop.
     */
    public void displayDashboard(String groupId) {
        if (groupId == null || groupId.trim().isEmpty()) {
            System.out.println("Error: Invalid Group ID provided.");
            return;
        }

        printHeader("GROUP DASHBOARD");

        // 1. Total Expenditure
        double total = groupService.getTotalGroupExpenditure(groupId);
        System.out.printf(" TOTAL GROUP EXPENDITURE: %s%n", currencyFormatter.format(total));
        printDivider('-');

        // 2. Member Balances
        System.out.println("\n--- MEMBER BALANCES ---");
        System.out.printf("%-20s | %-15s%n", "Member", "Net Balance");
        printDivider('-');
        Map<String, Double> balances = balanceService.getMemberBalances(groupId);
        if (balances != null && !balances.isEmpty()) {
            for (Map.Entry<String, Double> entry : balances.entrySet()) {
                double val = entry.getValue();
                String status = (val < 0) ? " (Owes)" : (val > 0) ? " (Is Owed)" : " (Settled)";
                System.out.printf("%-20s | %-15s%s%n", entry.getKey(), currencyFormatter.format(val), status);
            }
        } else {
            System.out.println("No balance records found.");
        }

        // 3. Category Summary
        System.out.println("\n--- CATEGORY BREAKDOWN ---");
        System.out.printf("%-20s | %-15s%n", "Category", "Total Spent");
        printDivider('-');
        Map<String, Double> categories = groupService.getCategoryBreakdown(groupId);
        if (categories != null && !categories.isEmpty()) {
            for (Map.Entry<String, Double> entry : categories.entrySet()) {
                System.out.printf("%-20s | %-15s%n", entry.getKey(), currencyFormatter.format(entry.getValue()));
            }
        } else {
            System.out.println("No categorical expenditure data.");
        }

        // 4. Transaction History
        System.out.println("\n--- TRANSACTION HISTORY ---");
        System.out.printf("%-12s | %-22s | %-12s | %-16s | %-10s%n", "Date", "Description", "Paid By", "Category", "Amount");
        printDivider('-');
        List<ExpenseItem> expenses = expenseService.getGroupExpenses(groupId);
        if (expenses != null && !expenses.isEmpty()) {
            for (ExpenseItem e : expenses) {
                System.out.printf("%-12s | %-22s | %-12s | %-16s | %-10s%n",
                        e.getDate(),
                        truncate(e.getDescription(), 22),
                        truncate(e.getPaidBy(), 12),
                        truncate(e.getCategory(), 16),
                        currencyFormatter.format(e.getAmount()));
            }
        } else {
            System.out.println("No transactions recorded yet.");
        }

        printDivider('=');
        System.out.println();
    }

    // Utility formatting methods
    private void printHeader(String title) {
        printDivider('=');
        System.out.println(" " + title);
        printDivider('=');
    }

    private void printDivider(char ch) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 85; i++) {
            sb.append(ch);
        }
        System.out.println(sb.toString());
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return (text.length() > maxLength) ? text.substring(0, maxLength - 3) + "..." : text;
    }

    // =========================================================================
    // 3. Independent Main Runner with Mock Data (Runs Standalone)
    // =========================================================================
    public static void main(String[] args) {
        // Mock implementations so you can run and test without teammates' classes
        GroupServiceBridge mockGroupService = new GroupServiceBridge() {
            @Override
            public double getTotalGroupExpenditure(String groupId) { return 450.00; }

            @Override
            public Map<String, Double> getCategoryBreakdown(String groupId) {
                Map<String, Double> map = new LinkedHashMap<>();
                map.put("Food & Dining", 200.00);
                map.put("Utilities", 150.00);
                map.put("Entertainment", 100.00);
                return map;
            }
        };

        BalanceServiceBridge mockBalanceService = new BalanceServiceBridge() {
            @Override
            public Map<String, Double> getMemberBalances(String groupId) {
                Map<String, Double> map = new LinkedHashMap<>();
                map.put("Alice", 62.50);   // Is owed $62.50
                map.put("Bob", -37.50);   // Owes $37.50
                map.put("Charlie", -25.00); // Owes $25.00
                return map;
            }
        };

        ExpenseServiceBridge mockExpenseService = new ExpenseServiceBridge() {
            @Override
            public List<ExpenseItem> getGroupExpenses(String groupId) {
                List<ExpenseItem> list = new ArrayList<>();
                list.add(new ExpenseItem("2026-03-01", "Dinner at Bistro", "Alice", "Food & Dining", 200.00));
                list.add(new ExpenseItem("2026-03-03", "Electricity Bill", "Bob", "Utilities", 150.00));
                list.add(new ExpenseItem("2026-03-04", "Movie Tickets", "Alice", "Entertainment", 100.00));
                return list;
            }
        };

        // Instantiate and test your console module standalone
        DashboardPanel dashboard = new DashboardPanel(mockGroupService, mockExpenseService, mockBalanceService);
        dashboard.displayDashboard("demo_group_101");
    }
}