import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class LoginPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardContainer;

    public LoginPanel(CardLayout cardLayout, JPanel cardContainer) {
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;

        this.setLayout(new BorderLayout());

        JTabbedPane loginPane = new JTabbedPane();

        JPanel loginPanel = new JPanel(new GridLayout(4,1));
        JPanel signUpPanel = new JPanel(new GridLayout(5,1));

        String logoImagePath = "/KCA_logo_60*60.jpeg";

        URL logoImageUrl = getClass().getResource(logoImagePath);

        ImageIcon logoIcon = null;
        if (logoImageUrl != null) {
            logoIcon = new ImageIcon(logoImageUrl);
        } else {
            System.err.println("Error: Image not found at " + logoImagePath);
        }

        loginPane.addTab("Login", loginPanel);
        loginPane.addTab("SignUp", signUpPanel);
        JPanel cancelButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");

        // Login section
        JPanel loginLogoPanel = new JPanel();
        JLabel loginLogoLabel = new JLabel(logoIcon);
        loginLogoLabel.setPreferredSize(new Dimension(60,60));

        JPanel loginUsernamePanel = new JPanel();
        JLabel loginUsernameLabel = new JLabel("Username");
        JTextField loginUsernameTextField = new JTextField();
        loginUsernameTextField.setPreferredSize(new Dimension(150, 40));

        JPanel loginPasswordPanel = new JPanel();
        JLabel loginPasswordLabel = new JLabel("Password");
        JTextField loginPasswordTextField = new JTextField();
        loginPasswordTextField.setPreferredSize(new Dimension(150, 40));

        JPanel loginButtonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40));

        loginLogoPanel.add(loginLogoLabel);
        loginUsernamePanel.add(loginUsernameLabel);
        loginUsernamePanel.add(loginUsernameTextField);
        loginPasswordPanel.add(loginPasswordLabel);
        loginPasswordPanel.add(loginPasswordTextField);
        loginButtonPanel.add(loginButton);

        loginPanel.add(loginLogoPanel);
        loginPanel.add(loginUsernamePanel);
        loginPanel.add(loginPasswordPanel);
        loginPanel.add(loginButtonPanel);

        //SignUp section
        JPanel signupLogoPanel = new JPanel();
        JLabel signUpLogoLabel = new JLabel(logoIcon);

        JPanel signUpUsernamePanel = new JPanel();
        JLabel signUpUsernameLabel = new JLabel("Username");
        JTextField signUpUsernameTextField = new JTextField();
        signUpUsernameTextField.setPreferredSize(new Dimension(150, 40));

        //JPanel signUpEmailPanel = new JPanel();
        //JLabel signUpEmailLabel = new JLabel("Email");
        //JTextField signUpEmailTextField = new JTextField();
        //signUpEmailTextField.setPreferredSize(new Dimension(150, 40));

        JPanel signUpPasswordPanel = new JPanel();
        JLabel signUpPasswordLabel = new JLabel("Password");
        JTextField signUpPasswordTextField = new JTextField();
        signUpPasswordTextField.setPreferredSize(new Dimension(150, 40));

        JPanel signUpPasswordConfirmPanel = new JPanel();
        JLabel signUpPasswordConfirmLabel = new JLabel("Confirm Password");
        JTextField signUpPasswordConfirmTextField = new JTextField();
        signUpPasswordConfirmTextField.setPreferredSize(new Dimension(150, 40));

        JPanel signUpButtonPanel = new JPanel();
        JButton signUpButton = new JButton("SignUp");
        signUpButton.setPreferredSize(new Dimension(100, 40));

        signupLogoPanel.add(signUpLogoLabel);
        signUpUsernamePanel.add(signUpUsernameLabel);
        signUpUsernamePanel.add(signUpUsernameTextField);
        //signUpEmailPanel.add(signUpEmailLabel);
        //signUpEmailPanel.add(signUpEmailTextField);
        signUpPasswordPanel.add(signUpPasswordLabel);
        signUpPasswordPanel.add(signUpPasswordTextField);
        signUpPasswordConfirmPanel.add(signUpPasswordConfirmLabel);
        signUpPasswordConfirmPanel.add(signUpPasswordConfirmTextField);
        signUpButtonPanel.add(signUpButton);

        signUpPanel.add(signupLogoPanel);
        signUpPanel.add(signUpUsernamePanel);
        //signUpPanel.add(signUpEmailPanel);
        signUpPanel.add(signUpPasswordPanel);
        signUpPanel.add(signUpPasswordConfirmPanel);
        signUpPanel.add(signUpButtonPanel);


        this.add(loginPane, BorderLayout.CENTER);
        this.add(cancelButtonPanel, BorderLayout.SOUTH);

        cancelButtonPanel.add(cancelButton);

        cancelButton.addActionListener(e -> {
            this.cardLayout.show(this.cardContainer, "Home");
        });

        loginButton.addActionListener(e -> {
            String userName = loginUsernameTextField.getText();
            String password = loginPasswordTextField.getText();
            String userRole = DatabaseManager.validateUser(userName, password);

            if (userRole != null) {
                System.out.println("Login successful for user: " + userName + " with role: " + userRole);

                Main.loggedInUserName = userName;
                Main.loggedInUserRole = userRole;

                Main.updateUIVisibility();

                cardLayout.show(cardContainer, "Home");
            } else {
                System.err.println("Login failed for user: " + userName);
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        signUpButton.addActionListener(e -> {
            String username = signUpUsernameTextField.getText();
            String password = signUpPasswordTextField.getText();
            String confirmPassword = signUpPasswordConfirmTextField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error",
                JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean registrationSuccess = DatabaseManager.registerUser(username, password);

            if (registrationSuccess) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);

                signUpUsernameTextField.setText("");
                //signUpEmailTextField.setText("");n
                signUpPasswordTextField.setText("");
                signUpPasswordConfirmTextField.setText("");

            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. The username may already be taken.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

