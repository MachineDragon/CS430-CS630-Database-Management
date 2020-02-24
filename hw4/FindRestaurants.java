
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

class FindRestaurants {
	public static void main(String args[]) {
		String dbSys = null;
		Scanner in = null;
		try {
			in = new Scanner(System.in);
			System.out.println("Please enter information to test connection to the database");
			dbSys = readEntry(in, "Using Oracle (o), MySql (m) or HSQLDB (h)? ");

		} catch (IOException e) {
			System.out.println("Problem with user input, please try again\n");
			System.exit(1);
		}
		// Prompt the user for connect information
		String user = null;
		String password = null;
		String connStr = null;
		String yesNo;
		try {
			if (dbSys.equals("o")) {
				user = readEntry(in, "user: ");
				password = readEntry(in, "password: ");
				yesNo = readEntry(in, "use canned Oracle connection string (y/n): ");
				if (yesNo.equals("y")) {
					String host = readEntry(in, "host: ");
					String port = readEntry(in, "port (often 1521): ");
					String sid = readEntry(in, "sid (site id): ");
					connStr = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
				} else {
					connStr = readEntry(in, "connection string: ");
				}
			} else if (dbSys.equals("m")) {// MySQL--
				user = readEntry(in, "user: ");
				password = readEntry(in, "password: ");
				yesNo = readEntry(in, "use canned MySql connection string (y/n): ");
				if (yesNo.equals("y")) {
					String host = readEntry(in, "host: ");
					String port = readEntry(in, "port (often 3306): ");
					String db = user + "db";
					connStr = "jdbc:mysql://" + host + ":" + port + "/" + db;
				} else {
					connStr = readEntry(in, "connection string: ");
				}
			} else if (dbSys.equals("h")) { // HSQLDB (Hypersonic) db
				yesNo = readEntry(in, "use canned HSQLDB connection string (y/n): ");
				if (yesNo.equals("y")) {
					String db = readEntry(in, "db or <CR>: ");
					connStr = "jdbc:hsqldb:hsql://localhost/" + db;
				} else {
					connStr = readEntry(in, "connection string: ");
				}
				user = "sa";
				password = "";
			} else {
				user = readEntry(in, "user: ");
				password = readEntry(in, "password: ");
				connStr = readEntry(in, "connection string: ");
			}
		} catch (IOException e) {
			System.out.println("Problem with user input, please try again\n");
			System.exit(3);
		}
		System.out.println("using connection string: " + connStr);
		System.out.print("Connecting to the database...");
		System.out.flush();
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(connStr, user, password);
			System.out.println("connected.");

			// Account for specific database query
			if (dbSys.equals("o")) {
				findRestaurants(conn, 'o');
			} else if (dbSys.equals("m")) {
				findRestaurants(conn, 'm');
			} else {
				System.out.print("HSQLDB not supported");
			}
		} catch (SQLException e) {
			System.out.println("Problem with JDBC Connection\n");
			printSQLException(e);
			System.exit(4);
		} finally {
			// Close the connection, if it was obtained, no matter what happens
			// above or within called methods
			if (conn != null) {
				try {
					conn.close(); // this also closes the Statement and
					// ResultSet, if any
				} catch (SQLException e) {
					System.out.println("Problem with closing JDBC Connection\n");
					printSQLException(e);
					System.exit(5);
				}
			}
		}
	}

	static void findRestaurants(Connection conn, char db) throws SQLException {

		Statement stmt = conn.createStatement();
		ResultSet rset = null;
		PreparedStatement sqlQuery = null;
		try {
			Scanner in = null;
			String apartment_Listing = null;
			try {
				in = new Scanner(System.in);
				apartment_Listing = readEntry(in, "Enter apartment listing: ");
			} catch (IOException e) {
				System.out.println("Problem with user input, please try again\n");
				System.exit(1);
			}

			// condition for oracle & mySQL
			if (db == 'o') {

				sqlQuery = conn.prepareStatement(
						"SELECT b.name, b.stars, b.review_count FROM yelp_db.business b, yelp_db.category c "
								+ " WHERE b.id = c.business_id AND b.city = 'Las Vegas' AND b.state = 'NV' AND "
								+ " c.category = 'Restaurants' AND 200 > " + " sdo_geom.sdo_distance ( "
								+ "   sdo_geometry (2001, 4326, null, sdo_elem_info_array(1, 1, 1), "
								+ "     sdo_ordinate_array( "
								+ "       (SELECT a.longitude FROM yelp_db.apartments a WHERE a.listing = ?), "
								+ "       (SELECT a.latitude FROM yelp_db.apartments a WHERE a.listing = ?) "
								+ "    )), " + "   sdo_geometry (2001,4326, null, sdo_elem_info_array(1, 1, 1), "
								+ "     sdo_ordinate_array( " + "       b.longitude, b.latitude " + "    )), "
								+ "  1, 'unit=M')" + " GROUP BY b.id, b.name, b.stars, b.review_count"
								+ " HAVING b.review_count > 9");
			} else {
				sqlQuery = conn.prepareStatement(
						"SELECT b.name, b.stars, b.review_count FROM yelp_db.business b, yelp_db.category c"
								+ " WHERE b.id = c.business_id AND b.city = 'Las Vegas' AND b.state = 'NV' AND "
								+ " c.category = 'Restaurants' AND 200 > " + " ST_Distance_Sphere( " + "  point( "
								+ "   (SELECT a.longitude FROM yelp_db.apartments a WHERE a.listing = ?), "
								+ "   (SELECT a.latitude FROM yelp_db.apartments a WHERE a.listing = ?)  " + "  ), "
								+ "  point(b.longitude, b.latitude) " + ") "
								+ " GROUP BY b.id, b.name, b.stars, b.review_count " + " HAVING b.review_count > 9");
			}
			sqlQuery.setInt(1, Integer.parseInt(apartment_Listing));
			sqlQuery.setInt(2, Integer.parseInt(apartment_Listing));

			rset = sqlQuery.executeQuery();
			if (!rset.isBeforeFirst()) {
				System.out.println("No restaurants result found.\n");

			} else {
				System.out.println(
						"\n Restaurants result listing within 200m radius from apartment with at least 10 reviews: \n");
				System.out.printf("%-50s\t%-10s\t%10s\n\n", "Business", "Rating", "Reviews");

				while (rset.next()) {

					String BusinessName = rset.getString(1);
					double ratings = rset.getDouble(2);
					int reviewCounts = rset.getInt(3);

					// display result

					System.out.printf("%-50s\t%-10.1f\t%10d\n", BusinessName, ratings, reviewCounts);
				}
			}
		} finally {
			stmt.close();
		}
	}

	// print out all exceptions connected to e by nextException or getCause
	static void printSQLException(SQLException e) {
		// SQLExceptions can be delivered in lists (e.getNextException)
		// Each such exception can have a cause (e.getCause, from Throwable)
		while (e != null) {
			System.out.println("SQLException Message:" + e.getMessage());
			Throwable t = e.getCause();
			while (t != null) {
				System.out.println("SQLException Cause:" + t);
				t = t.getCause();
			}
			e = e.getNextException();
		}
	}

	// super-simple prompted input from user
	public static String readEntry(Scanner in, String prompt) throws IOException {
		System.out.print(prompt);
		return in.nextLine().trim();
	}
}
