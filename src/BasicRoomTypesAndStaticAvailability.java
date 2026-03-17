abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 1000.0);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 1800.0);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 3000.0);
    }
}

public class BasicRoomTypesAndStaticAvailability {

    public static void main(String[] args) {

        Room single = new SingleRoom();
        Room doub = new DoubleRoom();
        Room suite = new SuiteRoom();

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        System.out.println("===== Room Details =====");

        System.out.println(single.getType());
        System.out.println("Beds: " + single.getBeds());
        System.out.println("Price: " + single.getPrice());
        System.out.println("Available: " + singleAvailable);
        System.out.println();

        System.out.println(doub.getType());
        System.out.println("Beds: " + doub.getBeds());
        System.out.println("Price: " + doub.getPrice());
        System.out.println("Available: " + doubleAvailable);
        System.out.println();

        System.out.println(suite.getType());
        System.out.println("Beds: " + suite.getBeds());
        System.out.println("Price: " + suite.getPrice());
        System.out.println("Available: " + suiteAvailable);
    }
}