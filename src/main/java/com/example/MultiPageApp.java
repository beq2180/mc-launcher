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
        sidebarWrapper.setPreferredSize(new Dimension(80, 650));
        sidebarWrapper.setBackground(COLOR_SIDEBAR);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebarWrapper.add(sidebar, BorderLayout.NORTH);

        // Core Viewport Component Workspace Card Manager
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(COLOR_BG);

        frame.add(sidebarWrapper, BorderLayout.WEST);
        frame.add(contentContainer, BorderLayout.CENTER);

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

        JLabel infoLabel = new JLabel("Click the action button below to spin up a new working viewport.", SwingConstants.CENTER);
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

        JPanel pageWorkspace = new JPanel(new GridBagLayout());
        pageWorkspace.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 30, 15, 30);
        gbc.fill = GridBagConstraints.BOTH;

        // Custom Compact Input Text Box Module Area
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setBackground(COLOR_TEXTBOX_BG);
        inputTextArea.setForeground(COLOR_TEXT_WHITE);
        inputTextArea.setCaretColor(COLOR_TEXT_WHITE);
        inputTextArea.setFont(new Font("Arial", Font.PLAIN, 15));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Restore saved document configurations dynamically
        File pageStorageFile = new File("mods", pageId + "_data.txt");
        if (pageStorageFile.exists()) {
            try {
                String savedText = new String(Files.readAllBytes(pageStorageFile.toPath()));
                inputTextArea.setText(savedText);
            } catch (IOException ignored) {}
        }

        // Live text modifications persistence engine thread sync tracker
        inputTextArea.getDocument().addDocumentListener(new DocumentListener() {
            private void commitToDisk() {
                try {
                    Files.write(pageStorageFile.toPath(), inputTextArea.getText().getBytes());
                } catch (IOException ignored) {}
            }
            @Override public void insertUpdate(DocumentEvent e) { commitToDisk(); }
            @Override public void removeUpdate(DocumentEvent e) { commitToDisk(); }
            @Override public void changedUpdate(DocumentEvent e) { commitToDisk(); }
        });

        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_TEXTBOX_BG);

        // Constrain layout sizing horizontally slightly down from default bounds
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        gbc.weightx = 0.65; 
        gbc.weighty = 0.75; 
        pageWorkspace.add(scrollPane, gbc);

        // Functional "Mods" Button Action Layer Component
        JButton modsButton = new JButton("Mods");
        modsButton.setBackground(COLOR_BUTTON_GRAY);
        modsButton.setForeground(COLOR_TEXT_WHITE);
        modsButton.setFocusPainted(false);
        modsButton.setBorderPainted(false);
        modsButton.setFont(new Font("Arial", Font.BOLD, 14));

        modsButton.addActionListener(e -> {
            File directory = new File("mods");
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(directory);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Unable to track system file target location path.");
                }
            }
            
            // Collect mod folder element tracking details and stream to interface view
            File[] fileCollection = directory.listFiles();
            if (fileCollection != null && fileCollection.length > 0) {
                StringBuilder builder = new StringBuilder();
                builder.append("\n=== Mod Folder Contents ===\n");
                for (File file : fileCollection) {
                    builder.append("- ").append(file.getName()).append("\n");
                }
                inputTextArea.append(builder.toString());
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.30;
        gbc.weighty = 0.10;
        pageWorkspace.add(modsButton, gbc);

        // Application Shutdown Execution Component Layer "Launch"
        JButton launchButton = new JButton("Launch");
        launchButton.setBackground(COLOR_BUTTON_GREEN);
        launchButton.setForeground(COLOR_TEXT_WHITE);
        launchButton.setFocusPainted(false);
        launchButton.setBorderPainted(false);
        launchButton.setFont(new Font("Arial", Font.BOLD, 16));
        launchButton.addActionListener(e -> System.exit(0));

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.35;
        gbc.weighty = 0.10;
        pageWorkspace.add(launchButton, gbc);

        // Sidebar Navigation Node Object Properties Initialization
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

        pages.add(0, newPageData);
        contentContainer.add(pageWorkspace, pageId);

        rebuildSidebarView();
        switchActivePage(newPageData);
    }

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
