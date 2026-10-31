import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExpenseService {

    private static final Scanner sc = new Scanner(System.in);

    // Group members
    private static final ArrayList<String> members = new ArrayList<>();

    // All expense records
    private static final ArrayList<Expense> expenses = new ArrayList<>();

    private static int nextExpenseId = 1;

    // Expense categories
    enum Category {
        FOOD, TRANSPORT, SHOPPING, RENT,
        UTILITIES, ENTERTAINMENT, OTHER
    }

    // Expense model
    static class Expense {
        int id;
        String title;
        BigDecimal amount;
        String payer;
        Category category;
        ArrayList<String> participants;

        Expense(int id, String title, BigDecimal amount,
                String payer, Category category,
                ArrayList<String> participants) {

            this.id = id;
            this.title = title;
            this.amount = amount;
            this.payer = payer;
            this.category = category;
            this.participants = new ArrayList<>(participants);
        }
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        System.out.println("==================================");
        System.out.println("     EXPENSE MANAGEMENT SYSTEM");
        System.out.println("==================================");

        registerMembers();

        int choice;

        do {
            showMenu();

            choice = readInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    addExpense();
                    break;

                case 2:
                    viewAllExpenses();
                    break;

                case 3:
                    searchExpense();
                    break;

                case 4:
                    updateExpense();
                    break;

                case 5:
                    deleteExpense();
                    break;

                case 6:
                    showExpenseSummary();
                    break;

                case 7:
                    viewGroupMembers();
                    break;

                case 8:
                    System.out.println("Thank you for using Expense Management System!");
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1 to 8.");
            }

        } while (choice != 8);

        sc.close();
    }

    // ================= MEMBER REGISTRATION =================

    private static void registerMembers() {

        System.out.println("\n--- GROUP MEMBER REGISTRATION ---");

        int count;

        do {
            count = readInt("Enter number of group members: ");

            if (count <= 0) {
                System.out.println("Please enter at least one member.");
            }

        } while (count <= 0);

        for (int i = 1; i <= count; i++) {

            while (true) {
                System.out.print("Enter member " + i + " name: ");
                String name = sc.nextLine().trim();

                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty.");
                    continue;
                }

                if (findMember(name)) {
                    System.out.println("This member already exists.");
                    continue;
                }

                members.add(name);
                break;
            }
        }

        System.out.println("\nMembers registered successfully!");
        viewGroupMembers();
    }

    private static boolean findMember(String name) {

        for (String member : members) {
            if (member.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    // ================= MENU =================

    private static void showMenu() {

        System.out.println("\n==================================");
        System.out.println("        EXPENSE MAIN MENU");
        System.out.println("==================================");
        System.out.println("1. Add New Expense");
        System.out.println("2. View All Expenses");
        System.out.println("3. Search Expense");
        System.out.println("4. Update Expense");
        System.out.println("5. Delete Expense");
        System.out.println("6. Expense Summary");
        System.out.println("7. View Group Members");
        System.out.println("8. Exit");
        System.out.println("==================================");
    }

    // ================= CREATE =================

    private static void addExpense() {

        System.out.println("\n--- ADD NEW EXPENSE ---");

        String title = readTitle();

        BigDecimal amount = readAmount();

        String payer = selectPayer();

        Category category = selectCategory();

        ArrayList<String> participants = selectParticipants();

        Expense expense = new Expense(
                nextExpenseId,
                title,
                amount,
                payer,
                category,
                participants
        );

        expenses.add(expense);

        System.out.println("\nExpense added successfully!");
        System.out.println("Generated Expense ID: " + nextExpenseId);

        nextExpenseId++;

        // Display the newly added expense
        displayExpense(expense);
    }

    // ================= READ: VIEW ALL =================

    private static void viewAllExpenses() {

        System.out.println("\n--- ALL EXPENSE RECORDS ---");

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
            return;
        }

        for (Expense expense : expenses) {
            displayExpense(expense);
        }

        System.out.println("\nTotal expense records: " + expenses.size());
    }

    // ================= READ: SEARCH =================

    private static void searchExpense() {

        System.out.println("\n--- SEARCH EXPENSE ---");

        if (expenses.isEmpty()) {
            System.out.println("No expenses available to search.");
            return;
        }

        System.out.println("1. Search by Expense ID");
        System.out.println("2. Search by Title");

        int choice = readInt("Select search option: ");

        boolean found = false;

        if (choice == 1) {

            int id = readInt("Enter Expense ID: ");

            Expense expense = findExpenseById(id);

            if (expense != null) {
                displayExpense(expense);
                found = true;
            }

        } else if (choice == 2) {

            System.out.print("Enter expense title: ");
            String title = sc.nextLine().trim();

            for (Expense expense : expenses) {

                if (expense.title.toLowerCase()
                        .contains(title.toLowerCase())) {

                    displayExpense(expense);
                    found = true;
                }
            }

        } else {
            System.out.println("Invalid search option.");
            return;
        }

        if (!found) {
            System.out.println("No matching expense found.");
        }
    }

    // ================= UPDATE =================

    private static void updateExpense() {

        System.out.println("\n--- UPDATE EXPENSE ---");

        if (expenses.isEmpty()) {
            System.out.println("No expenses available to update.");
            return;
        }

        int id = readInt("Enter Expense ID to update: ");

        Expense expense = findExpenseById(id);

        if (expense == null) {
            System.out.println("Expense not found.");
            return;
        }

        System.out.println("\nCurrent Expense Details:");
        displayExpense(expense);

        System.out.println("\nPress Enter to keep the existing value.");

        // Update title
        System.out.print("\nNew title [" + expense.title + "]: ");
        String newTitle = sc.nextLine().trim();

        if (!newTitle.isEmpty()) {
            expense.title = newTitle;
        }

        // Update amount
        System.out.print("New amount [" + expense.amount + "]: ");
        String amountInput = sc.nextLine().trim();

        if (!amountInput.isEmpty()) {

            try {
                BigDecimal newAmount = new BigDecimal(amountInput);

                if (newAmount.compareTo(BigDecimal.ZERO) <= 0
                        || newAmount.scale() > 2) {

                    System.out.println(
                            "Invalid amount. Keeping previous amount."
                    );

                } else {
                    expense.amount = newAmount.setScale(2);
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid amount. Keeping previous amount."
                );
            }
        }

        // Update payer
        System.out.println("\nCurrent payer: " + expense.payer);
        System.out.println("Do you want to change payer? (y/n)");

        if (readYesNo()) {
            expense.payer = selectPayer();
        }

        // Update category
        System.out.println("\nCurrent category: " + expense.category);
        System.out.println("Do you want to change category? (y/n)");

        if (readYesNo()) {
            expense.category = selectCategory();
        }

        // Update participants
        System.out.println("\nCurrent participants: "
                + expense.participants);

        System.out.println("Do you want to change participants? (y/n)");

        if (readYesNo()) {
            expense.participants = selectParticipants();
        }

        System.out.println("\nExpense updated successfully!");

        // Display updated record
        displayExpense(expense);
    }

    // ================= DELETE =================

    private static void deleteExpense() {

        System.out.println("\n--- DELETE EXPENSE ---");

        if (expenses.isEmpty()) {
            System.out.println("No expenses available to delete.");
            return;
        }

        int id = readInt("Enter Expense ID to delete: ");

        Expense expense = findExpenseById(id);

        if (expense == null) {
            System.out.println("Expense not found.");
            return;
        }

        System.out.println("\nExpense selected for deletion:");
        displayExpense(expense);

        System.out.print("\nAre you sure you want to delete it? (y/n): ");

        if (readYesNo()) {

            expenses.remove(expense);

            System.out.println("Expense deleted successfully!");

            System.out.println("\nRemaining Expense Records:");
            viewAllExpenses();

        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    // ================= SUMMARY =================

    private static void showExpenseSummary() {

        System.out.println("\n--- EXPENSE SUMMARY ---");

        if (expenses.isEmpty()) {
            System.out.println("No expenses available.");
            return;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Expense expense : expenses) {
            total = total.add(expense.amount);
        }

        System.out.println("Total Expense Records: " + expenses.size());
        System.out.println("Total Group Expenses: Rs. " + total);

        System.out.println("\n--- CATEGORY-WISE SUMMARY ---");

        for (Category category : Category.values()) {

            BigDecimal categoryTotal = BigDecimal.ZERO;

            for (Expense expense : expenses) {

                if (expense.category == category) {
                    categoryTotal = categoryTotal.add(expense.amount);
                }
            }

            if (categoryTotal.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(category + ": Rs. " + categoryTotal);
            }
        }

        System.out.println("\n--- PARTICIPANT SHARES ---");

        for (Expense expense : expenses) {

            List<BigDecimal> shares = calculateShares(expense);

            System.out.println("\nExpense: " + expense.title);

            for (int i = 0; i < expense.participants.size(); i++) {

                System.out.println(
                        expense.participants.get(i)
                                + " share: Rs. " + shares.get(i)
                );
            }
        }
    }

    // ================= EXPENSE DISPLAY =================

    private static void displayExpense(Expense expense) {

        System.out.println("\n----------------------------------");
        System.out.println("          EXPENSE DETAILS");
        System.out.println("----------------------------------");

        System.out.println("Expense ID: " + expense.id);
        System.out.println("Title: " + expense.title);
        System.out.println("Amount: Rs. " + expense.amount);
        System.out.println("Paid By: " + expense.payer);
        System.out.println("Category: " + expense.category);

        System.out.println("Participants:");

        List<BigDecimal> shares = calculateShares(expense);

        for (int i = 0; i < expense.participants.size(); i++) {

            System.out.println(
                    "  " + expense.participants.get(i)
                            + " - Rs. " + shares.get(i)
            );
        }

        System.out.println("----------------------------------");
    }

    // ================= FIND EXPENSE =================

    private static Expense findExpenseById(int id) {

        for (Expense expense : expenses) {

            if (expense.id == id) {
                return expense;
            }
        }

        return null;
    }

    // ================= INPUT HELPERS =================

    private static String readTitle() {

        while (true) {

            System.out.print("Enter expense title: ");
            String title = sc.nextLine().trim();

            if (!title.isEmpty()) {
                return title;
            }

            System.out.println("Title cannot be empty.");
        }
    }

    private static BigDecimal readAmount() {

        while (true) {

            System.out.print("Enter expense amount: Rs. ");
            String input = sc.nextLine().trim();

            try {

                BigDecimal amount = new BigDecimal(input);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be greater than zero.");
                    continue;
                }

                if (amount.scale() > 2) {
                    System.out.println(
                            "Amount cannot have more than 2 decimal places."
                    );
                    continue;
                }

                return amount.setScale(2);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid numeric amount.");
            }
        }
    }

    private static int readInt(String message) {

        while (true) {

            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static boolean readYesNo() {

        while (true) {

            System.out.print("Enter y or n: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("y")) {
                return true;
            }

            if (input.equalsIgnoreCase("n")) {
                return false;
            }

            System.out.println("Please enter y or n.");
        }
    }

    // ================= PAYER SELECTION =================

    private static String selectPayer() {

        System.out.println("\nSelect the member who paid:");

        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i));
        }

        while (true) {

            int choice = readInt("Select member number: ");

            if (choice >= 1 && choice <= members.size()) {
                return members.get(choice - 1);
            }

            System.out.println("Invalid member number.");
        }
    }

    // ================= CATEGORY SELECTION =================

    private static Category selectCategory() {

        System.out.println("\nSelect Expense Category:");

        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println(
                    (i + 1) + ". " + categories[i]
            );
        }

        while (true) {

            int choice = readInt("Select category number: ");

            if (choice >= 1 && choice <= categories.length) {
                return categories[choice - 1];
            }

            System.out.println("Invalid category number.");
        }
    }

    // ================= PARTICIPANT SELECTION =================

    private static ArrayList<String> selectParticipants() {

        ArrayList<String> selectedParticipants = new ArrayList<>();

        System.out.println("\nSelect participants for splitting:");

        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i));
        }

        System.out.println(
                "Enter member numbers separated by commas."
        );

        System.out.println("Example: 1,2,3");

        while (true) {

            System.out.print("Enter participants: ");
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Select at least one participant.");
                continue;
            }

            String[] choices = input.split(",");
            ArrayList<String> temporaryList = new ArrayList<>();

            boolean valid = true;

            for (String choice : choices) {

                try {

                    int number = Integer.parseInt(choice.trim());

                    if (number < 1 || number > members.size()) {
                        System.out.println("Invalid member number: " + number);
                        valid = false;
                        break;
                    }

                    String member = members.get(number - 1);

                    if (temporaryList.contains(member)) {
                        System.out.println(
                                "Duplicate participant: " + member
                        );
                        valid = false;
                        break;
                    }

                    temporaryList.add(member);

                } catch (NumberFormatException e) {

                    System.out.println("Invalid input. Enter numbers only.");
                    valid = false;
                    break;
                }
            }

            if (valid && !temporaryList.isEmpty()) {
                selectedParticipants.addAll(temporaryList);
                return selectedParticipants;
            }

            System.out.println("Please select participants again.");
        }
    }

    // ================= SHARE CALCULATION =================

    private static List<BigDecimal> calculateShares(Expense expense) {

        ArrayList<BigDecimal> shares = new ArrayList<>();

        int count = expense.participants.size();

        if (count == 0) {
            return shares;
        }

        // Convert amount into paisa to avoid rounding errors
        long totalPaisa = expense.amount
                .movePointRight(2)
                .longValueExact();

        long baseShare = totalPaisa / count;
        long remainder = totalPaisa % count;

        for (int i = 0; i < count; i++) {

            long memberShare = baseShare;

            // Distribute remaining paisa one by one
            if (i < remainder) {
                memberShare++;
            }

            shares.add(
                    BigDecimal.valueOf(memberShare)
                            .movePointLeft(2)
                            .setScale(2)
            );
        }

        return shares;
    }

    // ================= VIEW MEMBERS =================

    private static void viewGroupMembers() {

        System.out.println("\n--- GROUP MEMBERS ---");

        if (members.isEmpty()) {
            System.out.println("No group members found.");
            return;
        }

        for (int i = 0; i < members.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + members.get(i)
            );
        }

        System.out.println("Total Members: " + members.size());
    }
}
