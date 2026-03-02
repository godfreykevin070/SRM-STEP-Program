import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class TokenBucket {

    private final long maxTokens;
    private final double refillRatePerMillis;

    private double tokens;
    private long lastRefillTime;

    public TokenBucket(long maxTokens, long refillPeriodMillis) {

        this.maxTokens = maxTokens;
        this.refillRatePerMillis = (double) maxTokens / refillPeriodMillis;

        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // Refill tokens based on elapsed time
    private synchronized void refill() {

        long now = System.currentTimeMillis();

        long elapsed = now - lastRefillTime;

        double tokensToAdd = elapsed * refillRatePerMillis;

        if (tokensToAdd > 0) {

            tokens = Math.min(maxTokens, tokens + tokensToAdd);

            lastRefillTime = now;
        }
    }

    // Try consuming one token
    public synchronized boolean allowRequest() {

        refill();

        if (tokens >= 1) {

            tokens--;

            return true;
        }

        return false;
    }

    public synchronized long getRemainingTokens() {

        refill();

        return (long) tokens;
    }

    public synchronized long getRetryAfterSeconds() {

        if (tokens >= 1) return 0;

        double missingTokens = 1 - tokens;

        return (long) (missingTokens / refillRatePerMillis / 1000);
    }
}

class RateLimiter {

    private final ConcurrentHashMap<String, TokenBucket> clientBuckets;

    private final long MAX_REQUESTS = 1000;
    private final long REFILL_PERIOD = 60 * 60 * 1000; // 1 hour

    public RateLimiter() {

        clientBuckets = new ConcurrentHashMap<>();
    }

    private TokenBucket getBucket(String clientId) {

        return clientBuckets.computeIfAbsent(
                clientId,
                id -> new TokenBucket(MAX_REQUESTS, REFILL_PERIOD)
        );
    }

    // Check rate limit in O(1)
    public void checkRateLimit(String clientId) {

        TokenBucket bucket = getBucket(clientId);

        if (bucket.allowRequest()) {

            System.out.println(
                    "Allowed (" +
                            bucket.getRemainingTokens() +
                            " requests remaining)"
            );
        }
        else {

            System.out.println(
                    "Denied (0 requests remaining, retry after " +
                            bucket.getRetryAfterSeconds() +
                            " seconds)"
            );
        }
    }

    // Get client status
    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = getBucket(clientId);

        long remaining = bucket.getRemainingTokens();

        long used = MAX_REQUESTS - remaining;

        long resetSeconds =
                (long)((MAX_REQUESTS - remaining) /
                        (MAX_REQUESTS / (REFILL_PERIOD / 1000)));

        System.out.println("\nRate Limit Status for " + clientId);
        System.out.println("Used: " + used);
        System.out.println("Limit: " + MAX_REQUESTS);
        System.out.println("Remaining: " + remaining);
        System.out.println("Reset in: " + resetSeconds + " seconds\n");
    }
}

public class DistributedRateLimiter {

    public static void main(String[] args)
            throws InterruptedException {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        // Simulate requests
        for (int i = 1; i <= 5; i++) {

            limiter.checkRateLimit(client);

            Thread.sleep(200);
        }

        limiter.getRateLimitStatus(client);

        // Simulate burst traffic
        System.out.println("\nSimulating burst traffic...");

        for (int i = 0; i < 1005; i++) {

            if (i >= 998 && i <= 1005)
                limiter.checkRateLimit(client);
        }
    }
}