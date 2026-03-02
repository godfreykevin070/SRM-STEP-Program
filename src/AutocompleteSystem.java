import java.util.*;

// Trie Node
class TrieNode {

    Map<Character, TrieNode> children;

    // Stores query → frequency for this prefix
    Map<String, Integer> queryFrequencyMap;

    public TrieNode() {
        children = new HashMap<>();
        queryFrequencyMap = new HashMap<>();
    }
}

// Main Autocomplete System
class AutocompleteSystem {

    private TrieNode root;

    // Global frequency storage
    private Map<String, Integer> globalFrequency;

    public AutocompleteSystem() {
        root = new TrieNode();
        globalFrequency = new HashMap<>();
    }

    // Insert or update query
    public void insertQuery(String query) {

        globalFrequency.put(
                query,
                globalFrequency.getOrDefault(query, 0) + 1
        );

        int frequency = globalFrequency.get(query);

        TrieNode current = root;

        for (char c : query.toCharArray()) {

            current.children.putIfAbsent(c, new TrieNode());

            current = current.children.get(c);

            current.queryFrequencyMap.put(query, frequency);
        }
    }

    // Get top K suggestions
    public List<String> search(String prefix, int k) {

        TrieNode current = root;

        for (char c : prefix.toCharArray()) {

            if (!current.children.containsKey(c)) {
                return new ArrayList<>();
            }

            current = current.children.get(c);
        }

        // Min heap based on frequency
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(
                        Comparator.comparingInt(Map.Entry::getValue)
                );

        for (Map.Entry<String, Integer> entry :
                current.queryFrequencyMap.entrySet()) {

            minHeap.offer(entry);

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<String> result = new ArrayList<>();

        while (!minHeap.isEmpty()) {
            result.add(minHeap.poll().getKey());
        }

        Collections.reverse(result);

        return result;
    }

    // Update frequency when search happens
    public void updateFrequency(String query) {

        insertQuery(query);

        System.out.println(
                query + " frequency updated to " +
                        globalFrequency.get(query)
        );
    }

    // Display suggestions with frequency
    public void displaySuggestions(String prefix) {

        List<String> suggestions = search(prefix, 10);

        System.out.println("\nSuggestions for \"" + prefix + "\":");

        for (String query : suggestions) {

            System.out.println(
                    query + " (" +
                            globalFrequency.get(query) +
                            " searches)"
            );
        }
    }

    // Main method for testing
    public static void main(String[] args) {

        AutocompleteSystem system =
                new AutocompleteSystem();

        // Insert queries
        system.insertQuery("java tutorial");
        system.insertQuery("javascript tutorial");
        system.insertQuery("java download");
        system.insertQuery("java 21 features");
        system.insertQuery("java tutorial");
        system.insertQuery("java tutorial");
        system.insertQuery("javascript tutorial");
        system.insertQuery("java compiler");
        system.insertQuery("java course");
        system.insertQuery("java coding");

        // Search prefix
        system.displaySuggestions("jav");

        // Update frequency
        system.updateFrequency("java 21 features");
        system.updateFrequency("java 21 features");

        system.displaySuggestions("java");
    }
}