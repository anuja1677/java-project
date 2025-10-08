import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class Student {
    private String name;
    private String studentID;
    private ArrayList<Double> grades;

    public Student(String name, String studentID) {
        this.name = name;
        this.studentID = studentID;
        this.grades = new ArrayList<>();
    }

    public void addGrade(double grade) {
        grades.add(grade);
    }

    public String getGradesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < grades.size(); i++) {
            sb.append("Grade ").append(i + 1).append(": ").append(grades.get(i)).append("\n");
        }
        return sb.toString();
    }

    public double calculateAverage() {
        double sum = 0;
        for (double grade : grades) sum += grade;
        return grades.size() > 0 ? sum / grades.size() : 0;
    }

    public boolean hasPassed() {
        return calculateAverage() >= 50;
    }

    public String getName() {
        return name;
    }

    public String getStudentID() {
        return studentID;
    }
}

public class StudentManagementGUI {
    private JFrame frame;
    private JTextField nameField, idField, gradeField;
    private JTextArea displayArea;
    private ArrayList<Student> students;
    private Student currentStudent;

    public StudentManagementGUI() {
        students = new ArrayList<>();
        currentStudent = null;

        frame = new JFrame("Student Management System");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        frame.add(new JLabel("Student Name:"));
        nameField = new JTextField(15);
        frame.add(nameField);

        frame.add(new JLabel("Student ID:"));
        idField = new JTextField(10);
        frame.add(idField);

        JButton addStudentBtn = new JButton("Add Student");
        frame.add(addStudentBtn);

        frame.add(new JLabel("Enter Grade:"));
        gradeField = new JTextField(5);
        frame.add(gradeField);

        JButton addGradeBtn = new JButton("Add Grade");
        frame.add(addGradeBtn);

        JButton displayBtn = new JButton("Display All Students");
        frame.add(displayBtn);

        displayArea = new JTextArea(20, 40);
        displayArea.setEditable(false);
        frame.add(new JScrollPane(displayArea));

        // Action Listeners
        addStudentBtn.addActionListener(e -> {
            String name = nameField.getText();
            String id = idField.getText();
            if (name.isEmpty() || id.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter name and ID.");
                return;
            }
            currentStudent = new Student(name, id);
            students.add(currentStudent);
            displayArea.append("Added Student: " + name + " (" + id + ")\n");
            nameField.setText("");
            idField.setText("");
        });

        addGradeBtn.addActionListener(e -> {
            if (currentStudent == null) {
                JOptionPane.showMessageDialog(frame, "Add a student first.");
                return;
            }
            try {
                double grade = Double.parseDouble(gradeField.getText());
                currentStudent.addGrade(grade);
                displayArea.append("Added Grade: " + grade + " for " + currentStudent.getName() + "\n");
                gradeField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid number for grade.");
            }
        });

        displayBtn.addActionListener(e -> displayAllStudents());

        frame.setVisible(true);
    }

    private void displayAllStudents() {
        displayArea.append("\n--- Student Details ---\n");
        double totalClassAverage = 0;
        for (Student student : students) {
            displayArea.append("Name: " + student.getName() + "\n");
            displayArea.append("ID: " + student.getStudentID() + "\n");
            displayArea.append(student.getGradesString());
            double avg = student.calculateAverage();
            displayArea.append("Average: " + avg + "\n");
            displayArea.append("Status: " + (student.hasPassed() ? "Passed" : "Failed") + "\n");
            displayArea.append("---------------------------\n");
            totalClassAverage += avg;
        }
        double classAverage = students.size() > 0 ? totalClassAverage / students.size() : 0;
        displayArea.append("Class Average: " + classAverage + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementGUI());
    }
}
