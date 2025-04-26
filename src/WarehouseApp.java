
import javax.swing.*;

/**
 * Головний клас WarehouseApp для запуску програми управління складом.
 * Встановлює стиль інтерфейсу Nimbus та відкриває головне вікно програми.
 *
 * @author Артем Гриценко, Заровська Анастасія
 */

public class WarehouseApp {
    public static void main(String[] args) {
        // Встановлює стиль інтерфейсу Nimbus.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}