import java.util.Scanner;

public class PalindromeCheckerApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Input Text: ");
        String text = input.nextLine();

        // Convert String to Character Array
        char[] characters = text.toCharArray();

        // Two-pointer approach
        int start = 0;
        int end = characters.length - 1;
        boolean isPalindrome = true;

        while (start < end) {
            if (characters[start] != characters[end]) {
                isPalindrome = false;
                break;
            }
            start++;
            end--;
        }

        System.out.println("Is it a palindrome? : " + isPalindrome);

        input.close();
    }
}