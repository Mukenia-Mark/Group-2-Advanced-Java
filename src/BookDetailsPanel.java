import javax.swing.*;
import java.awt.*;

public class BookDetailsPanel extends JPanel {
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel genreLabel;
    private JLabel availabilityLabel;
    private JButton backButton;
    private JButton borrowBookButton;
    private JButton returnBookButton;
    private String currentISBN;

    public BookDetailsPanel(CardLayout cardLayout, JPanel cardContainer) {
        this.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Title: ");
        authorLabel = new JLabel("Author: ");
        isbnLabel = new JLabel("ISBN: ");
        genreLabel = new JLabel("Genre: ");
        availabilityLabel = new JLabel("Availability: ");
        borrowBookButton = new JButton("Borrow");
        returnBookButton = new JButton("Return");


        borrowBookButton.setVisible(false);
        returnBookButton.setVisible(false);

        detailsPanel.add(titleLabel);
        detailsPanel.add(authorLabel);
        detailsPanel.add(isbnLabel);
        detailsPanel.add(genreLabel);
        detailsPanel.add(availabilityLabel);
        detailsPanel.add(borrowBookButton);
        detailsPanel.add(returnBookButton);

        backButton = new JButton("<- Back to Book List");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttonPanel.add(backButton);


        backButton.addActionListener(e -> {
            cardLayout.show(cardContainer, "Home");
        });

        borrowBookButton.addActionListener(e -> {
            boolean success = DatabaseManager.borrowBook(currentISBN, Main.loggedInUserName);

            if (success) {
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                borrowBookButton.setVisible(false);
                availabilityLabel.setText("Availability: Not Available");

                Main.refreshBookTable();

                cardLayout.show(cardContainer, "Home");

            } else {
                JOptionPane.showMessageDialog(this, "Failed to borrow book. Please Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        returnBookButton.addActionListener(e -> {
            boolean success = DatabaseManager.returnBook(currentISBN, Main.loggedInUserName);

            if(success) {
                JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                returnBookButton.setVisible(false);
                availabilityLabel.setText("Availability: Available");

                Main.refreshBookTable();

                cardLayout.show(cardContainer, "Home");

            } else {
                JOptionPane.showMessageDialog(this, "Failed to return book. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(detailsPanel, BorderLayout.CENTER);
    }

    public void setBookDetails(String isbn, String title, String author, String genre, String availability, String userRole) {
        this.currentISBN = isbn;

        titleLabel.setText("Title: " + title);
        authorLabel.setText("Author: " + author);
        isbnLabel.setText("ISBN: " + isbn);
        genreLabel.setText("Genre: " + genre);
        availabilityLabel.setText("Availability: " + availability);

        if(userRole != null && "Available".equals(availability)) {
            borrowBookButton.setVisible(true);
            returnBookButton.setVisible(false);
        } else if (userRole != null && "Not Available".equals(availability)) {
            borrowBookButton.setVisible(false);

            boolean isMine = DatabaseManager.isBookBorrowedByUser(isbn, Main.loggedInUserName);
            returnBookButton.setVisible(isMine);
        } else {
            borrowBookButton.setVisible(false);
            returnBookButton.setVisible(false);
        }
    }
}
