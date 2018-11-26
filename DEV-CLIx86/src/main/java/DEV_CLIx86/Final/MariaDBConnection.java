package DEV_CLIx86.Final;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MariaDBConnection {
	String host;
	String dbname;
	String url;
	String password;
	String username;
	Connection conn;
	Statement st;
	
	public MariaDBConnection (String host, String dbname, String username, String url, String password) {
		this.host = host;
		this.dbname = dbname;
		this.url = url;
		this.username = username;
		this.password = password;
		this.conn = null;
		this.st = null;
	}
	
	public void createConnection () {
		try {
			this.conn = DriverManager.getConnection(this.url, this.username, this.password);
			this.st = conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void doSelectOperation (String query) {
		// execute the query, and get a java resultset
	    ResultSet rs;
		try {
			rs = this.st.executeQuery(query);
			// iterate through the java resultset
		    while (rs.next())
		    {
		      String engine = rs.getString("job");
		      System.out.println("ENGINE "+engine);
		    }
		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
	}
	
	

}
