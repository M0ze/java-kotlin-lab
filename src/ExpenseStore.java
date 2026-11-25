package com.expense;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExpenseStore {

    private final Path file;
    private final List<Expense> expenses = new ArrayList<>();

    public ExpenseStore(Path file) {
        this.file = file;
        load();
    }

    public void add(Expense e) {
        expenses.add(e);
        save();
    }

    public void delete(int index) {
        if (index < 0 || index >= expenses.size()) {
            throw new IndexOutOfBoundsException("no such item");
        }
        expenses.remove(index);
        save();
    }

    public List<Expense> all() {
        return new ArrayList<>(expenses);
    }

    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        try {
            String json = Files.readString(file);
            expenses.addAll(parse(json));
        } catch (IOException e) {
            System.err.println("could not read store: " + e.getMessage());
        }
    }

    private void save() {
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(file, toJson());
        } catch (IOException e) {
            System.err.println("could not write store: " + e.getMessage());
        }
    }

    private String toJson() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < expenses.size(); i++) {
            if (i > 0) sb.append(",");
            Expense e = expenses.get(i);
            sb.append("{\"desc\":").append(jsonString(e.desc())).append(",");
            sb.append("\"amount\":").append(e.amount()).append("}");
        }
        return sb.append("]").append("]").toString();
    }

    private String jsonString(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private List<Expense> parse(String json) {
        List<Expense> out = new ArrayList<>();
        int i = skipWhitespace(json, 0);
        if (i >= json.length() || json.charAt(i) != '[') {
            return out;
        }
        i = skipWhitespace(json, i + 1);
        if (i < json.length() && json.charAt(i) == ']') {
            return out;
        }
        while (i < json.length()) {
            if (json.charAt(i) == ',') {
                i = skipWhitespace(json, i + 1);
            }
            if (i < json.length() && json.charAt(i) == '{') {
                Expense e = parseObject(json, i);
                if (e != null) {
                    out.add(e);
                }
            }
            i = skipWhitespace(json, i + 1);
        }
        return out;
    }

    private Expense parseObject(String json, int start) {
        int i = skipWhitespace(json, start);
        if (i >= json.length() || json.charAt(i) != '{') {
            return null;
        }
        i = skipWhitespace(json, i + 1);
        String desc = parseString(json, i);
        if (desc == null) {
            return null;
        }
        i = skipWhitespace(json, i + desc.length() + 2);
        double amount = parseNumber(json, i);
        if (Double.isNaN(amount)) {
            return null;
        }
        i = skipWhitespace(json, i + Double.toString(amount).length());
        return new Expense(desc, amount);
    }

    private String parseString(String json, int i) {
        if (i >= json.length() || json.charAt(i) != '"') {
            return null;
        }
        i++;
        StringBuilder sb = new StringBuilder();
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == '"' || next == '\\') {
                    sb.append(next);
                    i += 2;
                    continue;
                }
            }
            if (c == '"') {
                return sb.toString();
            }
            sb.append(c);
            i++;
        }
        return null;
    }

    private double parseNumber(String json, int i) {
        int start = i;
        while (i < json.length() && (Character.isDigit(json.charAt(i)) || json.charAt(i) == '.')) {
            i++;
        }
        if (i == start) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(json.substring(start, i));
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    private int skipWhitespace(String s, int i) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
            i++;
        }
        return i;
    }
}
