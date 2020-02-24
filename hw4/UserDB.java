import java.sql.*;

public class UserDB {
	private static Connection connection;

	public static void setConnection(Connection conn) {
		connection = conn;
	}

	public static int insert(User user) throws SQLException {
		PreparedStatement ps = null;

		String query = "INSERT INTO Userdb (Email, FirstName, LastName) " + "VALUES (?, ?, ?)";

		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getEmail());
			ps.setString(2, user.getFirstName());
			ps.setString(3, user.getLastName());
			return ps.executeUpdate();
		} finally {
			closePreparedStatement(ps);
		}
	}

	public static int delete(User user) throws SQLException {

		PreparedStatement ps = null;
		String query = "DELETE FROM Userdb " + "WHERE Email = ?";
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, user.getEmail());

			return ps.executeUpdate();
		} finally {
			closePreparedStatement(ps);
		}
	}

	public static boolean emailExists(String email) throws SQLException {

		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT Email FROM Userdb " + "WHERE Email = ?";
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();
			return rs.next();
		} finally {
			closeResultSet(rs);
			closePreparedStatement(ps);
		}
	}

	public static User selectUser(String email) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT * FROM Userdb " + "WHERE Email = ?";
		User user = null;
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, email);
			rs = ps.executeQuery();

			if (rs.next()) {
				user = new User();
				user.setFirstName(rs.getString("FirstName"));
				user.setLastName(rs.getString("LastName"));
				user.setEmail(rs.getString("Email"));
			}
		} finally {
			closeResultSet(rs);
			closePreparedStatement(ps);
		}
		return user;
	}

	public static void closePreparedStatement(Statement ps) {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
