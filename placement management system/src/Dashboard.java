import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

// =========================================================================
// Placement Management System - Main Dashboard
// Yeh page login ke baad open hota hai
// =========================================================================
public class Dashboard {
    private boolean isAdmin;

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
    private JLabel lblProfileTotalStudents, lblProfileTotalCompanies, lblProfileTotalNotices;
    private int studentCount = 0;
    private int companyCount = 0;
    private int noticeCount = 0;
    private int placedCount = 0;

    public Dashboard(boolean isAdmin) {
        this.isAdmin = isAdmin;
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
        btnAbout.setVisible(!isAdmin);
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
            if (lblProfileTotalStudents != null) {
                lblProfileTotalStudents.setText(String.valueOf(studentTableModel.getRowCount()));
                lblProfileTotalCompanies.setText(String.valueOf(driveTableModel.getRowCount()));
                lblProfileTotalNotices.setText(String.valueOf(noticeTableModel.getRowCount()));
            }
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

        btnAddStudent.setVisible(isAdmin);
        actionPanel.add(btnAddStudent);
        
        studentHeader.add(actionPanel, BorderLayout.EAST);

        studentSection.add(studentHeader, BorderLayout.NORTH);

        String[] studentCols = {"Roll No", "Student Name", "Branch", "CGPA", "Status", "Actions"};
        studentTableModel = new DefaultTableModel(null, studentCols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 5; } // Actions column is editable
        };
        loadTableData(studentTableModel, "students_data.txt");
        studentCount = studentTableModel.getRowCount();
        studentTable = new JTable(studentTableModel);
        studentTable.setRowHeight(45); // Increased for multi-line support
        studentTable.setShowGrid(true);
        studentTable.setGridColor(new Color(220, 220, 220));
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

        btnAddCompany.setVisible(isAdmin);
        driveActionPanel.add(btnAddCompany);
        
        driveHeader.add(driveActionPanel, BorderLayout.EAST);

        tableContainer.add(driveHeader, BorderLayout.NORTH);

        String[] driveColumns = {"Drive ID", "Company Name", "Job Role", "Package", "Min CGPA", "Date", "Status", "Actions"};
        driveTableModel = new DefaultTableModel(null, driveColumns) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; } // Actions column is editable
        };
        loadTableData(driveTableModel, "companies_data.txt");
        companyCount = driveTableModel.getRowCount();
        driveTable = new JTable(driveTableModel);
        driveTable.setRowHeight(45); // Increased for multi-line support
        driveTable.setShowGrid(true);
        driveTable.setGridColor(new Color(220, 220, 220));
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
        btnAddNotice.setVisible(isAdmin);
        noticeActionPanel.add(btnAddNotice);
        noticeHeader.add(noticeActionPanel, BorderLayout.EAST);

        noticePagePanel.add(noticeHeader, BorderLayout.NORTH);

        String[] noticeCols = {"Notice ID", "Date", "Notice Details", "Actions"};
        noticeTableModel = new DefaultTableModel(null, noticeCols) {
            @Override public boolean isCellEditable(int row, int col) { return col == 3; }
        };
        loadTableData(noticeTableModel, "notices_data.txt");
        noticeCount = noticeTableModel.getRowCount();
        noticeTable = new JTable(noticeTableModel);
        noticeTable.setRowHeight(70); // Increased height to show multiple lines for big notices
        noticeTable.setShowGrid(true);
        noticeTable.setGridColor(new Color(220, 220, 220));
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

        JLabel lblDesc = new JLabel("<html><div style='text-align: center; width: 600px;'>"
            + "A simple and easy-to-use platform to manage college placements. "
            + "It brings students, companies, and the placement cell together in one place "
            + "to easily track job drives, student details, and important notices."
            + "</div></html>");
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 15));
        lblDesc.setForeground(new Color(100, 100, 100));

        JLabel lblFeatures = new JLabel("<html><center><b>Key Features:</b><br>"
            + "\u2022 Student Profile Management &nbsp;&nbsp;&nbsp; \u2022 Company Drive Tracking<br>"
            + "\u2022 Notice Board & Updates &nbsp;&nbsp;&nbsp; \u2022 Admin Dashboard</center></html>");
        lblFeatures.setFont(new Font("Arial", Font.PLAIN, 14));
        lblFeatures.setForeground(new Color(44, 62, 80));

        JLabel lblDev = new JLabel("<html><font color='#2C3E50'>Developed by:</font> <font color='#2980B9'>Krishna Bansal (PIET25CS083)</font></html>");
        lblDev.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel lblGuide = new JLabel("<html><font color='#2C3E50'>Guided by:</font> <font color='#27AE60'>Sikha Gautam (Associate Professor)</font></html>");
        lblGuide.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel lblCopy = new JLabel("\u00A9 2026 Poornima Institute of Engineering & Technology. All rights reserved.");
        lblCopy.setFont(new Font("Arial", Font.PLAIN, 12));
        lblCopy.setForeground(Color.GRAY);
        lblCopy.setHorizontalAlignment(JLabel.CENTER);
        lblCopy.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        gbc.insets = new Insets(10, 10, 20, 10);
        creditsPanel.add(lblTitle, gbc);
        
        gbc.insets = new Insets(0, 10, 20, 10);
        creditsPanel.add(lblDesc, gbc);
        
        gbc.insets = new Insets(0, 10, 30, 10);
        creditsPanel.add(lblFeatures, gbc);
        
        gbc.insets = new Insets(10, 10, 5, 10);
        creditsPanel.add(lblDev, gbc);
        
        gbc.insets = new Insets(5, 10, 40, 10);
        creditsPanel.add(lblGuide, gbc);
        
        aboutPanel.add(creditsPanel, BorderLayout.CENTER);
        aboutPanel.add(lblCopy, BorderLayout.SOUTH);

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
        pgbc.fill = GridBagConstraints.HORIZONTAL;
        pgbc.insets = new Insets(10, 20, 10, 20);

        JLabel lblProfileTitle = new JLabel(isAdmin ? "Training & Placement Officer Profile" : "B.Tech Computer Science Student");
        lblProfileTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblProfileTitle.setForeground(new Color(44, 62, 80));
        lblProfileTitle.setHorizontalAlignment(JLabel.CENTER);
        
        pgbc.insets = new Insets(0, 20, 15, 20);
        pContentPanel.add(lblProfileTitle, pgbc);

        // --- Info Card (Rounded) ---
        RoundedPanel infoCard = new RoundedPanel(20, Color.WHITE);
        infoCard.setLayout(new GridLayout(2, 1, 5, 10));
        infoCard.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        infoCard.add(createProfileRow("Email:", isAdmin ? "college@poornima.org" : "krishna@poornima.org"));
        infoCard.add(createProfileRow("Password:", "********"));
        
        pgbc.insets = new Insets(0, 20, 20, 20);
        pContentPanel.add(infoCard, pgbc);
        
        // --- Stats Card ---
        JPanel statsCard = new JPanel(new GridLayout(1, 3, 20, 0));
        statsCard.setOpaque(false);
        
        lblProfileTotalStudents = new JLabel(String.valueOf(studentTableModel.getRowCount()));
        lblProfileTotalCompanies = new JLabel(String.valueOf(driveTableModel.getRowCount()));
        lblProfileTotalNotices = new JLabel(String.valueOf(noticeTableModel.getRowCount()));
        
        statsCard.add(createStatBox("Total Students", lblProfileTotalStudents));
        statsCard.add(createStatBox("Companies Visited", lblProfileTotalCompanies));
        statsCard.add(createStatBox("Active Notices", lblProfileTotalNotices));
        
        if (isAdmin) {
            pgbc.insets = new Insets(0, 20, 30, 20);
            pContentPanel.add(statsCard, pgbc);
        }
        
        // --- Actions Card ---
        JPanel actionCard = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionCard.setOpaque(false);
        
        RoundedButton btnChangePwd = new RoundedButton("Change Password", 20);
        btnChangePwd.setBackground(new Color(41, 128, 185));
        btnChangePwd.setForeground(Color.WHITE);
        btnChangePwd.setPreferredSize(new Dimension(160, 40));
        btnChangePwd.setFont(new Font("Arial", Font.BOLD, 14));
        btnChangePwd.addActionListener(e -> showChangePasswordDialog());
        
        btnChangePwd.setVisible(isAdmin);
        actionCard.add(btnChangePwd);
        
        pgbc.insets = new Insets(0, 20, 20, 20);
        pContentPanel.add(actionCard, pgbc);
        
        profilePanel.add(pContentPanel, BorderLayout.CENTER);
        
        JLabel lblProfileCopy = new JLabel("\u00A9 2026 Poornima Institute of Engineering & Technology. All rights reserved.");
        lblProfileCopy.setFont(new Font("Arial", Font.PLAIN, 12));
        lblProfileCopy.setForeground(Color.GRAY);
        lblProfileCopy.setHorizontalAlignment(JLabel.CENTER);
        lblProfileCopy.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        
        profilePanel.add(lblProfileCopy, BorderLayout.SOUTH);
        
        cardPanel.add(profilePanel, "Profile");

        dashFrame.add(cardPanel, BorderLayout.CENTER);


        
        if (!isAdmin) {
            studentTable.removeColumn(studentTable.getColumnModel().getColumn(5));
            driveTable.removeColumn(driveTable.getColumnModel().getColumn(7));
            noticeTable.removeColumn(noticeTable.getColumnModel().getColumn(3));
        }
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
                saveTableData(studentTableModel, "students_data.txt");
                JOptionPane.showMessageDialog(dialog, "Student '" + name + "' has been successfully added!");
            } else {
                studentTableModel.setValueAt(roll, editRow, 0);
                studentTableModel.setValueAt(name, editRow, 1);
                studentTableModel.setValueAt(cbBranch.getSelectedItem().toString(), editRow, 2);
                studentTableModel.setValueAt(cgpaStr, editRow, 3);
                saveTableData(studentTableModel, "students_data.txt");
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
                saveTableData(noticeTableModel, "notices_data.txt");
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
            saveTableData(noticeTableModel, "notices_data.txt");
            JOptionPane.showMessageDialog(dashFrame, "Notice deleted successfully!");
        }
    }

    // Main method for testing directly
    // Main method for testing directly
    private void saveTableData(DefaultTableModel model, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                StringBuilder rowStr = new StringBuilder();
                for (int j = 0; j < model.getColumnCount() - 1; j++) {
                    rowStr.append(model.getValueAt(i, j).toString());
                    if (j < model.getColumnCount() - 2) {
                        rowStr.append("||");
                    }
                }
                bw.write(rowStr.toString());
                bw.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadTableData(DefaultTableModel model, String filename) {
        File file = new File(filename);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split("\\|\\|");
                Object[] rowData = new Object[model.getColumnCount()];
                for (int i = 0; i < data.length; i++) {
                    rowData[i] = data[i];
                }
                rowData[model.getColumnCount() - 1] = ""; // Actions column
                model.addRow(rowData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Dashboard(true);
    }
    
    private JPanel createProfileRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Arial", Font.BOLD, 15));
        lblLabel.setForeground(new Color(44, 62, 80));
        lblLabel.setPreferredSize(new Dimension(100, 20));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.PLAIN, 15));
        lblValue.setForeground(Color.GRAY);
        
        panel.add(lblLabel, BorderLayout.WEST);
        panel.add(lblValue, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatBox(String title, JLabel lblCount) {
        RoundedPanel panel = new RoundedPanel(20, Color.WHITE);
        panel.setLayout(new GridLayout(2, 1));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        lblCount.setHorizontalAlignment(JLabel.CENTER);
        lblCount.setFont(new Font("Arial", Font.BOLD, 24));
        lblCount.setForeground(new Color(41, 128, 185));
        
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);
        
        panel.add(lblCount);
        panel.add(lblTitle);
        return panel;
    }

    private JPanel createPasswordPanel(JPasswordField pwdField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        panel.add(pwdField, BorderLayout.CENTER);
        
        JToggleButton btnToggle = new JToggleButton("\uD83D\uDC41"); // Eye emoji
        btnToggle.setMargin(new Insets(0, 5, 0, 5));
        btnToggle.setFocusPainted(false);
        btnToggle.setBorderPainted(false);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        char defaultEcho = pwdField.getEchoChar();
        btnToggle.addActionListener(e -> {
            if (btnToggle.isSelected()) {
                pwdField.setEchoChar((char) 0);
                btnToggle.setText("\u2716"); // Cross symbol
            } else {
                pwdField.setEchoChar(defaultEcho);
                btnToggle.setText("\uD83D\uDC41"); // Eye emoji
            }
        });
        
        panel.add(btnToggle, BorderLayout.EAST);
        return panel;
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(dashFrame, "Change Password", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(dashFrame);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Current Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField txtCurr = new RoundedPasswordField(15);
        panel.add(createPasswordPanel(txtCurr), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField txtNew = new RoundedPasswordField(15);
        panel.add(createPasswordPanel(txtNew), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPasswordField txtConf = new RoundedPasswordField(15);
        panel.add(createPasswordPanel(txtConf), gbc);
        
        dialog.add(panel, BorderLayout.CENTER);
        
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        RoundedButton btnSave = new RoundedButton("Update", 15);
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            String curr = new String(txtCurr.getPassword());
            String newP = new String(txtNew.getPassword());
            String confP = new String(txtConf.getPassword());
            
            if (curr.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!curr.equals(frame.getPassword())) {
                JOptionPane.showMessageDialog(dialog, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newP.equals(confP)) {
                JOptionPane.showMessageDialog(dialog, "New password and Confirm password do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            frame.updatePassword(newP);
            JOptionPane.showMessageDialog(dialog, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        RoundedButton btnCancel = new RoundedButton("Cancel", 15);
        btnCancel.addActionListener(e -> dialog.dispose());
        
        bottom.add(btnSave);
        bottom.add(btnCancel);
        dialog.add(bottom, BorderLayout.SOUTH);
        
        dialog.getRootPane().setDefaultButton(btnSave);
        dialog.setVisible(true);
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
class TextAreaRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    private JTextArea textArea;

    public TextAreaRenderer() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 13));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(textArea, gbc);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            textArea.setForeground(table.getSelectionForeground());
        } else {
            setBackground(Color.WHITE);
            textArea.setForeground(new Color(44, 62, 80));
        }
        
        String text = value != null ? value.toString() : "";
        textArea.setText(text);
        
        // Calculate horizontal padding to center short text
        int colWidth = table.getColumnModel().getColumn(column).getWidth();
        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int textWidth = fm.stringWidth(text);
        
        int padX = 10;
        if (textWidth > 0 && textWidth < (colWidth - 20)) {
            padX = (colWidth - textWidth) / 2;
        }
        
        setBorder(BorderFactory.createEmptyBorder(2, padX, 2, 10));
        
        setToolTipText(text);
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
    public RoundedPasswordField(int columns) {
        super(columns);
        this.radius = 15;
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

// Custom Rounded Panel Class
class RoundedPanel extends JPanel {
    private int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
    }
}
