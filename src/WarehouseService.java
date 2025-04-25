

import java.util.ArrayList;
import java.util.List;

public class WarehouseService {
    private List<ProductGroup> groups;
    private DataManager dataManager;

    public WarehouseService() {
        dataManager = new DataManager();
        groups = dataManager.loadGroups();

        // Завантажуємо продукти для кожної групи
        for (ProductGroup group : groups) {
            List<Product> products = dataManager.loadProducts(group.getName());
            group.setProducts(products);
        }
    }

    public List<ProductGroup> getAllGroups() {
        return groups;
    }

    public boolean addGroup(ProductGroup group) {
        // Перевіряємо унікальність назви групи
        if (isGroupNameExists(group.getName())) {
            return false;
        }

        groups.add(group);
        saveData();
        return true;
    }

    public boolean updateGroup(String oldName, ProductGroup updatedGroup) {
        // Якщо назва групи змінилась, перевіряємо унікальність
        if (!oldName.equals(updatedGroup.getName()) && isGroupNameExists(updatedGroup.getName())) {
            return false;
        }

        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getName().equals(oldName)) {
                // Отримуємо старі продукти
                List<Product> products = groups.get(i).getProducts();

                // Видаляємо старий файл з продуктами
                dataManager.deleteProductsFile(oldName);

                // Оновлюємо зв'язок з групою у продуктах
                for (Product product : products) {
                    product.setGroupName(updatedGroup.getName());
                }

                updatedGroup.setProducts(products);
                groups.set(i, updatedGroup);

                // Зберігаємо продукти з новою назвою групи
                dataManager.saveProducts(updatedGroup.getName(), products);
                saveData();
                return true;
            }
        }

        return false;
    }

    public boolean deleteGroup(String groupName) {
        boolean removed = groups.removeIf(g -> g.getName().equals(groupName));
        if (removed) {
            // Видаляємо файл з продуктами
            dataManager.deleteProductsFile(groupName);
            saveData();
        }
        return removed;
    }

    public boolean addProduct(Product product) {
        // Перевіряємо унікальність назви продукту
        if (isProductNameExists(product.getName())) {
            return false;
        }

        for (ProductGroup group : groups) {
            if (group.getName().equals(product.getGroupName())) {
                group.addProduct(product);
                dataManager.saveProducts(group.getName(), group.getProducts());
                return true;
            }
        }

        return false;
    }

    public boolean updateProduct(String oldName, Product updatedProduct) {
        // Якщо назва продукту змінилась, перевіряємо унікальність
        if (!oldName.equals(updatedProduct.getName()) && isProductNameExists(updatedProduct.getName())) {
            return false;
        }

        for (ProductGroup group : groups) {
            for (int i = 0; i < group.getProducts().size(); i++) {
                if (group.getProducts().get(i).getName().equals(oldName)) {
                    group.getProducts().set(i, updatedProduct);
                    dataManager.saveProducts(group.getName(), group.getProducts());
                    return true;
                }
            }
        }

        return false;
    }

    public boolean deleteProduct(String productName) {
        for (ProductGroup group : groups) {
            if (group.removeProductByName(productName)) {
                dataManager.saveProducts(group.getName(), group.getProducts());
                return true;
            }
        }

        return false;
    }

    public boolean addProductQuantity(String productName, int quantity) {
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().equals(productName)) {
                    product.addQuantity(quantity);
                    dataManager.saveProducts(group.getName(), group.getProducts());
                    return true;
                }
            }
        }

        return false;
    }

    public boolean removeProductQuantity(String productName, int quantity) {
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().equals(productName)) {
                    boolean success = product.removeQuantity(quantity);
                    if (success) {
                        dataManager.saveProducts(group.getName(), group.getProducts());
                    }
                    return success;
                }
            }
        }

        return false;
    }

    public List<Product> searchProducts(String query) {
        List<Product> result = new ArrayList<>();

        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().toLowerCase().contains(query.toLowerCase()) ||
                        product.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                        product.getManufacturer().toLowerCase().contains(query.toLowerCase())) {
                    result.add(product);
                }
            }
        }

        return result;
    }

    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>();

        for (ProductGroup group : groups) {
            allProducts.addAll(group.getProducts());
        }

        return allProducts;
    }

    public List<Product> getProductsByGroup(String groupName) {
        for (ProductGroup group : groups) {
            if (group.getName().equals(groupName)) {
                return group.getProducts();
            }
        }

        return new ArrayList<>();
    }

    public double getTotalWarehouseValue() {
        double total = 0;

        for (ProductGroup group : groups) {
            total += group.getTotalValue();
        }

        return total;
    }

    public double getGroupTotalValue(String groupName) {
        for (ProductGroup group : groups) {
            if (group.getName().equals(groupName)) {
                return group.getTotalValue();
            }
        }

        return 0;
    }

    public ProductGroup getGroupByName(String groupName) {
        for (ProductGroup group : groups) {
            if (group.getName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    public Product getProductByName(String productName) {
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().equals(productName)) {
                    return product;
                }
            }
        }
        return null;
    }

    private boolean isGroupNameExists(String name) {
        for (ProductGroup group : groups) {
            if (group.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isProductNameExists(String name) {
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void saveData() {
        dataManager.saveGroups(groups);
    }
}