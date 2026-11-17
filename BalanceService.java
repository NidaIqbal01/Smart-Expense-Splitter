import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BalanceService {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        List<String> members = new ArrayList<>();
        List<ExpenseService.Expense> expenses = new ArrayList<>();

        System.out.println("=================================");
        System.out.println("     BALANCE & DEBT SETTLEMENT");
        System.out.println("=================================");

        int memberCount = readInt("Enter number of members: ");

        while (memberCount <= 0) {
            System.out.println("Enter at least one member.");
            memberCount = readInt("Enter number of members: ");
        }

        for (int i = 0; i < memberCount; i++) {
            System.out.print("Enter member " + (i + 1) + " name: ");
            String name = sc.nextLine().trim();

            while (name.isEmpty() || findMember(members, name) != null) {
                System.out.println("Name cannot be empty or duplicated.");
                System.out.print("Enter member name again: ");
                name = sc.nextLine().trim();
            }

            members.add(name);
        }

        int expenseCount = readInt("Enter number of expenses to test: ");

        while (expenseCount < 0) {
            System.out.println("Expense count cannot be negative.");
            expenseCount = readInt("Enter number of expenses to test: ");
        }

        for (int i = 0; i < expenseCount; i++) {

            System.out.println("\n--- Expense " + (i + 1) + " ---");

            System.out.print("Enter expense title: ");
            String title = sc.nextLine().trim();

            while (title.isEmpty()) {
                System.out.print("Title cannot be empty. Enter title: ");
                title = sc.nextLine().trim();
            }

            BigDecimal amount;

            while (true) {
                System.out.print("Enter amount: Rs. ");
                String input = sc.nextLine().trim();

                try {
                    amount = new BigDecimal(input);

                    if (amount.compareTo(BigDecimal.ZERO) <= 0
                            || amount.scale() > 2) {
                        System.out.println(
                                "Enter a positive amount with at most 2 decimals.");
                        continue;
                    }

                    amount = amount.setScale(2);
                    break;

                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid amount.");
                }
            }

            String payer;

            while (true) {
                System.out.println("Members: " + members);
                System.out.print("Who paid? Enter member name: ");
                payer = sc.nextLine().trim();

                String actualPayer = findMember(members, payer);

                if (actualPayer != null) {
                    payer = actualPayer;
                    break;
                }

                System.out.println("Member not found. Try again.");
            }

            List<String> participants = new ArrayList<>();

            while (participants.isEmpty()) {
                System.out.println("Members: " + members);
                System.out.print(
                        "Enter participants' names separated by commas: ");

                String input = sc.nextLine();
                String[] names = input.split(",");
                List<String> temporary = new ArrayList<>();
                boolean valid = true;

                for (String name : names) {
                    String actualName = findMember(members, name.trim());

                    if (actualName == null) {
                        System.out.println("Unknown member: " + name.trim());
                        valid = false;
                        break;
                    }

                    if (temporary.contains(actualName)) {
                        System.out.println(
                                "Duplicate participant: " + actualName);
                        valid = false;
                        break;
                    }

                    temporary.add(actualName);
                }

                if (valid && !temporary.isEmpty()) {
                    participants.addAll(temporary);
                } else {
                    System.out.println("Please enter participants again.");
                }
            }

            // Use the existing Expense class and constructor.
            ExpenseService.Expense expense =
                    new ExpenseService.Expense(
                            i + 1,
                            title,
                            amount,
                            payer,
                            ExpenseService.Category.OTHER,
                            new ArrayList<>(participants)
                    );

            expenses.add(expense);
        }

        displayBalances(members, expenses);

        System.out.println("\nWould you like to simplify debts? (y/n)");
        String choice = sc.nextLine().trim();

        if (choice.equalsIgnoreCase("y")) {
            displaySettlements(members, expenses);
        }

        System.out.println("\nBalance testing completed!");
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static String findMember(List<String> members, String name) {
        for (String member : members) {
            if (member.equalsIgnoreCase(name.trim())) {
                return member;
            }
        }
        return null;
    }

    // Calculate each member's balance from the supplied expenses.
    public static Map<String, BigDecimal> calculateBalances(
            List<String> members,
            List<ExpenseService.Expense> expenses) {

        Map<String, BigDecimal> balances = new LinkedHashMap<>();

        for (String member : members) {
            balances.put(member, new BigDecimal("0.00"));
        }

        for (ExpenseService.Expense expense : expenses) {

            BigDecimal amount = expense.amount.setScale(
                    2, RoundingMode.HALF_UP);

            String payer = findMember(members, expense.payer);

            if (payer == null) {
                continue;
            }

            List<String> participants = new ArrayList<>();

            for (String name : expense.participants) {
                String member = findMember(members, name);

                if (member != null && !participants.contains(member)) {
                    participants.add(member);
                }
            }

            if (participants.isEmpty()) {
                continue;
            }

            // Credit the person who paid the full amount.
            balances.put(payer, balances.get(payer).add(amount));

            // Split in paisa to avoid rounding errors.
            long totalPaisa = amount.movePointRight(2).longValueExact();
            long baseShare = totalPaisa / participants.size();
            long remainder = totalPaisa % participants.size();

            for (int i = 0; i < participants.size(); i++) {

                long sharePaisa = baseShare;

                if (i < remainder) {
                    sharePaisa++;
                }

                BigDecimal share = BigDecimal.valueOf(sharePaisa)
                        .movePointLeft(2).setScale(2);

                String member = participants.get(i);

                balances.put(
                        member,
                        balances.get(member).subtract(share));
            }
        }

        return balances;
    }

    // Display member balances.
    public static void displayBalances(
            List<String> members,
            List<ExpenseService.Expense> expenses) {

        Map<String, BigDecimal> balances =
                calculateBalances(members, expenses);

        System.out.println("\n========== MEMBER BALANCES ==========");

        for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {
            BigDecimal balance = entry.getValue();

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(entry.getKey()
                        + " should receive: Rs. " + balance);

            } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println(entry.getKey()
                        + " owes: Rs. " + balance.abs());

            } else {
                System.out.println(entry.getKey()
                        + " is settled: Rs. 0.00");
            }
        }
    }

    // Simplify debts into fewer payments.
    public static List<Settlement> simplifyDebts(
            Map<String, BigDecimal> balances) {

        List<Account> debtors = new ArrayList<>();
        List<Account> creditors = new ArrayList<>();
        List<Settlement> settlements = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : balances.entrySet()) {

            BigDecimal amount = entry.getValue();

            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new Account(entry.getKey(), amount.abs()));

            } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new Account(entry.getKey(), amount));
            }
        }

        int d = 0;
        int c = 0;

        while (d < debtors.size() && c < creditors.size()) {

            Account debtor = debtors.get(d);
            Account creditor = creditors.get(c);

            BigDecimal payment = debtor.amount.min(creditor.amount);

            settlements.add(new Settlement(
                    debtor.name, creditor.name, payment));

            debtor.amount = debtor.amount.subtract(payment);
            creditor.amount = creditor.amount.subtract(payment);

            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) {
                d++;
            }

            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) {
                c++;
            }
        }

        return settlements;
    }

    // Display the debt settlement plan.
    public static void displaySettlements(
            List<String> members,
            List<ExpenseService.Expense> expenses) {

        Map<String, BigDecimal> balances =
                calculateBalances(members, expenses);

        List<Settlement> settlements = simplifyDebts(balances);

        System.out.println("\n========== DEBT SETTLEMENT ==========");

        if (settlements.isEmpty()) {
            System.out.println("Everyone is settled. No payments needed!");
            return;
        }

        for (Settlement s : settlements) {
            System.out.println(s.getDebtor() + " pays "
                    + s.getCreditor() + ": Rs. " + s.getAmount());
        }
    }

    // NEW: View Member Balances.
    public static void viewBalances(
            List<String> members,
            List<ExpenseService.Expense> expenses) {

        displayBalances(members, expenses);
    }

    // NEW: View Debt Settlement.
    public static void viewDebtSettlement(
            List<String> members,
            List<ExpenseService.Expense> expenses) {

        displaySettlements(members, expenses);
    }

    private static class Account {
        String name;
        BigDecimal amount;

        Account(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }
    }

    public static class Settlement {
        private final String debtor;
        private final String creditor;
        private final BigDecimal amount;

        public Settlement(
                String debtor, String creditor, BigDecimal amount) {
            this.debtor = debtor;
            this.creditor = creditor;
            this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        }

        public String getDebtor() {
            return debtor;
        }

        public String getCreditor() {
            return creditor;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }
}