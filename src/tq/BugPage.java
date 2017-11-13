package tq;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class BugPage {
	private String url;
	
	public BugPage(String urlbug) {
		url = urlbug;
		Document doc = MyJsoup.getDocument(url);
		description = doc.select(".bz_first_comment").first()
				.select(".bz_comment_text").first().text();
		
		System.out.println(url);
		System.out.println("````````````````````````````````````````````````");
		System.out.println(description);
		System.out.println("++++++++++++++++++++++++++++++++++++");
	}

	private String description;
}
