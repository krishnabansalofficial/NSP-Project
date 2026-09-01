import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

// =========================================================================
// Placement Management System - Main Dashboard
// Yeh page login ke baad open hota hai
// =========================================================================
public class Dashboard {

    private JFrame dashFrame;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private DefaultTableModel driveTableModel;
    private DefaultTableModel studentTableModel;
    private JTable studentTable;
    private JTable driveTable;
    private DefaultTableModel noticeTableModel;
    private JTable noticeTable;
    private JPanel topNav;
    private JLabel lblTotalStudents, lblTotalCompanies, lblTotalPlaced;
    private int studentCount = 0;
    private int companyCount = 0;
    private int noticeCount = 0;
    private int placedCount = 0;

    public Dashboard() {
        initDashboard();
    }

    private void initDashboard() {
        // ================= 1. MAIN FRAME =================
        dashFrame = new JFrame("Placement Management System - Admin Dashboard");
        dashFrame.setSize(1200, 750);
        dashFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen me open hoga
        dashFrame.setLocationRelativeTo(null);
        dashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dashFrame.setLayout(new BorderLayout());

        // ================= 2. TOP NAVIGATION BAR =================
        topNav = new JPanel(new BorderLayout());
        topNav.setBackground(new Color(25, 42, 86)); // Deep Navy Blue
        topNav.setPreferredSize(new Dimension(1200, 65));
        topNav.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        // Logo & Title Left side
        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        navLeft.setOpaque(false);

        String logoPath = new java.io.File("src/images/img_4.png").exists() ? "src/images/img_4.png" : "placement management system/src/images/img_4.png";
        ImageIcon logoIcon = new ImageIcon(logoPath);
        Image logoImg = logoIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        navLeft.add(logoLabel);

        JLabel appTitle = new JLabel("POORNIMA - PLACEMENT MANAGEMENT SYSTEM");
        appTitle.setForeground(Color.WHITE);
        appTitle.setFont(new Font("Arial", Font.BOLD, 18));
        navLeft.add(appTitle);

        topNav.add(navLeft, BorderLayout.WEST);

        // Center: Main Navigation Menu Buttons (Centered in Navbar)
        JPanel navCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 5));
        navCenter.setOpaque(false);

        JButton btnHome = createNavButton("Home");
        JButton btnPlacements = createNavButton("Placements");
        JButton btnNotice = createNavButton("Notice");
        JButton btnAbout = createNavButton("About");
        JButton btnProfile = createNavButton("Profile");

        // Helper function (local) to set active border
        java.util.function.Consumer<JButton> setActiveTab = (activeBtn) -> {
            JButton[] navButtons = {btnHome, btnPlacements, btnNotice, btnAbout, btnProfile};
            for (JButton b : navButtons) {
                if (b == activeBtn) {
                    b.setForeground(new Color(243, 156, 18)); // Orange text
                    b.setBorder(new javax.swing.border.AbstractBorder() {
                        @Override
                        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(243, 156, 18)); // Orange line
                            int lineThickness = 3;
                            int padding = 12;
                            g2.fillRoundRect(x + padding, y + height - lineThickness, width - 2 * padding, lineThickness, 4, 4);
                            g2.dispose();
                        }
                        @Override
                        public Insets getBorderInsets(Component c) {
                            return new Insets(8, 16, 8, 16);
                        }
                    });
                } else {
                    b.setForeground(Color.WHITE); // Reset to white
                    b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
                }
            }
            if (activeBtn.getParent() != null) {
                activeBtn.getParent().revalidate();
                activeBtn.getParent().repaint();
            }
        };

        // Initialize with Home active
        setActiveTab.accept(btnHome);

        btnHome.addActionListener(e -> {
            cardLayout.show(cardPanel, "Home");
            setActiveTab.accept(btnHome);
        });
        btnPlacements.addActionListener(e -> {
            cardLayout.show(cardPanel, "Placements");
            setActiveTab.accept(btnPlacements);
        });
        btnNotice.addActionListener(e -> {
            cardLayout.show(cardPanel, "Notice");
            setActiveTab.accept(btnNotice);
        });
        btnAbout.addActionListener(e -> {
            topNav.setVisible(false);
            cardLayout.show(cardPanel, "About");
            setActiveTab.accept(btnAbout);
        });
        btnProfile.addActionListener(e -> {
            topNav.setVisible(false);
            cardLayout.show(cardPanel, "Profile");
            setActiveTab.accept(btnProfile);
        });

        navCenter.add(btnHome);
        navCenter.add(btnPlacements);
        navCenter.add(btnNotice);
        navCenter.add(btnAbout);
        navCenter.add(btnProfile);

        topNav.add(navCenter, BorderLayout.CENTER);

        // Removed Logout button from top nav (moved to Profile page)
        dashFrame.add(topNav, BorderLayout.NORTH);


        // ================= 3. CENTER CONTENT (STATS + ACTIONS + TABLE) =================
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(245, 246, 250)); // Light modern background
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Reduced margin

        // ================= 3. CARD LAYOUT SETUP (SWITCHING VIEWS) =================
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(new Color(245, 246, 250));

        // --- HOME PANEL ---
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(new Color(245, 246, 250));
        homePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10)); // Reduced margin

        // Stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(2000, 110));
        
        JPanel card1 = createStatCard("Total Students", "0", 1, new Color(41, 128, 185));
        lblTotalStudents = (JLabel)((JPanel)card1.getComponent(0)).getComponent(1);
        JPanel card2 = createStatCard("Companies Visited", "0", 2, new Color(142, 68, 173));
        lblTotalCompanies = (JLabel)((JPanel)card2.getComponent(0)).getComponent(1);
        JPanel card3 = createStatCard("Students Placed", "0", 3, new Color(39, 174, 96));
        lblTotalPlaced = (JLabel)((JPanel)card3.getComponent(0)).getComponent(1);

        statsPanel.add(card1);
        statsPanel.add(card2);
        statsPanel.add(card3);
        statsPanel.add(createStatCard("Highest Package", "0 LPA", 4, new Color(230, 126, 34)));

        homePanel.add(statsPanel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Student Table
        JPanel studentSection = new JPanel(new BorderLayout());
        studentSection.setOpaque(false);
        
        JPanel studentHeader = new JPanel(new BorderLayout());
        studentHeader.setOpaque(false);
        studentHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel studentTitle = new JLabel("Registered Student Details");
        studentTitle.setFont(new Font("Arial", Font.BOLD, 20));
        studentTitle.setForeground(new Color(44, 62, 80));
        
        studentHeader.add(studentTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnAddStudent = createActionButton("+ Add Student", new Color(52, 152, 219));
        btnAddStudent.setPreferredSize(new Dimension(150, 35));
        btnAddStudent.addActionListener(e -> showStudentForm(-1));

        actionPanel.add(btnAddStudent);
        
        studentHeader.add(actionPanel, BorderLayout.EAST);

        studentSection.add(studentHeader, BorderLayout.NORTH);

        String[] studentCols = {"Roll No", "Student Name", "Branch", "CGPA", "Status", "Actions"};
        studentTableModel = new DefaultTableModel(null, studentCols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; } // Actions column is editable
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(45); // Increased for multi-line support
        studentTable.setShowVerticalLines(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.setFillsViewportHeight(true);
        studentTable.setBackground(Color.WHITE);

        JTableHeader sHeader = studentTable.getTableHeader();
        sHeader.setFont(new Font("Arial", Font.BOLD, 14));
        sHeader.setBackground(new Color(236, 240, 241));
        sHeader.setForeground(new Color(44, 62, 80));
        sHeader.setPreferredSize(new Dimension(100, 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < studentTable.getColumnCount() - 1; i++) {
            if (i == 1) { // Apply to Student Name
                studentTable.getColumnModel().getColumn(i).setCellRenderer(new TextAreaRenderer());
            } else {
                studentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        studentTable.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        studentTable.getColumnModel().getColumn(5).setCellEditor(new ActionEditor(new JCheckBox(), 1, this));
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(160);
        
        JScrollPane sScroll = new JScrollPane(studentTable);
        sScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        sScroll.setPreferredSize(new Dimension(1000, 400));
        studentSection.add(sScroll, BorderLayout.CENTER);
        homePanel.add(studentSection);

        // Wrap Home in Scroll
        JScrollPane homeScroll = new JScrollPane(homePanel);
        homeScroll.setBorder(null);
        homeScroll.getVerticalScrollBar().setUnitIncrement(16);


        // --- PLACEMENTS PANEL ---
        JPanel placementsPanel = new JPanel();
        placementsPanel.setLayout(new BoxLayout(placementsPanel, BoxLayout.Y_AXIS));
        placementsPanel.setBackground(new Color(245, 246, 250));
        placementsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JPanel tableAndNoticePanel = new JPanel(new BorderLayout(20, 0));
        tableAndNoticePanel.setOpaque(false);

        // Drive Table
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        
        JPanel driveHeader = new JPanel(new BorderLayout());
        driveHeader.setOpaque(false);
        driveHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel driveTitle = new JLabel("Placement Drives & Records");
        driveTitle.setFont(new Font("Arial", Font.BOLD, 20));
        driveHeader.add(driveTitle, BorderLayout.WEST);

        JPanel driveActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        driveActionPanel.setOpaque(false);

        JButton btnAddCompany = createActionButton("+ Add Company Drive", new Color(155, 89, 182));
        btnAddCompany.setPreferredSize(new Dimension(180, 35));
        btnAddCompany.addActionListener(e -> showCompanyForm(-1));

        driveActionPanel.add(btnAddCompany);
        
        driveHeader.add(driveActionPanel, BorderLayout.EAST);

        tableContainer.add(driveHeader, BorderLayout.NORTH);

        String[] driveColumns = {"Drive ID", "Company Name", "Job Role", "Package", "Min CGPA", "Date", "Status", "Actions"};
        driveTableModel = new DefaultTableModel(null, driveColumns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; } // Actions column is editable
        };
        driveTable = new JTable(driveTableModel);
        driveTable.setRowHeight(45); // Increased for multi-line support
        driveTable.setShowVerticalLines(false);
        driveTable.setIntercellSpacing(new Dimension(0, 0));
        driveTable.setFillsViewportHeight(true);
        driveTable.setBackground(Color.WHITE);

        JTableHeader dHeader = driveTable.getTableHeader();
        dHeader.setFont(new Font("Arial", Font.BOLD, 14));
        dHeader.setBackground(new Color(236, 240, 241));
        dHeader.setForeground(new Color(44, 62, 80));
        dHeader.setPreferredSize(new Dimension(100, 40));
        
        for (int i = 0; i < driveTable.getColumnCount() - 1; i++) {
            if (i == 1 || i == 2) { // Apply to Company Name and Job Role
                driveTable.getColumnModel().getColumn(i).setCellRenderer(new TextAreaRenderer());
            } else {
                driveTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        driveTable.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
        driveTable.getColumnModel().getColumn(7).setCellEditor(new ActionEditor(new JCheckBox(), 2, this));
        driveTable.getColumnModel().getColumn(7).setPreferredWidth(160);
        
        JScrollPane dScroll = new JScrollPane(driveTable);
        dScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        tableContainer.add(dScroll, BorderLayout.CENTER);
        tableAndNoticePanel.add(tableContainer, BorderLayout.CENTER);

        // Removed side notice board from here
        placementsPanel.add(tableAndNoticePanel);

        // --- NEW NOTICE PANEL ---
        JPanel noticePagePanel = new JPanel(new BorderLayout());
        noticePagePanel.setBackground(new Color(245, 246, 250));
        noticePagePanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JPanel noticeHeader = new JPanel(new BorderLayout());
        noticeHeader.setOpaque(false);
        noticeHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblNoticePageTitle = new JLabel("Placement Notices");
        lblNoticePageTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblNoticePageTitle.setForeground(new Color(44, 62, 80));
        noticeHeader.add(lblNoticePageTitle, BorderLayout.WEST);

        JPanel noticeActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        noticeActionPanel.setOpaque(false);
        JButton btnAddNotice = createActionButton("+ Add Notice", new Color(230, 126, 34)); // Orange button
        btnAddNotice.setPreferredSize(new Dimension(150, 35));
        btnAddNotice.addActionListener(e -> showNoticeForm(-1));
        noticeActionPanel.add(btnAddNotice);
        noticeHeader.add(noticeActionPanel, BorderLayout.EAST);

        noticePagePanel.add(noticeHeader, BorderLayout.NORTH);

        String[] noticeCols = {"Notice ID", "Date", "Notice Details", "Actions"};
        noticeTableModel = new DefaultTableModel(null, noticeCols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        noticeTable = new JTable(noticeTableModel);
        noticeTable.setRowHeight(70); // Increased height to show multiple lines for big notices
        noticeTable.setShowVerticalLines(false);
        noticeTable.setIntercellSpacing(new Dimension(0, 0));
        noticeTable.setFillsViewportHeight(true);
        noticeTable.setBackground(Color.WHITE);

        JTableHeader nHeader = noticeTable.getTableHeader();
        nHeader.setFont(new Font("Arial", Font.BOLD, 14));
        nHeader.setBackground(new Color(236, 240, 241));
        nHeader.setForeground(new Color(44, 62, 80));
        nHeader.setPreferredSize(new Dimension(100, 40));
        
        for (int i = 0; i < noticeTable.getColumnCount() - 1; i++) {
            if (i == 2) {
                // Apply multi-line renderer for Notice Details
                noticeTable.getColumnModel().getColumn(i).setCellRenderer(new TextAreaRenderer());
            } else {
                noticeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
        
        // Adjust Column Widths
        noticeTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        noticeTable.getColumnModel().getColumn(0).setMaxWidth(120);
        
        noticeTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        noticeTable.getColumnModel().getColumn(1).setMaxWidth(150);
        
        noticeTable.getColumnModel().getColumn(3).setCellRenderer(new ActionRenderer());
        noticeTable.getColumnModel().getColumn(3).setCellEditor(new ActionEditor(new JCheckBox(), 3, this));
        noticeTable.getColumnModel().getColumn(3).setPreferredWidth(160);
        noticeTable.getColumnModel().getColumn(3).setMaxWidth(180);
        
        JScrollPane nScroll = new JScrollPane(noticeTable);
        nScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        noticePagePanel.add(nScroll, BorderLayout.CENTER);

        // Add to Cards
        cardPanel.add(homeScroll, "Home");
        cardPanel.add(placementsPanel, "Placements");
        cardPanel.add(noticePagePanel, "Notice");

        // --- ABOUT PANEL ---
        JPanel aboutPanel = new JPanel(new BorderLayout());
        aboutPanel.setBackground(new Color(245, 246, 250));
        
        // 1. Top Bar & Header Image
        JPanel aboutUpperPanel = new JPanel(new BorderLayout());
        
        JPanel aboutTopBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        aboutTopBar.setBackground(Color.WHITE);
        
        RoundedButton btnAboutBack = new RoundedButton("\u2190 Back", 15);
        btnAboutBack.setBackground(new Color(236, 240, 241));
        btnAboutBack.setForeground(new Color(44, 62, 80));
        btnAboutBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnAboutBack.setPreferredSize(new Dimension(90, 32));
        btnAboutBack.addActionListener(e -> {
            topNav.setVisible(true);
            cardLayout.show(cardPanel, "Home");
            setActiveTab.accept(btnHome);
        });
        
        JLabel lblAboutPageTitle = new JLabel("About Page");
        lblAboutPageTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblAboutPageTitle.setForeground(new Color(44, 62, 80));
        
        aboutTopBar.add(btnAboutBack);
        aboutTopBar.add(lblAboutPageTitle);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        String headerPath = new java.io.File("src/images/img_3.png").exists() ? "src/images/img_3.png" : "placement management system/src/images/img_3.png";
        ImageIcon headerIcon = new ImageIcon(headerPath);
        JLabel headerLabel = new JLabel(headerIcon, JLabel.CENTER);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        aboutUpperPanel.add(aboutTopBar, BorderLayout.NORTH);
        aboutUpperPanel.add(headerPanel, BorderLayout.CENTER);
        
        aboutPanel.add(aboutUpperPanel, BorderLayout.NORTH);
        
        // 2. Center Content (Credits & Back Button)
        JPanel creditsPanel = new JPanel(new GridBagLayout());
        creditsPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel lblTitle = new JLabel("Placement Management System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(44, 62, 80));

        JLabel lblVersion = new JLabel("Version 1.0");
        lblVersion.setFont(new Font("Arial", Font.PLAIN, 16));
        lblVersion.setForeground(Color.GRAY);

        JLabel lblDev = new JLabel("Developed by: Krishna Bansal");
        lblDev.setFont(new Font("Arial", Font.BOLD, 20));
        lblDev.setForeground(new Color(41, 128, 185));

        JLabel lblGuide = new JLabel("Guided by: Sikha Gautam");
        lblGuide.setFont(new Font("Arial", Font.BOLD, 20));
        lblGuide.setForeground(new Color(39, 174, 96));

        gbc.insets = new Insets(10, 10, 10, 10);
        creditsPanel.add(lblTitle, gbc);
        creditsPanel.add(lblVersion, gbc);
        
        gbc.insets = new Insets(40, 10, 5, 10);
        creditsPanel.add(lblDev, gbc);
        
        gbc.insets = new Insets(5, 10, 30, 10);
        creditsPanel.add(lblGuide, gbc);
        

        
        aboutPanel.add(creditsPanel, BorderLayout.CENTER);

        cardPanel.add(aboutPanel, "About");

        // --- PROFILE PANEL ---
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(new Color(245, 246, 250));
        
        // 1. Top Bar & Header Image
        JPanel pUpperPanel = new JPanel(new BorderLayout());
        
        JPanel pTopBar = new JPanel(new BorderLayout());
        pTopBar.setBackground(Color.WHITE);
        
        JPanel pTopLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pTopLeft.setOpaque(false);
        
        RoundedButton btnProfileBack = new RoundedButton("\u2190 Back", 15);
        btnProfileBack.setBackground(new Color(236, 240, 241));
        btnProfileBack.setForeground(new Color(44, 62, 80));
        btnProfileBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnProfileBack.setPreferredSize(new Dimension(90, 32));
        btnProfileBack.addActionListener(e -> {
            topNav.setVisible(true);
            cardLayout.show(cardPanel, "Home");
            setActiveTab.accept(btnHome);
        });
        
        JLabel lblProfilePageTitle = new JLabel("Profile Page");
        lblProfilePageTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblProfilePageTitle.setForeground(new Color(44, 62, 80));
        
        pTopLeft.add(btnProfileBack);
        pTopLeft.add(lblProfilePageTitle);
        
        JPanel pTopRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pTopRight.setOpaque(false);
        
        RoundedButton btnProfileLogout = new RoundedButton("Logout", 20);
        btnProfileLogout.setBackground(new Color(231, 76, 60));
        btnProfileLogout.setForeground(Color.WHITE);
        btnProfileLogout.setFont(new Font("Arial", Font.BOLD, 13));
        btnProfileLogout.setPreferredSize(new Dimension(90, 32));
        btnProfileLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(dashFrame, "Are you sure you want to logout?", "Logout Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dashFrame.dispose();
                frame.main(new String[]{});
            }
        });
        pTopRight.add(btnProfileLogout);

        pTopBar.add(pTopLeft, BorderLayout.WEST);
        pTopBar.add(pTopRight, BorderLayout.EAST);
        pUpperPanel.add(pTopBar, BorderLayout.NORTH);
        
        profilePanel.add(pUpperPanel, BorderLayout.NORTH);
        
        // 2. Profile Content
        JPanel pContentPanel = new JPanel(new GridBagLayout());
        pContentPanel.setOpaque(false);
        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.gridwidth = GridBagConstraints.REMAINDER;
        pgbc.anchor = GridBagConstraints.CENTER;
        
        // Profile Info
        JLabel lblProfileTitle = new JLabel("Training & Placement Officer");
        lblProfileTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblProfileTitle.setForeground(new Color(44, 62, 80));
        
        JLabel lblAdminName = new JLabel("Admin Profile");
        lblAdminName.setFont(new Font("Arial", Font.PLAIN, 18));
        lblAdminName.setForeground(Color.GRAY);
        
        JLabel lblEmail = new JLabel("Email: tpo@poornima.org");
        lblEmail.setFont(new Font("Arial", Font.PLAIN, 16));
        lblEmail.setForeground(Color.GRAY);
        
        pgbc.insets = new Insets(10, 10, 5, 10);
        pContentPanel.add(lblProfileTitle, pgbc);
        pgbc.insets = new Insets(0, 10, 5, 10);
        pContentPanel.add(lblAdminName, pgbc);
        pgbc.insets = new Insets(0, 10, 30, 10);
        pContentPanel.add(lblEmail, pgbc);
        
        // Settings Card
        JPanel settingsCard = new JPanel(new GridBagLayout());
        settingsCard.setBackground(Color.WHITE);
        settingsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 40, 20, 40)
        ));
        
        GridBagConstraints sGbc = new GridBagConstraints();
        sGbc.fill = GridBagConstraints.HORIZONTAL;
        sGbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel lblSettings = new JLabel("Change Password");
        lblSettings.setFont(new Font("Arial", Font.BOLD, 18));
        lblSettings.setForeground(new Color(41, 128, 185));
        
        sGbc.gridx = 0; sGbc.gridy = 0; sGbc.gridwidth = 2;
        settingsCard.add(lblSettings, sGbc);
        
        sGbc.gridwidth = 1; sGbc.gridy = 1;
        settingsCard.add(new JLabel("Current Password:"), sGbc);
        sGbc.gridx = 1;
        JPasswordField txtCurrPwd = new JPasswordField(15);
        settingsCard.add(txtCurrPwd, sGbc);
        
        sGbc.gridx = 0; sGbc.gridy = 2;
        settingsCard.add(new JLabel("New Password:"), sGbc);
        sGbc.gridx = 1;
        JPasswordField txtNewPwd = new JPasswordField(15);
        settingsCard.add(txtNewPwd, sGbc);
        
        sGbc.gridx = 0; sGbc.gridy = 3;
        settingsCard.add(new JLabel("Confirm Password:"), sGbc);
        sGbc.gridx = 1;
        JPasswordField txtConfPwd = new JPasswordField(15);
        settingsCard.add(txtConfPwd, sGbc);
        
        sGbc.gridx = 0; sGbc.gridy = 4; sGbc.gridwidth = 2;
        RoundedButton btnSavePwd = new RoundedButton("Save Changes", 15);
        btnSavePwd.setBackground(new Color(39, 174, 96));
        btnSavePwd.setForeground(Color.WHITE);
        btnSavePwd.setPreferredSize(new Dimension(150, 35));
        btnSavePwd.addActionListener(ev -> {
            JOptionPane.showMessageDialog(dashFrame, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtCurrPwd.setText(""); txtNewPwd.setText(""); txtConfPwd.setText("");
        });
        settingsCard.add(btnSavePwd, sGbc);
        
        pgbc.insets = new Insets(10, 10, 30, 10);
        pContentPanel.add(settingsCard, pgbc);
        

        
        profilePanel.add(pContentPanel, BorderLayout.CENTER);
        cardPanel.add(profilePanel, "Profile");

        dashFrame.add(cardPanel, BorderLayout.CENTER);


        // Frame Visible
        dashFrame.setVisible(true);
    }

    // ================= HELPER METHODS (VIVA ME EXPLAIN KARNA EASY HAI) =================

    // Helper: Colorful Stat Card banane ke liye (Graphics2D Vector Icons ke sath)
    private JPanel createStatCard(String title, String value, int iconType, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor), // Left colored accent border
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));

        // Left Side: Title & Value
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTitle.setForeground(new Color(120, 120, 120));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 22));
        lblValue.setForeground(new Color(40, 40, 40));

        textPanel.add(lblTitle);
        textPanel.add(lblValue);
        card.add(textPanel, BorderLayout.CENTER);

        // Right Side: Beautiful Vector Icon Drawn with Graphics2D
        JPanel badgePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 1. Soft Circular Background
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
                g2.fillOval(3, 3, 44, 44);

                // 2. Vector Icon Drawing
                g2.setColor(accentColor);

                if (iconType == 1) {
                    // --- Graduation Cap (Student) ---
                    int[] xPoints = {25, 40, 25, 10};
                    int[] yPoints = {15, 22, 29, 22};
                    g2.fillPolygon(xPoints, yPoints, 4); // Cap Top Diamond
                    g2.fillArc(16, 24, 18, 10, 0, -180); // Cap Bottom Base
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawLine(40, 22, 40, 32); // Tassel

                } else if (iconType == 2) {
                    // --- Company Building ---
                    g2.fillRect(14, 13, 22, 24); // Building Body
                    g2.setColor(Color.WHITE);
                    // Windows
                    g2.fillRect(17, 16, 4, 4);
                    g2.fillRect(25, 16, 4, 4);
                    g2.fillRect(17, 22, 4, 4);
                    g2.fillRect(25, 22, 4, 4);
                    // Door
                    g2.fillRect(22, 29, 6, 8);

                } else if (iconType == 3) {
                    // --- Checkmark / Placed Tick ---
                    g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(15, 25, 22, 32);
                    g2.drawLine(22, 32, 35, 17);

                } else if (iconType == 4) {
                    // --- Salary / Package Symbol ---
                    g2.setFont(new Font("Arial", Font.BOLD, 26));
                    FontMetrics fm = g2.getFontMetrics();
                    String text = "\u20B9"; // Rupee symbol
                    int textW = fm.stringWidth(text);
                    g2.drawString(text, 25 - textW / 2, 33);
                }

                g2.dispose();
            }
        };
        badgePanel.setOpaque(false);
        badgePanel.setPreferredSize(new Dimension(50, 50));

        card.add(badgePanel, BorderLayout.EAST);

        return card;
    }

    // Helper: Top Navbar Button banane ke liye
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Helper: Action Button banane ke liye
    private JButton createActionButton(String text, Color bg) {
        RoundedButton btn = new RoundedButton(text, 20);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(185, 38));
        return btn;
    }

    void deleteStudent(int row) {
        int choice = JOptionPane.showConfirmDialog(dashFrame, "Are you sure you want to delete this student?", "Delete Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            studentTableModel.removeRow(row);
            studentCount--;
            lblTotalStudents.setText(String.valueOf(studentCount));
        }
        studentTable.clearSelection();
    }

    void deleteCompany(int row) {
        int choice = JOptionPane.showConfirmDialog(dashFrame, "Are you sure you want to delete this company drive?", "Delete Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            driveTableModel.removeRow(row);
            companyCount--;
            lblTotalCompanies.setText(String.valueOf(companyCount));
        }
        driveTable.clearSelection();
    }

    // Form: Add/Edit Student
    void showStudentForm(int editRow) {
        String dialogTitle = (editRow == -1) ? "Add New Student" : "Edit Student Details";
        JDialog dialog = new JDialog(dashFrame, dialogTitle, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400); 
        dialog.setLocationRelativeTo(dashFrame);

        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 10, 20)); 
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25)); 
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new JLabel("Roll Number:"));
        JTextField txtRoll = new RoundedTextField(15);
        contentPanel.add(txtRoll);

        contentPanel.add(new JLabel("Student Name:"));
        JTextField txtName = new RoundedTextField(15);
        contentPanel.add(txtName);

        contentPanel.add(new JLabel("Branch:"));
        String[] branches = {"CSE", "AIDS", "CS AI", "CS DS", "IOT"};
        JComboBox<String> cbBranch = new JComboBox<>(branches);
        cbBranch.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        contentPanel.add(cbBranch);

        contentPanel.add(new JLabel("Current CGPA:"));
        JTextField txtCgpa = new RoundedTextField(15);
        contentPanel.add(txtCgpa);

        if (editRow != -1) {
            txtRoll.setText(studentTableModel.getValueAt(editRow, 0).toString());
            txtName.setText(studentTableModel.getValueAt(editRow, 1).toString());
            cbBranch.setSelectedItem(studentTableModel.getValueAt(editRow, 2).toString());
            txtCgpa.setText(studentTableModel.getValueAt(editRow, 3).toString());
        }

        JButton btnSave = new RoundedButton((editRow == -1) ? "Save Student" : "Update Student", 15);
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnSave.addActionListener(e -> {
            String roll = txtRoll.getText().trim();
            String name = txtName.getText().trim();
            String cgpaStr = txtCgpa.getText().trim();

            if(roll.isEmpty() || name.isEmpty() || cgpaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                return;
            }

            // Validation: Roll number only digits
            if (!roll.matches("\\d+")) {
                JOptionPane.showMessageDialog(dialog, "Roll Number should contain only numbers!");
                return;
            }

            // Validation: Name only letters and spaces
            if (!name.matches("^[a-zA-Z\\s]+$")) {
                JOptionPane.showMessageDialog(dialog, "Student Name should contain only letters!");
                return;
            }

            // Validation: CGPA between 1 and 10
            try {
                double cgpa = Double.parseDouble(cgpaStr);
                if (cgpa < 1.0 || cgpa > 10.0) {
                    JOptionPane.showMessageDialog(dialog, "CGPA must be between 1 and 10!");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "CGPA must be a valid number!");
                return;
            }

            if (editRow == -1) {
                studentCount++;
                lblTotalStudents.setText(String.valueOf(studentCount));
                
                studentTableModel.addRow(new Object[]{
                    roll, name, cbBranch.getSelectedItem().toString(), 
                    cgpaStr, "Applied", ""
                });
                JOptionPane.showMessageDialog(dialog, "Student '" + name + "' has been successfully added!");
            } else {
                studentTableModel.setValueAt(roll, editRow, 0);
                studentTableModel.setValueAt(name, editRow, 1);
                studentTableModel.setValueAt(cbBranch.getSelectedItem().toString(), editRow, 2);
                studentTableModel.setValueAt(cgpaStr, editRow, 3);
                JOptionPane.showMessageDialog(dialog, "Student '" + name + "' has been successfully updated!");
            }
            studentTable.clearSelection();
            dialog.dispose();
        });
        
        contentPanel.add(new JLabel("")); // Empty space
        contentPanel.add(btnSave);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(btnSave);
        dialog.setVisible(true);
        
        // Modal dialog band hone ke baad (chahe save ho ya cancel), selection hata do
        studentTable.clearSelection();
    }

    // Form: Add/Edit Company Drive
    void showCompanyForm(int editRow) {
        String dialogTitle = (editRow == -1) ? "Add Company Drive" : "Edit Company Drive";
        JDialog dialog = new JDialog(dashFrame, dialogTitle, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450); 
        dialog.setLocationRelativeTo(dashFrame);

        JPanel contentPanel = new JPanel(new GridLayout(7, 2, 10, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new JLabel("Company Name:"));
        JTextField txtComp = new RoundedTextField(15);
        contentPanel.add(txtComp);

        contentPanel.add(new JLabel("Job Role:"));
        JTextField txtRole = new RoundedTextField(15);
        contentPanel.add(txtRole);

        contentPanel.add(new JLabel("Package (LPA):"));
        JTextField txtPkg = new RoundedTextField(15);
        contentPanel.add(txtPkg);

        contentPanel.add(new JLabel("Min CGPA Req:"));
        JTextField txtMinCgpa = new RoundedTextField(15);
        contentPanel.add(txtMinCgpa);

        contentPanel.add(new JLabel("Drive Date:"));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MMM-yyyy");
        dateEditor.getTextField().setEditable(false);
        dateEditor.getTextField().setBackground(Color.WHITE);
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        contentPanel.add(dateSpinner);

        if (editRow != -1) {
            txtComp.setText(driveTableModel.getValueAt(editRow, 1).toString());
            txtRole.setText(driveTableModel.getValueAt(editRow, 2).toString());
            String pkg = driveTableModel.getValueAt(editRow, 3).toString().replace(" LPA", "");
            txtPkg.setText(pkg);
            txtMinCgpa.setText(driveTableModel.getValueAt(editRow, 4).toString());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date parsedDate = sdf.parse(driveTableModel.getValueAt(editRow, 5).toString());
                dateSpinner.setValue(parsedDate);
            } catch (Exception ex) { }
        }

        JButton btnAdd = new RoundedButton((editRow == -1) ? "Add Drive to Table" : "Update Drive", 15);
        btnAdd.setBackground(new Color(39, 174, 96));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));

        btnAdd.addActionListener(e -> {
            String comp = txtComp.getText().trim();
            String role = txtRole.getText().trim();
            String pkgStr = txtPkg.getText().trim();
            String cgpaStr = txtMinCgpa.getText().trim();

            if(comp.isEmpty() || role.isEmpty() || pkgStr.isEmpty() || cgpaStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                return;
            }

            // Package Validation (numeric)
            try {
                double pkg = Double.parseDouble(pkgStr);
                if (pkg < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Package should contain only valid positive numbers!");
                return;
            }

            // CGPA Validation (1 to 10)
            try {
                double cgpa = Double.parseDouble(cgpaStr);
                if (cgpa < 1.0 || cgpa > 10.0) {
                    JOptionPane.showMessageDialog(dialog, "Min CGPA must be between 1 and 10!");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Min CGPA must be a valid number!");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dateStr = sdf.format(dateSpinner.getValue());

            if (editRow == -1) {
                companyCount++;
                lblTotalCompanies.setText(String.valueOf(companyCount));
                String driveId = "DRV-" + (101 + driveTableModel.getRowCount());
                driveTableModel.addRow(new Object[]{
                    driveId, comp, role, pkgStr + " LPA", 
                    cgpaStr, dateStr, "Upcoming", ""
                });
                JOptionPane.showMessageDialog(dialog, "Drive has been successfully added!");
            } else {
                driveTableModel.setValueAt(comp, editRow, 1);
                driveTableModel.setValueAt(role, editRow, 2);
                driveTableModel.setValueAt(pkgStr + " LPA", editRow, 3);
                driveTableModel.setValueAt(cgpaStr, editRow, 4);
                driveTableModel.setValueAt(dateStr, editRow, 5);
                JOptionPane.showMessageDialog(dialog, "Drive has been successfully updated!");
            }
            driveTable.clearSelection();
            dialog.dispose();
        });

        contentPanel.add(new JLabel("")); 
        contentPanel.add(btnAdd);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(btnAdd);
        dialog.setVisible(true);
        
        // Modal dialog band hone ke baad, selection hata do
        driveTable.clearSelection();
    }

    // Form: Add/Edit Notice
    void showNoticeForm(int editRow) {
        String dialogTitle = (editRow == -1) ? "Add Notice" : "Edit Notice";
        JDialog dialog = new JDialog(dashFrame, dialogTitle, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 300); 
        dialog.setLocationRelativeTo(dashFrame);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setOpaque(false);
        topPanel.add(new JLabel("Notice Date: "));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MMM-yyyy");
        dateEditor.getTextField().setEditable(false);
        dateEditor.getTextField().setBackground(Color.WHITE);
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(140, 28)); // Made it wider so date is fully visible
        dateSpinner.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        topPanel.add(dateSpinner);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(new JLabel("Notice Details:"), BorderLayout.NORTH);
        JTextArea txtDetails = new JTextArea(5, 30);
        txtDetails.setLineWrap(true);
        txtDetails.setWrapStyleWord(true);
        JScrollPane scrollDetails = new JScrollPane(txtDetails);
        scrollDetails.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        centerPanel.add(scrollDetails, BorderLayout.CENTER);

        if (editRow != -1) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date parsedDate = sdf.parse(noticeTableModel.getValueAt(editRow, 1).toString());
                dateSpinner.setValue(parsedDate);
            } catch (Exception ex) { }
            txtDetails.setText(noticeTableModel.getValueAt(editRow, 2).toString());
        }

        JButton btnSave = new RoundedButton((editRow == -1) ? "Save Notice" : "Update Notice", 15);
        btnSave.setBackground(new Color(230, 126, 34)); // Orange button
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnSave.addActionListener(e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            String dateStr = sdf.format(dateSpinner.getValue());
            String detailsStr = txtDetails.getText().trim();

            if(detailsStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill Notice Details!");
                return;
            }

            if (editRow == -1) {
                noticeCount++;
                String noticeId = "N-" + (100 + noticeCount);
                noticeTableModel.addRow(new Object[]{ noticeId, dateStr, detailsStr, "" });
                JOptionPane.showMessageDialog(dialog, "Notice added successfully!");
            } else {
                noticeTableModel.setValueAt(dateStr, editRow, 1);
                noticeTableModel.setValueAt(detailsStr, editRow, 2);
                JOptionPane.showMessageDialog(dialog, "Notice updated successfully!");
            }
            noticeTable.clearSelection();
            dialog.dispose();
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnSave);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.getRootPane().setDefaultButton(btnSave);
        dialog.setVisible(true);
        noticeTable.clearSelection();
    }

    // Delete Notice
    void deleteNotice(int row) {
        String noticeId = noticeTableModel.getValueAt(row, 0).toString();
        int choice = JOptionPane.showConfirmDialog(dashFrame, "Are you sure you want to delete notice " + noticeId + "?", "Delete Confirm", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            noticeTableModel.removeRow(row);
            JOptionPane.showMessageDialog(dashFrame, "Notice deleted successfully!");
        }
    }

    // Main method for testing directly
    public static void main(String[] args) {
        new Dashboard();
    }
}

// Custom Renderer for Actions Column
class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    public ActionRenderer() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        setBackground(Color.WHITE);

        RoundedButton btnEdit = new RoundedButton("\u270E Edit", 15);
        btnEdit.setBackground(new Color(243, 156, 18));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEdit.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        RoundedButton btnDelete = new RoundedButton("\u2716 Delete", 15);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDelete.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4);
        
        add(btnEdit, gbc);
        add(btnDelete, gbc);
    }
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
        return this;
    }
}

// Custom Editor for Actions Column
class ActionEditor extends DefaultCellEditor {
    private JPanel panel;
    private JButton btnEdit;
    private JButton btnDelete;
    private int currentRow;
    private int tableType; // 1=Student, 2=Company, 3=Notice
    private Dashboard dashboard;

    public ActionEditor(JCheckBox checkBox, int tableType, Dashboard dashboard) {
        super(checkBox);
        this.tableType = tableType;
        this.dashboard = dashboard;
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        btnEdit = new RoundedButton("\u270E Edit", 15);
        btnEdit.setBackground(new Color(243, 156, 18));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEdit.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        btnDelete = new RoundedButton("\u2716 Delete", 15);
        btnDelete.setBackground(new Color(231, 76, 60));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDelete.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            if (this.tableType == 1) this.dashboard.showStudentForm(currentRow);
            else if (this.tableType == 2) this.dashboard.showCompanyForm(currentRow);
            else if (this.tableType == 3) this.dashboard.showNoticeForm(currentRow);
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            if (this.tableType == 1) this.dashboard.deleteStudent(currentRow);
            else if (this.tableType == 2) this.dashboard.deleteCompany(currentRow);
            else if (this.tableType == 3) this.dashboard.deleteNotice(currentRow);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4);

        panel.add(btnEdit, gbc);
        panel.add(btnDelete, gbc);
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentRow = row;
        panel.setBackground(table.getSelectionBackground());
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()));
        return panel;
    }
    
    @Override
    public Object getCellEditorValue() {
        return "";
    }
}

// Custom Renderer for Multi-line Text in JTable
class TextAreaRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {
    public TextAreaRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setFont(new Font("Arial", Font.PLAIN, 13));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(Color.WHITE);
            setForeground(new Color(44, 62, 80));
        }
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, table.getGridColor()), // Bottom line
            BorderFactory.createEmptyBorder(5, 10, 5, 10) // Inner padding
        ));
        
        setText(value != null ? value.toString() : "");
        setToolTipText(getText()); // Show full text on hover
        return this;
    }
}

// Custom Rounded Button Class
class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());
        } else if (getModel().isRollover()) {
            g2.setColor(getBackground().brighter());
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        super.paintComponent(g2);
        g2.dispose();
    }
}

// Custom Rounded Text Field
class RoundedTextField extends JTextField {
    private int radius;
    public RoundedTextField(int radius) {
        super();
        this.radius = radius;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    public RoundedTextField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
        super.paintComponent(g2);
        g2.dispose();
    }
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        g2.dispose();
    }
}

// Custom Rounded Password Field
class RoundedPasswordField extends JPasswordField {
    private int radius;
    public RoundedPasswordField(int radius) {
        super();
        this.radius = radius;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
        super.paintComponent(g2);
        g2.dispose();
    }
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
        g2.dispose();
    }
}
