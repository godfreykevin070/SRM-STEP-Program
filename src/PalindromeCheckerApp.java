import java.util.Scanner;

public class PalindromeCheckerApp {

    static boolean isPalindrome(String text) {

        // Normalize string
        String clean = text.replaceAll("\\s+", "").toLowerCase();

        int start = 0;
        int end = clean.length() - 1;

        while (start < end) {
            if (clean.charAt(start) != clean.charAt(end))
                return false;

            start++;
            end--;
        }
        return true;
    }

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Input Text: ");
        String text = input.nextLine();

        System.out.println("Is it a palindrome? : " + isPalindrome(text));

        input.close();
    }
}