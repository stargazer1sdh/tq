package tq;

import java.io.InputStream;
import java.util.Date;

public class Attachment {
	public Attachment(String bugid2, InputStream text2, Date time2, String person2,String filename,String type) {
		bugid = bugid2;
		text = text2;
		time = time2;
		person = person2;
		this.filename = filename;
		this.type = type;
	}

	public String bugid;
	public InputStream text;
	public Date time;
	public String person;
	public String filename;
	public String type;
}
