

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProductDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField manufacturerField;
    private JTextField priceField;
    private JTextField quantityField;

    private Product result;
    private String groupName;
    private Product originalProduct;

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
            quantityField.setText(String.valueOf(product.getQuantity()));
        }

        setSize(450, 400);
        setLocationRelativeTo(owner);
    }

    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Назва
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Назва:"), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        formPanel.add(nameField, gbc);

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
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Виробник:"), gbc);

        manufacturerField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        formPanel.add(manufacturerField, gbc);

        // Ціна
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Ціна:"), gbc);

        priceField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        formPanel.add(priceField, gbc);

        // Кількість
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Кількість:"), gbc);

        quantityField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        formPanel.add(quantityField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Зберегти");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProduct();
            }
        });

        JButton cancelButton = new JButton("Скасувати");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = null;
                dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

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
            int quantity = Integer.parseInt(quantityField.getText().trim());

            if (price < 0) {
                JOptionPane.showMessageDialog(this,
                        "Ціна не може бути від'ємною!",
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this,
                        "Кількість не може бути від'ємною!",
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            result = new Product(name, description, manufacturer, quantity, price, groupName);
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Будь ласка, введіть коректні числові значення для ціни та кількості!",
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public Product showDialog() {
        setVisible(true);
        return result;
    }
}
