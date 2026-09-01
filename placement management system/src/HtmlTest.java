import javax.swing.*;
import java.awt.*;

public class HtmlTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());
        
        JLabel label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        String text = "DWDDWDJWDWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW";
        String html = String.format("<html><div style='text-align: center; width: %dpx; word-wrap: break-word;'>%s</div></html>", 200, text);
        label.setText(html);
        
        frame.add(label, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        // Wait 2s and exit
        new Timer(2000, e -> System.exit(0)).start();
    }
}
