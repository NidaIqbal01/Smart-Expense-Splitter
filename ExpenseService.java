import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class ExpenseService {

    private static final Scanner scanner = new Scanner(System.in);

    // Expense categories
    public enum Category {
        FOOD,
        TRANSPORT,
        SHOPPING,
        RENT,
        UTILITIES,
        ENTERTAINMENT,
        OTHER
    }

    // Expense Model
    public static class Expense {

        private final String expenseId;
        private final String title;
        private final BigDecimal amount;
        private final String payer;
        private final Category category;
        private final List<String> participants;

        public Expense(String title, BigDecimal amount,
                       String payer, Category category,
                       List<String> participants) {

            this.expenseId = UUID.randomUUID().toString();
            this.title = title;
            this.amount = amount;
            this.payer = payer;
            this.category = category;

            this.participants = Collections.unmodifiableList(
                    new ArrayList<>(participants)
            );
        }

        public String getExpenseId() {
            return expenseId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public BigDecimal getSharePerPerson() {
            return amount.divide(
                    BigDecimal.valueOf(participants.size()),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        @Override
        public String toString() {
            return "\nExpense ID: " + expenseId +
                    "\nTitle: " + title +
                    "\nAmount: Rs. " + amount +
                    "\nPaid By: " + payer +
                    "\nCategory: " + category +
                    "\nParticipants: " + participants +
                    "\nShare Per Person: Rs. " + getSharePerPerson();
        }
    }

    // Group members
    private final Set<String> groupMembers = new HashSet<>();
    private final List<String> memberList = new ArrayList<>();

    // Saved expenses
    private final List<Expense> expenses = new ArrayList<>();

    // Add group member
    public void registerMember(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Member name cannot be empty."
            );
        }

        name = name.trim();

        if (!groupMembers.add(name.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Member already exists."
            );
        }

        memberList.add(name);
        System.out.println("Member added successfully!");
    }

    // Validate expense
    private void validateExpense(
            String title,
            BigDecimal amount,
            String payer,
            Category category,
            List<String> participants) {

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero."
            );
        }

        if (amount.scale() > 2) {
            throw new IllegalArgumentException(
                    "Maximum two decimal places allowed."
            );
        }

        if (payer == null ||
                !groupMembers.contains(payer.trim().toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid payer."
            );
        }

        if (category == null) {
            throw new IllegalArgumentException(
                    "Category is required."
            );
        }

        if (participants == null || participants.isEmpty()) {
            throw new IllegalArgumentException(
                    "Select at least one participant."
            );
        }

        Set<String> selected = new HashSet<>();

        for (String participant : participants) {

            if (participant == null ||
                    !groupMembers.contains(participant.trim().toLowerCase())) {
                throw new IllegalArgumentException(
                        "Invalid participant: " + participant
                );
            }

            if (!selected.add(participant.trim().toLowerCase())) {
                throw new IllegalArgumentException(
                        "Duplicate participant: " + participant
                );
            }
        }
    }

    // Add expense
    public Expense addExpense(
            String title,
            BigDecimal amount,
            String payer,
            Category category,
            List<String> participants) {

        validateExpense(title, amount, payer, category, participants);

        Expense expense = new Expense(
                title.trim(),
                amount.setScale(2, RoundingMode.UNNECESSARY),
                payer.trim(),
                category,
                participants
        );

        expenses.add(expense);

        System.out.println("\nExpense saved successfully!");

        return expense;
    }

    // Display members
    public void displayMembers() {

        if (memberList.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }

        System.out.println("\n===== GROUP MEMBERS =====");

        for (int i = 0; i < memberList.size(); i++) {
            System.out.println((i + 1) + ". " + memberList.get(i));
        }
    }

    // Get member through selection
    private String selectMember(String message) {

        displayMembers();

        int choice = readInt(message, 1, memberList.size());

        return memberList.get(choice - 1);
    }

    // Read integer safely
    private static int readInt(String message, int min, int max) {

        while (true) {
            System.out.print(message);

            try {
                int value = Integer.parseInt(scanner.nextLine().trim());

                if (value >= min && value <= max) {
                    return value;
                }

                System.out.println(
                        "Enter a number between " + min + " and " + max
                );

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // Add members through user input
    private void addMembers() {

        System.out.println("\n===== ADD GROUP MEMBERS =====");

        int count = readInt("How many members? ", 1, 100);

        for (int i = 0; i < count; i++) {

            while (true) {
                System.out.print("Enter member " + (i + 1) + " name: ");

                String name = scanner.nextLine();

                try {
                    registerMember(name);
                    break;

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    // Add expense through user input
    private void addExpenseFromUser() {

        if (memberList.isEmpty()) {
            System.out.println("Please add group members first.");
            return;
        }

        System.out.println("\n===== ADD NEW EXPENSE =====");

        System.out.print("Enter expense title: ");
        String title = scanner.nextLine();

        BigDecimal amount;

        while (true) {
            System.out.print("Enter amount: ");

            try {
                amount = new BigDecimal(scanner.nextLine().trim());

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Amount must be positive.");
                    continue;
                }

                if (amount.scale() > 2) {
                    System.out.println(
                            "Maximum two decimal places allowed."
                    );
                    continue;
                }

                break;

            } catch (NumberFormatException e) {
                System.out.println("Enter a valid amount.");
            }
        }

        System.out.println("\nSelect Payer:");

        String payer = selectMember("Choose payer number: ");

        // Category selection
        System.out.println("\n===== SELECT CATEGORY =====");

        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }

        int categoryChoice = readInt(
                "Choose category: ", 1, categories.length
        );

        Category category = categories[categoryChoice - 1];

        // Participant selection
        System.out.println("\n===== SELECT PARTICIPANTS =====");

        displayMembers();

        System.out.println(
                "Enter participant numbers separated by commas."
        );

        System.out.println("Example: 1,2,3");

        List<String> participants = new ArrayList<>();

        while (true) {
            System.out.print("Select participants: ");

            String input = scanner.nextLine();

            try {
                String[] choices = input.split(",");

                Set<Integer> selectedNumbers = new HashSet<>();

                for (String choice : choices) {

                    int number = Integer.parseInt(choice.trim());

                    if (number < 1 || number > memberList.size()) {
                        throw new IllegalArgumentException(
                                "Invalid member number: " + number
                        );
                    }

                    if (!selectedNumbers.add(number)) {
                        throw new IllegalArgumentException(
                                "Duplicate member selection."
                        );
                    }

                    participants.add(memberList.get(number - 1));
                }

                break;

            } catch (NumberFormatException e) {
                participants.clear();
                System.out.println("Enter valid comma-separated numbers.");

            } catch (IllegalArgumentException e) {
                participants.clear();
                System.out.println("Error: " + e.getMessage());
            }
        }

        try {
            addExpense(title, amount, payer, category, participants);

        } catch (IllegalArgumentException e) {
            System.out.println("Expense not saved: " + e.getMessage());
        }
    }

    // Display all expenses
    public void displayAllExpenses() {

        if (expenses.isEmpty()) {
            System.out.println("\nNo expenses found.");
            return;
        }

        System.out.println("\n===== ALL EXPENSES =====");

        for (Expense expense : expenses) {
            System.out.println(expense);
            System.out.println("-------------------------");
        }
    }

    // Total expenses
    public BigDecimal getTotalExpenses() {

        BigDecimal total = BigDecimal.ZERO;

        for (Expense expense : expenses) {
            total = total.add(expense.getAmount());
        }

        return total;
    }

    // Main menu
    public static void main(String[] args) {

        ExpenseService service = new ExpenseService();

        System.out.println("================================");
        System.out.println("     EXPENSE MANAGEMENT SYSTEM");
        System.out.println("================================");

        service.addMembers();

        while (true) {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Add New Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. View Group Members");
            System.out.println("4. View Total Expenses");
            System.out.println("5. Exit");

            int choice = readInt("Enter your choice: ", 1, 5);

            switch (choice) {

                case 1:
                    service.addExpenseFromUser();
                    break;

                case 2:
                    service.displayAllExpenses();
                    break;

                case 3:
                    service.displayMembers();
                    break;

                case 4:
                    System.out.println(
                            "\nTotal Expenses: Rs. " +
                            service.getTotalExpenses()
                    );
                    break;

                case 5:
                    System.out.println(
                            "\nThank you for using Expense Management System!"
                    );
                    scanner.close();
                    return;
            }
        }
    }
}
