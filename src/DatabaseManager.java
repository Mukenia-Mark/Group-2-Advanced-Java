import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=libraryManagementSystemDatabase;encrypt=true;trustServerCertificate=true;";
    private static String userName = "sa";
    private static String password = "Admin1234";

    public static Connection getConnection() {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connectionUrl, userName, password);
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Error: Failed to connect to the database.");
            e.printStackTrace();
        }
        return connection;
    };

    public static Object[][] getAllBooks() {
        List<Object[]> booksList = new ArrayList<>();
        String query = "SELECT ISBN, Title, Author, Genre, Availability FROM Books";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String genre = rs.getString("Genre");
                boolean isAvailable = rs.getBoolean("Availability");

                String availabilityString = isAvailable ? "Available" : "Not Available";

                booksList.add(new Object[]{isbn, title, author, genre, availabilityString});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksList.toArray(new Object[0][]);
    }

    public static Object[][] searchBooks (String searchTerm) {
        List<Object[]> booksList = new ArrayList<>();

        String query = "SELECT ISBN, Title, Author, Genre, Availability FROM Books " + "WHERE Title LIKE ? OR Author LIKE ? OR Genre LIKE ? OR ISBN LIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String fuzzySearchTerm = "%" + searchTerm + "%";

            pstmt.setString(1, fuzzySearchTerm);
            pstmt.setString(2, fuzzySearchTerm);
            pstmt.setString(3, fuzzySearchTerm);
            pstmt.setString(4, fuzzySearchTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("ISBN");
                    String title = rs.getString("Title");
                    String author = rs.getString("Author");
                    String genre = rs.getString("Genre");
                    boolean isAvailable = rs.getBoolean("Availability");
                    String availabilityString = isAvailable ? "Available" : "Not Available";

                    booksList.add(new Object[]{isbn, title, author, genre, availabilityString});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return booksList.toArray(new Object[0][]);
    }

    public static boolean registerUser(String userName, String password) {
        String role = "regular";
        String query = "INSERT INTO Users (Username, PasswordHash, Role) VALUES (?, ?, ?)";

        if (userExists(userName)) {
            System.err.println("Registration failed: Username '" + userName + "' already exists.");
            return false;
        }

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            int rowsAffected = pstmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean userExists(String userName) {
        String query = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String validateUser(String userName, String password) {
        String query = "SELECT Role FROM Users WHERE Username = ? AND PasswordHash = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean borrowBook(String isbn, String userName) {
        int bookId = getBookIdByISBN(isbn);
        int userId = getUserIdByUserName(userName);

        if (bookId == -1 || userId == -1) {
            return false;
        }

        String updateBookSQL = "UPDATE Books SET Availability = 0 WHERE BookID = ?";
        String insertBorrowSQL = "INSERT INTO BorrowedBooks (BookID, UserID, BorrowDate, DueDate) VALUES (?, ?, GETDATE(), DATEADD(day, 14, GETDATE()))";

        try (Connection conn = getConnection()) {

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateBookSQL)) {
                pstmtUpdate.setInt(1, bookId);
                pstmtUpdate.executeUpdate();
            }

            try (PreparedStatement pstmtInsert = conn.prepareStatement(insertBorrowSQL)) {
                pstmtInsert.setInt(1, bookId);
                pstmtInsert.setInt(2, userId);
                pstmtInsert.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getBookIdByISBN(String isbn) {
        String query = "SELECT BookID FROM Books WHERE ISBN = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, isbn);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("BookID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getUserIdByUserName(String userName) {
        String query = "SELECT UserID FROM Users WHERE Username = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, userName);

            try(ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Object[][] getMyBorrowedBooks(String userName) {
        List<Object[]> list = new ArrayList<>();
        int userId = getUserIdByUserName(userName);

        String query = "SELECT Books.ISBN, Books.Title, Books.Author, Books.Genre, BorrowedBooks.DueDate FROM BorrowedBooks JOIN Books ON BorrowedBooks.BookID = Books.BookID WHERE BorrowedBooks.UserID = ?";

        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try(ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String isbn = rs.getString("ISBN");
                    String title = rs.getString("Title");
                    String author = rs.getString("Author");
                    String genre = rs.getString("Genre");
                    String dueDate = rs.getString("DueDate");

                    list.add(new Object[]{isbn, title, author, genre, dueDate});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.toArray(new Object[0][]);
    }

    public static boolean returnBook(String isbn, String userName) {
        int bookId = getBookIdByISBN(isbn);
        int userId = getUserIdByUserName(userName);

        if (bookId == -1 || userId == -1) {
            return false;
        }

        String updateBookSQL = "UPDATE Books SET Availability = 1 WHERE BookID = ?";
        String insertReturnSQL = "DELETE FROM BorrowedBooks WHERE BookID = ? AND UserID = ?";

        try (Connection conn = getConnection()) {

            try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateBookSQL)){
                pstmtUpdate.setInt(1, bookId);
                pstmtUpdate.executeUpdate();
            }

            try (PreparedStatement pstmtDelete = conn.prepareStatement(insertReturnSQL)) {
                pstmtDelete.setInt(1, bookId);
                pstmtDelete.setInt(2, userId);
                pstmtDelete.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isBookBorrowedByUser(String isbn, String userName) {
        int bookId = getBookIdByISBN(isbn);
        int userId = getUserIdByUserName(userName);

        if (bookId == -1 || userId == -1) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM BorrowedBooks WHERE BookID = ? AND UserID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query))  {
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);


            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean addBook(String isbn, String title, String author, String genre) {
        String query = "INSERT INTO Books(isbn, title, author, genre, availability) VALUES (?, ?, ?, ?, 1)";

        if (bookExists(isbn)) {
            System.err.println("Cannot add book. This book already exists.");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, isbn);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.setString(4, genre);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean bookExists(String isbn) {
        String query = "SELECT COUNT(*) FROM Books WHERE ISBN = ?";

        try (Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, isbn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Object[][] getAllBorrowedBooks() {
        List<Object[]> list = new ArrayList<>();

        String query = "SELECT b.ISBN, b.Title, b.Author, b.Genre, u.Username, bb.DueDate FROM BorrowedBooks bb JOIN Books b ON bb.BookID = b.BookID JOIN Users u ON bb.UserID = u.UserID";

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String borrower = rs.getString("Username");
                String dueDate = rs.getString("DueDate");

                list.add(new Object[]{isbn, title, borrower, dueDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.toArray(new Object[0][]);
    }
}