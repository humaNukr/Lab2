
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static final String DATA_DIR = "data/";
    private static final String GROUPS_DIR = DATA_DIR + "groups/";
    private static final String PRODUCTS_DIR = DATA_DIR + "products/";
    private static final String GROUPS_FILE = "groups.dat";

    public DataManager() {
        new File(GROUPS_DIR).mkdirs();
        new File(PRODUCTS_DIR).mkdirs();
    }

    public void saveGroups(List<ProductGroup> groups) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(GROUPS_DIR + GROUPS_FILE))) {
            oos.writeObject(new ArrayList<>(groups));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<ProductGroup> loadGroups() {
        File file = new File(GROUPS_DIR + GROUPS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ProductGroup>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveProducts(String groupName, List<Product> products) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PRODUCTS_DIR + groupName + ".dat"))) {
            oos.writeObject(new ArrayList<>(products));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Product> loadProducts(String groupName) {
        File file = new File(PRODUCTS_DIR + groupName + ".dat");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void deleteProductsFile(String groupName) {
        File file = new File(PRODUCTS_DIR + groupName + ".dat");
        if (file.exists()) {
            file.delete();
        }
    }

    public Map<String, List<Product>> loadAllProducts() {
        Map<String, List<Product>> allProducts = new HashMap<>();

        File dir = new File(PRODUCTS_DIR);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat"));

        if (files != null) {
            for (File file : files) {
                String groupName = file.getName().replace(".dat", "");
                allProducts.put(groupName, loadProducts(groupName));
            }
        }
        return allProducts;
    }
}