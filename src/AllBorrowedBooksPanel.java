import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AllBorrowedBooksPanel extends JPanel {
    private JTable borrowedTable;
    private DefaultTableModel tableModel;
    private String[] columnNames = {"ISBN", "Title", "Borrower", "Due Date"};

    public AllBorrowedBooksPanel(CardLayout cardLayout, JPanel cardContainer) {
        this.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("<- Back");
        topPanel.add(backButton);

        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        borrowedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowedTable);

        backButton.addActionListener(e -> {
            cardLayout.show(cardContainer, "USER_SETTINGS");
        });

        JLabel headerLabel = new JLabel("Master Borrowing List (Librarian Only)", SwingConstants.CENTER);

        this.add(headerLabel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.SOUTH);
    }

    public void refreshAllBorrowedBooks() {
        Object[][] data = DatabaseManager.getAllBorrowedBooks();
        tableModel.setDataVector(data, columnNames);
    }
}
