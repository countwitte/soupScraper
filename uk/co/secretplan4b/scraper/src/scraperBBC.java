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


public class scraperBBC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// get date stamp for output file
		String stamp = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
		// set up print writer for output file
		PrintWriter q = null;
		File out = new File("ScrapedBBC-"+stamp+".csv");
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
			doc = Jsoup.connect("http://www.bbc.co.uk/news/16630456").get();
		} catch (IOException e) {
			// can't connect to url
			System.out.println("Error: Could not conect to "+"http://www.bbc.co.uk/news/16630456");
			e.printStackTrace();
		}
		// find the format for page links 
		Elements links = doc.getElementsByTag("a");
		ArrayList<String> stories = new ArrayList<String>();
		String l = null;
		for (int i=0;i<links.size();i++) {
			if ((l=links.get(i).absUrl("href")).contains("uk-scotland-scotland-politics")) {
				stories.add(l);
			}
		}
		Document page = null;
		int j = 0;
		// for each story link
		for (j=0;j<stories.size();j++) {
			try {
				page = Jsoup.connect(stories.get(j)).get();
			} catch (IOException e) {
				// couldn't connect to url
				System.out.println("Error: Could not connect to url \""+stories.get(j)+"\"");
				e.printStackTrace();
			}
			Elements links2 = page.getElementsByTag("a");
			ArrayList<String> stories2 = new ArrayList<String>();
			String l2;
			for (int i=0;i<links2.size();i++) {
				if ((l2=links2.get(i).absUrl("href")).contains("uk-scotland-scotland-politics")==true) {
					if (l2.contains("#")) {
					stories2.add(l2.substring(0, l2.indexOf("#")));
					} else {
						stories2.add(l2);
					}
				}
			}
		}
			
			
			
			/* extract title
					String title = page.getElementsByClass("story-header").text();
					// extract author and date published
					String date = page.getElementsByClass("date").text();
					// extract intro
					String intro = story.getElementsByClass("field-name-field-article-intro").text();
					// extract body of story - might be in a different class - see if
					String body = story.getElementsByClass("field-name-field-article-main").text();
					if (body.isEmpty()) {
						body = story.getElementsByClass("field-name-body").text();
					}
					// change quotes to ' to avoid csv confusion 
					body = body.replaceAll("\"","'");
					intro = intro.replaceAll("\"","'");
					// also remove commas
					body = body.replaceAll(",", "");
					intro = intro.replaceAll(",", "");
					try {
						q.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",title,author,date,intro,body);
					} catch (Exception e) {
						// couldn't write to file
						System.out.println("Error: Could not write to output file");
					}
		}
		*/// close printwriter
		q.close();
		System.out.println("Scrape successful");
	}
}