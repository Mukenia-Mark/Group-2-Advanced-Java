import javax.swing.*;
import java.awt.*;

public class UserSettingsPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardContainer;

    private JButton backButton;
    private JButton myBooksButton;
    private JButton logoutButton;
    private JButton addBookButton; // Librarian only
    private JButton viewAllBorrowedButton; // Librarian only

    public UserSettingsPanel(CardLayout cardLayout, JPanel cardContainer) {
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;
        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JButton("<- Back to Home");
        topPanel.add(backButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        myBooksButton = new JButton("My Borrowed Books");
        logoutButton = new JButton("Logout");

        addBookButton = new JButton("Add New Book");
        viewAllBorrowedButton = new JButton("View All Borrowed Books");
        addBookButton.setVisible(false);
        viewAllBorrowedButton.setVisible(false);

        centerPanel.add(myBooksButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Adds spacing
        centerPanel.add(addBookButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(viewAllBorrowedButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // More spacing
        centerPanel.add(logoutButton);

        backButton.addActionListener(e -> cardLayout.show(cardContainer, "Home"));

        logoutButton.addActionListener(e -> {
            Main.loggedInUserName = null;
            Main.loggedInUserRole = null;

            cardLayout.show(cardContainer, "Login");
        });

        this.add(topPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    public void updateUserView(String userRole) {
        if ("librarian".equals(userRole)) {
            addBookButton.setVisible(true);
            viewAllBorrowedButton.setVisible(true);

        } else {
            addBookButton.setVisible(false);
            viewAllBorrowedButton.setVisible(false);
        }
    }
}