package tq;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class BugPage {
	private static DateFormat tf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public String url;
	public String id;
	public String title;
	public String component;
	public String product;
	
	public String description;
	public Date descriptionTime;
	public String descriptionPerson;
	
	public String code;
	public String gccin;
	public String gccout;
	
	public BugPage(String id, String product, String component, String title, String url) {
		this.id = id;
		this.product = product;
		this.component = component;
		this.title = title;
		this.url = url;
		Document doc = MyJsoup.getDocument(url);
		Element dese = doc.select(".bz_first_comment").first();
		descriptionPerson = dese.selectFirst(".bz_comment_user").text();
		try {
			descriptionTime = tf.parse(dese.selectFirst(".bz_comment_time").text());
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(id+" timeparse err");
		}
		description = dese.select(".bz_comment_text").first().text();
		
	}

	List<Comment> comments = new ArrayList<Comment>();
	
	private static Logger logger = Logger.getLogger(BugPage.class.getName());
}
