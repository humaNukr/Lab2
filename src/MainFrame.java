import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Головне вікно програми для управління складом.
 * Реалізує інтерфейс для роботи з групами товарів та окремими товарами.
 *
 * @author Артем Гриценко, Заровська Анастасія
 */
public class MainFrame extends JFrame {
    /**
     * Створює об'єкт головного вікна, ініціалізує компоненти інтерфейсу та завантажує дані.
     */
    public MainFrame() {
        service = new WarehouseService();

        setupUI();
        loadData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Автоматизоване робоче місце - Склад");
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    /**
     * Налаштовує графічний інтерфейс користувача: панелі, кнопки, таблиці.
     */
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Ліва панель з групами
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Групи товарів"));

        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadProductsForSelectedGroup();
                }
            }
        });

        JScrollPane groupScrollPane = new JScrollPane(groupList);
        leftPanel.add(groupScrollPane, BorderLayout.CENTER);

        JPanel groupButtonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        groupButtonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        addGroupBtn = createTextButton("➕", e -> addGroup());
        editGroupBtn = createTextButton("✏️", e -> editGroup());
        deleteGroupBtn = createTextButton("🗑️", e -> deleteGroup());
        //Підказки для кнопок
        addGroupBtn.setToolTipText("Додати групу");
        editGroupBtn.setToolTipText("Редагувати групу");
        deleteGroupBtn.setToolTipText("Видалити групу");

        groupButtonPanel.add(addGroupBtn);
        groupButtonPanel.add(editGroupBtn);
        groupButtonPanel.add(deleteGroupBtn);

        leftPanel.add(groupButtonPanel, BorderLayout.SOUTH);

        // Права панель з товарами
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Товари"));

        // Заголовки таблиці товарів
        String[] columnNames = {"Назва", "Опис", "Виробник", "Кількість", "Ціна", "Вартість"};
        productTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setAutoCreateRowSorter(true);

        JScrollPane productScrollPane = new JScrollPane(productTable);
        rightPanel.add(productScrollPane, BorderLayout.CENTER);

        JPanel productButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addProductBtn = createTextButton("➕ Додати", e -> addProduct());
        editProductBtn = createTextButton("✏️ Редагувати", e -> editProduct());
        deleteProductBtn = createTextButton("🗑️ Видалити", e -> deleteProduct());
        addQuantityBtn = createTextButton("📥 Додати кількість", e -> addQuantity());
        removeQuantityBtn = createTextButton("📤 Прибрати кількість", e -> removeQuantity());

        productButtonPanel.add(addProductBtn);
        productButtonPanel.add(editProductBtn);
        productButtonPanel.add(deleteProductBtn);
        productButtonPanel.add(new JSeparator(JSeparator.VERTICAL));
        productButtonPanel.add(addQuantityBtn);
        productButtonPanel.add(removeQuantityBtn);

        rightPanel.add(productButtonPanel, BorderLayout.SOUTH);

        // Верхня панель з пошуком
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchProducts();
                }
            }
        });

        searchBtn = createTextButton("🔍 Пошук", e -> searchProducts());
        statBtn = createTextButton("📊 Статистика", e -> showStatistics());

        topPanel.add(new JLabel("Пошук: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        topPanel.add(statBtn);

        // Нижня панель зі статусом
        JPanel bottomPanel = new JPanel(new BorderLayout());

        statusLabel = new JLabel("Готово");
        totalValueLabel = new JLabel("Загальна вартість: 0.00 грн");

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(totalValueLabel, BorderLayout.EAST);

        // Додаємо всі панелі до головної
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Метод для створення кнопки
     *
     * @param text     Текст на кнопці
     * @param listener Обробник події
     * @return Об'єкт JButton
     */
    private JButton createTextButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    /**
     * Завантажує дані про групи товарів у список.
     */
    private void loadData() {
        groupListModel.clear();

        List<ProductGroup> groups = service.getAllGroups();
        for (ProductGroup group : groups) {
            groupListModel.addElement(group);
        }

        updateTotalValue();
        statusLabel.setText("Дані завантажено");
    }

    /**
     * Завантажує товари для обраної групи та відображає їх у таблиці.
     */
    private void loadProductsForSelectedGroup() {
        productTableModel.setRowCount(0);

        ProductGroup selectedGroup = groupList.getSelectedValue();
        if (selectedGroup != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");

            List<Product> products = service.getProductsByGroup(selectedGroup.getName());
            for (Product product : products) {
                productTableModel.addRow(new Object[]{
                        product.getName(),
                        product.getDescription(),
                        product.getManufacturer(),
                        product.getQuantity(),
                        df.format(product.getPrice()),
                        df.format(product.getTotalValue())
                });
            }

            statusLabel.setText("Завантажено товарів: " + products.size());
        }
    }

    /**
     * Оновлює відображення загальної вартості складу.
     */
    private void updateTotalValue() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double totalValue = service.getTotalWarehouseValue();
        totalValueLabel.setText("Загальна вартість: " + df.format(totalValue) + " грн");
    }

    /**
     * Відкриває діалог додавання нової групи товарів.
     */
    private void addGroup() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Ви хочете додати нову групу товарів?",
                "Додавання групи",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            GroupDialog dialog = new GroupDialog(this, null);
            ProductGroup newGroup = dialog.showDialog();

            if (newGroup != null) {
                boolean success = service.addGroup(newGroup);
                if (success) {
                    groupListModel.addElement(newGroup);
                    JOptionPane.showMessageDialog(this,
                            "Групу \"" + newGroup.getName() + "\" успішно додано!",
                            "Успіх",
                            JOptionPane.INFORMATION_MESSAGE);
                    statusLabel.setText("Група товарів додана: " + newGroup.getName());
                    updateTotalValue();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Група з такою назвою вже існує!",
                            "Помилка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Відкриває діалог редагування обраної групи товарів.
     */
    private void editGroup() {
        ProductGroup selectedGroup = groupList.getSelectedValue();
        if (selectedGroup != null) {
            GroupDialog dialog = new GroupDialog(this, selectedGroup);
            ProductGroup updatedGroup = dialog.showDialog();

            if (updatedGroup != null) {
                String oldName = selectedGroup.getName();
                boolean success = service.updateGroup(oldName, updatedGroup);
                if (success) {
                    // Оновлюємо список
                    loadData();

                    // Вибираємо оновлену групу
                    for (int i = 0; i < groupListModel.size(); i++) {
                        if (groupListModel.get(i).getName().equals(updatedGroup.getName())) {
                            groupList.setSelectedIndex(i);
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this,
                            "Групу успішно відредаговано!",
                            "Успіх",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    /**
     * Видаляє обрану групу товарів разом з її товарами.
     */
    private void deleteGroup() {
        ProductGroup selectedGroup = groupList.getSelectedValue();

        if (selectedGroup != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ви впевнені, що хочете видалити групу \"" + selectedGroup.getName() + "\"?\n" +
                            "Всі товари в цій групі також будуть видалені.",
                    "Підтвердження видалення",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = service.deleteGroup(selectedGroup.getName());

                if (success) {
                    groupListModel.removeElement(selectedGroup);
                    productTableModel.setRowCount(0);
                    statusLabel.setText("Група товарів видалена: " + selectedGroup.getName());
                    updateTotalValue();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть групу для видалення",
                    "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Відкриває діалог додавання нового товару до обраної групи.
     */
    private void addProduct() {
        ProductGroup selectedGroup = groupList.getSelectedValue();
        if (selectedGroup != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ви хочете додати новий товар до групи \"" + selectedGroup.getName() + "\"?",
                    "Додавання товару",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                ProductDialog dialog = new ProductDialog(this, null, selectedGroup.getName());
                Product newProduct = dialog.showDialog();

                if (newProduct != null) {
                    boolean success = service.addProduct(newProduct);
                    if (success) {
                        loadProductsForSelectedGroup();
                        JOptionPane.showMessageDialog(this,
                                "Товар \"" + newProduct.getName() + "\" успішно додано!",
                                "Успіх",
                                JOptionPane.INFORMATION_MESSAGE);
                        statusLabel.setText("Товар доданий: " + newProduct.getName());
                        updateTotalValue();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Товар з такою назвою вже існує!",
                                "Помилка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть товар для додавання",
                    "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Відкриває діалог редагування обраного товару.
     */
    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow >= 0) {
            int modelRow = productTable.convertRowIndexToModel(selectedRow);
            String productName = (String) productTableModel.getValueAt(modelRow, 0);
            Product product = service.getProductByName(productName);

            if (product != null) {
                // Показуємо діалог підтвердження
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Ви хочете редагувати товар \"" + productName + "\"?",
                        "Редагування товару",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    ProductDialog dialog = new ProductDialog(this, product, product.getGroupName());
                    Product updatedProduct = dialog.showDialog();

                    if (updatedProduct != null) {
                        String oldName = product.getName();
                        boolean success = service.updateProduct(oldName, updatedProduct);

                        if (success) {
                            loadProductsForSelectedGroup();
                            JOptionPane.showMessageDialog(this,
                                    "Товар \"" + updatedProduct.getName() + "\" успішно відредаговано!",
                                    "Успіх",
                                    JOptionPane.INFORMATION_MESSAGE);
                            statusLabel.setText("Товар оновлено: " + updatedProduct.getName());
                            updateTotalValue();
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Товар з такою назвою вже існує!",
                                    "Помилка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть товар для редагування",
                    "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Видаляє обраний товар зі складу.
     */
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = productTable.convertRowIndexToModel(selectedRow);
            String productName = (String) productTableModel.getValueAt(modelRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ви впевнені, що хочете видалити товар \"" + productName + "\"?",
                    "Видалення товару",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = service.deleteProduct(productName);
                if (success) {
                    loadProductsForSelectedGroup();
                    JOptionPane.showMessageDialog(this,
                            "Товар \"" + productName + "\" успішно видалено!",
                            "Успіх",
                            JOptionPane.INFORMATION_MESSAGE);
                    statusLabel.setText("Товар видалено: " + productName);
                    updateTotalValue();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть товар для видалення",
                    "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Додає вказану кількість одиниць товару до складу.
     */
    private void addQuantity() {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow >= 0) {
            int modelRow = productTable.convertRowIndexToModel(selectedRow);
            String productName = (String) productTableModel.getValueAt(modelRow, 0);

            String input = JOptionPane.showInputDialog(this,
                    "Введіть кількість товару \"" + productName + "\" для додавання:",
                    "Прихід товару",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(input.trim());

                    if (quantity > 0) {
                        boolean success = service.addProductQuantity(productName, quantity);

                        if (success) {
                            loadProductsForSelectedGroup();
                            statusLabel.setText("Додано " + quantity + " од. товару: " + productName);
                            updateTotalValue();
                            // Додаємо інформаційне повідомлення
                            JOptionPane.showMessageDialog(this,
                                    String.format("Товар \"%s\" додано на склад у кількості %d од.",
                                            productName, quantity),
                                    "Успішне додавання",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Кількість повинна бути більше нуля!",
                                "Помилка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Будь ласка, введіть коректне число!",
                            "Помилка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть товар для додавання кількості",
                    "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Прибирає вказану кількість одиниць товару зі складу.
     */
    private void removeQuantity() {
        int selectedRow = productTable.getSelectedRow();

        if (selectedRow >= 0) {
            int modelRow = productTable.convertRowIndexToModel(selectedRow);
            String productName = (String) productTableModel.getValueAt(modelRow, 0);

            String input = JOptionPane.showInputDialog(this,
                    "Введіть кількість товару \"" + productName + "\" для відвантаження:",
                    "Відвантаження товару",
                    JOptionPane.QUESTION_MESSAGE);

            if (input != null && !input.trim().isEmpty()) {
                try {
                    int quantity = Integer.parseInt(input.trim());

                    if (quantity > 0) {
                        boolean success = service.removeProductQuantity(productName, quantity);

                        if (success) {
                            loadProductsForSelectedGroup();
                            statusLabel.setText("Відвантажено " + quantity + " од. товару: " + productName);
                            updateTotalValue();
                            // Додаємо інформаційне повідомлення
                            JOptionPane.showMessageDialog(this,
                                    String.format("Товар \"%s\" вилучено зі складу у кількості %d од.",
                                            productName, quantity),
                                    "Успішне відвантаження",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Недостатньо товару на складі!",
                                    "Помилка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Кількість повинна бути більше нуля!",
                                "Помилка",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Будь ласка, введіть коректне число!",
                            "Помилка",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, виберіть товар для відвантаження",
                    "Інформація",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Шукає товари за введеним запитом у полі пошуку.
     */
    private void searchProducts() {
        String query = searchField.getText().trim().toLowerCase();

        if (!query.isEmpty()) {
            List<Product> results = service.searchProducts(query);
            productTableModel.setRowCount(0);

            if (!results.isEmpty()) {
                DecimalFormat df = new DecimalFormat("#,##0.00");

                // Тимчасово видаляємо слухача подій вибору групи
                ListSelectionListener[] listeners = groupList.getListSelectionListeners();
                for (ListSelectionListener listener : listeners) {
                    groupList.removeListSelectionListener(listener);
                }

                // Знаходимо та виділяємо групу першого знайденого товару
                String groupName = results.get(0).getGroupName();
                for (int i = 0; i < groupListModel.size(); i++) {
                    ProductGroup group = groupListModel.getElementAt(i);
                    if (group.getName().toLowerCase().equals(groupName.toLowerCase())) {
                        groupList.setSelectedIndex(i);
                        break;
                    }
                }

                // Показуємо тільки знайдені товари
                for (Product product : results) {
                    productTableModel.addRow(new Object[]{
                            product.getName(),
                            product.getDescription(),
                            product.getManufacturer(),
                            product.getQuantity(),
                            df.format(product.getPrice()),
                            df.format(product.getTotalValue())
                    });
                }

                // Відновлюємо слухача подій
                for (ListSelectionListener listener : listeners) {
                    groupList.addListSelectionListener(listener);
                }

                statusLabel.setText("Знайдено товарів: " + results.size());
            } else {
                statusLabel.setText("Товарів не знайдено");
                groupList.clearSelection();
            }
        } else {
            ProductGroup selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                loadProductsForSelectedGroup();
            } else {
                productTableModel.setRowCount(0);
            }
        }
    }


    /**
     * Відкриває діалог зі статистикою складу.
     */
    private void showStatistics() {
        StatisticsDialog dialog = new StatisticsDialog(this, service);
        dialog.setVisible(true);
    }

    private WarehouseService service;

    private JList<ProductGroup> groupList;
    private DefaultListModel<ProductGroup> groupListModel;

    private JTable productTable;
    private DefaultTableModel productTableModel;

    // Кнопки для роботи з групами та товарами
    private JButton addGroupBtn, editGroupBtn, deleteGroupBtn;
    private JButton addProductBtn, editProductBtn, deleteProductBtn;
    private JButton addQuantityBtn, removeQuantityBtn;
    private JButton searchBtn, statBtn;
    private JTextField searchField;

    private JLabel statusLabel;
    private JLabel totalValueLabel;
}
