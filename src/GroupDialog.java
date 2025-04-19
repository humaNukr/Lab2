

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GroupDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;

    private ProductGroup result;
    private ProductGroup originalGroup;

    public GroupDialog(Frame owner, ProductGroup group) {
        super(owner, group == null ? "Додати групу товарів" : "Редагувати групу товарів", true);

        this.originalGroup = group;

        setupUI();

        if (group != null) {
            nameField.setText(group.getName());
            descriptionArea.setText(group.getDescription());
        }

        setSize(400, 300);
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

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton saveButton = new JButton("Зберегти");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGroup();
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

    private void saveGroup() {
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Назва групи не може бути порожньою!",
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        result = new ProductGroup(name, description);

        // Зберігаємо оригінальні продукти, якщо це редагування
        if (originalGroup != null) {
            result.setProducts(originalGroup.getProducts());
        }

        dispose();
    }

    public ProductGroup showDialog() {
        setVisible(true);
        return result;
    }
}