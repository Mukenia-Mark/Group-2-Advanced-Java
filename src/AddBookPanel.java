import javax.swing.*;
import java.awt.*;

public class AddBookPanel extends JPanel {
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField genreField;

    public AddBookPanel(CardLayout cardLayout, JPanel cardContainer) {
        this.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("ISBN:"));
        isbnField = new JTextField();
        formPanel.add(isbnField);

        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        formPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        formPanel.add(authorField);

        formPanel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        formPanel.add(genreField);

        JButton addBookButton = new JButton(("Add Book"));
        JButton cancelButton = new JButton("Cancel");
        formPanel.add(addBookButton);
        formPanel.add(cancelButton);

        cancelButton.addActionListener(e -> cardLayout.show(cardContainer, "USER_SETTINGS"));

        addBookButton.addActionListener(e -> {
            String isbn = isbnField.getText();
            String title = titleField.getText();
            String author = authorField.getText();
            String genre = genreField.getText();

            if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || genre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = DatabaseManager.addBook(isbn, title, author, genre);

            if (success) {
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                isbnField.setText("");
                titleField.setText("");
                authorField.setText("");
                genreField.setText("");

                Main.refreshBookTable();
                cardLayout.show(cardContainer, "Home");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book. ISBN might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        this.add(new JLabel("Add New Book", SwingConstants.CENTER), BorderLayout.NORTH);
        this.add(formPanel, BorderLayout.CENTER);
    }
}
