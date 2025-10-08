import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Book {
    String id, title, author, issuedTo;
    boolean isIssued;

    Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isIssued = false;
        this.issuedTo = "";
    }

    Object[] toRow() {
        return new Object[]{id, title, author, (isIssued ? "Issued to: " + issuedTo : "Available")};
    }
}

public class LibraryManagementSystem extends JFrame {
    private ArrayList<Book> books = new ArrayList<>();
    private DefaultTableModel model;

    // UI Components
    private JTextField idField, titleField, authorField, memberField;
    private JTable bookTable;

    public LibraryManagementSystem() {
        setTitle("📚 Library Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Top Panel (Input fields) ---
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        idField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        memberField = new JTextField();

        inputPanel.add(new JLabel("Book ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Member Name (for issue):"));
        inputPanel.add(memberField);

        add(inputPanel, BorderLayout.NORTH);

        // --- Center Panel (Table) ---
        String[] columns = {"Book ID", "Title", "Author", "Status"};
        model = new DefaultTableModel(columns, 0);
        bookTable = new JTable(model);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // --- Bottom Panel (Buttons) ---
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Book");
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Book");
        JButton refreshBtn = new JButton("Show All Books");

        btnPanel.add(addBtn);
        btnPanel.add(issueBtn);
        btnPanel.add(returnBtn);
        btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        // --- Button actions ---
        addBtn.addActionListener(e -> addBook());
        issueBtn.addActionListener(e -> issueBook());
        returnBtn.addActionListener(e -> returnBook());
        refreshBtn.addActionListener(e -> refreshTable());

        setVisible(true);
    }

    private void addBook() {
        String id = idField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();

        if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        books.add(new Book(id, title, author));
        JOptionPane.showMessageDialog(this, "Book added successfully!");
        clearFields();
        refreshTable();
    }

    private void issueBook() {
        String id = idField.getText().trim();
        String member = memberField.getText().trim();

        if (id.isEmpty() || member.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Book ID and Member Name.");
            return;
        }

        for (Book b : books) {
            if (b.id.equals(id)) {
                if (b.isIssued) {
                    JOptionPane.showMessageDialog(this, "Book already issued.");
                    return;
                }
                b.isIssued = true;
                b.issuedTo = member;
                JOptionPane.showMessageDialog(this, "Book issued to " + member + "!");
                refreshTable();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Book not found.");
    }

    private void returnBook() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Book ID to return.");
            return;
        }

        for (Book b : books) {
            if (b.id.equals(id) && b.isIssued) {
                b.isIssued = false;
                b.issuedTo = "";
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                refreshTable();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Invalid book ID or book not issued.");
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(b.toRow());
        }
    }

    private void clearFields() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        memberField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }
}
