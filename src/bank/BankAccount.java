package bank;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
class BankAccount {
    private String accountHolderName;
    private long accountNumber;
    private double balance;
    private double totalWithdraw;
    private double totalDeposit;
    public BankAccount(String accountHolderName, long accountNumber, double initialBalance) {
        this.accountHolderName = accountHolderName;
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.totalWithdraw = 0.0;
        this.totalDeposit = 0.0;
    }
    public void deposit(double amount) {
        balance += amount;
        totalDeposit += amount;
    }
    public void withdraw(double amount) throws InsufficientBalanceException {
        if (amount > balance) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        balance -= amount;
        totalWithdraw += amount;
    }
    public String getAccountDetails() {
        return "Account Holder: " + accountHolderName +
               "\nAccount Number: " + accountNumber +
               "\nWithdraw: " + totalWithdraw +
               "\nDeposit: " + totalDeposit +
               "\nBalance: " + balance;
    }
    public long getAccountNumber() {
        return accountNumber;
    }
}
// Custom exception for insufficient balance
class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
class BankingApplicationGUI extends JFrame {
    private JTextField nameField, accNumberField, amountField;
    private JTextArea resultArea;
    private HashMap<Long, BankAccount> accounts;
    public BankingApplicationGUI() {
        setTitle("Banking Application");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 10, 10));
        setLocationRelativeTo(null);  
        accounts = new HashMap<>();
        // Create components
        nameField = new JTextField(20);
        accNumberField = new JTextField(10);
        amountField = new JTextField(10);
        resultArea = new JTextArea(5, 5);
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton createAccountButton = new JButton("Create Account");
        // Set layout
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Account Number:"));
        add(accNumberField);
        add(new JLabel("Amount:"));
        add(amountField);
        add(depositButton);
        add(withdrawButton);
        add(createAccountButton);
        add(new JScrollPane(resultArea));
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performTransaction(true);
            }
        });
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performTransaction(false);
            }
        });
        createAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAccount();
            }
        });
    }
    private void writeToFile(String data, boolean isTransaction) {
        String filename = "bank_accounts.csv"; // Change file extension to .csv
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // Fetching the current date
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(currentDate);
            
            // Writing current date and data to the file with rows
            if (!isTransaction) {
                writer.write("Date,Account Holder,Account Number,Withdraw,Deposit"); // Header row
                writer.newLine();
            }
            writer.write(formattedDate + "," + data.replaceAll("\n", ",")); // Replace newlines with commas
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void performTransaction(boolean isDeposit) {
        try {
            String accountHolderName = nameField.getText();
            long accountNumber = Long.parseLong(accNumberField.getText());
            double amount = Double.parseDouble(amountField.getText());
            // Check if the account exists
            BankAccount currentAccount = accounts.get(accountNumber);
            if (currentAccount == null) {
                JOptionPane.showMessageDialog(this, "Account does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isDeposit) {
                currentAccount.deposit(amount);
                resultArea.setText("Deposit Successful!\n" + currentAccount.getAccountDetails());
            } else {
                currentAccount.withdraw(amount);
                resultArea.setText("Withdrawal Successful!\n" + currentAccount.getAccountDetails());
            }
            writeToFile(currentAccount.getAccountDetails(), isDeposit);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (InsufficientBalanceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            nameField.setText("");
            accNumberField.setText("");
            amountField.setText("");
        }
    }
    private void createAccount() {
        try {
            String accountHolderName = nameField.getText();
            long accountNumber = Long.parseLong(accNumberField.getText());
            double initialBalance = 0.0; // Default initial balance
            // Check if amount field is not empty
            String amountText = amountField.getText().trim();
            if (!amountText.isEmpty()) {
                initialBalance = Double.parseDouble(amountText);
            }
            if (accounts.containsKey(accountNumber)) {
                JOptionPane.showMessageDialog(this, "Account already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BankAccount newAccount = new BankAccount(accountHolderName, accountNumber, initialBalance);
            accounts.put(accountNumber, newAccount);
            resultArea.setText("Account created!\n" + newAccount.getAccountDetails());
            writeToFile(newAccount.getAccountDetails(), false); // Write account creation details
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            nameField.setText("");
            accNumberField.setText("");
            amountField.setText("");
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BankingApplicationGUI().setVisible(true);
            }
        });
    }
}
