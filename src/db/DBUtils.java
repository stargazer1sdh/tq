package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import tq.Attachment;
import tq.BugPage;
import tq.Comment;

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

	public static int insertSimplebug(BugPage bug) {
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

			return pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(bug.id + " l:" + bug.description.length() + " nl:" + bug.descriptionPerson.length() + " til:"
					+ bug.title.length() + " insert err:" + e);
			return 0;
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
	private static void insertComment(Comment c) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO gcc_comment (bugid,text,time,person) VALUES (?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			pstmt.setInt(i++, Integer.parseInt(c.bugid));
			pstmt.setString(i++, c.text);
			pstmt.setTimestamp(i++, new Timestamp(c.time.getTime()));
			pstmt.setString(i++, c.person);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("co "+c.person +" of "+ c.bugid + " l:" + c.text.length() 
			+ " nl:" + c.person.length() + "insert err:" + e);
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
	public static void insertbug$cs(BugPage bugbean) {
		insertSimplebug(bugbean);
		List<Comment> cs = bugbean.comments;
		for (Comment c : cs) {
			insertComment(c);
		}
	}
	private static void insertAttachment(Attachment a) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO gcc_attachment (bugid,text,time,person,filename,type)VALUES (?,?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			pstmt.setInt(i++, Integer.parseInt(a.bugid));
			pstmt.setBlob(i++, a.text);
			pstmt.setTimestamp(i++, new Timestamp(a.time.getTime()));
			pstmt.setString(i++, a.person);
			pstmt.setString(i++, a.filename);
			pstmt.setString(i++, a.type);

			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("att "+a.person +" of "+ a.bugid  + " til:"+ a.filename.length()
			+ " nl:" + a.person.length() + "insert err:" + e);
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
	public static void insertbug$cs$as(BugPage bugbean) {
		insertbug$cs(bugbean);
		List<Attachment> as = bugbean.attachments;
		for (Attachment a : as) {
			insertAttachment(a);
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
