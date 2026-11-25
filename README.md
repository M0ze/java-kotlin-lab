# expense-tracker — console expense tracker with file persistence (Java P1)

A console app that lets you add, list, and delete expenses, saved to a JSON
file. Teaches: JVM, collections, file I/O, JSON parsing, exceptions.

## How to run

    cd expense-tracker
    javac -d out src/*.java
    java -cp out com.expense.ExpenseTracker

## Files

    src/ExpenseTracker.java   main entry
    src/Expense.java          record type
    src/ExpenseStore.java     JSON file persistence (hand-rolled)

## Roadmap

| # | Project | Language | Core concept |
|---|---------|----------|--------------|
| 1 | expense-tracker | Java | JVM, collections, file I/O |
| 2 | spring-api | Java | DI, annotations, REST, JPA/H2 |
| 3 | android-app | Kotlin | Compose, ViewModel, coroutines |

Run with:

    java -cp out com.expense.ExpenseTracker
