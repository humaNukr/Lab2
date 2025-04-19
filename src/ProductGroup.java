import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private List<Product> products;

    public ProductGroup(String name, String description) {
    }

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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
    }

    public boolean removeProduct(Product product) {
        return false;
    }

    public boolean removeProductByName(String productName) {
        return false;
    }

    public double getTotalValue() {
        return 0.0;
    }

    @Override
    public String toString() {
        return "";
    }
}