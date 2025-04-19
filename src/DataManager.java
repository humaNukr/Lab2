// DataModel.java
import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private List<ProductGroup> groups;

    public DataModel() {
        // Ініціалізація файлової системи
        FileManager.initFileSystem();

        // Завантаження груп з файлу
        groups = FileManager.loadGroups();

        // Завантаження товарів для кожної групи
        for (ProductGroup group : groups) {
            FileManager.loadProductsForGroup(group);
        }

        // Якщо груп немає, створюємо декілька зразків
        if (groups.isEmpty()) {
            initializeDefaultData();
        }
    }

    private void initializeDefaultData() {
        // Створення зразків груп і товарів
        ProductGroup food = new ProductGroup("Продовольчі", "Харчові продукти");
        food.addProduct(new Product("Гречка", "Крупа гречана", "Україна", 100, 42.50));
        food.addProduct(new Product("Рис", "Рис довгозернистий", "Китай", 80, 35.75));

        ProductGroup nonFood = new ProductGroup("Непродовольчі", "Нехарчові товари");
        nonFood.addProduct(new Product("Мило", "Господарське мило", "Україна", 50, 15.20));

        groups.add(food);
        groups.add(nonFood);

        // Збереження зразків
        saveAllData();
    }

    public List<ProductGroup> getGroups() {
        return groups;
    }

    public void addGroup(ProductGroup group) {
        groups.add(group);
        FileManager.saveGroups(groups);
    }

    public void updateGroup(String oldName, ProductGroup group) {
        FileManager.renameProductFile(oldName, group.getName());
        FileManager.saveGroups(groups);
    }

    public void removeGroup(ProductGroup group) {
        groups.remove(group);
        FileManager.deleteProductFile(group.getName());
        FileManager.saveGroups(groups);
    }

    public ProductGroup getGroupByName(String name) {
        for (ProductGroup group : groups) {
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    public boolean groupExists(String name) {
        return getGroupByName(name) != null;
    }

    public boolean isProductNameUnique(String productName, String currentGroupName) {
        return FileManager.isProductNameUnique(productName, groups, currentGroupName);
    }

    public void saveAllData() {
        FileManager.saveGroups(groups);
        for (ProductGroup group : groups) {
            FileManager.saveProductsForGroup(group);
        }
    }

    public Product findProduct(String productName) {
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().toLowerCase().contains(productName.toLowerCase())) {
                    return product;
                }
            }
        }
        return null;
    }

    public ProductGroup findProductGroup(String productName) {
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().toLowerCase().contains(productName.toLowerCase())) {
                    return group;
                }
            }
        }
        return null;
    }

    public void addProductToGroup(ProductGroup group, Product product) {
        group.addProduct(product);
        FileManager.saveProductsForGroup(group);
    }

    public void updateProductInGroup(ProductGroup group) {
        FileManager.saveProductsForGroup(group);
    }

    public void removeProductFromGroup(ProductGroup group, Product product) {
        group.removeProduct(product);
        FileManager.saveProductsForGroup(group);
    }
}