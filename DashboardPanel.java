import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    // =========================================================================
    // 1. Data Models & Interfaces (Decouples your code from teammates')
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
    // 2. Dashboard Panel UI Implementation
    // =========================================================================
    private final GroupServiceBridge groupService;
    private final ExpenseServiceBridge expenseService;
    private final BalanceServiceBridge balanceService;

    private JLabel totalExpenditureLabel;
    private DefaultTableModel balanceTableModel;
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel transactionTableModel;

    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    public DashboardPanel(GroupServiceBridge groupService, ExpenseServiceBridge expenseService, BalanceServiceBridge balanceService) {
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.balanceService = balanceService;

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(245, 247, 250));

        initComponents();
    }

    private void initComponents() {
        // --- Header Banner: Total Expenditure ---
        JPanel bannerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bannerPanel.setBackground(Color.WHITE);
        bannerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel titleLabel = new JLabel("Total Group Expenditure:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        totalExpenditureLabel = new JLabel("$0.00");
        totalExpenditureLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalExpenditureLabel.setForeground(new Color(40, 167, 69));

        bannerPanel.add(titleLabel);
        bannerPanel.add(totalExpenditureLabel);

        add(bannerPanel, BorderLayout.NORTH);

        // --- Center Row: Balances & Category Summaries ---
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 12, 0));
        middlePanel.setOpaque(false);

        // Member Balances Table
        balanceTableModel = new DefaultTableModel(new Object[]{"Member", "Net Balance"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable balanceTable = new JTable(balanceTableModel);
        styleTable(balanceTable);
        balanceTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (val != null) {
                    String s = val.toString();
                    if (s.startsWith("-")) {
                        c.setForeground(new Color(220, 53, 69)); // Net Owed (Red)
                    } else if (!s.equals("$0.00")) {
                        c.setForeground(new Color(40, 167, 69)); // Net Owed To (Green)
                    } else {
                        c.setForeground(Color.DARK_GRAY);
                    }
                }
                return c;
            }
        });

        // Category Summary Table
        categoryTableModel = new DefaultTableModel(new Object[]{"Category", "Total Spent"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable categoryTable = new JTable(categoryTableModel);
        styleTable(categoryTable);

        middlePanel.add(createCard("Member Balances", new JScrollPane(balanceTable)));
        middlePanel.add(createCard("Category Breakdown", new JScrollPane(categoryTable)));

        // --- Bottom Row: Transaction History ---
        transactionTableModel = new DefaultTableModel(new Object[]{"Date", "Description", "Paid By", "Category", "Amount"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable transactionTable = new JTable(transactionTableModel);
        styleTable(transactionTable);

        JPanel historyCard = createCard("Transaction History", new JScrollPane(transactionTable));
        historyCard.setPreferredSize(new Dimension(0, 220));

        JPanel contentContainer = new JPanel(new BorderLayout(0, 12));
        contentContainer.setOpaque(false);
        contentContainer.add(middlePanel, BorderLayout.CENTER);
        contentContainer.add(historyCard, BorderLayout.SOUTH);

        add(contentContainer, BorderLayout.CENTER);
    }

    /**
     * Primary entry point to refresh all metrics and display tables.
     */
    public void refreshDashboard(String groupId) {
        if (groupId == null || groupId.trim().isEmpty()) return;

        // Update Total
        double total = groupService.getTotalGroupExpenditure(groupId);
        totalExpenditureLabel.setText(currencyFormatter.format(total));

        // Update Balances
        balanceTableModel.setRowCount(0);
        Map<String, Double> balances = balanceService.getMemberBalances(groupId);
        if (balances != null) {
            balances.forEach((member, balance) -> 
                balanceTableModel.addRow(new Object[]{member, currencyFormatter.format(balance)})
            );
        }

        // Update Category Summary
        categoryTableModel.setRowCount(0);
        Map<String, Double> categories = groupService.getCategoryBreakdown(groupId);
        if (categories != null) {
            categories.forEach((cat, amt) -> 
                categoryTableModel.addRow(new Object[]{cat, currencyFormatter.format(amt)})
            );
        }

        // Update Transactions
        transactionTableModel.setRowCount(0);
        List<ExpenseItem> expenses = expenseService.getGroupExpenses(groupId);
        if (expenses != null) {
            for (ExpenseItem e : expenses) {
                transactionTableModel.addRow(new Object[]{
                        e.getDate(), e.getDescription(), e.getPaidBy(), e.getCategory(), currencyFormatter.format(e.getAmount())
                });
            }
        }
    }

    private JPanel createCard(String title, JComponent content) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel header = new JLabel(title);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        card.add(header, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(238, 240, 243));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
    }

    // =========================================================================
    // 3. Independent Main Runner with Mock Data (Runs Standalone)
    // =========================================================================
    public static void main(String[] args) {
        // Simple mock implementation for testing without teammates' modules
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

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Smart Expense Splitter - Dashboard Panel Test");
            DashboardPanel dashboard = new DashboardPanel(mockGroupService, mockExpenseService, mockBalanceService);

            // Load test data
            dashboard.refreshDashboard("group_demo");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(850, 600);
            frame.setLocationRelativeTo(null);
            frame.add(dashboard);
            frame.setVisible(true);
        });
    }
}