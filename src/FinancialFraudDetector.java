import java.util.*;

// Transaction class
class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long timestamp;

    public Transaction(int id, int amount, String merchant,
                       String account, long timestamp) {

        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "(ID:" + id + ", Amount:" + amount +
                ", Merchant:" + merchant +
                ", Account:" + account + ")";
    }
}

// Fraud Detection System
public class FinancialFraudDetector {

    private List<Transaction> transactions;

    public FinancialFraudDetector() {
        transactions = new ArrayList<>();
    }

    // Add transaction
    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum (O(n))
    public List<List<Transaction>> findTwoSum(int target) {

        Map<Integer, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                result.add(
                        Arrays.asList(map.get(complement), t)
                );
            }

            map.put(t.amount, t);
        }

        return result;
    }

    // Two-Sum with time window (1 hour)
    public List<List<Transaction>> findTwoSumWithTimeWindow(
            int target, long windowMillis) {

        Map<Integer, List<Transaction>> map =
                new HashMap<>();

        List<List<Transaction>> result =
                new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                for (Transaction prev :
                        map.get(complement)) {

                    if (Math.abs(
                            t.timestamp - prev.timestamp
                    ) <= windowMillis) {

                        result.add(
                                Arrays.asList(prev, t)
                        );
                    }
                }
            }

            map.putIfAbsent(t.amount,
                    new ArrayList<>());

            map.get(t.amount).add(t);
        }

        return result;
    }

    // Duplicate detection
    public List<List<Transaction>> detectDuplicates() {

        Map<String, List<Transaction>> map =
                new HashMap<>();

        List<List<Transaction>> result =
                new ArrayList<>();

        for (Transaction t : transactions) {

            String key =
                    t.amount + "_" + t.merchant;

            map.putIfAbsent(key,
                    new ArrayList<>());

            map.get(key).add(t);
        }

        for (List<Transaction> list : map.values()) {

            Set<String> accounts =
                    new HashSet<>();

            for (Transaction t : list) {
                accounts.add(t.account);
            }

            if (accounts.size() > 1 &&
                    list.size() > 1) {

                result.add(list);
            }
        }

        return result;
    }

    // K-Sum (recursive)
    public List<List<Transaction>> findKSum(
            int k, int target) {

        List<List<Transaction>> result =
                new ArrayList<>();

        kSumHelper(
                new ArrayList<>(),
                0,
                k,
                target,
                result
        );

        return result;
    }

    private void kSumHelper(
            List<Transaction> current,
            int start,
            int k,
            int target,
            List<List<Transaction>> result) {

        if (k == 0 && target == 0) {

            result.add(
                    new ArrayList<>(current)
            );

            return;
        }

        if (k == 0 || target < 0)
            return;

        for (int i = start;
             i < transactions.size();
             i++) {

            Transaction t = transactions.get(i);

            current.add(t);

            kSumHelper(
                    current,
                    i + 1,
                    k - 1,
                    target - t.amount,
                    result
            );

            current.remove(
                    current.size() - 1
            );
        }
    }

    // Display results
    public void displayResults(
            List<List<Transaction>> results) {

        if (results.isEmpty()) {

            System.out.println("No matches found.");
            return;
        }

        for (List<Transaction> pair :
                results) {

            System.out.println(pair);
        }
    }

    // Main method
    public static void main(String[] args) {

        FinancialFraudDetector detector =
                new FinancialFraudDetector();

        long now =
                System.currentTimeMillis();

        detector.addTransaction(
                new Transaction(
                        1, 500,
                        "StoreA",
                        "ACC1",
                        now
                ));

        detector.addTransaction(
                new Transaction(
                        2, 300,
                        "StoreB",
                        "ACC2",
                        now + 1000
                ));

        detector.addTransaction(
                new Transaction(
                        3, 200,
                        "StoreC",
                        "ACC3",
                        now + 2000
                ));

        detector.addTransaction(
                new Transaction(
                        4, 500,
                        "StoreA",
                        "ACC4",
                        now + 3000
                ));

        System.out.println("\nTwo Sum (Target 500):");

        detector.displayResults(
                detector.findTwoSum(500)
        );

        System.out.println(
                "\nTwo Sum with Time Window:");

        detector.displayResults(
                detector.findTwoSumWithTimeWindow(
                        500,
                        3600000
                )
        );

        System.out.println(
                "\nDuplicate Transactions:");

        detector.displayResults(
                detector.detectDuplicates()
        );

        System.out.println(
                "\nK-Sum (k=3, target=1000):");

        detector.displayResults(
                detector.findKSum(3, 1000)
        );
    }
}