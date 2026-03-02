import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryManager {

    // productId -> stock count
    private final ConcurrentHashMap<String, AtomicInteger> inventory;

    // productId -> waiting list (FIFO)
    private final ConcurrentHashMap<String, Queue<Integer>> waitingList;

    public InventoryManager() {
        inventory = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // add product with stock
    public void addProduct(String productId, int stock) {
        inventory.put(productId, new AtomicInteger(stock));
        waitingList.put(productId, new ConcurrentLinkedQueue<>());
    }

    // check stock in O(1)
    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return stock != null ? stock.get() : -1;
    }

    // purchase item (thread-safe)
    public synchronized String purchaseItem(String productId, int userId) {

        if (!inventory.containsKey(productId)) {
            return "Product does not exist";
        }

        AtomicInteger stock = inventory.get(productId);

        // if stock available
        if (stock.get() > 0) {

            int remaining = stock.decrementAndGet();

            return "Purchase successful for User "
                    + userId + ". Remaining stock: " + remaining;
        }

        // add to waiting list
        Queue<Integer> queue = waitingList.get(productId);
        queue.add(userId);

        return "Out of stock. User " + userId +
                " added to waiting list at position " + queue.size();
    }

    // restock product
    public synchronized void restock(String productId, int quantity) {

        if (!inventory.containsKey(productId)) {
            return;
        }

        AtomicInteger stock = inventory.get(productId);
        Queue<Integer> queue = waitingList.get(productId);

        stock.addAndGet(quantity);

        // allocate stock to waiting list users
        while (stock.get() > 0 && !queue.isEmpty()) {

            int userId = queue.poll();
            stock.decrementAndGet();

            System.out.println("Waiting list user " + userId +
                    " purchase completed.");
        }
    }

    // display waiting list
    public void showWaitingList(String productId) {

        Queue<Integer> queue = waitingList.get(productId);

        if (queue == null || queue.isEmpty()) {
            System.out.println("No waiting list.");
            return;
        }

        System.out.println("Waiting list for " + productId + ":");
        for (Integer user : queue) {
            System.out.println("User: " + user);
        }
    }

    // main method (testing)
    public static void main(String[] args) {

        InventoryManager manager = new InventoryManager();

        // add product
        manager.addProduct("IPHONE15_256GB", 3);

        // check stock
        System.out.println("Stock: "
                + manager.checkStock("IPHONE15_256GB"));

        // simulate purchases
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 101));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 102));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 103));

        // out of stock users
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 104));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 105));

        // show waiting list
        manager.showWaitingList("IPHONE15_256GB");

        // restock
        System.out.println("\nRestocking 2 units...");
        manager.restock("IPHONE15_256GB", 2);

        // check stock again
        System.out.println("Final stock: "
                + manager.checkStock("IPHONE15_256GB"));
    }
}