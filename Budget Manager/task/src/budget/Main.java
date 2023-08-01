package budget;

import java.io.*;
import java.util.*;

import static budget.ExpenseCategory.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static float income = 0;
    static ArrayList<Expense> expenses = new ArrayList<>();

    public static void main(String[] args) {
        var state = MenuActions.MainMenu;
        while (state != MenuActions.Exit) {
            state = switch (state) {
                case Exit -> state;
                case MainMenu -> mainMenu();
                case AddIncome -> addIncome();
                case AddPurchase -> addPurchase();
                case ShowPurchases -> showPurchases();
                case ShowBalance -> showBalance();
                case Save -> save();
                case Load -> load();
                case Analyze -> analyze();
            };
            System.out.println();
        }
        System.out.println("Bye!");
    }

    private static MenuActions mainMenu() {
        System.out.println("""
                Choose your action:
                1) Add income
                2) Add purchase
                3) Show list of purchases
                4) Balance
                5) Save
                6) Load
                7) Analyze (Sort)
                0) Exit
                """);
        return switch (Integer.parseInt(scanner.nextLine())) {
            case 0 -> MenuActions.Exit;
            case 1 -> MenuActions.AddIncome;
            case 2 -> MenuActions.AddPurchase;
            case 3 -> MenuActions.ShowPurchases;
            case 4 -> MenuActions.ShowBalance;
            case 5 -> MenuActions.Save;
            case 6 -> MenuActions.Load;
            case 7 -> MenuActions.Analyze;
            default -> MenuActions.MainMenu;
        };
    }

    private static MenuActions analyze() {
        System.out.println("""
                How do you want to sort?
                1) Sort all purchases
                2) Sort by type
                3) Sort certain type
                4) Back
                """);
        var cats = new ExpenseCategory[]{Food, Entertainment, Clothes, Other};
        var list = switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> expenses.stream().sorted(Collections.reverseOrder()).map(Expense::toString);
            case 2 -> Arrays.stream(cats).map(category -> "%s - $%.2f".formatted(category.name(), expenses.stream().filter(e -> e.category == category).mapToDouble(e -> e.price).sum()));
            case 3 -> {
                System.out.println("""
                        Choose the type of purchase
                        1) Food
                        2) Clothes
                        3) Entertainment
                        4) Other
                        """);
                var type = switch (Integer.parseInt(scanner.nextLine())) {
                    case 1 -> Food;
                    case 2 -> Clothes;
                    case 3 -> Entertainment;
                    case 4 -> Other;
                    default -> All;
                };
                yield expenses.stream().filter(e -> e.category == type).sorted(Collections.reverseOrder()).map(Expense::toString);
            }
            default -> null;
        };
        if (list == null) {
            return MenuActions.MainMenu;
        }
        System.out.println();
        if (list.peek(System.out::println).toArray().length == 0) System.out.println("The purchase list is empty!");
        return MenuActions.Analyze;
    }

    private static MenuActions save() {
        try (var file = new FileWriter("purchases.txt")) {
            file.write("%.2f\n".formatted(income));
            for (Expense e : expenses) {
                file.write(e.serialize());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Purchases were saved!");
        return MenuActions.MainMenu;
    }

    private static MenuActions load() {
        var file = new File("purchases.txt");
        try (var s = new Scanner(file)) {
            income = Float.parseFloat(s.nextLine());
            while (s.hasNext()) {
                expenses.add(Expense.deserialize(s.nextLine()));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Purchases were loaded!");
        return MenuActions.MainMenu;
    }

    private static MenuActions addIncome() {
        System.out.println("Enter income:");
        income += Integer.parseInt(scanner.nextLine());
        System.out.println("Income was added!");
        return MenuActions.MainMenu;
    }

    private static MenuActions addPurchase() {
        System.out.println("""
                Choose the type of purchase
                1) Food
                2) Clothes
                3) Entertainment
                4) Other
                5) Back
                """);
        return switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> addPurchase(Food);
            case 2 -> addPurchase(ExpenseCategory.Clothes);
            case 3 -> addPurchase(ExpenseCategory.Entertainment);
            case 4 -> addPurchase(ExpenseCategory.Other);
            default -> MenuActions.MainMenu;
        };
    }

    private static MenuActions addPurchase(ExpenseCategory category) {
        System.out.println("Enter purchase name:");
        var name = scanner.nextLine();
        System.out.println("Enter its price:");
        var price = Float.parseFloat(scanner.nextLine());
        expenses.add(new Expense(name, price, category));
        System.out.println("Purchase was added!");
        return MenuActions.AddPurchase;
    }

    private static MenuActions showPurchases() {
        System.out.println("""
                Choose the type of purchases
                1) Food
                2) Clothes
                3) Entertainment
                4) Other
                5) All
                6) Back
                """);
        return switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> showPurchases(Food);
            case 2 -> showPurchases(ExpenseCategory.Clothes);
            case 3 -> showPurchases(ExpenseCategory.Entertainment);
            case 4 -> showPurchases(ExpenseCategory.Other);
            case 5 -> showPurchases(ExpenseCategory.All);
            default -> MenuActions.MainMenu;
        };
    }

    private static MenuActions showPurchases(ExpenseCategory category) {
        var e = expenses.stream();
        e = switch (category) {
            case Food, Clothes, Entertainment, Other -> e.filter(p -> p.category == category);
            case All -> e;
        };
        var expenses = e.toList();
        System.out.println(category.name() + ":");
        if (expenses.isEmpty()) {
            System.out.println("The purchase list is empty");
        } else {
            var total = expenses.stream().peek(System.out::println).mapToDouble(purchase -> purchase.price).sum();
            System.out.printf("Total sum: $%.2f\n", total);
        }
        return MenuActions.ShowPurchases;
    }

    private static MenuActions showBalance() {
        var total = expenses.stream().mapToDouble(expense -> expense.price).sum();
        System.out.printf("Balance: $%.2f\n", income - total);
        return MenuActions.MainMenu;
    }

}
