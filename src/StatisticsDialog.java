
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class StatisticsDialog extends JDialog {
    private WarehouseService service;
    private JTabbedPane tabbedPane;

    private JTable allProductsTable;
    private DefaultTableModel allProductsModel;

    private JComboBox<ProductGroup> groupComboBox;
    private JTable groupProductsTable;
    private DefaultTableModel groupProductsModel;

    private JLabel totalWarehouseValueLabel;
    private JLabel selectedGroupValueLabel;

    public StatisticsDialog(Frame owner, WarehouseService service) {
        super(owner, "Статистика складу", true);
        this.service = service;

        setupUI();
        loadData();

        setSize(800, 600);
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        tabbedPane = new JTabbedPane();

        // Вкладка всіх товарів
        JPanel allProductsPanel = new JPanel(new BorderLayout(5, 5));

        String[] allProductsColumns = {"Група", "Назва", "Опис", "Виробник", "Кількість", "Ціна", "Вартість"};
        allProductsModel = new DefaultTableModel(allProductsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        allProductsTable = new JTable(allProductsModel);
        allProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allProductsTable.setAutoCreateRowSorter(true);

        JScrollPane allProductsScrollPane = new JScrollPane(allProductsTable);
        allProductsPanel.add(allProductsScrollPane, BorderLayout.CENTER);

        totalWarehouseValueLabel = new JLabel("Загальна вартість складу: 0.00 грн");
        totalWarehouseValueLabel.setFont(new Font(totalWarehouseValueLabel.getFont().getName(), Font.BOLD, 14));
        allProductsPanel.add(totalWarehouseValueLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Всі товари", allProductsPanel);

        // Вкладка товарів по групі
        JPanel groupProductsPanel = new JPanel(new BorderLayout(5, 5));

        JPanel topGroupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topGroupPanel.add(new JLabel("Група товарів: "));

        groupComboBox = new JComboBox<>();
        groupComboBox.addActionListener(e -> loadGroupProducts());
        topGroupPanel.add(groupComboBox);

        groupProductsPanel.add(topGroupPanel, BorderLayout.NORTH);

        String[] groupProductsColumns = {"Назва", "Опис", "Виробник", "Кількість", "Ціна", "Вартість"};
        groupProductsModel = new DefaultTableModel(groupProductsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        groupProductsTable = new JTable(groupProductsModel);
        groupProductsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupProductsTable.setAutoCreateRowSorter(true);

        JScrollPane groupProductsScrollPane = new JScrollPane(groupProductsTable);
        groupProductsPanel.add(groupProductsScrollPane, BorderLayout.CENTER);

        selectedGroupValueLabel = new JLabel("Вартість групи: 0.00 грн");
        selectedGroupValueLabel.setFont(new Font(selectedGroupValueLabel.getFont().getName(), Font.BOLD, 14));
        groupProductsPanel.add(selectedGroupValueLabel, BorderLayout.SOUTH);

        tabbedPane.addTab("Товари по групі", groupProductsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Кнопка закриття
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Закрити");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadData() {
        // Завантажуємо всі товари
        allProductsModel.setRowCount(0);

        DecimalFormat df = new DecimalFormat("#,##0.00");

        List<Product> allProducts = service.getAllProducts();
        for (Product product : allProducts) {
            allProductsModel.addRow(new Object[]{
                    product.getGroupName(),
                    product.getName(),
                    product.getDescription(),
                    product.getManufacturer(),
                    product.getQuantity(),
                    df.format(product.getPrice()),
                    df.format(product.getTotalValue())
            });
        }

        double totalWarehouseValue = service.getTotalWarehouseValue();
        totalWarehouseValueLabel.setText("Загальна вартість складу: " + df.format(totalWarehouseValue) + " грн");

        // Заповнюємо комбобокс групами
        groupComboBox.removeAllItems();
        List<ProductGroup> groups = service.getAllGroups();
        for (ProductGroup group : groups) {
            groupComboBox.addItem(group);
        }

        // Завантажуємо товари для першої групи, якщо є
        if (!groups.isEmpty()) {
            loadGroupProducts();
        }
    }

    private void loadGroupProducts() {
        groupProductsModel.setRowCount(0);

        ProductGroup selectedGroup = (ProductGroup) groupComboBox.getSelectedItem();

        if (selectedGroup != null) {
            DecimalFormat df = new DecimalFormat("#,##0.00");

            List<Product> products = service.getProductsByGroup(selectedGroup.getName());
            for (Product product : products) {
                groupProductsModel.addRow(new Object[]{
                        product.getName(),
                        product.getDescription(),
                        product.getManufacturer(),
                        product.getQuantity(),
                        df.format(product.getPrice()),
                        df.format(product.getTotalValue())
                });
            }

            double groupValue = service.getGroupTotalValue(selectedGroup.getName());
            selectedGroupValueLabel.setText("Вартість групи \"" + selectedGroup.getName() + "\": " + df.format(groupValue) + " грн");
        }
    }
}