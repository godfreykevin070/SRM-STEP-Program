import java.util.Scanner;
import java.util.Stack;

public class PalindromeCheckerApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        Stack<Character> stack = new Stack<>();

        System.out.print("Input Text: ");
        String text = input.nextLine();

        // Push characters into stack
        for (int i = 0; i < text.length(); i++) {
            stack.push(text.charAt(i));   // Push Operation
        }

        boolean isPalindrome = true;

        // Pop and compare
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != stack.pop()) {   // Pop Operation
                isPalindrome = false;
                break;
            }
        }

        System.out.println("Is it a palindrome? : " + isPalindrome);

        input.close();
    }
}