package tq;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MyJsoup {
	private static Logger logger = Logger.getLogger(MyJsoup.class.getName());
	public static Document getDocument(String url){
		try {
			Connection conn = Jsoup.connect(url);
			conn.maxBodySize(0);
			return conn.get();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("jousp err: "+url);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				logger.error("jousp sleep err");
			}
			return getDocument(url);
		}
//		return null;
	}
	
//	public static Element getFirstOrNull(Document doc,String css) {
//		Elements es = doc.select(css);
//		if(es == null || es.isEmpty() )
//			return null;
//		else
//			return es.first();
//	}
}
