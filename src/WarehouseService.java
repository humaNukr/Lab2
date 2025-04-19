import java.util.ArrayList;
import java.util.List;

public class WarehouseService {
    private List<ProductGroup> groups;
    private DataManager dataManager;

    public WarehouseService() {
    }

    public Product getProductByName(String productName) {
            for (Product product : getAllProducts()) {
            if (product.getName().equals(productName)) {
                return product;
            }
        }
        return null;
    }
    public List<ProductGroup> getAllGroups() {
        return new ArrayList<>();
    }

    public boolean addGroup(ProductGroup group) {
        return false;
    }

    public boolean updateGroup(String oldName, ProductGroup updatedGroup) {
        return false;
    }

    public boolean deleteGroup(String groupName) {
        return false;
    }

    public boolean addProduct(Product product) {
        return false;
    }

    public boolean updateProduct(String oldName, Product updatedProduct) {
        return false;
    }

    public boolean deleteProduct(String productName) {
        return false;
    }

    public boolean addProductQuantity(String productName, int quantity) {
        return false;
    }

    public boolean removeProductQuantity(String productName, int quantity) {
        return false;
    }

    public List<Product> searchProducts(String query) {
        return new ArrayList<>();
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>();
    }

    public List<Product> getProductsByGroup(String groupName) {
        return new ArrayList<>();
    }

    public double getTotalWarehouseValue() {
        return 0.0;
    }

    public double getGroupTotalValue(String groupName) {
        return 0.0;
    }

    private boolean isGroupNameExists(String name) {
        return false;
    }

    private boolean isProductNameExists(String name) {
        return false;
    }

    private void saveData() {
    }
}