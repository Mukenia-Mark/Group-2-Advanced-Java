import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Main {
    public static String loggedInUserRole = null;
    public static String loggedInUserName = null;

    public static DefaultTableModel tableModel;
    public static String[] columnNames = {"ISBN", "Title", "Author", "Genre", "Availability"};

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Library Management System");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                String logoImagePath = "/KCA_logo.jpeg";
                String notificationImagePath = "/notification_icon.jpeg";
                String userImagePath = "/user_icon.jpeg";
                String searchImagePath = "/search_icon.jpeg";

                URL logoImageUrl = Main.class.getResource(logoImagePath);
                URL notificationImageUrl = Main.class.getResource(notificationImagePath);
                URL userImageUrl = Main.class.getResource(userImagePath);
                URL searchImageUrl = Main.class.getResource(searchImagePath);

                ImageIcon logoIcon = null;
                if (logoImageUrl != null) {
                    logoIcon = new ImageIcon(logoImageUrl);
                } else {
                    System.err.println("Error: Image not found at " + logoImagePath);
                }

                ImageIcon notificationIcon = null;
                if (notificationImageUrl != null) {
                    notificationIcon = new ImageIcon(notificationImageUrl);
                } else {
                    System.err.println("Error: Image not found at " + notificationImagePath);
                }

                ImageIcon userIcon = null;
                if (userImageUrl != null) {
                    userIcon = new ImageIcon(userImageUrl);
                } else {
                    System.err.println("Error: Image not found at " + userImagePath);
                }

                ImageIcon searchIcon = null;
                if (searchImageUrl != null) {
                    searchIcon = new ImageIcon(searchImageUrl);
                } else {
                    System.err.println("Error: Image not found at " + searchImagePath);
                }

                CardLayout cardLayout = new CardLayout();
                JPanel cardContainer = new JPanel(cardLayout);

                JPanel mainPanel = new JPanel(new BorderLayout());
                LoginPanel loginPanel = new LoginPanel(cardLayout, cardContainer);
                BookDetailsPanel bookDetailsPanel = new BookDetailsPanel(cardLayout, cardContainer);
                AllBorrowedBooksPanel allBorrowedBooksPanel = new AllBorrowedBooksPanel(cardLayout, cardContainer);
                MyBooksPanel myBooksPanel = new MyBooksPanel(cardLayout, cardContainer, bookDetailsPanel);
                UserSettingsPanel userSettingsPanel = new UserSettingsPanel(cardLayout, cardContainer, myBooksPanel, allBorrowedBooksPanel);
                AddBookPanel addBookPanel = new AddBookPanel(cardLayout, cardContainer);

                Object[][] data = DatabaseManager.getAllBooks();

                tableModel = new DefaultTableModel(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                JTable bookTable = new JTable(tableModel);

                JScrollPane scrollPane = new JScrollPane(bookTable);

                JPanel homeSearchPanel = new JPanel(new BorderLayout());
                JPanel leftHomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JPanel centerHomePanel = new JPanel(new FlowLayout());
                JPanel rightHomePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

                JButton homeButton = new JButton(logoIcon);
                homeButton.setPreferredSize(new Dimension(40, 40));
                JTextField homeSearchTextField = new JTextField();
                homeSearchTextField.setPreferredSize(new Dimension(150, 40));
                JButton searchButton = new JButton(searchIcon);
                searchButton.setPreferredSize(new Dimension(40, 40));
                JButton notificationButton = new JButton(notificationIcon);
                notificationButton.setPreferredSize(new Dimension(40, 40));
                JButton userButton = new JButton(userIcon);
                userButton.setPreferredSize(new Dimension(40, 40));

                homeSearchPanel.add(leftHomePanel, BorderLayout.WEST);
                homeSearchPanel.add(centerHomePanel, BorderLayout.CENTER);
                homeSearchPanel.add(rightHomePanel, BorderLayout.EAST);

                leftHomePanel.add(homeButton);
                centerHomePanel.add(homeSearchTextField);
                centerHomePanel.add(searchButton);
                rightHomePanel.add(notificationButton);
                rightHomePanel.add(userButton);

                mainPanel.add(homeSearchPanel, BorderLayout.NORTH);
                mainPanel.add(scrollPane, BorderLayout.CENTER);

                cardContainer.add(mainPanel, "Home");
                cardContainer.add(loginPanel, "Login");
                cardContainer.add(bookDetailsPanel, "BOOK_DETAILS");
                cardContainer.add(userSettingsPanel, "USER_SETTINGS");
                cardContainer.add(myBooksPanel, "My_Books");
                cardContainer.add(addBookPanel, "ADD_BOOK");
                cardContainer.add(allBorrowedBooksPanel, "ALL_BORROWED");

                frame.add(cardContainer);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                homeButton.addActionListener(e -> {
                    cardLayout.show(cardContainer, "Home");
                });

                searchButton.addActionListener(e -> {
                    String searchTerm = homeSearchTextField.getText();
                    Object[][] searchData = DatabaseManager.searchBooks(searchTerm);
                    tableModel.setDataVector(searchData, columnNames);
                });

                notificationButton.addActionListener(e -> { });

                userButton.addActionListener(e -> {
                    if (Main.loggedInUserName != null) {
                        userSettingsPanel.updateUserView(Main.loggedInUserRole);
                        cardLayout.show(cardContainer, "USER_SETTINGS");
                    } else {
                        cardLayout.show(cardContainer, "Login");
                    }
                });

                bookTable.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        int rowIndex = bookTable.rowAtPoint(e.getPoint());

                        if (rowIndex >= 0) {
                            String isbn = tableModel.getValueAt(rowIndex, 0).toString();
                            String title = tableModel.getValueAt(rowIndex, 1).toString();
                            String author = tableModel.getValueAt(rowIndex, 2).toString();
                            String genre = tableModel.getValueAt(rowIndex, 3).toString();
                            String availability = tableModel.getValueAt(rowIndex, 4).toString();

                            bookDetailsPanel.setBookDetails(isbn, title, author, genre, availability, Main.loggedInUserRole);

                            cardLayout.show(cardContainer, "BOOK_DETAILS");
                        }
                    }
                });
            }
        });
    }

    public static void refreshBookTable() {
        Object[][] newData = DatabaseManager.getAllBooks();
        tableModel.setDataVector(newData, columnNames);
    }
}