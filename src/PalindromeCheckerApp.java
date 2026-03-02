import java.util.Scanner;
import java.util.Stack;

// PalindromeChecker class (Encapsulated Service)
class PalindromeChecker {

    private String text;

    // Constructor
    public PalindromeChecker(String text) {
        this.text = normalize(text);
    }

    // Private method for normalization (Encapsulation)
    private String normalize(String input) {
        return input.replaceAll("\\s+", "").toLowerCase();
    }

    // Public method to check palindrome using Stack
    public boolean checkPalindrome() {

        Stack<Character> stack = new Stack<>();

        // Push all characters into stack
        for (int i = 0; i < text.length(); i++) {
            stack.push(text.charAt(i));
        }

        // Compare with original
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != stack.pop()) {
                return false;
            }
        }

        return true;
    }
}


// Application class
public class PalindromeCheckerApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Input Text: ");
        String text = input.nextLine();

        // Create object (Encapsulation)
        PalindromeChecker checker = new PalindromeChecker(text);

        // Call service method
        boolean result = checker.checkPalindrome();

        System.out.println("Is it a palindrome? : " + result);

        input.close();
    }
}