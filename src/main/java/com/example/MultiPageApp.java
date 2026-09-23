// src/main/java/com/example/MultiPageApp.java
package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // UI Style Constants
    private static final Color COLOR_BG = new Color(0, 0, 0);
    private static final Color COLOR_SIDEBAR = new Color(74, 74, 74);
    private static final Color COLOR_BUTTON_GREEN = new Color(0, 185, 106);
    private static final Color COLOR_TEXT_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_HIGHLIGHT = new Color(130, 130, 130);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MultiPageApp().initAndShowGUI());
    }

    private void initAndShowGUI() {
        frame = new JFrame("Multi-Page Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(COLOR_BG);

        // Main Layout: Sidebar Left, Content Right
        frame.setLayout(new BorderLayout());

        // Setup Sidebar Container
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setPreferredSize(new Dimension(80, 600));
        sidebarWrapper.setBackground(COLOR_SIDEBAR);

        // Sidebar list for page icons (grows downward)
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebarWrapper.add(sidebar, BorderLayout.NORTH);

        // Setup Main Content Layout
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(COLOR_BG);

        // Add core panels to frame
        frame.add(sidebarWrapper, BorderLayout.WEST);
        frame.add(contentContainer, BorderLayout.CENTER);

        // Initialize with Creation Screen
        showCreationScreen();

        frame.setVisible(true);
    }

    private void showCreationScreen() {
        // Create the specialized Page Creation View
        JPanel creationPanel = new JPanel(new BorderLayout());
        creationPanel.setBackground(COLOR_BG);

        // Top Header Panel containing a back arrow
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        headerPanel.setBackground(COLOR_BG);
        JLabel backArrow = new JLabel("➔"); // Placeholder visual navigation anchor
        backArrow.setForeground(COLOR_TEXT_WHITE);
        backArrow.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(backArrow);
        creationPanel.add(headerPanel, BorderLayout.NORTH);

        // Central Body Text Instruction Area
        JLabel infoLabel = new JLabel("Click the button below to initialize a new viewport page.", SwingConstants.CENTER);
        infoLabel.setForeground(COLOR_TEXT_WHITE);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        creationPanel.add(infoLabel, BorderLayout.CENTER);

        // Bottom Action Panel hosting the creation action
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        bottomPanel.setBackground(COLOR_BG);
        
        JButton createButton = new JButton("Create New Page");
        createButton.setPreferredSize(new Dimension(160, 40));
        createButton.setBackground(COLOR_BUTTON_GREEN);
        createButton.setForeground(COLOR_TEXT_WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setFont(new Font("Arial", Font.BOLD, 14));

        createButton.addActionListener(e -> {
            createNewPage();
        });

        bottomPanel.add(createButton);
        creationPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Register view structure
        contentContainer.add(creationPanel, "CREATION_SCREEN");
        cardLayout.show(contentContainer, "CREATION_SCREEN");
    }

    private void createNewPage() {
        pageCounter++;
        String pageId = "PAGE_" + pageCounter;
        String pageTitle = "Page " + pageCounter;

        // Formulate canvas panel for content architecture
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_BG);
        
        JLabel contentLabel = new JLabel("Welcome to " + pageTitle + " Content Body Layer", SwingConstants.CENTER);
        contentLabel.setForeground(COLOR_TEXT_WHITE);
        contentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(contentLabel, BorderLayout.CENTER);

        // Formulate corresponding sidebar element
        JPanel iconItem = new JPanel(new GridBagLayout());
        iconItem.setPreferredSize(new Dimension(60, 60));
        iconItem.setMaximumSize(new Dimension(60, 60));
        iconItem.setBackground(COLOR_SIDEBAR);
        iconItem.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JLabel circleVisual = new JLabel(String.valueOf(pageCounter), SwingConstants.CENTER);
        circleVisual.setPreferredSize(new Dimension(45, 45));
        circleVisual.setOpaque(true);
        circleVisual.setBackground(COLOR_BG);
        circleVisual.setForeground(COLOR_TEXT_WHITE);
        circleVisual.setFont(new Font("Arial", Font.BOLD, 14));
        iconItem.add(circleVisual);

        PageData newPageData = new PageData(pageId, iconItem, circleVisual, contentPanel);

        // Wire click interactivity directly onto selection item
        iconItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchActivePage(newPageData);
            }
        });

        // Insert at index 0 pushes prior nodes downward
        pages.add(0, newPageData);
        contentContainer.add(contentPanel, pageId);

        // Reconstruct graphical hierarchy of sidebar
        rebuildSidebarView();
        
        // Auto-focus the newly instantiated workspace
        switchActivePage(newPageData);
    }

    private void rebuildSidebarView() {
        sidebar.removeAll();
        sidebar.add(Box.createVerticalStrut(15));

        // Inject the primary add operator control top-level
        JPanel addIconPanel = new JPanel(new GridBagLayout());
        addIconPanel.setPreferredSize(new Dimension(60, 60));
        addIconPanel.setMaximumSize(new Dimension(60, 60));
        addIconPanel.setBackground(COLOR_SIDEBAR);
        
        JLabel plusLabel = new JLabel("+", SwingConstants.CENTER);
        plusLabel.setPreferredSize(new Dimension(45, 45));
        plusLabel.setOpaque(true);
        plusLabel.setBackground(COLOR_BG);
        plusLabel.setForeground(COLOR_TEXT_WHITE);
        plusLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        addIconPanel.add(plusLabel);
        addIconPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearSidebarHighlights();
                cardLayout.show(contentContainer, "CREATION_SCREEN");
            }
        });
        
        sidebar.add(addIconPanel);
        sidebar.add(Box.createVerticalStrut(15));

        // Append managed application pages down the collection hierarchy
        for (PageData p : pages) {
            sidebar.add(p.iconPanel);
            sidebar.add(Box.createVerticalStrut(10));
        }

        sidebar.revalidate();
        sidebar.repaint();
    }

    private void switchActivePage(PageData targetPage) {
        activePage = targetPage;
        clearSidebarHighlights();
        
        // Apply target highlight configuration
        targetPage.visualNode.setBackground(COLOR_HIGHLIGHT);
        cardLayout.show(contentContainer, targetPage.id);
    }

    private void clearSidebarHighlights() {
        for (PageData p : pages) {
            p.visualNode.setBackground(COLOR_BG);
        }
    }

    // Structured Entity wrapping state data for programmatic mutation
    private static class PageData {
        String id;
        JPanel iconPanel;
        JLabel visualNode;
        JPanel contentPanel;

        PageData(String id, JPanel iconPanel, JLabel visualNode, JPanel contentPanel) {
            this.id = id;
            this.iconPanel = iconPanel;
            this.visualNode = visualNode;
            this.contentPanel = contentPanel;
        }
    }
}
