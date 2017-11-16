package tq;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class Attblob {
	private static Logger logger = Logger.getLogger(Attblob.class.getName());
	public static InputStream getInput(String src) {
		URL url;
		InputStream is;
		try {
			url = new URL(src);
			URLConnection uri = url.openConnection();
			// 获取数据流
			uri.setConnectTimeout(50000);
			uri.setReadTimeout(50000);
			is = uri.getInputStream();
			System.out.println(uri.getReadTimeout() +"\t"+uri.getConnectTimeout());
			return is;
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(src +" link err: "+e);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				logger.error("link sleep err");
			}
			return getInput(src);
		}
	}

}
