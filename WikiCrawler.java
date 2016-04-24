package Homework5;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kojob
 */
public class WikiCrawler {
	// Global Variable to store the values of the seed url,
	// the max integer and the filename.
	private String root;
	private int crest;
	private String output;
	
	// Base url
	static final String BASE_URL = "https://en.wikipedia.org";
	// The regular expression for matching the anchor tag
	static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
	//The regular expression for extracting the href value
    static final String HTML_A_HREF_TAG_PATTERN = "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";
	
	
    /**
     * @param seedUrl
     * @param max
     * @param fileName
     */
    public WikiCrawler( String seedUrl, int max, String fileName){
		root = seedUrl;
		crest = max;
		output = fileName;
	}
	
	
	/**
	 * This method extracts the links from a string which is made of html code.
	 * It first splits the string into the parts before the <p> tag and save the string after the <p> tag.
	 * The method then loops to find a match of the pattern for anchor tag and then uses the HTML_A_HREF_TAG_PATTERN 
	 * to find a match for the href value which is the link.
	 * It only adds links which do not contain "#" and ":". 
	 * It also does not add links which are on the left hand side of the page.
	 * It then returns all the links in an ArrayList
	 * 
	 * @param doc
	 * @return links
	 */
	public ArrayList<String> extractLinks(final String doc){
		String match = "<(p|P>";
		int position = doc.indexOf(match);
		String after = doc.substring(position+3, doc.length());
		ArrayList<String> links = new ArrayList<String>();
		Pattern patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
		Pattern patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
		
		
		Matcher matcherTag = patternTag.matcher(after);
		Matcher matcherLink;
		while (matcherTag.find()){
			String href = matcherTag.group(1); // String in anchor tag 
			matcherLink = patternLink.matcher(href);
			while (matcherLink.find()){
				String link = matcherLink.group(1); // link
				link = link.replace("\"", "");
				//... Validate the extracted link
				if(!link.contains("#") && !link.contains(":") && !link.contains("https") && link.substring(1, 5).equals("wiki")){
					links.add(link); //add link
				}
			}
		}
		return links;
	}
		
	/**
	 * This method extracts the HTML content of the web page declared in the parameter.
	 * The method concatenates the provided site to the BASE_URL and makes a URL connection.
	 * An inputstream is then used to read the content of the page and written to a string variable.
	 * 
	 * @param site
	 * @return result
	 * @throws IOException
	 */
	public static String getHtmlContent(String site) throws IOException{
		URL url = new URL(BASE_URL+site); // Concatenate the BASE_URL with the site provided
		URLConnection urlConnection = url.openConnection();
		InputStream inputStreamer = urlConnection.getInputStream();
		InputStreamReader inputStreamerReader = new InputStreamReader(inputStreamer);
		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		while ((numCharsRead = inputStreamerReader.read(charArray)) > 0) {
			sb.append(charArray, 0, numCharsRead);
		}
		String result = sb.toString();		
		return result;
	}
	
	
	/**
	 * This method constructs a graph by performing a BFS the seedUrl
	 * The method crawls the max number of pages specified in the constructor
	 * It then outputs the graph to the filename provided in the constructor.
	 *  
	 * 
	 * @throws IOException
	 */
	public void crawl() throws IOException{
		int numberOfRequests = 100;
		int i = 1;
		int j = 1;
		int k = 0;
		int child_count = 100;
		// Creates file based on filename
		File fout = new File(output);
		FileOutputStream fos = new FileOutputStream(fout);
		// BufferedWriter to write to the new file
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		String doc = "";
		// A queue for the BFS
		Queue<String> queue = new LinkedList<String>();
		// A set for visited links
		Set<String> visited = new HashSet<String>();
		//Writes the max number of pages to the file.
		bw.write(Integer.toString(crest));
		bw.newLine();
		
		queue.add(root);
		visited.add(root); 
		// While the queue is not empty and max pages is not reached,
		// continue to make extract links and create graph
			while(!queue.isEmpty() && i < crest+1 ){
				String v = queue.remove().toString(); 
				String page = v;
				System.out.println(i+"."+page);
				try{
					// call to getHtmlContent method to extract html document of the current page
					 doc = getHtmlContent(v);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				// Store extracted links of the current page in an array and loop through to create graph.
				// Write to file
				ArrayList<String> children = extractLinks(doc);
					for(String child : children){
						if(!visited.contains(child) && k < child_count){
							queue.add(child);
							visited.add(child);
							bw.write(page + "------------------->" + child);
							bw.newLine();
							k++;
						}
					 }
					i++;
					j++;
					k = 0;
					// If 100 requests are made, start a thread to make the method wait for 3 seconds
					// then resume making requests.
					if(j > numberOfRequests){
						try{
						    Thread.sleep(3000);                 //3000 milliseconds is one second.
						}
						catch(InterruptedException ex){
						    Thread.currentThread().interrupt();
						}
						j = 0;
					 }
			} 
		   bw.close();
	   }
	

}
