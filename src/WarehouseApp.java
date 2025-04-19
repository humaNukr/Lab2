
import javax.swing.*;

public class WarehouseApp {
    public static void main(String[] args) {
        //Потрібно, щоб програма виглядала гарніше
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        }catch (Exception e) {
            e.printStackTrace();
        }
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }
}