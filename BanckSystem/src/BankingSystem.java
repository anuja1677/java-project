import java.sql.*;
import java.util.Scanner;

public class BankingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.println("\n--- Simple Banking System ---");
            System.out.println("1. Create Customer & Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch(choice) {
                case 1:
                    createCustomerAndAccount(sc);
                    break;
                case 2:
                    deposit(sc);
                    break;
                case 3:
                    withdraw(sc);
                    break;
                case 4:
                    checkBalance(sc);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void createCustomerAndAccount(Scanner sc) {
        System.out.print("Enter customer name: ");
        sc.nextLine();
        String name = sc.nextLine();

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
                System.out.println("Account created successfully! Customer ID: " + customerId);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deposit(Scanner sc) {
        System.out.print("Enter account ID: ");
        int accountId = sc.nextInt();
        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();

        try(Connection conn = Database.getConnection()) {
            String sql = "SELECT balance FROM bank_accounts WHERE account_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                double balance = rs.getDouble("balance");
                balance += amount;
                String update = "UPDATE bank_accounts SET balance=? WHERE account_id=?";
                PreparedStatement ps2 = conn.prepareStatement(update);
                ps2.setDouble(1, balance);
                ps2.setInt(2, accountId);
                ps2.executeUpdate();
                System.out.println("Deposited successfully. New balance: " + balance);
            } else {
                System.out.println("Account not found!");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static void withdraw(Scanner sc) {
        System.out.print("Enter account ID: ");
        int accountId = sc.nextInt();
        System.out.print("Enter amount to withdraw: ");
        double amount = sc.nextDouble();

        try(Connection conn = Database.getConnection()) {
            String sql = "SELECT balance FROM bank_accounts WHERE account_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                double balance = rs.getDouble("balance");
                if(amount <= balance) {
                    balance -= amount;
                    String update = "UPDATE bank_accounts SET balance=? WHERE account_id=?";
                    PreparedStatement ps2 = conn.prepareStatement(update);
                    ps2.setDouble(1, balance);
                    ps2.setInt(2, accountId);
                    ps2.executeUpdate();
                    System.out.println("Withdrawn successfully. New balance: " + balance);
                } else {
                    System.out.println("Insufficient balance!");
                }
            } else {
                System.out.println("Account not found!");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    private static void checkBalance(Scanner sc) {
        System.out.print("Enter account ID: ");
        int accountId = sc.nextInt();

        try(Connection conn = Database.getConnection()) {
            String sql = "SELECT balance FROM bank_accounts WHERE account_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                System.out.println("Current Balance: " + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found!");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}