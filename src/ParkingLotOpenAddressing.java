import java.util.*;

// Vehicle Entry class
class VehicleEntry {

    String licensePlate;
    long entryTime;

    public VehicleEntry(String licensePlate) {
        this.licensePlate = licensePlate;
        this.entryTime = System.currentTimeMillis();
    }
}

// Parking Lot System
public class ParkingLotOpenAddressing {

    private VehicleEntry[] parkingTable;
    private int capacity;
    private int size;

    private int totalProbes;
    private int totalOperations;

    private static final double HOURLY_RATE = 50.0; // ₹50 per hour

    // Constructor
    public ParkingLotOpenAddressing(int capacity) {
        this.capacity = capacity;
        this.parkingTable = new VehicleEntry[capacity];
        this.size = 0;
        this.totalProbes = 0;
        this.totalOperations = 0;
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle using linear probing
    public void parkVehicle(String licensePlate) {

        if (size == capacity) {
            System.out.println("Parking Full!");
            return;
        }

        int index = hash(licensePlate);
        int probes = 0;

        int originalIndex = index;

        while (parkingTable[index] != null) {

            index = (index + 1) % capacity;
            probes++;

            if (index == originalIndex) {
                System.out.println("Parking Full!");
                return;
            }
        }

        parkingTable[index] = new VehicleEntry(licensePlate);

        size++;
        totalProbes += probes;
        totalOperations++;

        System.out.println(
                "parkVehicle(\"" + licensePlate + "\") → Assigned spot #"
                        + index + " (" + probes + " probes)"
        );
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        int originalIndex = index;

        while (parkingTable[index] != null) {

            if (parkingTable[index].licensePlate.equals(licensePlate)) {

                long exitTime = System.currentTimeMillis();
                long durationMillis =
                        exitTime - parkingTable[index].entryTime;

                double hours =
                        durationMillis / (1000.0 * 60 * 60);

                double fee = Math.max(10, hours * HOURLY_RATE);

                parkingTable[index] = null;

                size--;

                System.out.println(
                        "exitVehicle(\"" + licensePlate +
                                "\") → Spot #" + index +
                                " freed, Duration: " +
                                String.format("%.2f", hours) +
                                " hours, Fee: ₹" +
                                String.format("%.2f", fee)
                );

                return;
            }

            index = (index + 1) % capacity;
            probes++;

            if (index == originalIndex)
                break;
        }

        System.out.println("Vehicle not found.");
    }

    // Find nearest available spot from entrance (index 0)
    public void findNearestAvailableSpot() {

        for (int i = 0; i < capacity; i++) {

            if (parkingTable[i] == null) {

                System.out.println(
                        "Nearest available spot: #" + i
                );
                return;
            }
        }

        System.out.println("Parking Full!");
    }

    // Display parking status
    public void displayParking() {

        System.out.println("\nParking Status:");

        for (int i = 0; i < capacity; i++) {

            if (parkingTable[i] != null) {

                System.out.println(
                        "Spot #" + i +
                                " → " +
                                parkingTable[i].licensePlate
                );
            }
        }
    }

    // Statistics
    public void getStatistics() {

        double occupancy =
                (size * 100.0) / capacity;

        double avgProbes =
                totalOperations == 0 ?
                        0 :
                        (double) totalProbes / totalOperations;

        System.out.println("\nParking Statistics:");
        System.out.println(
                "Occupancy: " +
                        String.format("%.2f", occupancy) + "%"
        );

        System.out.println(
                "Average Probes: " +
                        String.format("%.2f", avgProbes)
        );

        System.out.println(
                "Total Vehicles Parked: " + size
        );
    }

    // Main method for testing
    public static void main(String[] args) {

        ParkingLotOpenAddressing lot =
                new ParkingLotOpenAddressing(10);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");
        lot.parkVehicle("CAR-4567");

        lot.displayParking();

        lot.findNearestAvailableSpot();

        // Simulate parking duration
        try {
            Thread.sleep(2000);
        } catch (Exception e) {}

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();
    }
}