package com.rohanth.diary.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rohanth.diary.model.DiaryEntry;
import com.rohanth.diary.model.User;
import com.rohanth.diary.service.DiaryService;

@Component
public class MainFrame extends JFrame {
    @Autowired
    private DiaryService diaryService;

    private User currentUser;
    private DefaultListModel<DiaryEntry> entriesListModel;
    private JList<DiaryEntry> entriesList;
    private JTextField titleField;
    private JTextArea contentArea;
    private DiaryEntry currentEntry;
    private List<DiaryEntry> allEntries = new ArrayList<>();
    private JTextField searchField;

    public MainFrame() {
        setupUI();
        setupIcon();
    }

    public void setUser(User user) {
        this.currentUser = user;
        refreshEntries();
    }

    private void setupUI() {
        setTitle("My Diary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Set background color for the main frame
        getContentPane().setBackground(new Color(245, 245, 247));

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);

        // Left panel - entries list
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(245, 245, 247));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Search panel with more padding
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBackground(new Color(245, 245, 247));
        searchField = new JTextField() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !(getFocusListeners().length > 0 && getFocusListeners()[0].equals(this))) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(150, 150, 150)); // Gray color for placeholder
                    g2.setFont(getFont());
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    g2.drawString("Search...", getInsets().left, (getHeight() + fm.getAscent())/2 - 2);
                    g2.dispose();
                }
            }
        };
        searchField.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 218, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterEntries(); }
            public void removeUpdate(DocumentEvent e) { filterEntries(); }
            public void insertUpdate(DocumentEvent e) { filterEntries(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Entries list
        entriesListModel = new DefaultListModel<>();
        entriesList = new JList<>(entriesListModel);
        entriesList.setBackground(Color.WHITE);
        entriesList.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        entriesList.setCellRenderer(new EntryListCellRenderer());
        entriesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entriesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEntry();
            }
        });

        JScrollPane listScrollPane = new JScrollPane(entriesList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(218, 218, 218)));
        
        // New Entry button
        JButton newEntryButton = new JButton("New Entry");
        styleButton(newEntryButton);
        newEntryButton.addActionListener(e -> newEntry());

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(newEntryButton, BorderLayout.SOUTH);

        // Right panel - entry editor
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(new Color(245, 245, 247));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        titleField = new JTextField();
        titleField.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(218, 218, 218)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        contentArea = new JTextArea();
        contentArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setBorder(BorderFactory.createLineBorder(new Color(218, 218, 218)));

        // Save button
        JButton saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> saveEntry());

        rightPanel.add(titleField, BorderLayout.NORTH);
        rightPanel.add(contentScroll, BorderLayout.CENTER);
        rightPanel.add(saveButton, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane);
    }

    private void setupIcon() {
        try {
            // Load the icon from resources
            Image icon = ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/diary-icon.png")
            ));
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    private void refreshEntries() {
        allEntries = diaryService.getEntriesForUser(currentUser.getId());
        filterEntries(); // Apply any existing search filter
    }

    private void filterEntries() {
        String searchText = searchField.getText().toLowerCase().trim();
        entriesListModel.clear();
        
        for (DiaryEntry entry : allEntries) {
            if (searchText.isEmpty() || 
                entry.getTitle().toLowerCase().contains(searchText) ||
                entry.getContent().toLowerCase().contains(searchText)) {
                entriesListModel.addElement(entry);
            }
        }
    }

    private void loadSelectedEntry() {
        var selected = entriesList.getSelectedValue();
        if (selected != null) {
            currentEntry = selected;
            titleField.setText(selected.getTitle());
            contentArea.setText(selected.getContent());
        }
    }

    private void newEntry() {
        entriesList.clearSelection();
        currentEntry = null;
        titleField.setText("");
        contentArea.setText("");
        titleField.requestFocus();
    }

    private void saveEntry() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a title",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate titles, excluding the current entry being edited
        boolean isDuplicate = allEntries.stream()
                .anyMatch(entry -> 
                    entry.getTitle().equalsIgnoreCase(title) && 
                    (currentEntry == null || !entry.getId().equals(currentEntry.getId()))
                );

        if (isDuplicate) {
            JOptionPane.showMessageDialog(this,
                    "An entry with this title already exists. Please choose a different title.",
                    "Duplicate Title",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DiaryEntry entry = new DiaryEntry(
                currentEntry != null ? currentEntry.getId() : null,
                currentUser.getId(),
                title,
                content,
                currentEntry != null ? currentEntry.getCreatedAt() : null
        );

        diaryService.saveEntry(entry);
        refreshEntries();
        newEntry();
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        button.setBackground(new Color(0, 122, 255));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 111, 230));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 122, 255));
            }
        });
    }
}
