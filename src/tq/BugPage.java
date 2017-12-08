package tq;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BugPage {
	public static final String NOCA = "NOCA";//not take care of comments and attachments
	public static final String WITHCA = "WITHCA";
	private static DateFormat tf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static DateFormat tf2= new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
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
	
	public String assignee;
	public String status;
	
	//used to compared with DB to decide save or update or do nothing
	public BugPage(String id2, String assignee, String status) {
		id = id2;
		this.assignee = assignee;
		this.status = status;
	}	
	public boolean equals(BugPage o) {
		return id.equals(o.id)&&assignee.equals(o.assignee)&&status.equals(o.status);
	}

	private BugPage(String id, String product, String component, String assignee, String status, String title, String url) {
		this.id = id;
		this.product = product;
		this.component = component;
		this.title = title;
		this.assignee = assignee;
		this.status = status;
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
	public BugPage(String id, String product, String component, String assignee, String status, String title, String url, String flag) {
		this(id,product,component,assignee, status, title, url);
		Document doc = MyJsoup.getDocument(url);
		if(WITHCA.equals(flag)) {
			Elements cs = doc.select(".bz_comment");
			int csl = cs.size();
			for(int i=1;i<csl;++i) {
				Element c = cs.get(i);
				String text = c.select(".bz_comment_text").first().text();
				Date time = null;
				try {
					time = tf.parse(c.select(".bz_comment_time").first().text());
				} catch (ParseException e) {
					e.printStackTrace();
					logger.error(id+"co timeparse err");
				}
				String person = c.select(".bz_comment_user").first().text();
				comments.add(new Comment(id,text,time,person));
			}
			
			Elements as = doc.selectFirst("#attachment_table").select("tr").not("#a0").not(".bz_attach_footer").not("bz_tr_obsolete");
			//27972    bz_tr_obsolete
			for(Element a : as) {
				Element td = a.selectFirst("td");
				Element contentE = td.selectFirst("a");
				String filename = contentE.text();
//				Document doc2 = MyJsoup.getDocument(contentE.absUrl("href"));
//				String text = doc2.text();
				InputStream text = Attblob.getInput(contentE.absUrl("href"));
				
				Element span = td.selectFirst(".bz_attach_extra_info");
				String type = span.text();
				Date time = null;
				try {
					time = tf2.parse(span.select("a").first().text());
				} catch (ParseException e) {
					e.printStackTrace();
					logger.error(id+" att timeparse err");
				}
				String person = span.selectFirst(".vcard").text();
				attachments.add(new Attachment(id, text, time, person, filename,type));
			}
		}
	}


	public List<Comment> comments = new ArrayList<Comment>();
	public List<Attachment> attachments = new ArrayList<Attachment>();
	
	private static Logger logger = Logger.getLogger(BugPage.class.getName());
}
