package la;

import db.DBUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) {
		logger.info("la start");
//		updateCodeBy("gcc","c");
		updateCodeAll();
		DBUtils.shut();
	}

	private static void updateCodeAll() {
		List<Bug> bugs= DBUtils.queryBugs();
		if(bugs != null) {
			for(Bug bug : bugs) {
				System.out.println(bug.id);
				String desc = bug.Description;
				String code = TextParser.getCode(desc);
				DBUtils.updateCodeBy(bug.id,code);
			}
		}
	}

	private static void updateCodeBy(String product, String component) {
		List<Bug> bugs= DBUtils.queryBugsBy(product, component);
		if(bugs != null) {
			for(Bug bug : bugs) {
				System.out.println(bug.id);
				String desc = bug.Description;
				String code = TextParser.getCode(desc);
				DBUtils.updateCodeBy(bug.id,code);
			}
		}
	}

}
