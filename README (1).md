
# SBI Banking System – Java Project

## 📌 Overview
A secure, interactive desktop-based banking system built using Java SE, Swing for GUI, JDBC for database connectivity, and MySQL for data storage. The system simulates core banking operations such as account creation, login, fund transfer, and transaction history.

---

## 🛠 Technologies Used
- **Java SE 17**
- **Swing (Java GUI Toolkit)**
- **JDBC API**
- **MySQL 8.x**
- **NetBeans / Eclipse IDE**

---

## ✅ Features
- User Registration & Secure Login
- Account Creation (Savings / Current)
- Balance Inquiry, Deposits, Withdrawals
- Fund Transfer with OTP simulation
- Mini Statement and Transaction Reports
- Admin Panel for User Management
- Data Validation & Error Handling
- Clean, Modular Code with MVC-like structure

---

## 🚀 Setup Instructions

### Prerequisites:
- Java JDK installed
- MySQL Server
- MySQL Connector/J JDBC driver
- IDE (Eclipse or NetBeans)

### Steps:
1. Clone or download the project folder.
2. Import into your IDE.
3. Import `sbi_bank.sql` file to create required tables in MySQL.
4. Update DB credentials in `DBConnection.java`.
5. Run `Main.java` to start the application.

---

## 🧩 Database Schema Overview

**Tables:**
- `users` – Stores login info
- `accounts` – Holds customer account data
- `transactions` – Tracks deposit/withdrawal/transfer history
- `admins` – Admin credentials

---

## 🧪 Testing
Includes manual test cases for:
- Login validation
- Withdraw with low balance
- Fund transfer error handling

---

## 📘 Documentation
- Inline JavaDocs
- UML Class and Use Case Diagrams
- Flowcharts for system logic

---

## 📈 Future Enhancements
- Mobile App Integration
- Biometric Authentication
- Fraud Detection with AI

---

## 🙌 Author
- Udit Dixit

---



