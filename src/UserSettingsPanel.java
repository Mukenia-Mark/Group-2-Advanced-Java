import javax.swing.*;
import java.awt.*;

public class UserSettingsPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cardContainer;
    private MyBooksPanel myBooksPanel;
    private AllBorrowedBooksPanel allBorrowedBooksPanel;

    private JButton backButton;
    private JButton myBooksButton;
    private JButton logoutButton;
    private JButton addBookButton;
    private JButton viewAllBorrowedButton;

    public UserSettingsPanel(CardLayout cardLayout, JPanel cardContainer, MyBooksPanel myBooksPanel, AllBorrowedBooksPanel allBorrowedBooksPanel) {
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;
        this.myBooksPanel = myBooksPanel;
        this.allBorrowedBooksPanel = allBorrowedBooksPanel;

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
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(addBookButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(viewAllBorrowedButton);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(logoutButton);

        backButton.addActionListener(e -> {
            cardLayout.show(cardContainer, "Home");
        });

        myBooksButton.addActionListener(e -> {
            myBooksPanel.refreshBorrowedBooks(Main.loggedInUserName);

            cardLayout.show(cardContainer, "My_Books");
        });

        logoutButton.addActionListener(e -> {
            Main.loggedInUserName = null;
            Main.loggedInUserRole = null;

            cardLayout.show(cardContainer, "Login");
        });

        addBookButton.addActionListener(e -> {
            cardLayout.show(cardContainer, "ADD_BOOK");
        });

        viewAllBorrowedButton.addActionListener(e -> {
            allBorrowedBooksPanel.refreshAllBorrowedBooks();
            cardLayout.show(cardContainer, "ALL_BORROWED");
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