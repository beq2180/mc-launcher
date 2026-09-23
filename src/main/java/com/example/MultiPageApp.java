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

    // UI Style Constants from layout specifications
    private static final Color COLOR_BG = new Color(0, 0, 0);
    private static final Color COLOR_SIDEBAR = new Color(74, 74, 74);
    private static final Color COLOR_TEXTBOX_BG = new Color(90, 90, 90);
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

        // Setup Sidebar Container with pill layout parameters
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setPreferredSize(new Dimension(80, 600));
        sidebarWrapper.setBackground(COLOR_SIDEBAR);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebarWrapper.add(sidebar, BorderLayout.NORTH);

        // Setup Main Content Card Manager Layout
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(COLOR_BG);

        frame.add(sidebarWrapper, BorderLayout.WEST);
        frame.add(contentContainer, BorderLayout.CENTER);

        // Initialize with default template page workspace context
        showCreationScreen();

        frame.setVisible(true);
    }

    private void showCreationScreen() {
        JPanel creationPanel = new JPanel(new BorderLayout());
        creationPanel.setBackground(COLOR_BG);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        headerPanel.setBackground(COLOR_BG);
        JLabel backArrow = new JLabel("➔");
        backArrow.setForeground(COLOR_TEXT_WHITE);
        backArrow.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(backArrow);
        creationPanel.add(headerPanel, BorderLayout.NORTH);

        JLabel infoLabel = new JLabel("Click the button below to initialize a new page view.", SwingConstants.CENTER);
        infoLabel.setForeground(COLOR_TEXT_WHITE);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        creationPanel.add(infoLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        bottomPanel.setBackground(COLOR_BG);
        
        JButton createButton = new JButton("Create New Page");
        createButton.setPreferredSize(new Dimension(160, 40));
        createButton.setBackground(COLOR_BUTTON_GREEN);
        createButton.setForeground(COLOR_TEXT_WHITE);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setFont(new Font("Arial", Font.BOLD, 14));

        createButton.addActionListener(e -> createNewPage());

        bottomPanel.add(createButton);
        creationPanel.add(bottomPanel, BorderLayout.SOUTH);

        contentContainer.add(creationPanel, "CREATION_SCREEN");
        cardLayout.show(contentContainer, "CREATION_SCREEN");
    }

    private void createNewPage() {
        pageCounter++;
        String pageId = "PAGE_" + pageCounter;

        // Parent container for individual page layout layers
        JPanel pageWorkspace = new JPanel(new GridBagLayout());
        pageWorkspace.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 40);
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Large Functional Input Layer (Gray Text Box Area)
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setBackground(COLOR_TEXTBOX_BG);
        inputTextArea.setForeground(COLOR_TEXT_WHITE);
        inputTextArea.setCaretColor(COLOR_TEXT_WHITE);
        inputTextArea.setFont(new Font("Arial", Font.PLAIN, 16));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Enforce rounded pane view via clean abstraction constraints
        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_TEXTBOX_BG);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.8; // Occupies large upper grid sector
        pageWorkspace.add(scrollPane, gbc);

        // 2. Green Launch Control Operator Interaction Action Block
        JButton launchButton = new JButton("Launch");
        launchButton.setBackground(COLOR_BUTTON_GREEN);
        launchButton.setForeground(COLOR_TEXT_WHITE);
        launchButton.setFocusPainted(false);
        launchButton.setBorderPainted(false);
        launchButton.setFont(new Font("Arial", Font.BOLD, 18));
        launchButton.setPreferredSize(new Dimension(0, 50));

        // Triggers safe exit protocol down operational scope context lines
        launchButton.addActionListener(e -> System.exit(0));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1; // Occupies lower action tier space
        pageWorkspace.add(launchButton, gbc);

        // Formulate corresponding sidebar list container elements
        JPanel iconItem = new JPanel(new GridBagLayout());
        iconItem.setPreferredSize(new Dimension(60, 60));
        iconItem.setMaximumSize(new Dimension(60, 60));
        iconItem.setBackground(COLOR_SIDEBAR);

        JLabel circleVisual = new JLabel("", SwingConstants.CENTER);
        circleVisual.setPreferredSize(new Dimension(45, 45));
        circleVisual.setOpaque(true);
        circleVisual.setBackground(COLOR_BG);
        
        iconItem.add(circleVisual);

        PageData newPageData = new PageData(pageId, iconItem, circleVisual, pageWorkspace);

        iconItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchActivePage(newPageData);
            }
        });

        // Insert operations at index location zero pushes older entities downwards
        pages.add(0, newPageData);
        contentContainer.add(pageWorkspace, pageId);

        rebuildSidebarView();
        switchActivePage(newPageData);
    }

    private void rebuildSidebarView() {
        sidebar.removeAll();
        sidebar.add(Box.createVerticalStrut(15));

        // Operational instantiation node creation action setup (+)
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

        // Draw active tracking paths array into target hierarchy positions
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
        
        // Highlights the specific interior layer module elements on selected tracking targets
        targetPage.visualNode.setBackground(COLOR_HIGHLIGHT);
        cardLayout.show(contentContainer, targetPage.id);
    }

    private void clearSidebarHighlights() {
        for (PageData p : pages) {
            p.visualNode.setBackground(COLOR_BG);
        }
    }

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
