/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import lp.reminiscens.crawler.entities.Fuzzy_Date;
import lp.reminiscens.crawler.entities.Life_Event;
import lp.reminiscens.crawler.entities.Location;
import lp.reminiscens.crawler.entities.Media;
import lp.reminiscens.crawler.entities.Participant;
import lp.reminiscens.crawler.entities.Person;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DomParser {

	CoordSearcher coord;

	private String catinabibURL_1 = "http://www.catinabib.it/?q=digitalib/searchfull&page=";
	private String catinabibURL_2 = "&action=1&collection_nid=247&string_to_search=";
	private String catinabibURL_3 = "&view_mode=preview&orderBy=167&form_build_id=form-f2068e904102b59efbc8681fecdb95f3&form_id=digitalib_searchfull_search_form";

	public static void main(String[] a) throws IOException {
		DomParser dom = new DomParser();
		Database db = new Database();
		dom.coord = new CoordSearcher();

		// dom.getPeopleBornIn(1941);
		List<Media> m;
		for (int i = 0; i < 7; i++) {
			m = dom.getPicturesOfTrentino("Pergine", i);

			for (Media p : m) {
				db.addMedia(p);
			}
		}

	}

	public List<Media> getPicturesOfTrentino(String subject, int page)
			throws UnsupportedEncodingException, IOException {
		Document doc = Jsoup.connect(
				catinabibURL_1 + page + catinabibURL_2
						+ URLEncoder.encode(subject, "UTF-8") + catinabibURL_3)
				.get();
		Elements pics = doc.select("h2 > a[href^=/?q=node/]");
		List<Media> list = new ArrayList<Media>();

		for (Element pic : pics) {
			Media m = parsePicture(pic.attr("href"), subject);
			if (m != null) {
				list.add(m);
			}
		}
		return list;
	}

	public Media parsePicture(String nodeURL, String subject)
			throws IOException {
		Document doc = Jsoup.connect(
				"http://www.catinabib.it/?q=digitalib/generate_mag/"
						+ nodeURL.substring(9, nodeURL.length())).get();

		System.out.println("http://www.catinabib.it/?q=digitalib/generate_mag/"
				+ nodeURL.substring(9, nodeURL.length()));

		Media photo = new Media();
		photo.setMedia_type("postcard");
		photo.setIs_public(1);

		Location location = new Location();
		Fuzzy_Date start = new Fuzzy_Date();
		// Fuzzy_Date end = new Fuzzy_Date();

		try {
			photo.setCaption(doc.getElementsByTag("dc:subject").first().text());
			location.setTextual(doc.getElementsByTag("dc:subject").get(1)
					.text());
		} catch (Exception e) {
			photo.setCaption(subject);
			location.setTextual(subject);
		}

		photo.setLocation(location);
		location.setPhoto(photo);

		try {
			String out = coord.getJsonByGoogle(location.getTextual());
			coord.parseGeoJson(out, photo);
		} catch (Exception e) {
			return null;
		}

		try {
			String date = doc.getElementsByTag("dc:date").text();
			System.out.println(date);
			start.splitCatinaBibDate(date);
		} catch (Exception e) {
		}

		start.setPhoto(photo);
		photo.setTakenDate(start);

		photo.setMedia_url(doc.getElementsByTag("file").first()
				.attr("xlink:href"));
		photo.setSource("catinabib");
		photo.setSource_url("http://www.catinabib.it" + nodeURL);
		photo.setLocale("ita");

		return photo;

	}
}
