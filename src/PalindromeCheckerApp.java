import java.util.Scanner;

class Node {
    char data;
    Node next;

    Node(char data) {
        this.data = data;
        this.next = null;
    }
}

public class PalindromeCheckerApp {

    // Insert node at end
    static Node insert(Node head, char data) {
        Node newNode = new Node(data);

        if (head == null)
            return newNode;

        Node temp = head;
        while (temp.next != null)
            temp = temp.next;

        temp.next = newNode;
        return head;
    }

    // Reverse linked list
    static Node reverse(Node head) {
        Node prev = null;
        Node curr = head;

        while (curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    // Palindrome check
    static boolean isPalindrome(Node head) {
        if (head == null || head.next == null)
            return true;

        Node slow = head, fast = head;

        // Find middle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        // Reverse second half
        Node secondHalf = reverse(slow);
        Node firstHalf = head;

        // Compare halves
        while (secondHalf != null) {
            if (firstHalf.data != secondHalf.data)
                return false;

            firstHalf = firstHalf.next;
            secondHalf = secondHalf.next;
        }

        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Input Text: ");
        String text = sc.nextLine();

        Node head = null;

        // Convert string to linked list
        for (char c : text.toCharArray())
            head = insert(head, c);

        System.out.println("Is palindrome? : " + isPalindrome(head));

        sc.close();
    }
}