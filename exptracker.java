import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTrackerApp {
    private List<Expense> expenses = new ArrayList<>();
    private JFrame frame;
    private JTextField descriptionField, amountField, categoryField;
    private JTextArea expenseListArea;

    private static final String DATA_FILE = "expenses.dat";

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();
        expenseListArea = new JTextArea(10, 40);
        expenseListArea.setEditable(false);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(expenseListArea), BorderLayout.SOUTH);

        loadExpensesFromFile();

        frame.pack();
        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionField = new JTextField(20);

        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(20);

        JLabel categoryLabel = new JLabel("Category:");
        categoryField = new JTextField(20);

        panel.add(descriptionLabel);
        panel.add(descriptionField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(categoryLabel);
        panel.add(categoryField);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton addButton = new JButton("Add Expense");
        JButton viewButton = new JButton("View Expenses");
        JButton totalExpenseButton = new JButton("Total Expense");
        JButton resetButton = new JButton("Reset");

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewExpenses();
            }
        });

        totalExpenseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTotalExpense();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetExpenses();
            }
        });

        panel.add(addButton);
        panel.add(viewButton);
        panel.add(totalExpenseButton);
        panel.add(resetButton);

        return panel;
    }

    private void addExpense() {
        String description = descriptionField.getText();
        String amountStr = amountField.getText();
        String category = categoryField.getText();

        if (description.isEmpty() || amountStr.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Expense expense = new Expense(description, amount, category);
            expenses.add(expense);
            saveExpensesToFile();
            JOptionPane.showMessageDialog(frame, "Expense added successfully.");
            clearInputFields();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Amount should be a valid number.");
        }
    }

    private void viewExpenses() {
        refreshExpenseList();
    }

    private void calculateTotalExpense() {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        JOptionPane.showMessageDialog(frame, "Total Expense: " + total);
    }

    private void resetExpenses() {
        int option = JOptionPane.showConfirmDialog(frame, "Are you sure you want to reset all expenses?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            expenses.clear();
            saveExpensesToFile();
            refreshExpenseList();
        }
    }

    private void clearInputFields() {
        descriptionField.setText("");
        amountField.setText("");
        categoryField.setText("");
    }

    private void refreshExpenseList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expense List:\n");
        for (Expense expense : expenses) {
            sb.append(expense.toString()).append("\n");
        }
        expenseListArea.setText(sb.toString());
    }

    private void saveExpensesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadExpensesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object data = ois.readObject();
            if (data instanceof List) {
                expenses = (List<Expense>) data;
            }
        } catch (IOException | ClassNotFoundException e) {
            expenses = new ArrayList<>();
        }
    }
}

class Expense implements Serializable {
    private String description;
    private double amount;
    private String category;

    public Expense(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Description: " + description + ", Amount: " + amount + ", Category: " + category;
    }

    public double getAmount() {
        return amount;
    }
}