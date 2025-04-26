

import java.util.ArrayList;
import java.util.List;
/**
 * Клас WarehouseService відповідає за управління складом, групами товарів та окремими товарами.
 * Здійснює завантаження, збереження та обробку даних за допомогою DataManager.
 *
 * @author Артем Гриценко, Заровська Анастасія
 */

public class WarehouseService {
    /**
     * Конструктор класу WarehouseService.
     * Ініціалізує менеджер даних та завантажує групи та відповідні товари.
     */
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

    /**
     * Додає нову групу товарів.
     *
     * @param group Група товарів для додавання.
     * @return true, якщо група була успішно додана, інакше false.
     */

    public boolean addGroup(ProductGroup group) {
        // Перевіряємо унікальність назви групи
        if (isGroupNameExists(group.getName())) {
            return false;
        }

        groups.add(group);
        saveData();
        return true;
    }

    /**
     * Оновлює існуючу групу товарів.
     *
     * @param oldName      Назва групи, що редагується.
     * @param updatedGroup Оновлена група.
     * @return true, якщо групу було успішно оновлено, інакше false.
     */

    public boolean updateGroup(String oldName, ProductGroup updatedGroup) {
        // Якщо назва групи змінилась, перевіряємо унікальність
        if (!oldName.toLowerCase().equals(updatedGroup.getName().toLowerCase()) &&
                isGroupNameExists(updatedGroup.getName())) {
            return false;
        }

        for (int i = 0; i < groups.size(); i++) {
            if (groups.get(i).getName().toLowerCase().equals(oldName.toLowerCase())) {
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

    /**
     * Видаляє групу товарів за назвою.
     *
     * @param groupName Назва групи для видалення.
     * @return true, якщо групу було видалено, інакше false.
     */

    public boolean deleteGroup(String groupName) {
        String lowercaseName = groupName.toLowerCase();
        boolean removed = groups.removeIf(g -> g.getName().toLowerCase().equals(lowercaseName));
        if (removed) {
            dataManager.deleteProductsFile(groupName);
            saveData();
        }
        return removed;
    }

    /**
     * Додає новий товар до групи.
     *
     * @param product Товар для додавання.
     * @return true, якщо товар успішно додано, інакше false.
     */

    public boolean addProduct(Product product) {
        // Перевіряємо унікальність назви продукту
        if (isProductNameExists(product.getName())) {
            return false;
        }

        for (ProductGroup group : groups) {
            if (group.getName().toLowerCase().equals(product.getGroupName().toLowerCase())) {
                group.addProduct(product);
                dataManager.saveProducts(group.getName(), group.getProducts());
                return true;
            }
        }

        return false;
    }

    /**
     * Оновлює інформацію про існуючий товар.
     *
     * @param oldName        Назва товару, що редагується.
     * @param updatedProduct Оновлений товар.
     * @return true, якщо товар успішно оновлено, інакше false.
     */

    public boolean updateProduct(String oldName, Product updatedProduct) {
        oldName = oldName.toLowerCase();
        // Якщо назва продукту змінилась, перевіряємо унікальність
        if (!oldName.equals(updatedProduct.getName().toLowerCase()) && isProductNameExists(updatedProduct.getName().toLowerCase())) {
            return false;
        }

        for (ProductGroup group : groups) {
            for (int i = 0; i < group.getProducts().size(); i++) {
                if (group.getProducts().get(i).getName().toLowerCase().equals(oldName)) {
                    group.getProducts().set(i, updatedProduct);
                    dataManager.saveProducts(group.getName(), group.getProducts());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Видаляє товар за назвою.
     *
     * @param productName Назва товару для видалення.
     * @return true, якщо товар успішно видалено, інакше false.
     */

    public boolean deleteProduct(String productName) {
        productName = productName.toLowerCase();
        for (ProductGroup group : groups) {
            if (group.removeProductByName(productName)) {
                dataManager.saveProducts(group.getName(), group.getProducts());
                return true;
            }
        }

        return false;
    }

    /**
     * Додає кількість товару.
     *
     * @param productName назва товару
     * @param quantity    кількість
     * @return true, якщо товар успішно додано, інакше false
     */
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

    /**
     * Прибирає кількість товару.
     *
     * @param productName назва товару
     * @param quantity    кількість
     * @return true, якщо товар успішно прибрано, інакше false
     */
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

    /**
     * Шукає товари за частковим збігом у назві.
     *
     * @param query Текст для пошуку.
     * @return Список знайдених товарів.
     */

    public List<Product> searchProducts(String query) {
        List<Product> result = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();

        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().toLowerCase().contains(lowercaseQuery)) {
                    result.add(product);
                }
            }
        }

        return result;
    }

    /**
     * Повертає список усіх товарів складу.
     *
     * @return Список усіх товарів.
     */

    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>();

        for (ProductGroup group : groups) {
            allProducts.addAll(group.getProducts());
        }

        return allProducts;
    }

    /**
     * Повертає список товарів конкретної групи.
     *
     * @param groupName Назва групи.
     * @return Список товарів групи.
     */

    public List<Product> getProductsByGroup(String groupName) {
        String lowercaseName = groupName.toLowerCase();
        for (ProductGroup group : groups) {
            if (group.getName().toLowerCase().equals(lowercaseName)) {
                return group.getProducts();
            }
        }
        return new ArrayList<>();
    }

    /**
     * Розраховує загальну вартість складу.
     *
     * @return Загальна вартість складу.
     */

    public double getTotalWarehouseValue() {
        double total = 0;

        for (ProductGroup group : groups) {
            total += group.getTotalValue();
        }

        return total;
    }

    /**
     * Розраховує загальну вартість групи товарів.
     *
     * @param groupName Назва групи.
     * @return Загальна вартість групи товарів.
     */

    public double getGroupTotalValue(String groupName) {
        String lowercaseName = groupName.toLowerCase();
        for (ProductGroup group : groups) {
            if (group.getName().toLowerCase().equals(lowercaseName)) {
                return group.getTotalValue();
            }
        }
        return 0;
    }

    /**
     * Повертає групу за назвою.
     *
     * @param groupName Назва групи.
     * @return Група, якщо знайдена, інакше null.
     */
    public ProductGroup getGroupByName(String groupName) {
        String lowercaseName = groupName.toLowerCase();
        for (ProductGroup group : groups) {
            if (group.getName().toLowerCase().equals(lowercaseName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Повертає товар за назвою.
     *
     * @param productName Назва товару.
     * @return Товар, якщо знайдено, інакше null.
     */
    public Product getProductByName(String productName) {
        String lowercaseName = productName.toLowerCase();
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().toLowerCase().equals(lowercaseName)) {
                    return product;
                }
            }
        }
        return null;
    }

    /**
     * Перевіряє існування групи за назвою.
     *
     * @param name Назва групи.
     * @return true, якщо група існує, інакше false.
     */

    private boolean isGroupNameExists(String name) {
        String lowercaseName = name.toLowerCase();
        for (ProductGroup group : groups) {
            if (group.getName().toLowerCase().equals(lowercaseName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Перевіряє існування продукту за назвою.
     *
     * @param name Назва продукту.
     * @return true, якщо продукт існує, інакше false.
     */
    private boolean isProductNameExists(String name) {
        String lowercaseName = name.toLowerCase();
        for (ProductGroup group : groups) {
            for (Product product : group.getProducts()) {
                if (product.getName().toLowerCase().equals(lowercaseName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Зберігає зміни у даних складу.
     */

    private void saveData() {
        dataManager.saveGroups(groups);
    }

    private List<ProductGroup> groups;
    private DataManager dataManager;
}