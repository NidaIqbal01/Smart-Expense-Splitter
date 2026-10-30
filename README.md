# Smart Expense Splitter

A console-based Java application designed to manage group expenses, split bills accurately among participants, and track category-wise summaries using precise financial calculations (`BigDecimal`).

## Features

* **Group Member Registration:** Register and manage a list of unique group members before tracking expenses.
* **Add Expenses:** Record new expenses with details such as title, amount, payer, category, and participating members.
* **Search Expenses:** Find specific expense records either by entering a unique **Expense ID** or by searching keywords in the **Title**.
* **Update & Delete Expenses:** Modify existing expense details or remove records safely accurately.
* **Expense Summaries:** View overall group expenses, category-wise breakdowns, and individual participant share distributions.
* **Precise Share Calculation:** Splits amounts accurately down to the lowest currency unit (e.g., paisa) to prevent rounding discrepancies.

---

## Project Structure

* `ExpenseService.java`: Main program file containing the core application logic, menus, validations, and calculation algorithms.
* `prompt_log.md`: Development log tracking AI assistance and troubleshooting steps during implementation.

---

## Technologies Used

* **Java** (JDK)
* **Java Collections** (`ArrayList`, `List`)
* **Java Math** (`BigDecimal`, `RoundingMode`)

---

## How to Run the Application

1. Open your terminal or command prompt inside the project directory.
2. Compile the Java source file:
   ```bash
   javac ExpenseService.java
