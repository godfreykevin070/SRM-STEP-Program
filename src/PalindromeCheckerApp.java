import java.util.*;

// Strategy Interface
interface PalindromeStrategy {
    boolean check(String text);
}


// Stack Strategy Implementation
class StackStrategy implements PalindromeStrategy {

    @Override
    public boolean check(String text) {

        String clean = text.replaceAll("\\s+", "").toLowerCase();

        Stack<Character> stack = new Stack<>();

        for (char c : clean.toCharArray()) {
            stack.push(c);
        }

        for (char c : clean.toCharArray()) {
            if (c != stack.pop()) {
                return false;
            }
        }

        return true;
    }
}


// Deque Strategy Implementation
class DequeStrategy implements PalindromeStrategy {

    @Override
    public boolean check(String text) {

        String clean = text.replaceAll("\\s+", "").toLowerCase();

        Deque<Character> deque = new ArrayDeque<>();

        for (char c : clean.toCharArray()) {
            deque.addLast(c);
        }

        while (deque.size() > 1) {
            if (!deque.removeFirst().equals(deque.removeLast())) {
                return false;
            }
        }

        return true;
    }
}


// Context Class
class PalindromeChecker {

    private PalindromeStrategy strategy;

    // Constructor Injection
    public PalindromeChecker(PalindromeStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean checkPalindrome(String text) {
        return strategy.check(text);
    }
}


// Main Application
public class PalindromeApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter text: ");
        String text = input.nextLine();

        System.out.println("\nChoose Strategy:");
        System.out.println("1. Stack Strategy");
        System.out.println("2. Deque Strategy");
        System.out.print("Enter choice: ");

        int choice = input.nextInt();

        PalindromeStrategy strategy;

        if (choice == 1) {
            strategy = new StackStrategy();
        } else {
            strategy = new DequeStrategy();
        }

        PalindromeChecker checker = new PalindromeChecker(strategy);

        boolean result = checker.checkPalindrome(text);

        System.out.println("\nIs Palindrome? : " + result);

        input.close();
    }
}