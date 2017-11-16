package tq;

import java.util.Date;

public class Comment {
	public Comment(String bugid2, String text2, Date time2, String person2) {
		bugid = bugid2;
		text = text2;
		time = time2;
		person = person2;
	}
	public String bugid;
	public String text;
	public Date time;
	public String person;
}
