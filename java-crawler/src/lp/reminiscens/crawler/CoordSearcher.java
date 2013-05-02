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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lp.reminiscens.crawler.entities.City;
import lp.reminiscens.crawler.entities.Event;
import lp.reminiscens.crawler.entities.Media;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * 
 * @author Nicola.Parrello
 */
public class CoordSearcher {

	String geocodingString;
	String reverseGeocodingString;
	String address;
	Database db; // used once to add cities to reminiscens.City
	public Collection<String> province; // used once to add cities to
										// reminiscens.City

	public CoordSearcher() {

		geocodingString = "http://maps.googleapis.com/maps/api/geocode/json?address=";
		reverseGeocodingString = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
		province = new ArrayList<String>(); // used once to add cities to
											// reminiscens.City

	}

	// "main" function used once to add cities to reminiscens.City
	public static void main(String[] args) throws MalformedURLException,
			UnsupportedEncodingException, IOException, JsonParseException {

		CoordSearcher coord = new CoordSearcher();
		coord.db = new Database();
		String out;
		boolean mode = false;

		if (mode) {
			String path = "/Users/nicola/Documents/workspace/Reminiscens/src/cities.csv";
			File filReadMe = new File(path);

			BufferedReader brReadMe = new BufferedReader(new InputStreamReader(
					new FileInputStream(filReadMe), "UTF-8"));

			City city = null;

			String strLine = brReadMe.readLine();

			while (strLine != null) {
				coord.province.add(strLine);
				strLine = brReadMe.readLine();
			}

			for (String address : coord.province) {
				System.out.println(address + ": ");
				city = coord.getCityFromLine(address);
				coord.db.addCity(city);
			}
			brReadMe.close();
		} else {
			List<City> cities = coord.db.getCities();

			for (City c : cities) {
				boolean res = coord.db.isCityGeotagged(c.getCity_name());
				System.out.println("Geotag = " + res);
				if (!res) {
					out = coord.getJsonByGoogle(c.getCity_name() + ", "
							+ c.getRegion());
					coord.parseGeoJson(out, c);
					coord.db.addCoords(c);
				}
			}
		}

	}

	public String getJsonByGoogle(String address) throws MalformedURLException,
			UnsupportedEncodingException, IOException {

		URL url = new URL(geocodingString + URLEncoder.encode(address, "UTF-8")
				+ "&sensor=false");
		System.out.println(url);

		URLConnection conn = url.openConnection();
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);

		IOUtils.copy(conn.getInputStream(), output);

		output.close();

		return output.toString();
	}

	public String getReverseJsonByGoogle(String lat, String lon)
			throws MalformedURLException, UnsupportedEncodingException,
			IOException {

		URL url = new URL(reverseGeocodingString + lat + "," + lon
				+ "&sensor=false");
		System.out.println(url);

		URLConnection conn = url.openConnection();
		ByteArrayOutputStream output = new ByteArrayOutputStream(1024);

		IOUtils.copy(conn.getInputStream(), output);

		output.close();

		return output.toString();
	}

	public void parseReverseGeoJson(String json, Event event)
			throws JsonParseException {

		try {
			JsonElement jelement = new JsonParser().parse(json);
			JsonObject Jsonresults = jelement.getAsJsonObject();
			JsonArray results = Jsonresults.getAsJsonArray("results");
			JsonArray components = results.get(0).getAsJsonObject()
					.getAsJsonArray("address_components");
			JsonArray types = null;
			String region = null;
			String country = null;
			String name = null;
			try {
				for (int i = 0; i < components.size(); i++) {
					types = components.get(i).getAsJsonObject()
							.getAsJsonArray("types");

					if (types.get(0).getAsString().equals("locality")) {
						name = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}

					if (types.get(0).getAsString()
							.equals("administrative_area_level_1")) {
						region = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}

					if (types.get(0).getAsString().equals("country")) {
						country = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}
				}
			} catch (JsonParseException ex) {
			}

			event.getLocation().setName(name);
			event.getLocation().setRegion(region);
			event.getLocation().setCountry(country);

		} catch (JsonParseException ex) {
		} catch (IndexOutOfBoundsException ex2) {
		}

	}

	// used once to add cities to reminiscens.City
	public void parseGeoJson(String json, City city) throws JsonParseException {

		try {
			JsonElement jelement = new JsonParser().parse(json);
			JsonObject Jsonresults = jelement.getAsJsonObject();
			JsonArray results = Jsonresults.getAsJsonArray("results");
			JsonObject geometry = results.get(0).getAsJsonObject()
					.getAsJsonObject("geometry");
			JsonObject location = geometry.getAsJsonObject("location");
			Double lat = Double.parseDouble(location.get("lat").getAsString());
			Double lon = Double.parseDouble(location.get("lng").getAsString());
			city.setLat(lat);
			city.setLon(lon);
			System.out.println(lat);
			System.out.println(lon);
		} catch (JsonParseException ex) {
		} catch (IndexOutOfBoundsException ex2) {
		}

	}

	public void parseGeoJson(String json, Media photo)
			throws JsonParseException {
		try {
			JsonElement jelement = new JsonParser().parse(json);
			JsonObject Jsonresults = jelement.getAsJsonObject();
			JsonArray results = Jsonresults.getAsJsonArray("results");
			JsonObject geometry = results.get(0).getAsJsonObject()
					.getAsJsonObject("geometry");
			JsonObject location = geometry.getAsJsonObject("location");
			String lat = location.get("lat").getAsString();
			String lon = location.get("lng").getAsString();
			JsonArray components = results.get(0).getAsJsonObject()
					.getAsJsonArray("address_components");
			JsonArray types = null;
			String region = null;
			String country = null;
			String city = null;
			try {
				for (int i = 0; i < components.size(); i++) {
					types = components.get(i).getAsJsonObject()
							.getAsJsonArray("types");

					if (types.get(0).getAsString()
							.equals("administrative_area_level_3")) {
						city = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}

					if (types.get(0).getAsString()
							.equals("administrative_area_level_1")) {
						region = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}

					if (types.get(0).getAsString().equals("country")) {
						country = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}
				}
				photo.getLocation().setCity(city);
				photo.getLocation().setRegion(region);
				photo.getLocation().setCountry(country);
				photo.getLocation().setLat(lat);
				photo.getLocation().setLon(lon);
				photo.getLocation().setGoogled(true);
				photo.getLocation().setCoordinates_trust(0);

			} catch (JsonParseException ex) {
				country = "Italy";
				photo.getLocation().setCountry(country);
				photo.getLocation().setGoogled(true);
			}
		} catch (JsonParseException ex) {
		} catch (IndexOutOfBoundsException ex2) {
		}
	}

	public void parseGeoJson(String json, Event event)
			throws JsonParseException {
		try {
			JsonElement jelement = new JsonParser().parse(json);
			JsonObject Jsonresults = jelement.getAsJsonObject();
			JsonArray results = Jsonresults.getAsJsonArray("results");
			JsonObject geometry = results.get(0).getAsJsonObject()
					.getAsJsonObject("geometry");
			JsonObject location = geometry.getAsJsonObject("location");
			String lat = location.get("lat").getAsString();
			String lon = location.get("lng").getAsString();
			JsonArray components = results.get(0).getAsJsonObject()
					.getAsJsonArray("address_components");
			JsonArray types = null;
			String region = null;
			String country = null;
			String city = null;
			try {
				for (int i = 0; i < components.size(); i++) {
					types = components.get(i).getAsJsonObject()
							.getAsJsonArray("types");

					if (types.get(0).getAsString()
							.equals("administrative_area_level_3")) {
						city = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}

					if (types.get(0).getAsString()
							.equals("administrative_area_level_1")) {
						region = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}

					if (types.get(0).getAsString().equals("country")) {
						country = components.get(i).getAsJsonObject()
								.get("long_name").getAsString();
					}
				}
				event.getLocation().setCity(city);
				event.getLocation().setRegion(region);
				event.getLocation().setCountry(country);
				event.getLocation().setLat(lat);
				event.getLocation().setLon(lon);
				event.getLocation().setGoogled(true);

			} catch (JsonParseException ex) {
				country = "Italy";
				event.getLocation().setCountry(country);
				event.getLocation().setGoogled(true);
			}
		} catch (JsonParseException ex) {
		} catch (IndexOutOfBoundsException ex2) {
		}
	}

	// used once to add cities to reminiscens.City
	public City getCityFromLine(String line) {
		City city = new City();
		String name = line.substring(0, line.indexOf(','));
		city.setCity_name(name);
		String region = line.substring(line.indexOf(',') + 1, line.length());
		city.setRegion(region);
		city.setCountry("Italy");
		return city;
	}
}
