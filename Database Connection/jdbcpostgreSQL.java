import java.sql.*;
//import javax.swing.JOptionPane;

/*
Robert lightfoot
CSCE 315
9-25-2019 Original
2/7/2020 Update for AWS
 */
public class jdbcpostgreSQL {
	public static void main(String args[]) {
		// GUI.dbSetup hides my username and password
		dbSetup my = new dbSetup();
		// Building the connection
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team15_project2", my.user,
					my.pswd);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} // end try catch
		//System.out.println("Opened database successfully");
		try {
			// create a statement object
			Statement stmt = conn.createStatement();
			// create an SQL statement
			String sqlStatement = "select * from conferenceseason where conferenceid = 821";
			// send statement to DBMS
			ResultSet result = stmt.executeQuery(sqlStatement);

			// OUTPUT
			//System.out.println("Query:");
			//System.out.println("______________________________________");
			while (result.next()) {
				System.out.println(result.getString("name"));
			}
		} catch (Exception e) {
			System.out.println("Error accessing Database.");
		}
		// closing the connection
		try {
			conn.close();
			//System.out.println("Connection Closed.");
		} catch (Exception e) {
			//System.out.println("Connection NOT Closed.");
			e.printStackTrace();
		} // end try catch
	}// end main
}// end Class