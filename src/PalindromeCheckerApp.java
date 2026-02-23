import java.util.Scanner;

public class PalindromeCheckerApp {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.print("Input Text: ");
        String original = input.nextLine();   // Full line input

        String reversed = "";

        // Reverse string using for loop
        for (int i = original.length() - 1; i >= 0; i--) {
            reversed = reversed + original.charAt(i);  // String Concatenation
        }

        // Compare using equals()
        if (original.equals(reversed)) {
            System.out.println("Reversed Text : " + reversed);
            System.out.println("Is it a palindrome? : true");
        } else {
            System.out.println("Reversed Text : " + reversed);
            System.out.println("Is it a palindrome? : false");
        }

        input.close();
    }
}