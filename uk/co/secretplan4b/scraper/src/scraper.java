import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class scraper {

        /**
         * @param args
         */
        public static void main(String[] args) {
                // test - grab wikipedia.org
                Document doc = null;
                try {
                        doc = Jsoup.connect("http://yesscotland.net/news/").get();
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                Element newsStories = doc.getElementById("masonry-item");
                Elements links = newsStories.getElementsByTag("a");
                for (Element link : links) {
                  String linkHref = link.attr("href");
                  String linkText = link.text();

                //for (int i=0;i<newsStories.size();i++) {
                //System.out.println(newsStories.get(i).absUrl("a"));
                //}
                }
        }
}
