# Smart Expense Splitter

## Project Overview

Smart Expense Splitter is a group expense management application designed to simplify the process of sharing expenses, calculating balances, and settling debts among group members.

The system allows users to create expense groups, manage members, record shared expenses, calculate individual contributions, and simplify payments. It also includes an AI-powered Budget Advisor that analyzes spending patterns and provides practical money-saving recommendations.

## Project Objectives

- Simplify group expense management.
- Maintain accurate records of shared expenses.
- Calculate individual shares and outstanding balances.
- Reduce the number of transactions required to settle debts.
- Provide AI-powered spending analysis and budget recommendations.
- Generate clear expense summaries and reports.

## Project Modules and Responsibilities

### Module 1: Group & Member Management
**Assigned to: Member 1 (Nida Iqbal)**

Main Class: `GroupService.java`

Responsibilities:
- Create and name expense groups.
- Add and remove group members.
- Validate duplicate and empty member or group names.
- Display the group member list.

### Module 2: Expense Management
**Assigned to: Member 2 (Izza Zia)**

Main Class: `ExpenseService.java`

Responsibilities:
- Add shared expenses.
- Enter expense amount, title, payer, and category.
- Select participants for splitting expenses.
- Validate and save expense records.

### Module 3: Balance Calculation & Debt Settlement
**Assigned to: Member 3 (Wajeeha Aqdas)**

Main Class: `BalanceService.java`

Responsibilities:
- Calculate each member's share of expenses.
- Calculate who owes money and who should receive money.
- Simplify debts into fewer transactions.
- Test calculations and handle rounding accurately.

### Module 4: AI Budget Advisor
**Assigned to: Member 4 (Fatima Bukhari)**

Main Class: `AIAdvisorService.java`

Responsibilities:
- Analyze spending summaries using a Large Language Model (LLM).
- Identify high-spending categories.
- Suggest practical ways to save money.
- Handle API errors and display AI-generated budget advice.

### Module 5: Dashboard & Reports
**Assigned to: Member 5 (Anfal Faruqi)**

Main Class: `DashboardPanel.java`

Responsibilities:
- Display total group expenditure.
- Show individual member balances.
- Generate category-wise expense summaries.
- Display transaction history.
- Integrate all modules through the main interface.

## Technologies Used

- **Programming Language:** Java
- **Version Control:** Git and GitHub
- **Development Environment:** Eclipse / IntelliJ IDEA / VS Code
- **AI Integration:** Large Language Model (LLM) API

## Project Structure

```text
Smart-Expense-Splitter/
│
├── src/
│   ├── GroupService.java
│   ├── ExpenseService.java
│   ├── BalanceService.java
│   ├── AIAdvisorService.java
│   ├── DashboardPanel.java
│   └── Main.java
│
├── README.md
├── .gitignore
└── CODE_PROMPT.md
```


## How the System Works

1. Display an interactive main menu to navigate system features and select actions.
2. Create an expense group.
3. Add members to the group.
4. Record shared expenses with their details.
5. Select participants who share each expense.
6. Calculate individual shares and outstanding balances.
7. Simplify debts to minimize transactions.
8. Analyze spending patterns using the AI Budget Advisor.
9. View expense summaries, balances, and reports through the dashboard.

## Team Members

| Member | Module | Main Class |
|---|---|---|
| Member 1 | Group & Member Management | GroupService.java |
| Member 2 | Expense Management | ExpenseService.java |
| Member 3 | Balance Calculation & Debt Settlement | BalanceService.java |
| Member 4 | AI Budget Advisor | AIAdvisorService.java |
| Member 5 | Dashboard & Reports | DashboardPanel.java |

## Version Control and Collaboration

This project uses Git and GitHub for collaborative development.

- Each member works on a separate feature branch.
- Changes are committed with meaningful commit messages.
- Pull Requests (PRs) are created to merge completed features.
- Team members review code before merging.
- The main branch maintains the integrated project version.

## Future Enhancements

- User authentication and profile management.
- Database integration for persistent expense records.
- Multiple expense-splitting methods.
- Export expense reports to PDF or Excel.
- Enhanced AI-powered financial insights.
- Notifications for pending payments.

## Terminal Output
<img width="678" height="391" alt="WhatsApp Image 2026-11-17 at 5 24 38 PM" src="https://github.com/user-attachments/assets/8a193e97-00d5-4c61-a1e4-6f7fd7524322" />
<img width="630" height="357" alt="WhatsApp Image 2026-11-17 at 5 24 50 PM" src="https://github.com/user-attachments/assets/3b6ea6ac-4a99-4380-b6e0-3e2b6b3c0629" />
<img width="635" height="323" alt="WhatsApp Image 2026-11-17 at 5 24 59 PM" src="https://github.com/user-attachments/assets/602d4fc0-78c4-44f1-b0e7-6c29eeefe224" />
<img width="627" height="362" alt="WhatsApp Image 2026-11-17 at 5 25 11 PM" src="https://github.com/user-attachments/assets/aa897ba3-5ec3-4bfe-a71a-5ae67555611f" />
<img width="712" height="666" alt="WhatsApp Image 2026-11-17 at 5 25 28 PM" src="https://github.com/user-attachments/assets/422432a6-384b-4689-bd2f-6be6da9a258a" />
<img width="636" height="637" alt="WhatsApp Image 2026-11-17 at 5 25 38 PM" src="https://github.com/user-attachments/assets/b55dd9f2-66a7-47f8-870c-d11caf0fb6e5" />
<img width="643" height="492" alt="WhatsApp Image 2026-11-17 at 5 25 50 PM" src="https://github.com/user-attachments/assets/27c62f7e-8712-4ef1-8e90-b227d4576563" />
<img width="622" height="372" alt="WhatsApp Image 2026-11-17 at 5 26 07 PM" src="https://github.com/user-attachments/assets/020b8605-9aa9-4a79-9f0e-eeb7318f209a" />
<img width="952" height="662" alt="WhatsApp Image 2026-11-17 at 5 26 16 PM" src="https://github.com/user-attachments/assets/98806245-535a-46c6-aee7-4fc787344e8e" />
<img width="632" height="368" alt="WhatsApp Image 2026-11-17 at 5 26 26 PM" src="https://github.com/user-attachments/assets/0403dc2f-1039-4877-9de3-bfdeed453313" />

## Conclusion

Smart Expense Splitter aims to make group expense management easier, more transparent, and efficient. By combining expense tracking, automated balance calculations, debt settlement, and AI-powered budgeting, the application provides a practical solution for managing shared finances.

---

**Project:** Smart Expense Splitter  
**Category:** Software Engineering / Java Application  
**Development Approach:** Collaborative Modular Development
