import java.util.*;
import java.util.concurrent.*;

class PageViewEvent {

    String url;
    String userId;
    String source;
    long timestamp;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }
}

class RealTimeAnalyticsDashboard {

    // Total visit count per page
    private Map<String, Integer> pageVisitCount;

    // Unique visitors per page
    private Map<String, Set<String>> uniqueVisitors;

    // Traffic source count
    private Map<String, Integer> sourceCount;

    // Constructor
    public RealTimeAnalyticsDashboard() {

        pageVisitCount = new HashMap<>();
        uniqueVisitors = new HashMap<>();
        sourceCount = new HashMap<>();
    }

    // Process incoming event in O(1)
    public void processEvent(PageViewEvent event) {

        // Update page visit count
        pageVisitCount.put(
                event.url,
                pageVisitCount.getOrDefault(event.url, 0) + 1
        );

        // Update unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Update traffic source count
        sourceCount.put(
                event.source,
                sourceCount.getOrDefault(event.source, 0) + 1
        );
    }

    // Get Top N pages using PriorityQueue O(n log k)
    private List<Map.Entry<String, Integer>> getTopPages(int k) {

        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : pageVisitCount.entrySet()) {

            minHeap.offer(entry);

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(minHeap);

        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    // Display dashboard
    public void displayDashboard() {

        System.out.println("\n===== REAL-TIME ANALYTICS DASHBOARD =====");

        // Top Pages
        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages(10);

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String url = entry.getKey();
            int visits = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.printf(
                    "%d. %s - %,d views (%d unique visitors)\n",
                    rank++, url, visits, unique
            );
        }

        // Traffic Sources
        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : sourceCount.entrySet()) {

            System.out.printf(
                    "%s: %,d visits\n",
                    entry.getKey(),
                    entry.getValue()
            );
        }

        System.out.println("=========================================\n");
    }

    // Simulate real-time events
    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalyticsDashboard dashboard =
                new RealTimeAnalyticsDashboard();

        String[] urls = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-revolution",
                "/world/politics",
                "/entertainment/movie-review"
        };

        String[] users = {
                "user_101", "user_102", "user_103",
                "user_104", "user_105", "user_106"
        };

        String[] sources = {
                "Google", "Facebook", "Direct", "Twitter", "LinkedIn"
        };

        Random random = new Random();

        // Simulate streaming events
        for (int i = 1; i <= 50; i++) {

            PageViewEvent event = new PageViewEvent(
                    urls[random.nextInt(urls.length)],
                    users[random.nextInt(users.length)],
                    sources[random.nextInt(sources.length)]
            );

            dashboard.processEvent(event);

            // Update dashboard every 5 events (simulate 5 seconds)
            if (i % 5 == 0) {

                dashboard.displayDashboard();

                Thread.sleep(1000); // simulate delay
            }
        }
    }
}