

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас ProductGroup представляє групу товарів з загальною назвою та описом.
 * Клас також дозволяє додавати, видаляти товари та розраховувати загальну вартість групи.
 *
 * @author Артем Гриценко, Заровська Анастасія
 */

public class ProductGroup implements Serializable {
    /**
     * Конструктор, що створює нову групу товарів.
     *
     * @param name        Назва групи.
     * @param description Опис групи.
     */

    public ProductGroup(String name, String description) {
        this.name = name;
        this.description = description;
        this.products = new ArrayList<>();
    }

    // Getters and setters
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
        products.add(product);
    }

    public boolean removeProduct(Product product) {
        return products.remove(product);
    }

    /**
     * Видаляє товар за назвою з групи.
     *
     * @param productName Назва товару, який потрібно видалити.
     * @return true, якщо товар був успішно видалений; false, якщо такого товару немає в групі.
     */

    public boolean removeProductByName(String productName) {
        return products.removeIf(p -> p.getName().equals(productName));
    }

    public double getTotalValue() {
        double total = 0;
        for (Product product : products) {
            total += product.getTotalValue();
        }
        return total;
    }

    @Override
    public String toString() {
        return name;
    }

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private List<Product> products;
}