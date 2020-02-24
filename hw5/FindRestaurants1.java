import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

class FindRestaurants1 {
   public static void main(String args[]) {
      String dbSys = null;
      Scanner in = null;
      try {
         in = new Scanner(System.in);
         System.out
            	.println("Please enter information to test connection to the database");
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
            yesNo = readEntry(in,
               	"use canned Oracle connection string (y/n): ");
            if (yesNo.equals("y")) {
               String host = readEntry(in, "host: ");
               String port = readEntry(in, "port (often 1521): ");
               String sid = readEntry(in, "sid (site id): ");
               connStr = "jdbc:oracle:thin:@" + host + ":" + port + ":"
                  	+ sid;
            } else {
               connStr = readEntry(in, "connection string: ");
            }
         } else if (dbSys.equals("m")) {// MySQL--
            user = readEntry(in, "user: ");
            password = readEntry(in, "password: ");
            yesNo = readEntry(in,
               	"use canned MySql connection string (y/n): ");
            if (yesNo.equals("y")) {
               String host = readEntry(in, "host: ");
               String port = readEntry(in, "port (often 3306): ");
               String db = user + "db";
               connStr = "jdbc:mysql://" + host + ":" + port + "/" + db;
            } else {
               connStr = readEntry(in, "connection string: ");
            }
         } else if (dbSys.equals("h")) { // HSQLDB (Hypersonic) db
            yesNo = readEntry(in,
               	"use canned HSQLDB connection string (y/n): ");
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
   	// Connect to the database
   	// Use finally clause to close connection
      try {
         conn = DriverManager.getConnection(connStr, user, password);
         System.out.println("connected.");
         RestaurantFinder(conn);
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
               System.out
                  	.println("Problem with closing JDBC Connection\n");
               printSQLException(e);
               System.exit(5);
            }
         }
      }
   }
   
   // try to create and use a "finder" table, or throw if this fails
   static void RestaurantFinder(Connection conn) throws SQLException {
   	// Create a statement
      Statement stmt = conn.createStatement();
      ResultSet rset = null;
      try {
         try {
            stmt.execute("drop table finder");
         } catch (SQLException e) {
         	// assume not there yet, so OK to continue
         }
         System.out.println("Enter an apartment listing number:");
         Scanner listnum = new Scanner(System.in);
         String listingnumber = listnum.nextLine();
         rset = stmt.executeQuery("select b.name, b.stars, b.review_count from yelp_db.business b, yelp_db.apartments a, yelp_db.category c where c.business_id = b.id and b.review_count >= 10 and c.category = 'Restaurants' and b.city = 'Las Vegas' and geo_distance(a.longitude, a.latitude, b.longitude, b.latitude) < 200 and a.listing = " + "'" + listingnumber + "'");
         while (rset.next()) {
            System.out.println("Restaurant Name" + "\t" + "Stars" + "\t" + "Review Count");
            System.out.print(rset.getString(1));
            System.out.print("\t" + rset.getString(2));
            System.out.print("\t" + rset.getString(3));
            System.out.println();
         }
      } finally {   // Note: try without catch: let the caller handle
      			  // any exceptions of the "normal" db actions. 
         stmt.close(); // clean up statement resources, incl. rset
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
   public static String readEntry(Scanner in, String prompt)
   		throws IOException {
      System.out.print(prompt);
      return in.nextLine().trim();
   }
}
