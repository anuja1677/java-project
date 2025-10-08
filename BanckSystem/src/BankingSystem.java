import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BankingSystem {
    private JFrame frame;
    private JTextField accountIdField, amountField;
    private JTextArea outputArea;

    public BankingSystem() {
        frame = new JFrame("Banking System");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel for inputs
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));

        panel.add(new JLabel("Account ID:"));
        accountIdField = new JTextField();
        panel.add(accountIdField);

        panel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        panel.add(amountField);

        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton checkBalanceBtn = new JButton("Check Balance");
        JButton createAccountBtn = new JButton("Create Account");

        panel.add(depositBtn);
        panel.add(withdrawBtn);
        panel.add(checkBalanceBtn);
        panel.add(createAccountBtn);

        frame.add(panel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        frame.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Button actions
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        checkBalanceBtn.addActionListener(e -> checkBalance());
        createAccountBtn.addActionListener(e -> createAccount());

        frame.setVisible(true);
    }

    private void createAccount() {
        String name = JOptionPane.showInputDialog(frame, "Enter Customer Name:");
        if(name == null || name.trim().isEmpty()) return;

        try(Connection conn = Database.getConnection()) {
            String sql1 = "INSERT INTO customers(name) VALUES(?)";
            PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, name);
            ps1.executeUpdate();
            ResultSet rs = ps1.getGeneratedKeys();
            if(rs.next()) {
                int customerId = rs.getInt(1);
                String sql2 = "INSERT INTO bank_accounts(customer_id, balance) VALUES(?, 0)";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, customerId);
                ps2.executeUpdate();
                outputArea.append("Account created! Customer ID: " + customerId + "\n");
            }
        } catch(SQLException e) {
            e.printStackTrace();
            outputArea.append("Error creating account!\n");
        }
    }

    private void deposit() {
        try {
            int accountId = Integer.parseInt(accountIdField.getText());
            double amount = Double.parseDouble(amountField.getText());
            if(amount <= 0) {
                outputArea.append("Enter valid deposit amount.\n");
                return;
            }

            try(Connection conn = Database.getConnection()) {
                String sql = "SELECT balance FROM bank_accounts WHERE account_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, accountId);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    double balance = rs.getDouble("balance") + amount;
                    String update = "UPDATE bank_accounts SET balance=? WHERE account_id=?";
                    PreparedStatement ps2 = conn.prepareStatement(update);
                    ps2.setDouble(1, balance);
                    ps2.setInt(2, accountId);
                    ps2.executeUpdate();
                    outputArea.append("Deposited successfully. New balance: " + balance + "\n");
                } else {
                    outputArea.append("Account not found!\n");
                }
            }
        } catch(NumberFormatException e) {
            outputArea.append("Enter valid numbers!\n");
        } catch(SQLException e) {
            e.printStackTrace();
            outputArea.append("Database error!\n");
        }
    }

    private void withdraw() {
        try {
            int accountId = Integer.parseInt(accountIdField.getText());
            double amount = Double.parseDouble(amountField.getText());

            try(Connection conn = Database.getConnection()) {
                String sql = "SELECT balance FROM bank_accounts WHERE account_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, accountId);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    double balance = rs.getDouble("balance");
                    if(amount > 0 && amount <= balance) {
                        balance -= amount;
                        String update = "UPDATE bank_accounts SET balance=? WHERE account_id=?";
                        PreparedStatement ps2 = conn.prepareStatement(update);
                        ps2.setDouble(1, balance);
                        ps2.setInt(2, accountId);
                        ps2.executeUpdate();
                        outputArea.append("Withdrawn successfully. New balance: " + balance + "\n");
                    } else {
                        outputArea.append("Insufficient balance or invalid amount!\n");
                    }
                } else {
                    outputArea.append("Account not found!\n");
                }
            }
        } catch(NumberFormatException e) {
            outputArea.append("Enter valid numbers!\n");
        } catch(SQLException e) {
            e.printStackTrace();
            outputArea.append("Database error!\n");
        }
    }

    private void checkBalance() {
        try {
            int accountId = Integer.parseInt(accountIdField.getText());
            try(Connection conn = Database.getConnection()) {
                String sql = "SELECT balance FROM bank_accounts WHERE account_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, accountId);
                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    double balance = rs.getDouble("balance");
                    outputArea.append("Current balance: " + balance + "\n");
                } else {
                    outputArea.append("Account not found!\n");
                }
            }
        } catch(NumberFormatException e) {
            outputArea.append("Enter valid account ID!\n");
        } catch(SQLException e) {
            e.printStackTrace();
            outputArea.append("Database error!\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BankingSystem::new);
    }
}