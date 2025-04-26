
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Клас DataManager відповідає за збереження, завантаження та видалення даних для груп товарів та продуктів.
 * Управляє файлами, що містять інформацію про групи і продукти на складі.
 *
 * @author Артем Гриценко, Заровська Анастасія
 */

public class DataManager {

    /**
     * Конструктор класу DataManager.
     * Автоматично створює необхідні каталоги для даних, якщо вони не існують.
     */

    public DataManager() {
        new File(GROUPS_DIR).mkdirs();
        new File(PRODUCTS_DIR).mkdirs();
    }

    /**
     * Зберігає список груп товарів у файл.
     *
     * @param groups Список груп товарів для збереження.
     */

    public void saveGroups(List<ProductGroup> groups) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(GROUPS_DIR + GROUPS_FILE))) {
            oos.writeObject(new ArrayList<>(groups));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Завантажує список груп товарів з файлу.
     *
     * @return Список груп товарів. Повертає порожній список, якщо файл не існує або виникла помилка.
     */

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

    /**
     * Зберігає список продуктів для певної групи у файл.
     *
     * @param groupName Назва групи, до якої належать продукти.
     * @param products  Список продуктів для збереження.
     */

    public void saveProducts(String groupName, List<Product> products) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PRODUCTS_DIR + groupName + ".dat"))) {
            oos.writeObject(new ArrayList<>(products));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Завантажує список продуктів певної групи з файлу.
     *
     * @param groupName Назва групи, для якої завантажуються продукти.
     * @return Список продуктів. Повертає порожній список, якщо файл не існує або виникла помилка.
     */

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

    /**
     * Видаляє файл з продуктами певної групи.
     *
     * @param groupName Назва групи, файл якої потрібно видалити.
     */

    public void deleteProductsFile(String groupName) {
        File file = new File(PRODUCTS_DIR + groupName + ".dat");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Завантажує всі продукти для кожної групи з відповідних файлів.
     *
     * @return Карта, де ключ - назва групи, значення - список продуктів цієї групи.
     */

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

    private static final String DATA_DIR = "data/";
    private static final String GROUPS_DIR = DATA_DIR + "groups/";
    private static final String PRODUCTS_DIR = DATA_DIR + "products/";
    private static final String GROUPS_FILE = "groups.dat";
}