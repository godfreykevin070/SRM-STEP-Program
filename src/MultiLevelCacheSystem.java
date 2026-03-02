import java.util.*;

// Video Data class
class VideoData {

    String videoId;
    String content;
    int accessCount;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
        this.accessCount = 0;
    }
}

// Multi-Level Cache System
public class MultiLevelCacheSystem {

    // Cache capacities
    private final int L1_CAPACITY = 3;
    private final int L2_CAPACITY = 5;

    // L1 Cache (Memory) - LRU
    private LinkedHashMap<String, VideoData> L1Cache;

    // L2 Cache (SSD) - LRU
    private LinkedHashMap<String, VideoData> L2Cache;

    // L3 Cache (Database)
    private HashMap<String, VideoData> L3Database;

    // Statistics
    private int L1Hits = 0;
    private int L2Hits = 0;
    private int L3Hits = 0;

    private int totalRequests = 0;

    // Constructor
    public MultiLevelCacheSystem() {

        // L1 Cache with LRU
        L1Cache = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(
                    Map.Entry<String, VideoData> eldest) {

                if (size() > L1_CAPACITY) {

                    System.out.println(
                            "L1 EVICT → " + eldest.getKey());

                    L2Cache.put(
                            eldest.getKey(),
                            eldest.getValue());

                    return true;
                }

                return false;
            }
        };

        // L2 Cache with LRU
        L2Cache = new LinkedHashMap<>(L2_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(
                    Map.Entry<String, VideoData> eldest) {

                if (size() > L2_CAPACITY) {

                    System.out.println(
                            "L2 EVICT → " + eldest.getKey());

                    return true;
                }

                return false;
            }
        };

        // L3 Database
        L3Database = new HashMap<>();

        // Populate database
        for (int i = 1; i <= 10; i++) {

            L3Database.put(
                    "video_" + i,
                    new VideoData(
                            "video_" + i,
                            "Content of video " + i
                    ));
        }
    }

    // Get Video
    public VideoData getVideo(String videoId) {

        totalRequests++;

        long start = System.nanoTime();

        // L1 Cache check
        if (L1Cache.containsKey(videoId)) {

            L1Hits++;

            VideoData video = L1Cache.get(videoId);

            video.accessCount++;

            printResult(
                    "L1 Cache HIT",
                    videoId,
                    start
            );

            return video;
        }

        // L2 Cache check
        if (L2Cache.containsKey(videoId)) {

            L2Hits++;

            VideoData video = L2Cache.get(videoId);

            video.accessCount++;

            promoteToL1(video);

            printResult(
                    "L2 Cache HIT → Promoted to L1",
                    videoId,
                    start
            );

            return video;
        }

        // L3 Database check
        if (L3Database.containsKey(videoId)) {

            L3Hits++;

            VideoData video = L3Database.get(videoId);

            video.accessCount++;

            L2Cache.put(videoId, video);

            printResult(
                    "L3 Database HIT → Added to L2",
                    videoId,
                    start
            );

            return video;
        }

        printResult(
                "VIDEO NOT FOUND",
                videoId,
                start
        );

        return null;
    }

    // Promote video to L1
    private void promoteToL1(VideoData video) {

        L1Cache.put(video.videoId, video);
    }

    // Invalidate cache
    public void invalidate(String videoId) {

        L1Cache.remove(videoId);
        L2Cache.remove(videoId);
        L3Database.remove(videoId);

        System.out.println(
                "Cache INVALIDATED → " + videoId);
    }

    // Print result with timing
    private void printResult(
            String message,
            String videoId,
            long startTime) {

        long end = System.nanoTime();

        double timeMs =
                (end - startTime) / 1_000_000.0;

        System.out.println(
                message + ": " + videoId +
                        " (" +
                        String.format("%.4f", timeMs)
                        + " ms)"
        );
    }

    // Statistics
    public void getStatistics() {

        System.out.println("\nCache Statistics:");

        System.out.println(
                "Total Requests: " + totalRequests);

        System.out.println(
                "L1 Hits: " + L1Hits);

        System.out.println(
                "L2 Hits: " + L2Hits);

        System.out.println(
                "L3 Hits: " + L3Hits);

        double hitRate =
                ((L1Hits + L2Hits + L3Hits)
                        * 100.0) / totalRequests;

        System.out.println(
                "Overall Hit Rate: "
                        + String.format("%.2f", hitRate)
                        + "%"
        );
    }

    // Display cache contents
    public void displayCaches() {

        System.out.println("\nL1 Cache: "
                + L1Cache.keySet());

        System.out.println("L2 Cache: "
                + L2Cache.keySet());
    }

    // Main method
    public static void main(String[] args) {

        MultiLevelCacheSystem cache =
                new MultiLevelCacheSystem();

        cache.getVideo("video_1");
        cache.getVideo("video_2");
        cache.getVideo("video_3");

        cache.displayCaches();

        cache.getVideo("video_1"); // L2 → L1 promotion

        cache.getVideo("video_4");
        cache.getVideo("video_5");

        cache.displayCaches();

        cache.getVideo("video_1"); // L1 hit

        cache.invalidate("video_2");

        cache.getStatistics();
    }
}