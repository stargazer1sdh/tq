package tq;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import db.DBUtils;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class.getName());
	private static final String url0 = "https://gcc.gnu.org/bugzilla/";
	private static final String allend = "&limit=0&order=priority%2Cbug_severity&query_format=advanced";
	private static String url = "https://gcc.gnu.org/bugzilla/describecomponents.cgi";

	public static void main(String[] args) {
		logger.info(" start");
		Document doc = MyJsoup.getDocument(url);
		Element table = doc.select("table#choose_product").first();// <table id="choose_product">
		Elements as = table.select("tr").select("a");
		for (Element a : as) {
			String urll = new StringBuilder(url0).append(a.attr("href")).toString();
			System.out.println(urll);
			doc = MyJsoup.getDocument(urll);
			table = doc.select("table#component_table").first();
			Elements aas = table.select("a");
			System.out.println();
			for (Element aa : aas) {
				String urlll = new StringBuilder(url0).append(aa.attr("href")).append(allend).toString();
				System.out.println(urlll);
				doc = MyJsoup.getDocument(urlll);
				Elements bugs = doc.select("tr.bz_bugitem");
				for (Element bug : bugs) {
					System.out.println(bug.select("td").first().text());
					String id = bug.selectFirst("td.bz_id_column").text();
					String product = bug.selectFirst("td.bz_product_column").text();
					String component = bug.selectFirst("td.bz_component_column").text();
					Element href = bug.selectFirst("td.bz_short_desc_column");
					String title = href.text();
					String urlbug = href.selectFirst("a").absUrl("href");
					BugPage bugbean = new BugPage(id,product,component,title,urlbug);
//					DBUtils.insertSimplebug(bugbean);
//					DBUtils.insertbug$cs(bugbean);
					DBUtils.insertbug$cs$as(bugbean);
				}

			}
			System.out.println();
		}
		
		DBUtils.shut();
	}

}
