import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MyBooksPanel extends JPanel {
    private DefaultTableModel tableModel;
    private String[] columnNames = {"ISBN", "Title", "Author", "Genre", "Due Date"};

    public MyBooksPanel(CardLayout cardLayout, JPanel cardContainer, BookDetailsPanel bookDetailsPanel) {
        this.setLayout(new BorderLayout());

        JButton backButton = new JButton("<- Back");
        tableModel = new DefaultTableModel(null, columnNames);
        JTable table = new JTable(tableModel);

        backButton.addActionListener(e ->
                cardLayout.show(cardContainer, "USER_SETTINGS")
        );

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String isbn = tableModel.getValueAt(row, 0).toString();
                    String title = tableModel.getValueAt(row, 1).toString();
                    String author = tableModel.getValueAt(row, 2).toString();
                    String genre = tableModel.getValueAt(row, 3).toString();

                    bookDetailsPanel.setBookDetails(isbn, title, author, genre, "Not Available", Main.loggedInUserRole);
                    cardLayout.show(cardContainer, "BOOK_DETAILS");
                }
            }
        });

        this.add(backButton, BorderLayout.NORTH);
        this.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void refreshBorrowedBooks(String userName) {
        Object[][] data = DatabaseManager.getMyBorrowedBooks(userName);
        tableModel.setDataVector(data, columnNames);
    }
}
