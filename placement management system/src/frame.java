import javax.swing.*;
import java.awt.*;
import java.io.*;

// Placement Management System - Main UI Frame
public class frame {

    private static final String PWD_FILE = "admin_pwd.txt";

    public static String getPassword() {
        try {
            File f = new File(PWD_FILE);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String pwd = br.readLine();
                br.close();
                if (pwd != null && !pwd.trim().isEmpty()) {
                    return pwd.trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin"; // default
    }

    public static void updatePassword(String newPwd) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(PWD_FILE));
            bw.write(newPwd);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // ================= 1. MAIN FRAME =================
        JFrame frame = new JFrame("Placement Management System");
        frame.setSize(1200, 700);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen me open hoga
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        // ================= 2. TOP HEADER IMAGE =================
        // Header me Poornima banner ko full width aur HD clarity ke sath set kiya hai
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Thoda top/bottom spacing

        String headerPath = new java.io.File("src/images/img_3.png").exists() ? "src/images/img_3.png" : "placement management system/src/images/img_3.png";
        ImageIcon headerIcon = new ImageIcon(headerPath);
        
        // Original full resolution use kar rahe hain taaki blur na ho aur bada/chauda dikhe
        JLabel headerLabel = new JLabel(headerIcon, JLabel.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        frame.add(headerPanel, BorderLayout.NORTH); // Frame ke TOP pe set kiya


        // ================= 3. CENTER CONTENT (LOGIN FORM + WATERMARK BG) =================
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(Color.WHITE);

        // Watermark image load kar rahe hain
        String sideImgPath = new java.io.File("src/images/img.png").exists() ? "src/images/img.png" : "placement management system/src/images/img.png";
        ImageIcon watermarkIcon = new ImageIcon(sideImgPath);
        Image watermarkImg = watermarkIcon.getImage();

        // Login Card Panel (Watermark background ke sath)
        JPanel loginCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Graphics2D se image ko halka (watermark / transparent) banaya hai
                Graphics2D g2d = (Graphics2D) g.create();
                // 0.22f opacity -> image watermark jaisi 22% light dikhegi
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
                g2d.drawImage(watermarkImg, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        loginCard.setLayout(null);
        loginCard.setPreferredSize(new Dimension(800, 360));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); // Card border

        // Main Headings
        JLabel heading = new JLabel("PLACEMENT MANAGEMENT SYSTEM", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setBounds(0, 15, 800, 30);
        loginCard.add(heading);

        JLabel loginTitle = new JLabel("LOGIN", JLabel.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 18));
        loginTitle.setForeground(new Color(60, 60, 60));
        loginTitle.setBounds(0, 48, 800, 25);
        loginCard.add(loginTitle);

        // Email Label & Input (Watermark ke upar aayenge)
        JLabel email = new JLabel("Email");
        email.setFont(new Font("Arial", Font.BOLD, 15));
        email.setBounds(220, 90, 100, 22);
        loginCard.add(email);

        JTextField txtEmail = new RoundedTextField(20);
        txtEmail.setFont(new Font("Arial", Font.PLAIN, 14));
        txtEmail.setBounds(220, 115, 360, 36);
        txtEmail.setBackground(new Color(255, 255, 255, 240)); // Clean background
        loginCard.add(txtEmail);

        // Password Label & Input
        JLabel password = new JLabel("Password");
        password.setFont(new Font("Arial", Font.BOLD, 15));
        password.setBounds(220, 160, 120, 22);
        loginCard.add(password);

        JPasswordField txtPassword = new RoundedPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBounds(220, 185, 360, 36);
        txtPassword.setBackground(new Color(255, 255, 255, 240));
        loginCard.add(txtPassword);

        JToggleButton btnShowPwd = new JToggleButton("\uD83D\uDC41"); // Eye emoji
        btnShowPwd.setBounds(585, 185, 45, 36);
        btnShowPwd.setFocusPainted(false);
        btnShowPwd.setContentAreaFilled(false);
        btnShowPwd.setBorderPainted(false);
        btnShowPwd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        char defaultEcho = txtPassword.getEchoChar();
        btnShowPwd.addActionListener(e -> {
            if (btnShowPwd.isSelected()) {
                txtPassword.setEchoChar((char) 0);
                btnShowPwd.setText("\u2716"); // Cross symbol
            } else {
                txtPassword.setEchoChar(defaultEcho);
                btnShowPwd.setText("\uD83D\uDC41"); // Eye emoji
            }
        });
        loginCard.add(btnShowPwd);

        // Login Button
        JButton btnLogin = new RoundedButton("LOGIN", 20);
        btnLogin.setBackground(new Color(0, 153, 76)); // Green color
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBounds(320, 240, 160, 40);

        // Login Click Event: Direct smooth transition to Dashboard
        btnLogin.addActionListener(e -> {
            String emailVal = txtEmail.getText().trim();
            String passVal = new String(txtPassword.getPassword()).trim();

            if (emailVal.isEmpty() || passVal.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter Email and Password!", "Login Alert", JOptionPane.WARNING_MESSAGE);
            } else if (emailVal.equals("college@poornima.org") && passVal.equals(getPassword())) {
                // Bina kisi delay/popup ke direct Dashboard open hoga
                new Dashboard();
                frame.dispose(); // Purana frame close
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Email or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Trigger login button click when Enter is pressed in text fields
        txtEmail.addActionListener(e -> btnLogin.doClick());
        txtPassword.addActionListener(e -> btnLogin.doClick());

        loginCard.add(btnLogin);
        frame.getRootPane().setDefaultButton(btnLogin);

        centerContainer.add(loginCard);
        frame.add(centerContainer, BorderLayout.CENTER); // Center me login area add kiya


        // ================= 4. VISIBILITY =================
        frame.setVisible(true);
    }
}

