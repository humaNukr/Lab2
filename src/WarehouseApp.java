// Клас для управління іконками (IconManager.java)
import javax.swing.*;
import java.awt.*;

public class IconManager {

    private static IconManager instance;

    // Приватний конструктор для сінглтону
    private IconManager() {
    }

    // Доступ до єдиного екземпляру класу
    public static IconManager getInstance() {
        if (instance == null) {
            instance = new IconManager();
        }
        return instance;
    }

    // Завантаження іконки з ресурсів
    public ImageIcon loadIcon(String path) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            return icon;
        } catch (Exception e) {
            System.err.println("Помилка завантаження іконки " + path + ": " + e.getMessage());
            return null;
        }
    }

    // Завантаження і масштабування іконки
    public ImageIcon loadAndResizeIcon(String path, int width, int height) {
        ImageIcon icon = loadIcon(path);
        if (icon != null) {
            Image img = icon.getImage();
            Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImg);
        }
        return null;
    }

    // Встановлення іконки для кнопки
    public void setButtonIcon(JButton button, String iconPath) {
        ImageIcon icon = loadAndResizeIcon(iconPath, 16, 16);
        if (icon != null) {
            button.setIcon(icon);
        }
    }

    // Встановлення іконки для кнопки з вказаними розмірами
    public void setButtonIcon(JButton button, String iconPath, int width, int height) {
        ImageIcon icon = loadAndResizeIcon(iconPath, width, height);
        if (icon != null) {
            button.setIcon(icon);
        }
    }
}