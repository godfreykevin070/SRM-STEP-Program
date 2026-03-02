import java.util.*;

class PlagiarismDetector {

    // n-gram size
    private static final int N = 5;

    // n-gram → set of document IDs
    private Map<String, Set<String>> nGramIndex;

    // document → list of its n-grams
    private Map<String, List<String>> documentNGrams;

    public PlagiarismDetector() {
        nGramIndex = new HashMap<>();
        documentNGrams = new HashMap<>();
    }

    // Extract n-grams from text
    private List<String> extractNGrams(String text) {
        List<String> nGrams = new ArrayList<>();

        String[] words = text.toLowerCase().replaceAll("[^a-z0-9 ]", "").split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }
            nGrams.add(sb.toString().trim());
        }

        return nGrams;
    }

    // Add document to system
    public void addDocument(String docId, String text) {

        List<String> nGrams = extractNGrams(text);

        documentNGrams.put(docId, nGrams);

        for (String nGram : nGrams) {
            nGramIndex.putIfAbsent(nGram, new HashSet<>());
            nGramIndex.get(nGram).add(docId);
        }

        System.out.println("Indexed document: " + docId +
                " (" + nGrams.size() + " n-grams)");
    }

    // Analyze document for plagiarism
    public void analyzeDocument(String docId) {

        List<String> nGrams = documentNGrams.get(docId);

        if (nGrams == null) {
            System.out.println("Document not found.");
            return;
        }

        Map<String, Integer> matchCount = new HashMap<>();

        // Count matching n-grams
        for (String nGram : nGrams) {

            Set<String> docs = nGramIndex.get(nGram);

            if (docs != null) {
                for (String otherDoc : docs) {

                    if (!otherDoc.equals(docId)) {
                        matchCount.put(otherDoc,
                                matchCount.getOrDefault(otherDoc, 0) + 1);
                    }
                }
            }
        }

        System.out.println("\nAnalyzing: " + docId);
        System.out.println("Extracted " + nGrams.size() + " n-grams");

        // Calculate similarity
        for (String otherDoc : matchCount.keySet()) {

            int matches = matchCount.get(otherDoc);

            double similarity = (matches * 100.0) / nGrams.size();

            System.out.printf(
                    "Found %d matching n-grams with \"%s\"\n",
                    matches, otherDoc);

            System.out.printf(
                    "Similarity: %.2f%% %s\n",
                    similarity,
                    similarity >= 60 ? "(PLAGIARISM DETECTED)"
                            : similarity >= 15 ? "(Suspicious)"
                            : "(Safe)"
            );

            System.out.println();
        }
    }

    // Benchmark hash vs linear search
    public void benchmarkSearch(String nGram) {

        long start = System.nanoTime();

        boolean found = nGramIndex.containsKey(nGram);

        long end = System.nanoTime();

        System.out.println("Hash search time: " +
                (end - start) / 1_000_000.0 + " ms");
    }

    // Main method
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 =
                "Artificial intelligence is transforming the world. " +
                        "Machine learning enables computers to learn from data. " +
                        "Deep learning is a subset of machine learning.";

        String essay2 =
                "Machine learning enables computers to learn from data. " +
                        "Deep learning is a subset of machine learning. " +
                        "Artificial intelligence is transforming industries.";

        String essay3 =
                "The quick brown fox jumps over the lazy dog. " +
                        "This sentence is unrelated to artificial intelligence.";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay3);

        detector.analyzeDocument("essay_123.txt");
        detector.analyzeDocument("essay_092.txt");
    }
}