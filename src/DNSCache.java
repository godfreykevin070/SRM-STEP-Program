import java.util.*;

public class DNSCache {

    // DNS Entry class
    static class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU Cache using LinkedHashMap
    private final LinkedHashMap<String, DNSEntry> cache;

    private final int maxSize;

    // statistics
    private int hits;
    private int misses;

    // constructor
    public DNSCache(int maxSize) {

        this.maxSize = maxSize;
        this.hits = 0;
        this.misses = 0;

        // accessOrder=true enables LRU
        this.cache = new LinkedHashMap<String, DNSEntry>(maxSize, 0.75f, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.maxSize;
            }
        };
    }

    // resolve domain
    public synchronized String resolve(String domain) {

        long startTime = System.nanoTime();

        // check cache
        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {

                hits++;

                double time = (System.nanoTime() - startTime) / 1_000_000.0;

                System.out.println("Cache HIT: " + domain +
                        " -> " + entry.ipAddress +
                        " (" + time + " ms)");

                return entry.ipAddress;
            }

            // expired
            cache.remove(domain);

            System.out.println("Cache EXPIRED: " + domain);
        }

        // cache miss
        misses++;

        String ip = queryUpstreamDNS(domain);

        // TTL = 5 seconds
        cache.put(domain, new DNSEntry(domain, ip, 5));

        double time = (System.nanoTime() - startTime) / 1_000_000.0;

        System.out.println("Cache MISS: " + domain +
                " -> " + ip +
                " (" + time + " ms)");

        return ip;
    }

    // simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        try {
            Thread.sleep(100); // simulate network delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Random rand = new Random();

        return rand.nextInt(256) + "." +
                rand.nextInt(256) + "." +
                rand.nextInt(256) + "." +
                rand.nextInt(256);
    }

    // remove expired entries
    public synchronized void cleanup() {

        Iterator<Map.Entry<String, DNSEntry>> iterator =
                cache.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, DNSEntry> entry = iterator.next();

            if (entry.getValue().isExpired()) {

                System.out.println("Removing expired: " + entry.getKey());

                iterator.remove();
            }
        }
    }

    // display cache safely
    public synchronized void displayCache() {

        System.out.println("\nCurrent Cache:");

        if (cache.isEmpty()) {
            System.out.println("Cache is empty");
            return;
        }

        for (Map.Entry<String, DNSEntry> entry : cache.entrySet()) {

            System.out.println(entry.getKey()
                    + " -> "
                    + entry.getValue().ipAddress);
        }
    }

    // display stats
    public synchronized void getStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);

        System.out.println("\nCache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    // main method
    public static void main(String[] args) throws InterruptedException {

        DNSCache dnsCache = new DNSCache(3);

        dnsCache.resolve("google.com");

        dnsCache.resolve("facebook.com");

        dnsCache.resolve("google.com"); // HIT

        Thread.sleep(6000); // wait for TTL expiry

        dnsCache.resolve("google.com"); // expired

        dnsCache.resolve("youtube.com");

        dnsCache.resolve("amazon.com"); // triggers LRU eviction

        dnsCache.displayCache();

        dnsCache.getStats();
    }
}