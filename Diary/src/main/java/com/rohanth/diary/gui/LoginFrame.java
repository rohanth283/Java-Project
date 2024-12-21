package com.rohanth.diary.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rohanth.diary.service.UserService;

@Component
public class LoginFrame extends JFrame {
    @Autowired
    private UserService userService;
    @Autowired
    private MainFrame mainFrame;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setupUI();
        setupIcon();
    }

    public UserService getUserService() {
        return userService;
    }

    private void setupUI() {
        setTitle("Diary Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());
        panel.add(loginButton, gbc);

        // Add Register button
        gbc.gridx = 0;
        gbc.gridy = 3;  // Adjust if needed based on your layout
        gbc.gridwidth = 2;
        JButton registerButton = new JButton("Register New User");
        styleButton(registerButton);
        registerButton.addActionListener(e -> {
            this.setVisible(false);
            var registerFrame = new RegisterFrame(this);
            registerFrame.setVisible(true);
        });
        panel.add(registerButton, gbc);

        add(panel);
    }

    private void setupIcon() {
        try {
            Image icon = ImageIO.read(Objects.requireNonNull(
                getClass().getResourceAsStream("/images/diary-icon.png")
            ));
            setIconImage(icon);
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        var user = userService.authenticate(username, password);
        if (user != null) {
            mainFrame.setUser(user);
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add the same styleButton method as in RegisterFrame
    private void styleButton(JButton button) {
        button.setFont(new Font("SF Pro Text", Font.PLAIN, 13));
        button.setBackground(new Color(0, 122, 255));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 111, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 122, 255));
            }
        });
    }
}