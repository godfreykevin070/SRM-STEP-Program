import java.util.Scanner;

public class PalindromeCheckerApp {

    // Recursive palindrome function
    static boolean isPalindrome(String text, int start, int end) {

        // Base condition
        if (start >= end)
            return true;

        // Mismatch condition
        if (text.charAt(start) != text.charAt(end))
            return false;

        // Recursive call
        return isPalindrome(text, start + 1, end - 1);
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Input Text: ");
        String text = input.nextLine();

        boolean result = isPalindrome(text, 0, text.length() - 1);

        System.out.println("Is it a palindrome? : " + result);

        input.close();
    }
}