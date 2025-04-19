// Основний клас програми (ProductManagementSystem.java)
import javax.swing.*;
import java.awt.*;

public class ProductManagementSystem {
    public static void main(String[] args) {
        // Встановлення Look and Feel системи
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame app = new MainFrame();
            app.setVisible(true);
        });
    }
}