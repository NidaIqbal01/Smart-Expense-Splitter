import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Master Integration Hub - Smart Expense Splitter
 * Connects Modules 1-4 into a single live interactive workflow.
 */
public class DashboardPanel {

    private GroupService groupService;
    private final List<ExpenseService.Expense> expenseList;
    private final AIAdvisorService aiAdvisorService;
    private final Scanner scanner;
    private int nextExpenseId = 1;

    public DashboardPanel() {
        this.expenseList = new ArrayList<>();
        this.aiAdvisorService = new AIAdvisorService(getApiKeyFromEnv());
        this.scanner = new Scanner(System.in);
    }

    private static String getApiKeyFromEnv() {
        String key = System.getProperty("GEMINI_API_KEY");
        if (key == null || key.trim().isEmpty()) {
            key = System.getenv("GEMINI_API_KEY");
        }
        return key != null ? key : "";
    }

    // =========================================================================
    // INITIAL SETUP: Create Group & Register Members (Module 1 Integration)
    // =========================================================================
    
    public void setupGroupInteractive() {
        System.out.println("==================================================================");
        System.out.println("        WELCOME TO SMART EXPENSE SPLITTER SYSTEM SETUP            ");
        System.out.println("==================================================================");
        
        System.out.print("Enter Group Name (e.g. Apartment 4B, Hostel Room 12): ");
        String groupName = scanner.nextLine().trim();
        while (groupName.isEmpty()) {
            System.out.print("Group name cannot be empty. Enter Group Name: ");
            groupName = scanner.nextLine().trim();
        }

        this.groupService = new GroupService(groupName);

        System.out.print("Enter initial number of group members: ");
        int memberCount = readInt("");
        while (memberCount <= 0) {
            System.out.println("Must have at least 1 member.");
            memberCount = readInt("Enter number of group members: ");
        }

        for (int i = 1; i <= memberCount; i++) {
            while (true) {
                System.out.print("Enter member " + i + " name: ");
                String name = scanner.nextLine().trim();
                if (groupService.addMember(name)) {
                    break;
                }
            }
        }

        System.out.println("\nGroup Setup Complete! Launching Master Dashboard...\n");
    }

    // =========================================================================
    // MASTER MENU LOOP
    // =========================================================================

    public void start() {
        if (groupService == null) {
            setupGroupInteractive();
        }

        int choice = 0;
        do {
            printMainMenu();
            choice = readInt("Enter your choice (1-8): ");

            switch (choice) {
                case 1:
                    displayOverview();
                    break;
                case 2:
                    displayBalancesAndSummaries();
                    break;
                case 3:
                    displayTransactionHistory();
                    break;
                case 4:
                    triggerAIAdvisor();
                    break;
                case 5:
                    BalanceService.displaySettlements(groupService.getMemberList(), expenseList);
                    break;
                case 6:
                    groupService.displayMembers();
                    break;
                case 7:
                    addNewExpenseInteractive();
                    break;
                case 8:
                    System.out.println("\nThank you for using Smart Expense Splitter Dashboard!");
                    break;
                default:
                    System.out.println("\nInvalid choice! Please select a number between 1 and 8.");
            }

            if (choice != 8) {
                System.out.print("\nPress Enter to return to the master menu...");
                scanner.nextLine();
            }

        } while (choice != 8);
    }

    private void printMainMenu() {
        System.out.println("\n\n==================================================================");
        System.out.println("               SMART EXPENSE SPLITTER - MASTER MENU               ");
        System.out.println("==================================================================");
        System.out.printf(" Active Group: %-20s | Total Members: %d%n",
                groupService.getGroupName(), groupService.getMemberList().size());
        System.out.printf(" Total Spend : Rs. %-17.2f | Total Expenses Logs: %d%n",
                calculateTotalExpenditure(), expenseList.size());
        System.out.println("==================================================================");
        System.out.println(" 1. View Dashboard Overview & Key Metrics");
        System.out.println(" 2. View Member Balances & Category Summaries (Module 3)");
        System.out.println(" 3. View Full Transaction Logs");
        System.out.println(" 4. Run AI Budget Advisor Analysis (Module 4)");
        System.out.println(" 5. View Debt Settlement Plan (Module 3)");
        System.out.println(" 6. Manage Group Members (Module 1)");
        System.out.println(" 7. Add New Shared Expense (Module 2)");
        System.out.println(" 8. Exit System");
        System.out.println("==================================================================");
    }

    // =========================================================================
    // FEATURE IMPLEMENTATIONS
    // =========================================================================

    public void displayOverview() {
        BigDecimal totalExpenditure = calculateTotalExpenditure();
        List<String> members = groupService.getMemberList();
        Map<ExpenseService.Category, BigDecimal> categoryTotals = calculateCategoryTotals();

        String highestCategory = "None";
        BigDecimal maxSpending = BigDecimal.ZERO;

        for (Map.Entry<ExpenseService.Category, BigDecimal> entry : categoryTotals.entrySet()) {
            if (entry.getValue().compareTo(maxSpending) > 0) {
                maxSpending = entry.getValue();
                highestCategory = entry.getKey().name();
            }
        }

        System.out.println("\n==================================================================");
        System.out.println("                   EXECUTIVE DASHBOARD OVERVIEW                   ");
        System.out.println("==================================================================");
        System.out.printf(" Group Name               : %s%n", groupService.getGroupName());
        System.out.printf(" Total Members Registered : %d%n", members.size());
        System.out.printf(" Total Expense Records    : %d%n", expenseList.size());
        System.out.printf(" Total Group Expenditure  : Rs. %.2f%n", totalExpenditure);
        System.out.printf(" Top Spending Category    : %s (Rs. %.2f)%n", highestCategory, maxSpending);
        System.out.println("==================================================================");
    }

    public void displayBalancesAndSummaries() {
        List<String> members = groupService.getMemberList();

        System.out.println("\n==================================================================");
        System.out.println("                    BALANCES & CATEGORY SUMMARIES                 ");
        System.out.println("==================================================================");

        if (members.isEmpty()) {
            System.out.println("No members currently registered in the group.");
            return;
        }

        // Live calculation via Wajiya's BalanceService
        BalanceService.displayBalances(members, expenseList);

        System.out.println("\n----------------- CATEGORY-WISE BREAKDOWN -----------------");
        Map<ExpenseService.Category, BigDecimal> categoryTotals = calculateCategoryTotals();
        BigDecimal totalSpent = calculateTotalExpenditure();

        if (totalSpent.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("No expenses logged yet.");
        } else {
            for (Map.Entry<ExpenseService.Category, BigDecimal> entry : categoryTotals.entrySet()) {
                double percentage = entry.getValue()
                        .divide(totalSpent, 4, RoundingMode.HALF_UP)
                        .doubleValue() * 100.0;

                System.out.printf(" - %-15s : Rs. %-10.2f (%.1f%%)%n",
                        entry.getKey(), entry.getValue(), percentage);
            }
        }
        System.out.println("-----------------------------------------------------------");
    }

    public void displayTransactionHistory() {
        System.out.println("\n==================================================================");
        System.out.println("                      TRANSACTION HISTORY LOGS                    ");
        System.out.println("==================================================================");

        if (expenseList.isEmpty()) {
            System.out.println("No transaction logs found.");
            System.out.println("==================================================================");
            return;
        }

        System.out.printf("%-5s %-20s %-12s %-12s %-15s %-20s%n",
                "ID", "TITLE", "AMOUNT", "PAID BY", "CATEGORY", "PARTICIPANTS");
        System.out.println("------------------------------------------------------------------");

        for (ExpenseService.Expense expense : expenseList) {
            String participantStr = String.join(", ", expense.participants);
            System.out.printf("%-5d %-20s Rs. %-8.2f %-12s %-15s %-20s%n",
                    expense.id,
                    truncate(expense.title, 18),
                    expense.amount,
                    expense.payer,
                    expense.category,
                    truncate(participantStr, 18));
        }
        System.out.println("==================================================================");
    }

    public void triggerAIAdvisor() {
        System.out.println("\n==================================================================");
        System.out.println("                     AI BUDGET ADVISOR (MODULE 4)                  ");
        System.out.println("==================================================================");

        if (expenseList.isEmpty()) {
            System.out.println("Please add at least one expense before running the AI Advisor.");
            return;
        }

        Map<String, Double> categoryTotalsDouble = new HashMap<>();
        Map<ExpenseService.Category, BigDecimal> categoryTotals = calculateCategoryTotals();

        for (Map.Entry<ExpenseService.Category, BigDecimal> entry : categoryTotals.entrySet()) {
            categoryTotalsDouble.put(entry.getKey().name(), entry.getValue().doubleValue());
        }

        SpendingSummary summary = new SpendingSummary(
                groupService.getGroupName(),
                calculateTotalExpenditure().doubleValue(),
                categoryTotalsDouble
        );

        System.out.println("Analyzing live group expenditure patterns...");
        String advice = aiAdvisorService.getBudgetAdvice(summary);
        System.out.println("\n" + advice);
    }

    private void addNewExpenseInteractive() {
        List<String> members = groupService.getMemberList();

        if (members.isEmpty()) {
            System.out.println("\nError: Add group members first before creating an expense.");
            return;
        }

        System.out.println("\n--- ADD NEW SHARED EXPENSE ---");
        System.out.print("Enter expense title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        System.out.print("Enter amount: Rs. ");
        BigDecimal amount;
        try {
            amount = new BigDecimal(scanner.nextLine().trim()).setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric amount.");
            return;
        }

        System.out.println("\nSelect Payer:");
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i));
        }
        int payerChoice = readInt("Select member number: ");
        if (payerChoice < 1 || payerChoice > members.size()) {
            System.out.println("Invalid payer selection.");
            return;
        }
        String payer = members.get(payerChoice - 1);

        System.out.println("\nSelect Category:");
        ExpenseService.Category[] categories = ExpenseService.Category.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int catChoice = readInt("Select category number: ");
        if (catChoice < 1 || catChoice > categories.length) {
            System.out.println("Invalid category selection.");
            return;
        }
        ExpenseService.Category category = categories[catChoice - 1];

        // Select Participants
        System.out.println("\nSelect Participants (Enter member numbers separated by commas, e.g. 1,2,3):");
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i));
        }
        System.out.print("Enter participants: ");
        String partInput = scanner.nextLine().trim();
        ArrayList<String> participants = new ArrayList<>();

        if (!partInput.isEmpty()) {
            String[] tokens = partInput.split(",");
            for (String token : tokens) {
                try {
                    int idx = Integer.parseInt(token.trim());
                    if (idx >= 1 && idx <= members.size()) {
                        String m = members.get(idx - 1);
                        if (!participants.contains(m)) {
                            participants.add(m);
                        }
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        if (participants.isEmpty()) {
            System.out.println("No valid participants chosen. Adding all group members by default.");
            participants.addAll(members);
        }

        ExpenseService.Expense newExpense = new ExpenseService.Expense(
                nextExpenseId++,
                title,
                amount,
                payer,
                category,
                participants
        );

        expenseList.add(newExpense);
        System.out.println("\nSuccess: Expense logged with ID #" + newExpense.id);
    }

    // =========================================================================
    // UTILITY METHODS
    // =========================================================================

    private BigDecimal calculateTotalExpenditure() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExpenseService.Expense expense : expenseList) {
            total = total.add(expense.amount);
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private Map<ExpenseService.Category, BigDecimal> calculateCategoryTotals() {
        Map<ExpenseService.Category, BigDecimal> categoryMap = new HashMap<>();
        for (ExpenseService.Category cat : ExpenseService.Category.values()) {
            categoryMap.put(cat, BigDecimal.ZERO);
        }
        for (ExpenseService.Expense expense : expenseList) {
            BigDecimal current = categoryMap.getOrDefault(expense.category, BigDecimal.ZERO);
            categoryMap.put(expense.category, current.add(expense.amount));
        }
        categoryMap.entrySet().removeIf(entry -> entry.getValue().compareTo(BigDecimal.ZERO) == 0);
        return categoryMap;
    }

    private int readInt(String prompt) {
        while (true) {
            if (!prompt.isEmpty()) System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }

    public static void main(String[] args) {
        DashboardPanel dashboard = new DashboardPanel();
        dashboard.start();
    }
}