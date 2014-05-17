import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class scraper {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// get date stamp for output file
		String stamp = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
		// set up print writer for output file
		PrintWriter q = null;
		File out = new File("Scraped-"+stamp+".csv");
		try {
			q = new PrintWriter(new FileOutputStream(out),
						true);
		} catch (FileNotFoundException e1) {
			// file not found
			System.out.println("Error: file "+out+" not found");
			e1.printStackTrace();
		}
		;
		// grab the top level page
		Document doc = null;
		try {
			doc = Jsoup.connect("http://yesscotland.net/").get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// find the format for page links 
		Elements pages = doc.getElementsByClass("pager-item");
		Element plink = pages.get(0).select("a").first();
		String p = plink.absUrl("href").substring(0, (plink.absUrl("href").length()-1));
		System.out.println(p);
		Document page = null;
		int j = 0;
		// for each page (there are 130) TODO automate no. of pages?
		while (j<131) {
			try {
				page = Jsoup.connect(p.concat(String.valueOf(j))).get();
			} catch (IOException e) {
				// couldn't connect to url
				System.out.println("Error: Could not connect to url \""+p.concat(String.valueOf(j))+"\"");
				e.printStackTrace();
			}
			// grab all news stories on the page
			Elements newsStories = page.getElementsByClass("masonry-item");
			Element link;
			// extract the link to the full story
			for (int i=0;i<newsStories.size();i++) {
				link = newsStories.get(i).select("a").first();
				try {
					// connect to full story url
					Document story = Jsoup.connect(link.absUrl("href")).get();
					// extract title
					String title = story.getElementsByClass("title").text();
					// extract author and date published
					String inner = story.getElementsByClass("inner").text();
					//parse author and date seperately
					String author = (inner.substring((inner.indexOf("y")+1), inner.indexOf(","))).trim();
					String date = (inner.substring((inner.indexOf(",")+1),(inner.length()))).trim();
					// extract intro
					String intro = story.getElementsByClass("field-name-field-article-intro").text();
					// extract body of story - might be in a different class - see if
					String body = story.getElementsByClass("field-name-field-article-main").text();
					if (body.isEmpty()) {
						body = story.getElementsByClass("field-name-body").text();
					}
					try {
						q.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",title,author,date,intro,body);
					} catch (Exception e) {
						// couldn't write to file
						System.out.println("Error: Could not write to output file");
					}
				} catch (IOException e) {
					// couldn't connect full story url
					System.out.println("Error: Could not connect to url \""+link.absUrl("href")+"\"");
					e.printStackTrace();
				}
			}
			j++;
		}
		// close printwriter
		q.close();
	}
}