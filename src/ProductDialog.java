

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Клас ProductDialog являє собою діалогове вікно для додавання або редагування товару.
 * Містить текстові поля для введення основних даних про товар, таких як назва, опис, виробник,
 * ціна та кількість. Використовується для взаємодії користувача з графічним інтерфейсом.
 *
 * @author Артем Гриценко, Заровська Анастасія
 */

public class ProductDialog extends JDialog {
    /**
     * Конструктор класу ProductDialog.
     *
     * @param owner     Головне вікно, до якого прив'язаний цей діалог.
     * @param product   Екземпляр товару для редагування або null, якщо створюється новий товар.
     * @param groupName Назва групи, до якої належить товар.
     */

    public ProductDialog(Frame owner, Product product, String groupName) {
        super(owner, product == null ? "Додати товар" : "Редагувати товар", true);

        this.groupName = groupName;
        this.originalProduct = product;

        setupUI();

        if (product != null) {
            nameField.setText(product.getName());
            descriptionArea.setText(product.getDescription());
            manufacturerField.setText(product.getManufacturer());
            priceField.setText(String.valueOf(product.getPrice()));
            // Встановлюємо кількість тільки якщо створюємо новий товар
            if (quantityField != null) {
                quantityField.setText(String.valueOf(product.getQuantity()));
            }
        }

        setSize(450, 400);
        setLocationRelativeTo(owner);
    }

    /**
     * Налаштовує графічний інтерфейс діалогового вікна.
     * Додає текстові поля, кнопки та інші компоненти для введення даних.
     */

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Назва
        addFormField(formPanel, "Назва:", nameField = new JTextField(20), gbc, 0);

        // Опис
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Опис:"), gbc);

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(scrollPane, gbc);

        // Виробник
        addFormField(formPanel, "Виробник:", manufacturerField = new JTextField(20), gbc, 2);

        // Ціна
        addFormField(formPanel, "Ціна:", priceField = new JTextField(10), gbc, 3);

        // Кількість (тільки для нового товару)
        if (originalProduct == null) {
            addFormField(formPanel, "Кількість:", quantityField = new JTextField(10), gbc, 4);
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButtons(buttonPanel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    /**
     * Додає кнопки "OK" та "Скасувати" до панелі кнопок діалогового вікна.
     *
     * @param buttonPanel Панель, до якої додаються кнопки.
     */
    private void addButtons(JPanel buttonPanel) {
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Скасувати");

        okButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Встановлюємо OK як кнопку за замовчуванням
        getRootPane().setDefaultButton(okButton);
    }

    /**
     * Додає текстове поле з міткою до форми.
     *
     * @param panel Панель, до якої додаються компоненти.
     * @param label Текст мітки для поля.
     * @param field Компонент, що використовується для введення даних.
     * @param gbc   Параметри компонування.
     * @param row   Рядок, у якому розташовується компонент.
     */

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    /**
     * Зберігає дані про товар з введених користувачем значень.
     * Перевіряє правильність введених даних та відображає повідомлення про помилки.
     */

    private void saveProduct() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String manufacturer = manufacturerField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Назва товару не може бути порожньою!",
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            int quantity;

            if (price < 0) {
                JOptionPane.showMessageDialog(this,
                        "Ціна не може бути від'ємною!",
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Якщо це редагування, зберігаємо стару кількість
            if (originalProduct != null) {
                quantity = originalProduct.getQuantity();
            } else {
                quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Кількість не може бути від'ємною!",
                            "Помилка",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            result = new Product(name, description, manufacturer, quantity, price, groupName);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, введіть коректні числові значення!",
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Показує діалогове вікно та повертає результат - новий або змінений товар.
     *
     * @return Екземпляр класу Product з даними про товар, якщо користувач натиснув "OK",
     * або null, якщо діалог було закрито.
     */
    public Product showDialog() {
        setVisible(true);
        return result;
    }

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField manufacturerField;
    private JTextField priceField;
    private JTextField quantityField;

    private Product result;
    private String groupName;
    private Product originalProduct;
}
