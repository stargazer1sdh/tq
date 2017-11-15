package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import tq.BugPage;

public class DBUtils {
	private static Logger logger = Logger.getLogger(DBUtils.class.getName());
	private static Connection conn = null;
	static {
		while (!setCon()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				logger.error("dbcon err");
			}
		}
	}

	private static boolean setCon() {
		boolean flag = false;
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/mysql?user=root&password=root&useSSL=true");
			if (null != conn)
				flag = true;
		} catch (SQLException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public static void insertSimplebug(BugPage bug) {
		// Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// stmt = conn.createStatement();
			// String sql = new StringBuilder("INSERT INTO gcc_bug (id,
			// title,Product,Component,Description,DescriptionTime) VALUES
			// (").append(bug.id)
			// .append(",").append(bug.title).append(",").append(bug.product).append(",").append(bug.component).append(",")
			// .append(bug.description).append(",").append(bug.descriptionTime)
			// .append(")").toString();
			// stmt.executeUpdate(sql);
			String sql = "INSERT INTO gcc_bug (id, title,Product,Component,Description,DescriptionTime,DescriptionPerson) VALUES (?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			pstmt.setInt(i++, Integer.parseInt(bug.id));
			pstmt.setString(i++, bug.title);
			pstmt.setString(i++, bug.product);
			pstmt.setString(i++, bug.component);
			pstmt.setString(i++, bug.description);
			pstmt.setTimestamp(i++, new Timestamp(bug.descriptionTime.getTime()));
			pstmt.setString(i++, bug.descriptionPerson);

			System.out.println(pstmt.executeUpdate());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(bug.id + " l:" + bug.description.length() + " nl:" + bug.descriptionPerson.length() + " til:"
					+ bug.title.length() + " insert err:" + e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore
				rs = null;
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException sqlEx) {
				} // ignore
				pstmt = null;
			}
		}

	}

	public static void shut() {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
				logger.error("db shut err");
			}
		}
	}

}
