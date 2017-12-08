package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import la.Bug;
import tq.Attachment;
import tq.BugPage;
import tq.Comment;

public class DBUtils {
	public static final String CHOICE_SAVE = "SAVE";
	public static final String CHOICE_UPDATE = "UPDATE";
	public static final String CHOICE_NONE = "NONE";
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

	private static int insertSimplebug(BugPage bug) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "INSERT INTO gcc_bug (id, Assignee,Status,title,Product,Component,Description,DescriptionTime,DescriptionPerson) VALUES (?,?,?,?,?,?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			pstmt.setInt(i++, Integer.parseInt(bug.id));
			pstmt.setString(i++, bug.assignee);
			pstmt.setString(i++, bug.status);
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
	private static void insertbug$cs(BugPage bugbean) {
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

	public static List<Bug> queryBugsBy(String product, String component) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Bug> bugs = new ArrayList<Bug>();
		String sql = "SELECT id,Description FROM gcc_bug where Product=? and Component = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, product);
			pstmt.setString(2, component);
			rs = pstmt.executeQuery();
		    while(rs.next()) {
		    	bugs.add(new Bug(rs.getInt(1), rs.getString(2)));
		    }
		    return bugs;
		}
		catch (SQLException e) {
			e.printStackTrace();
			logger.error("db query "+e);
			return null;
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
	public static List<Bug> queryBugs() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Bug> bugs = new ArrayList<Bug>();
		String sql = "SELECT id,Description FROM gcc_bug";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
		    while(rs.next()) {
		    	bugs.add(new Bug(rs.getInt(1), rs.getString(2)));
		    }
		    return bugs;
		}
		catch (SQLException e) {
			e.printStackTrace();
			logger.error("db query "+e);
			return null;
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

	public static void updateCodeBy(int id, String code) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "UPDATE gcc_bug SET code = ? WHERE id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			pstmt.setString(i++, code);
			pstmt.setInt(i++, id);

			if(pstmt.executeUpdate()==0) {
				logger.error("update nothing id@"+id +" code:"+code);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("update err id@"+id +"codel:"+code.length()+"  "+ e);
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

	//to decide save or update or do nothing
	public static String choosesaveOrUpdate(BugPage simplebug) {
		BugPage existbug = querySimpleBugPage(simplebug);
		if(existbug == null) {
			return CHOICE_SAVE;
		}else if(!simplebug.equals(existbug)) {
			return CHOICE_UPDATE;
		}else
			return CHOICE_NONE;
	}
	private static BugPage querySimpleBugPage(BugPage simplebug) {
		BugPage res = null;
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT Assignee,Status FROM gcc_bug where id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(simplebug.id));
			rs = pstmt.executeQuery();
		    if(rs.next()) {
		    	res = new BugPage(simplebug.id, rs.getString(1), rs.getString(2));
		    }
		    return res;
		}
		catch (SQLException e) {
			e.printStackTrace();
			logger.error("db querySimpleBugPage "+e);
			return null;
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

	public static void updatebug(BugPage bugbean) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "UPDATE gcc_bug SET Assignee = ?,Status = ?,title = ?,Product = ?,Component = ?,Description = ?,"
				+ "DescriptionTime = ?,DescriptionPerson = ? WHERE id = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			pstmt.setString(i++, bugbean.assignee);
			pstmt.setString(i++, bugbean.status);
			pstmt.setString(i++, bugbean.title);
			pstmt.setString(i++, bugbean.product);
			pstmt.setString(i++, bugbean.component);
			pstmt.setString(i++, bugbean.description);
			pstmt.setTimestamp(i++, new Timestamp(bugbean.descriptionTime.getTime()));
			pstmt.setString(i++, bugbean.descriptionPerson);
			pstmt.setInt(i++, Integer.parseInt(bugbean.id));

			if(pstmt.executeUpdate()==0) {
				logger.error("just update nothing id@"+bugbean.id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("just update err id@"+bugbean.id+"  "+ e);
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
}
