import java.io.Serializable;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String manufacturer;
    private int quantity;
    private double price;
    private String groupName;

    public Product(String name, String description, String manufacturer, int quantity, double price, String groupName) {
    }

    // Basic getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        // TODO: Реалізуйте метод додавання кількості
    }

    public boolean removeQuantity(int amount) {
        // TODO: Реалізуйте метод віднімання кількості
        return false;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public double getTotalValue() {
        return 0.0;
    }

    public String getProductByName(String name) {
        return name;
    }

    @Override
    public String toString() {
        return "";
    }
}
