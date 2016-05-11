import java.io.IOException;
import java.util.ArrayList;

public class WikiCSCrawler {
	
	public static void main (String args[]) throws IOException{
		String site = "/wiki/K.html";
		int max = 1000;
		String filename = "WikiCS.txt";
		
		
		WikiCrawler crawl = new WikiCrawler(site, max, filename);
		crawl.crawl();
	}
}
