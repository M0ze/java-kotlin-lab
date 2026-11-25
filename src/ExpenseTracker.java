package com.expense;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ExpenseTracker {

    private static final Path STORE_FILE = Paths.get("expenses.json");
    private static ExpenseStore store;
    private static Scanner scanner;

    public static void main(String[] args) {
        store = new ExpenseStore(STORE_FILE);
        scanner = new Scanner(System.in);
        int choice;

        do {
            printMenu();
            choice = readInt("> ");
            handle(choice);
        } while (choice != 4);

        System.out.println("bye");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n=== Expense Tracker ===");
        System.out.println("1) Add expense");
        System.out.println("2) List expenses");
        System.out.println("3) Delete expense");
        System.out.println("4) Quit");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException ignored) {
                System.out.println("enter a number");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static void handle(int choice) {
        switch (choice) {
            case 1 -> {
                String desc = readLine("description: ");
                double amount = readDouble("amount: ");
                store.add(new Expense(desc, amount));
                System.out.println("added");
            }
            case 2 -> showAll();
            case 3 -> {
                if (store.all().isEmpty()) {
                    System.out.println("(empty)");
                    return;
                }
                List<Expense> expenses = store.all();
                for (int i = 0; i < expenses.size(); i++) {
                    Expense e = expenses.get(i);
                    System.out.printf(" %d) %s: $%.2f%n", i + 1, e.desc(), e.amount());
                }
                int idx = readInt("delete #: ");
                if (idx >= 1 && idx <= expenses.size()) {
                    store.delete(idx - 1);
                    System.out.println("deleted");
                } else {
                    System.out.println("no such item");
                }
            }
            default -> {
            }
        }
    }

    private static void showAll() {
        List<Expense> expenses = store.all();
        if (expenses.isEmpty()) {
            System.out.println("(empty)");
            return;
        }
        expenses.stream()
                .sorted((a, b) -> Double.compare(b.amount(), a.amount()))
                .forEach(e -> System.out.printf("  %s: $%.2f%n", e.desc(), e.amount()));
    }

    private static double readDouble(String prompt) {
        while (true) {
            String line = readLine(prompt);
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("enter a number");
            }
        }
    }
}
