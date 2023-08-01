package budget;

import java.util.Scanner;

public class Expense implements Comparable<Expense> {
    String product;
    float price;
    ExpenseCategory category;

    public Expense(String product, float price, ExpenseCategory category) {
        this.product = product;
        this.price = price;
        this.category = category;
    }

    @Override
    public String toString() {
        return "%s $%.2f".formatted(product, price);
    }

    public String serialize() {
        return "%s %s $%.2f\n".formatted(category.name(), product, price);
    }

    public static Expense deserialize(String string) {
        var categoryAndRemainder = string.split(" ", 2);
        var category = ExpenseCategory.valueOf(categoryAndRemainder[0]);
        var productAndPrice = categoryAndRemainder[1].split(" \\$(?=[\\d.]+$)");
        return new Expense(productAndPrice[0], Float.parseFloat(productAndPrice[1]), category);
    }

    @Override
    public int compareTo(Expense that) {
        return that == null ? 1 : Float.compare(this.price, that.price);
    }
}
