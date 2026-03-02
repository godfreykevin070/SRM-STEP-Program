import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UsernameChecker {

    // username -> userId
    private final ConcurrentHashMap<String, Integer> usernameMap;

    // username -> attempt frequency
    private final ConcurrentHashMap<String, AtomicInteger> attemptFrequency;

    // constructor
    public UsernameChecker() {
        usernameMap = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    // register username
    public boolean registerUsername(String username, int userId) {
        if (usernameMap.containsKey(username)) {
            return false;
        }
        usernameMap.put(username, userId);
        return true;
    }

    // check availability in O(1)
    public boolean checkAvailability(String username) {

        // increase attempt frequency
        attemptFrequency.putIfAbsent(username, new AtomicInteger(0));
        attemptFrequency.get(username).incrementAndGet();

        return !usernameMap.containsKey(username);
    }

    // suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        // append numbers
        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // replace underscore with dot
        if (username.contains("_")) {
            String dotVersion = username.replace("_", ".");
            if (!usernameMap.containsKey(dotVersion)) {
                suggestions.add(dotVersion);
            }
        }

        // add random number suffix
        String randomSuggestion = username + new Random().nextInt(1000);
        if (!usernameMap.containsKey(randomSuggestion)) {
            suggestions.add(randomSuggestion);
        }

        return suggestions;
    }

    // get most attempted username
    public String getMostAttemptedUsername() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, AtomicInteger> entry : attemptFrequency.entrySet()) {
            if (entry.getValue().get() > max) {
                max = entry.getValue().get();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }

    // display all registered usernames
    public void displayUsers() {
        System.out.println("Registered Users:");
        usernameMap.forEach((username, id) ->
                System.out.println(username + " -> UserID: " + id));
    }

    // main method (testing)
    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        // register users
        checker.registerUsername("john_doe", 101);
        checker.registerUsername("alice", 102);
        checker.registerUsername("admin", 103);

        // check availability
        System.out.println("john_doe available: " +
                checker.checkAvailability("john_doe"));

        System.out.println("jane_smith available: " +
                checker.checkAvailability("jane_smith"));

        // suggestions
        System.out.println("\nSuggestions for john_doe:");
        List<String> suggestions = checker.suggestAlternatives("john_doe");
        suggestions.forEach(System.out::println);

        // simulate attempts
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("john_doe");

        // most attempted
        System.out.println("\nMost attempted username: " +
                checker.getMostAttemptedUsername());

        // display users
        checker.displayUsers();
    }
}