import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class scraperBT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// get date stamp for output file
		String stamp = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
		// set up print writer for output file
		PrintWriter q = null;
		File out = new File("ScrapedBT-"+stamp+".csv");
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
			doc = Jsoup.connect("http://bettertogether.net/blog").get();
		} catch (IOException e) {
			// can't connect to url
			System.out.println("Error: Could not conect to "+"http://bettertogether.net/blog");
			e.printStackTrace();
		}
		// find the format for page links 
		Elements pages = doc.getElementsByClass("pagination");
		Element plink = pages.get(0).select("a").first();
		String p = plink.absUrl("href").substring(0, (plink.absUrl("href").length()-1));
		Document page = null;
		int j = 0;
		// for each page (there are 130) TODO automate no. of pages?
		while (j<379) {
			try {
				page = Jsoup.connect(p.concat(String.valueOf(j))).get();
			} catch (IOException e) {
				// couldn't connect to url
				System.out.println("Error: Could not connect to url \""+p.concat(String.valueOf(j))+"\"");
				e.printStackTrace();
			}
			// grab all news stories on the page
			Elements newsStories = page.getElementsByClass("entry");
			Element link;
			// extract the link to the full story
			for (int i=0;i<newsStories.size();i++) {
				link = newsStories.get(i).select("a").first();
				try {
					// connect to full story url
					Document story = Jsoup.connect(link.absUrl("href")).get();
					// extract title
					String title = story.getElementsByTag("h3").first().text();
					//extract date
					String date = newsStories.get(i).getElementsByClass("date").text().trim();
					// extract body of story
					String body = story.getElementById("main").getElementsByTag("p").text();
					// remove date from body
					body = (body.replace(date, " ")).trim();
					// change quotes to ' to avoid csv confusion 
					body = body.replaceAll("\"","'");
					// also remove commas
					body = body.replaceAll(",", " ");
					try {
						q.printf("\"%s\",\"%s\",\"%s\"\n",title,date,body);
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
			j = j+7;
		}
		// close printwriter
		q.close();
		System.out.println("Scrape successful");
	}
}