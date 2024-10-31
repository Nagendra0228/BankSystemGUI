package p1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class BankAccount implements Serializable { // Added Serializable
    private String accountHolderName;
    private String accountNumber;
    private double balance;
    private List<Transaction> transactionHistory;

    public BankAccount(String accountHolderName, String accountNumber, double initialBalance) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        recordTransaction("Account Created", initialBalance);
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            recordTransaction("Deposit", amount);
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            recordTransaction("Withdraw", amount);
            return true;
        }
        return false;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    private void recordTransaction(String type, double amount) {
        transactionHistory.add(new Transaction(type, amount));
    }
}

class Transaction implements Serializable { // Added Serializable
    private String type;
    private double amount;
    private String dateTime;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.dateTime = java.time.LocalDateTime.now().toString();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDateTime() {
        return dateTime;
    }
}

public class BankSystemGUI {
    private List<BankAccount> accounts;
    private JFrame mainMenuFrame;

    public BankSystemGUI() {
        accounts = loadAccounts();
        createLoginPage();
    }

    private void createLoginPage() {
        JFrame loginFrame = new JFrame("Bank System - Login");
        loginFrame.setSize(300, 150);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Username:");
        JTextField userText = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordText = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginFrame.add(userLabel);
        loginFrame.add(userText);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordText);
        loginFrame.add(loginButton);
        loginFrame.add(registerButton);

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            if (username.equals("user") && password.equals("pass")) {  // Change as needed
                loginFrame.dispose();
                createMainMenu();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.");
            }
        });

        registerButton.addActionListener(e -> openRegistrationPage());

        loginFrame.setVisible(true);
    }

    private void openRegistrationPage() {
        JFrame frame = new JFrame("Register");
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField nameField = new JTextField(15);
        JTextField accountNumberField = new JTextField(15);
        JTextField initialBalanceField = new JTextField(15);
        JButton registerButton = new JButton("Register");

        frame.add(new JLabel("Account Holder Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Account Number:"));
        frame.add(accountNumberField);
        frame.add(new JLabel("Initial Balance:"));
        frame.add(initialBalanceField);
        frame.add(registerButton);

        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String accNumber = accountNumberField.getText();
            double initialBalance;
            try {
                initialBalance = Double.parseDouble(initialBalanceField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid balance amount.");
                return;
            }
            accounts.add(new BankAccount(name, accNumber, initialBalance));
            saveAccounts();
            JOptionPane.showMessageDialog(frame, "Account registered successfully!");
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void createMainMenu() {
        mainMenuFrame = new JFrame("Bank System - Main Menu");
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenuFrame.setSize(300, 300);
        mainMenuFrame.setLayout(new GridLayout(0, 1, 10, 10));

        JButton deleteAccountButton = new JButton("Delete Account");
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton checkBalanceButton = new JButton("Check Balance");
        JButton viewTransactionButton = new JButton("View Transaction History");
        JButton completeReportButton = new JButton("Generate Complete Report");

        mainMenuFrame.add(deleteAccountButton);
        mainMenuFrame.add(depositButton);
        mainMenuFrame.add(withdrawButton);
        mainMenuFrame.add(checkBalanceButton);
        mainMenuFrame.add(viewTransactionButton);
        mainMenuFrame.add(completeReportButton);

        deleteAccountButton.addActionListener(e -> openDeleteAccountPage());
        depositButton.addActionListener(e -> openDepositPage());
        withdrawButton.addActionListener(e -> openWithdrawPage());
        checkBalanceButton.addActionListener(e -> openCheckBalancePage());
        viewTransactionButton.addActionListener(e -> openTransactionHistoryPage());
        completeReportButton.addActionListener(e -> generateCompleteReport());

        mainMenuFrame.setVisible(true);
    }

    private void openDeleteAccountPage() {
        JFrame frame = new JFrame("Delete Account");
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField accountNumberField = new JTextField(15);
        JButton deleteButton = new JButton("Delete");

        frame.add(new JLabel("Account Number:"));
        frame.add(accountNumberField);
        frame.add(deleteButton);

        deleteButton.addActionListener(e -> {
            String accNumber = accountNumberField.getText();
            BankAccount accountToDelete = findAccount(accNumber);
            if (accountToDelete != null) {
                accounts.remove(accountToDelete);
                saveAccounts();
                JOptionPane.showMessageDialog(frame, "Account deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openDepositPage() {
        JFrame frame = new JFrame("Deposit");
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField accountNumberField = new JTextField(15);
        JTextField amountField = new JTextField(15);
        JButton depositButton = new JButton("Deposit");

        frame.add(new JLabel("Account Number:"));
        frame.add(accountNumberField);
        frame.add(new JLabel("Amount to Deposit:"));
        frame.add(amountField);
        frame.add(depositButton);

        depositButton.addActionListener(e -> {
            String accNumber = accountNumberField.getText();
            BankAccount account = findAccount(accNumber);
            if (account != null) {
                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid amount.");
                    return;
                }
                account.deposit(amount);
                saveAccounts();
                JOptionPane.showMessageDialog(frame, "Deposit successful!");
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openWithdrawPage() {
        JFrame frame = new JFrame("Withdraw");
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField accountNumberField = new JTextField(15);
        JTextField amountField = new JTextField(15);
        JButton withdrawButton = new JButton("Withdraw");

        frame.add(new JLabel("Account Number:"));
        frame.add(accountNumberField);
        frame.add(new JLabel("Amount to Withdraw:"));
        frame.add(amountField);
        frame.add(withdrawButton);

        withdrawButton.addActionListener(e -> {
            String accNumber = accountNumberField.getText();
            BankAccount account = findAccount(accNumber);
            if (account != null) {
                double amount;
                try {
                    amount = Double.parseDouble(amountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid amount.");
                    return;
                }
                if (account.withdraw(amount)) {
                    saveAccounts();
                    JOptionPane.showMessageDialog(frame, "Withdrawal successful!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Insufficient funds.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openCheckBalancePage() {
        JFrame frame = new JFrame("Check Balance");
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField accountNumberField = new JTextField(15);
        JButton checkBalanceButton = new JButton("Check Balance");

        frame.add(new JLabel("Account Number:"));
        frame.add(accountNumberField);
        frame.add(checkBalanceButton);

        checkBalanceButton.addActionListener(e -> {
            String accNumber = accountNumberField.getText();
            BankAccount account = findAccount(accNumber);
            if (account != null) {
                JOptionPane.showMessageDialog(frame, "Balance: " + account.getBalance());
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
            frame.dispose();
        });

        frame.setVisible(true);
    }

    private void openTransactionHistoryPage() {
        JFrame frame = new JFrame("Transaction History");
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField accountNumberField = new JTextField(15);
        JButton viewHistoryButton = new JButton("View History");
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);

        frame.add(new JLabel("Account Number:"));
        frame.add(accountNumberField);
        frame.add(viewHistoryButton);
        frame.add(scrollPane);

        viewHistoryButton.addActionListener(e -> {
            String accNumber = accountNumberField.getText();
            BankAccount account = findAccount(accNumber);
            if (account != null) {
                StringBuilder history = new StringBuilder();
                for (Transaction transaction : account.getTransactionHistory()) {
                    history.append(transaction.getDateTime())
                            .append(" - ")
                            .append(transaction.getType())
                            .append(": ")
                            .append(transaction.getAmount())
                            .append("\n");
                }
                historyArea.setText(history.toString());
            } else {
                JOptionPane.showMessageDialog(frame, "Account not found.");
            }
        });

        frame.setVisible(true);
    }

    private void generateCompleteReport() {
        StringBuilder report = new StringBuilder();
        for (BankAccount account : accounts) {
            report.append("Account Holder: ").append(account.getAccountHolderName())
                    .append("\nAccount Number: ").append(account.getAccountNumber())
                    .append("\nBalance: ").append(account.getBalance())
                    .append("\nTransaction History:\n");
            for (Transaction transaction : account.getTransactionHistory()) {
                report.append(" - ").append(transaction.getDateTime())
                        .append(" - ").append(transaction.getType())
                        .append(": ").append(transaction.getAmount()).append("\n");
            }
            report.append("\n");
        }
        JOptionPane.showMessageDialog(mainMenuFrame, report.toString());
    }

    private BankAccount findAccount(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    private void saveAccounts() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("accounts.dat"))) {
            oos.writeObject(accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<BankAccount> loadAccounts() {
        List<BankAccount> loadedAccounts = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("accounts.dat"))) {
            loadedAccounts = (List<BankAccount>) ois.readObject();
        } catch (FileNotFoundException e) {
            // No file to load from, starting fresh
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedAccounts;
    }

    public static void main(String[] args) {
        new BankSystemGUI();
    }
}
