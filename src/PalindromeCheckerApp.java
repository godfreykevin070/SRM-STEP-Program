import java.util.*;

// Strategy Interface
interface PalindromeStrategy {
    boolean check(String text);
}


// Stack Strategy
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


// Deque Strategy
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


// Two Pointer Strategy (Fastest baseline)
class TwoPointerStrategy implements PalindromeStrategy {

    @Override
    public boolean check(String text) {

        String clean = text.replaceAll("\\s+", "").toLowerCase();

        int start = 0;
        int end = clean.length() - 1;

        while (start < end) {
            if (clean.charAt(start) != clean.charAt(end)) {
                return false;
            }
            start++;
            end--;
        }

        return true;
    }
}


// Performance Tester
class PerformanceTester {

    public static void testStrategy(String name, PalindromeStrategy strategy, String text) {

        long startTime = System.nanoTime();

        boolean result = strategy.check(text);

        long endTime = System.nanoTime();

        long duration = endTime - startTime;

        System.out.println(name + " Result      : " + result);
        System.out.println(name + " Time (ns)   : " + duration);
        System.out.println(name + " Time (ms)   : " + duration / 1_000_000.0);
        System.out.println("----------------------------------");
    }
}


// Main Class
public class PalindromePerformanceApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter text: ");
        String text = input.nextLine();

        System.out.println("\nPerformance Comparison:\n");

        PerformanceTester.testStrategy(
                "Stack Strategy",
                new StackStrategy(),
                text
        );

        PerformanceTester.testStrategy(
                "Deque Strategy",
                new DequeStrategy(),
                text
        );

        PerformanceTester.testStrategy(
                "Two Pointer Strategy",
                new TwoPointerStrategy(),
                text
        );

        input.close();
    }
}