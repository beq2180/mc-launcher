// src/main/java/com/example/MultiPageApp.java
package com.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MultiPageApp {
    private JFrame frame;
    private JPanel sidebar;
    private JPanel contentContainer;
    private CardLayout cardLayout;
    
    private final List<PageData> pages = new ArrayList<>();
    private PageData activePage = null;
    private int pageCounter = 0;

    // UI Configuration Theme Colors
    private static final Color COLOR_BG = new Color(0, 0, 0);
    private static final Color COLOR_SIDEBAR = new Color(74, 74, 74);
    private static final Color COLOR_TEXTBOX_BG = new Color(90, 90, 90);
    private static final Color COLOR_BUTTON_GREEN = new Color(0, 185, 106);
    private static final Color COLOR_BUTTON_GRAY = new Color(60, 60, 60);
    private static final Color COLOR_TEXT_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_HIGHLIGHT = new Color(130, 130, 130);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultiPageApp().initAndShowGUI());
    }

    private void initAndShowGUI() {
        // Initialize the local 'mods' directory safely if it doesn't exist
        File modsDir = new File("mods");
        if (!modsDir.exists()) {
            modsDir.mkdirs();
        }

        frame = new JFrame("Multi-Page App with Mods Integration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(COLOR_BG);
        frame.setLayout(new BorderLayout());

        // Left Navigation Pillar Sidebar Layout
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setPreferredSize(new Dimension(200, 650));
        sidebarWrapper.setBackground(COLOR_SIDEBAR);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        
        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(BorderFactory.createEmptyBorder());
        sidebarScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setBackground(COLOR_SIDEBAR);
        sidebarScroll.getViewport().setBackground(COLOR_SIDEBAR);

        // Control buttons at the top of the sidebar
        JPanel controls = new JPanel(new GridLayout(2, 1, 5, 5));
        controls.setBackground(COLOR_SIDEBAR);
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAddPage = createStyledButton("Add Page", COLOR_BUTTON_GREEN);
        btnAddPage.addActionListener(e -> addNewPage());
        controls.add(btnAddPage);

        JButton btnLoadMods = createStyledButton("Scan Mods", COLOR_BUTTON_GRAY);
        btnLoadMods.addActionListener(e -> scanModsFolder(modsDir));
        controls.add(btnLoadMods);

        sidebarWrapper.add(controls, BorderLayout.NORTH);
        sidebarWrapper.add(sidebarScroll, BorderLayout.CENTER);

        // Main content area using CardLayout
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(COLOR_BG);

        // Add a placeholder panel for when no pages exist
        JPanel placeholder = new JPanel(new GridBagLayout());
        placeholder.setBackground(COLOR_BG);
        JLabel lblPlaceholder = new JLabel("Click 'Add Page' to create a new workspace.");
        lblPlaceholder.setForeground(COLOR_TEXT_WHITE);
        placeholder.add(lblPlaceholder);
        contentContainer.add(placeholder, "PLACEHOLDER");
        cardLayout.show(contentContainer, "PLACEHOLDER");

        // Assemble frame
        frame.add(sidebarWrapper, BorderLayout.WEST);
        frame.add(contentContainer, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addNewPage() {
        pageCounter++;
        String pageId = "page_" + pageCounter;
        String pageName = "Page " + pageCounter;

        // Create UI elements for the page view
        JPanel pagePanel = new JPanel(new BorderLayout());
        pagePanel.setBackground(COLOR_BG);
        pagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(pageName);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        pagePanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea();
        textArea.setBackground(COLOR_TEXTBOX_BG);
        textArea.setForeground(COLOR_TEXT_WHITE);
        textArea.setCaretColor(COLOR_TEXT_WHITE);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBorder(BorderFactory.createLineBorder(COLOR_HIGHLIGHT));
        pagePanel.add(textScroll, BorderLayout.CENTER);

        // Track page state
        PageData pageData = new PageData(pageId, pageName, pagePanel, textArea);
        pages.add(pageData);

        // Sync text modifications directly inside PageData state tracking
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { pageData.setContent(textArea.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { pageData.setContent(textArea.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { pageData.setContent(textArea.getText()); }
        });

        // Add to main content viewport structure
        contentContainer.add(pagePanel, pageId);
        
        // Rebuild sidebar navigation tabs
        refreshSidebar();
        switchToPage(pageData);
    }

    private void switchToPage(PageData page) {
        activePage = page;
        if (page == null) {
            cardLayout.show(contentContainer, "PLACEHOLDER");
        } else {
            cardLayout.show(contentContainer, page.getId());
        }
        refreshSidebar();
    }

    private void refreshSidebar() {
        sidebar.removeAll();

        for (PageData page : pages) {
            JPanel tab = new JPanel(new BorderLayout());
            tab.setMaximumSize(new Dimension(200, 40));
            tab.setPreferredSize(new Dimension(200, 40));
            tab.setBackground(page == activePage ? COLOR_HIGHLIGHT : COLOR_SIDEBAR);
            tab.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            JLabel lblName = new JLabel(page.getName());
            lblName.setForeground(COLOR_TEXT_WHITE);
            tab.add(lblName, BorderLayout.CENTER);

            tab.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    switchToPage(page);
                }
            });

            sidebar.add(tab);
            sidebar.add(Box.createVerticalStrut(2)); // Slight divider spacing
        }

        sidebar.revalidate();
        sidebar.repaint();
    }

    private void scanModsFolder(File modsDir) {
        File[] files = modsDir.listFiles();
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(frame, "No files found in the 'mods' folder.", "Mods Scanner", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder fileList = new StringBuilder("Discovered files inside /mods:\n");
        for (File f : files) {
            if (f.isFile()) {
                fileList.append("- ").append(f.getName()).append(" (").append(f.length()).append(" bytes)\n");
            }
        }
        
        JOptionPane.showMessageDialog(frame, fileList.toString(), "Mods Found", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(COLOR_TEXT_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Inner model layer tracking page attributes and buffered inputs safely
    private static class PageData {
        private final String id;
        private final String name;
        private final JPanel panel;
        private final JTextArea textArea;
        private String content = "";

        public PageData(String id, String name, JPanel panel, JTextArea textArea) {
            this.id = id;
            this.name = name;
            this.panel = panel;
            this.textArea = textArea;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public JPanel getPanel() { return panel; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
