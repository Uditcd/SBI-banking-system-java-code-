import java.io.*;
import java.util.*;

class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    long accountNumber;
    String username;
    String password;
    String name, aadhar, mobile, address, nominee;
    int age;
    boolean hasATM, hasChequeBook;
    int atmPin; // 4-digit PIN for ATM
    double balance;

    // Cheque book: for simplicity, we just track if issued and last cheque number used
    int lastChequeNumber; // for issued cheques

    Customer(long accountNumber, String username, String password, String name, String aadhar, String mobile,
             int age, String address, String nominee, boolean hasATM, boolean hasChequeBook, int atmPin) {
        this.accountNumber = accountNumber;
        this.username = username;
        this.password = password;
        this.name = name;
        this.aadhar = aadhar;
        this.mobile = mobile;
        this.age = age;
        this.address = address;
        this.nominee = nominee;
        this.hasATM = hasATM;
        this.hasChequeBook = hasChequeBook;
        this.atmPin = atmPin;
        this.balance = 0.0;
        this.lastChequeNumber = 100000; // starting cheque number
    }
}

public class SBIBanking {
    static final String BANK_NAME = "State Bank of India";
    static final String BRANCH = "Durgapur";
    static final String IFSC = "SBIN0007337";
    static final String DATA_FILE = "bankdata.ser";

    static Scanner sc = new Scanner(System.in);
    static Map<Long, Customer> customersByAccount = new HashMap<>();
    static Map<String, Customer> customersByUsername = new HashMap<>();
    static long nextAccount = 1;

    static final String ADMIN_USER = "Udit";
    static final String ADMIN_PASS = "Udit123";

    // Helper class for serialization of all data
    static class DataStorage implements Serializable {
        private static final long serialVersionUID = 1L;
        Map<Long, Customer> customersByAccount;
        Map<String, Customer> customersByUsername;
        long nextAccount;
        public DataStorage(Map<Long, Customer> cba, Map<String, Customer> cbu, long na) {
            customersByAccount = new HashMap<>(cba);
            customersByUsername = new HashMap<>(cbu);
            nextAccount = na;
        }
    }

    public static void main(String[] args) {
        loadData();
        while (true) {
            System.out.println("\n--- Welcome to " + BANK_NAME + " ---");
            System.out.println("Branch: " + BRANCH + " | IFSC: " + IFSC);
            System.out.println("1. Admin Login");
            System.out.println("2. Customer Login");
            System.out.println("3. Create New Account");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {
                case 1: adminMenu(); break;
                case 2: customerMenu(); break;
                case 3: openAccount(); break;
                case 0:
                    saveData();
                    System.out.println("Thank you for banking with us.");
                    System.exit(0);
                default: System.out.println("Invalid option.");
            }
        }
    }

    static void adminMenu() {
        System.out.print("Enter admin username: ");
        String user = sc.nextLine();
        System.out.print("Enter admin password: ");
        String pass = sc.nextLine();
        if (!user.equals(ADMIN_USER) || !pass.equals(ADMIN_PASS)) {
            System.out.println("Invalid admin credentials.");
            return;
        }
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Customers");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");
            int ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1: viewAllCustomers(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    static void customerMenu() {
        Customer c = loginCustomer();
        if (c == null) return;
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Deposit (Bank Teller)");
            System.out.println("2. Withdraw (Bank Teller)");
            System.out.println("3. ATM Deposit");
            System.out.println("4. ATM Withdrawal");
            System.out.println("5. Money Transfer");
            System.out.println("6. Issue ATM Card");
            System.out.println("7. Issue Cheque Book");
            System.out.println("8. Show Account Details");
            System.out.println("9. Change Password");
            System.out.println("10. Change ATM PIN");
            System.out.println("11. Check Balance");
            System.out.println("12. Cheque Deposit");
            System.out.println("13. Cheque Withdrawal");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");
            int ch = sc.nextInt();
            sc.nextLine();
            switch (ch) {
                case 1: deposit(c); break;
                case 2: withdraw(c); break;
                case 3: atmDeposit(c); break;
                case 4: atmWithdrawal(c); break;
                case 5: fundTransfer(c); break;
                case 6: issueATM(c); break;
                case 7: issueChequeBook(c); break;
                case 8: showDetails(c); break;
                case 9: changePassword(c); break;
                case 10: changeAtmPin(c); break;
                case 11: checkBalance(c); break;
                case 12: chequeDeposit(c); break;
                case 13: chequeWithdrawal(c); break;
                case 0: saveData(); return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    static Customer loginCustomer() {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        Customer c = customersByUsername.get(username);
        if (c == null || !c.password.equals(password)) {
            System.out.println("Invalid username or password.");
            return null;
        }
        System.out.println("Welcome, " + c.name + "!");
        return c;
    }

    static void openAccount() {
        System.out.print("Choose a username: ");
        String username = sc.nextLine();
        if (customersByUsername.containsKey(username)) {
            System.out.println("Username already taken. Try another.");
            return;
        }
        System.out.print("Choose a password: ");
        String password = sc.nextLine();

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Aadhaar Number: ");
        String aadhar = sc.nextLine();
        System.out.print("Enter Mobile Number: ");
        String mobile = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Address: ");
        String address = sc.nextLine();
        System.out.print("Enter Nominee Name: ");
        String nominee = sc.nextLine();
        System.out.print("Do you want ATM card? (yes/no): ");
        boolean hasATM = sc.nextLine().equalsIgnoreCase("yes");
        int atmPin = 0;
        if (hasATM) {
            atmPin = inputAtmPin();
        }
        System.out.print("Do you want Cheque Book? (yes/no): ");
        boolean hasChequeBook = sc.nextLine().equalsIgnoreCase("yes");

        Customer c = new Customer(nextAccount, username, password, name, aadhar, mobile, age, address, nominee, hasATM, hasChequeBook, atmPin);
        customersByAccount.put(nextAccount, c);
        customersByUsername.put(username, c);
        System.out.println("Account created successfully.");
        System.out.println("Your Account Number is: " + nextAccount);
        if (hasATM) System.out.println("ATM card issued with your chosen PIN.");
        if (hasChequeBook) System.out.println("Cheque Book will be issued.");
        nextAccount++;
        saveData();
    }

    static int inputAtmPin() {
        int pin = 0;
        while (true) {
            System.out.print("Set a 4-digit ATM PIN: ");
            String pinStr = sc.nextLine();
            if (pinStr.matches("\\d{4}")) {
                pin = Integer.parseInt(pinStr);
                break;
            } else {
                System.out.println("Invalid PIN format. Please enter exactly 4 digits.");
            }
        }
        return pin;
    }

    static void viewAllCustomers() {
        if (customersByAccount.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        System.out.println("\n--- All Customers ---");
        for (Customer c : customersByAccount.values()) {
            System.out.printf("Acc#: %d | Username: %s | Name: %s | Balance: ₹%.2f\n",
                    c.accountNumber, c.username, c.name, c.balance);
        }
    }

    // ----------- OTP/Verification Code -----------
    static boolean verifyOtp() {
        Random rand = new Random();
        int otp = 100000 + rand.nextInt(900000); // 6-digit code
        System.out.println("Verification code: " + otp); // In real apps, send via SMS/email
        System.out.print("Enter the verification code: ");
        int entered;
        try {
            entered = Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input. Transaction cancelled.");
            return false;
        }
        if (entered != otp) {
            System.out.println("Incorrect verification code. Transaction cancelled.");
            return false;
        }
        return true;
    }

    static void deposit(Customer c) {
        System.out.print("Enter deposit amount: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return ;
        }
        if (!verifyOtp()) return;
        c.balance += amt;
        System.out.printf("₹%.2f deposited. New balance: ₹%.2f\n", amt, c.balance);
        saveData();
    }

    static void withdraw(Customer c) {
        System.out.print("Enter withdrawal amount: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        if (amt > c.balance) {
            System.out.println("Insufficient balance.");
            return;
        }
        if (!verifyOtp()) return;
        c.balance -= amt;
        System.out.printf("₹%.2f withdrawn. New balance: ₹%.2f\n", amt, c.balance);
        saveData();
    }

    static void atmDeposit(Customer c) {
        if (!c.hasATM) {
            System.out.println("ATM card not issued. Cannot use ATM deposit.");
            return;
        }
        if (!verifyAtmPin(c)) return;
        System.out.print("Insert cash into ATM and enter deposit amount: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        if (amt <= 0) {
            System.out.println("Invalid amount entered.");
            return;
        }
        if (!verifyOtp()) return;
        System.out.println("Processing your deposit...");
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        c.balance += amt;
        System.out.printf("₹%.2f deposited via ATM. New balance: ₹%.2f\n", amt, c.balance);
        System.out.println("Thank you for using SBI ATM deposit.");
        saveData();
    }

    static void atmWithdrawal(Customer c) {
        if (!c.hasATM) {
            System.out.println("ATM card not issued. Cannot use ATM withdrawal.");
            return;
        }
        if (!verifyAtmPin(c)) return;
        System.out.print("Enter withdrawal amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        if (amount <= 0) {
            System.out.println("Invalid amount entered.");
            return;
        }
        if (amount > c.balance) {
            System.out.println("Insufficient balance. Withdrawal denied.");
            return;
        }
        if (!verifyOtp()) return;
        System.out.println("Processing your withdrawal...");
        try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        c.balance -= amount;
        System.out.printf("Please collect your cash: ₹%.2f\n", amount);
        System.out.printf("Remaining balance: ₹%.2f\n", c.balance);
        System.out.println("Thank you for using SBI ATM.");
        saveData();
    }

    static boolean verifyAtmPin(Customer c) {
        System.out.print("Enter your 4-digit ATM PIN: ");
        String pinStr = sc.nextLine();
        if (!pinStr.matches("\\d{4}")) {
            System.out.println("Invalid PIN format.");
            return false;
        }
        int enteredPin = Integer.parseInt(pinStr);
        if (enteredPin != c.atmPin) {
            System.out.println("Incorrect ATM PIN.");
            return false;
        }
        return true;
    }

    static void fundTransfer(Customer sender) {
        System.out.print("Enter beneficiary account number: ");
        long benAcc = sc.nextLong();
        sc.nextLine();
        Customer receiver = customersByAccount.get(benAcc);
        if (receiver == null) {
            System.out.println("Beneficiary account not found.");
            return;
        }
        System.out.print("Enter amount to transfer: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        if (amt > sender.balance) {
            System.out.println("Insufficient balance.");
            return;
        }
        if (!verifyOtp()) return;
        sender.balance -= amt;
        receiver.balance += amt;
        System.out.printf("₹%.2f transferred to %s (Acc#: %d).\n", amt, receiver.name, receiver.accountNumber);
        System.out.printf("Your new balance: ₹%.2f\n", sender.balance);
        saveData();
    }

    static void issueATM(Customer c) {
        if (c.hasATM) {
            System.out.println("ATM card already issued.");
        } else {
            c.hasATM = true;
            c.atmPin = inputAtmPin();
            System.out.println("ATM card issued with your chosen PIN.");
            saveData();
        }
    }

    static void issueChequeBook(Customer c) {
        if (c.hasChequeBook) {
            System.out.println("Cheque Book already issued.");
        } else {
            c.hasChequeBook = true;
            System.out.println("Cheque Book will be issued shortly.");
            saveData();
        }
    }

    static void changePassword(Customer c) {
        System.out.print("Enter current password: ");
        String currentPass = sc.nextLine();
        if (!c.password.equals(currentPass)) {
            System.out.println("Incorrect current password.");
            return;
        }
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine();
        System.out.print("Confirm new password: ");
        String confirmPass = sc.nextLine();
        if (!newPass.equals(confirmPass)) {
            System.out.println("Passwords do not match.");
            return;
        }
        c.password = newPass;
        System.out.println("Password changed successfully.");
        saveData();
    }

    static void changeAtmPin(Customer c) {
        if (!c.hasATM) {
            System.out.println("ATM card not issued. Cannot change PIN.");
            return;
        }
        System.out.print("Enter current ATM PIN: ");
        String currentPinStr = sc.nextLine();
        if (!currentPinStr.matches("\\d{4}") || Integer.parseInt(currentPinStr) != c.atmPin) {
            System.out.println("Incorrect current ATM PIN.");
            return;
        }
        int newPin = inputAtmPin();
        c.atmPin = newPin;
        System.out.println("ATM PIN changed successfully.");
        saveData();
    }

    static void showDetails(Customer c) {
        System.out.println("\n--- Account Details ---");
        System.out.println("Account Number: " + c.accountNumber);
        System.out.println("Username: " + c.username);
        System.out.println("Name: " + c.name);
        System.out.println("Aadhaar Number: " + c.aadhar);
        System.out.println("Mobile Number: " + c.mobile);
        System.out.println("Age: " + c.age);
        System.out.println("Address: " + c.address);
        System.out.println("Nominee Name: " + c.nominee);
        System.out.println("ATM Card: " + (c.hasATM ? "Yes" : "No"));
        System.out.println("Cheque Book: " + (c.hasChequeBook ? "Yes" : "No"));
        System.out.printf("Balance: ₹%.2f\n", c.balance);
    }

    static void checkBalance(Customer c) {
        System.out.printf("Your current balance: ₹%.2f\n", c.balance);
    }

    // ----------- Cheque Deposit/Withdrawal ------------

    static void chequeDeposit(Customer c) {
        if (!c.hasChequeBook) {
            System.out.println("You do not have a cheque book issued.");
            return;
        }
        System.out.print("Enter cheque number (6 digits): ");
        String chequeNo = sc.nextLine();
        if (!chequeNo.matches("\\d{6}")) {
            System.out.println("Invalid cheque number format.");
            return;
        }
        System.out.print("Enter deposit amount: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        if (!verifyOtp()) return;
        c.balance += amt;
        System.out.printf("Cheque #%s deposited: ₹%.2f\nNew balance: ₹%.2f\n", chequeNo, amt, c.balance);
        saveData();
    }

    static void chequeWithdrawal(Customer c) {
        if (!c.hasChequeBook) {
            System.out.println("You do not have a cheque book issued.");
            return;
        }
        System.out.print("Enter cheque number (6 digits): ");
        String chequeNo = sc.nextLine();
        if (!chequeNo.matches("\\d{6}")) {
            System.out.println("Invalid cheque number format.");
            return;
        }
        System.out.print("Enter withdrawal amount: ");
        double amt = sc.nextDouble();
        sc.nextLine();
        if (amt <= 0) {
            System.out.println("Invalid amount.");
            return;
        }
        if (amt > c.balance) {
            System.out.println("Insufficient balance.");
            return;
        }
        if (!verifyOtp()) return;
        c.balance -= amt;
        System.out.printf("Cheque #%s withdrawn: ₹%.2f\nNew balance: ₹%.2f\n", chequeNo, amt, c.balance);
        saveData();
    }

    // --------- File Handling Methods ---------
    static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(new DataStorage(customersByAccount, customersByUsername, nextAccount));
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    static void loadData() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            DataStorage ds = (DataStorage) ois.readObject();
            customersByAccount = ds.customersByAccount;
            customersByUsername = ds.customersByUsername;
            nextAccount = ds.nextAccount;
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}