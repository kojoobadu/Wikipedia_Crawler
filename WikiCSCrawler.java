package Homework5;

import java.io.IOException;
import java.util.ArrayList;
import Homework5.WikiCrawler;

public class WikiCSCrawler {
	
	public static void main (String args[]) throws IOException{
		String site = "/wiki/Computer_Science";
		int max = 1000;
		String filename = "WikiCS.txt";
		
		
		WikiCrawler crawl = new WikiCrawler(site, max, filename);
		crawl.crawl();
	}
}
