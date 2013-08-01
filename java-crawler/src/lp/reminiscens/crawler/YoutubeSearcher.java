/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lp.reminiscens.crawler.entities.City;
import lp.reminiscens.crawler.entities.Fuzzy_Date;
import lp.reminiscens.crawler.entities.Media_Metadata;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 
 * @author Nicola
 */
public class YoutubeSearcher {

	private final String youtubeUrlString = "https://gdata.youtube.com/feeds/api/videos?q=";
	private final String youtubeUrlParameterString = "&max-results=2&v=2&alt=jsonc&key=";
	private final String youtubeKeyString = "AI39si7gAsH_3JEgM1dX11kMLFNN8qLlFe-JuAcpz6KCuQ3gKxxzSi0kw7LmTE0znLnkrLi99pIpKNyAwR9A6GUO2u-nrVkXKA";

	public YoutubeSearcher() {
	}

	public static void main(String[] a) throws MalformedURLException,
			IOException {
		YoutubeSearcher y = new YoutubeSearcher();
		Database db = new Database();

		List<Media_Metadata> songs = new ArrayList<Media_Metadata>();

		//String path = "/Users/nicola/Documents/Reminiscens/lifecontext/java-crawler/src/playlist.csv";
		String path = "ABSOLUTE PATH OF THE PLAYLIST";
		
		File filReadMe = new File(path);

		BufferedReader brReadMe = new BufferedReader(new InputStreamReader(
				new FileInputStream(filReadMe), "UTF-8"));

		Media_Metadata song = null;

		String strLine = brReadMe.readLine();

		while (strLine != null) {

			song = y.populateSong(strLine);

			String url = y.getVideoUrl(song.getTitle(),song.getAuthor());

			if (!(url.equals("PROBLEMA"))) {
				song.setResource_url(url);
			}

			songs.add(song);

			strLine = brReadMe.readLine();
		}

		brReadMe.close();

		for (Media_Metadata s : songs) {
			// db.addWork(s);
			System.out.println("RESULT:");
			System.out.println(s.getTitle());
			System.out.println(s.getAuthor());
			System.out.println(s.getReleaseDate().getYear());
			System.out.println(s.getReleaseDate().getDecade());
			System.out.println(s.getDescription());
			System.out.println(s.getResource_url());
		}
	}

	// the function only looks for the first video in the list (actually it
	// looks just for one video)
	public String getVideoUrl(String title,String author) throws MalformedURLException,
			IOException {
		
		String keyword = title + " " + author;

		String urlString = youtubeUrlString
				+ URLEncoder.encode(keyword, "UTF-8")
				+ youtubeUrlParameterString + youtubeKeyString;
		System.out.println(urlString);
		URL url = new URL(urlString);

		URLConnection conn = url.openConnection();
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);

		IOUtils.copy(conn.getInputStream(), output);

		output.close();

		return parseJSON(output.toString());

	}

	public String parseJSON(String json) {

		String url = null;

		try {
			JsonElement jelement = new JsonParser().parse(json);
			JsonObject jObj = jelement.getAsJsonObject();
			System.out.println(jObj);
			jObj = jObj.getAsJsonObject("data");
			System.out.println(jObj);
			JsonArray items = jObj.getAsJsonArray("items");
			System.out.println(items);
			url = items.get(0).getAsJsonObject().get("content")
					.getAsJsonObject().get("5").getAsString();

		} catch (Exception e) {
			try {
				JsonElement jelement = new JsonParser().parse(json);
				JsonObject jObj = jelement.getAsJsonObject();
				jObj = jObj.getAsJsonObject("data");
				JsonArray items = jObj.getAsJsonArray("items");
				url = items.get(1).getAsJsonObject().get("content")
						.getAsJsonObject().get("5").getAsString();
			} catch (Exception ex) {
				return "PROBLEMA";
			}
		}
		return url;
	}

	public Media_Metadata populateSong(String line) {
		String title, author, year, desc;

		Media_Metadata song = new Media_Metadata();

		int i1 = line.indexOf(',');
		int i2 = line.indexOf(',', i1 + 1);
		int i3 = line.lastIndexOf(',');

		title = line.substring(0, i1);
		author = line.substring(i1 + 1, i2);
		year = line.substring(i2 + 1, i3);
		desc = line.substring(i3 + 1);

		song.setTitle(title);
		song.setAuthor(author);
		song.setDescription(desc);

		Fuzzy_Date releaseDate = new Fuzzy_Date();
		if (year.matches("^[1-2][0-9][0-9][0-9]$")) {
			releaseDate.setYear(year);
			releaseDate.setDecade(year.substring(0, 3) + "0");
		}

		releaseDate.setMediaMD(song);
		song.setReleaseDate(releaseDate);

		return song;
	}
}
