package lp.reminiscens.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import lp.reminiscens.crawler.entities.Fuzzy_Date;
import lp.reminiscens.crawler.entities.Life_Event;
import lp.reminiscens.crawler.entities.Location;
import lp.reminiscens.crawler.entities.Participant;
import lp.reminiscens.crawler.entities.Person;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class OpenData {

	CoordSearcher coord;
	Database db;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OpenData open = new OpenData();
		open.lookUpFamousPeopleInOpenData();

	}

	public OpenData() {
		coord = new CoordSearcher();
		db = new Database();
	}

	public void lookUpFamousPeopleInOpenData() {

		String qString = "http://dati.trentino.it/api/action/datastore_search?resource_id=f401c8aa-41e0-4c72-8944-edb196972208";

		String qResult = null;

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet(qString);

		try {

			HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

			if (httpEntity != null) {

				InputStream inputStream = httpEntity.getContent();
				Reader in = new InputStreamReader(inputStream);
				BufferedReader bufferedreader = new BufferedReader(in);
				StringBuilder stringBuilder = new StringBuilder();
				String stringReadLine = null;

				while ((stringReadLine = bufferedreader.readLine()) != null) {

					stringBuilder.append(stringReadLine + "\n");
				}

				qResult = stringBuilder.toString();

			}

		} catch (ClientProtocolException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		System.out.println(qResult);

		try {

			JsonElement jelement = new JsonParser().parse(qResult);
			JsonObject jObj = jelement.getAsJsonObject();
			System.out.println(jObj);
			jObj = jObj.getAsJsonObject("result");
			System.out.println(jObj);
			JsonArray items = jObj.getAsJsonArray("records");
			for (JsonElement jEl : items) {
				
				System.out.println(jEl.getAsJsonObject().get("Storia"));
				Person p = parsePerson(jEl.getAsJsonObject());
								
			}


		} catch (Exception e) {

		}

	}

	public Person parsePerson(JsonObject obj) {

		Person p = new Person();

		p.setFullName(obj.get("Nome").getAsString());
		p.setSource("trentinocultura");
		p.setSource_url(obj.get("url").getAsString());
		p.setCreator_type("SYSTEM");
		p.setFamous(true);

		if (obj.get("La vita") != null) {
			p.setFamous_for(obj.get("La vita").getAsString());
		} else if (obj.get("Attivita") != null) {
			p.setFamous_for(obj.get("Attivita").getAsString());
		} else if (obj.get("Storia") == null) {
			p.setFamous_for(obj.get("Storia").getAsString());
		}

		if ((obj.get("Data di nascita") != null)
				|| (obj.get("Luogo di nascita") != null)) {

			Participant birth = new Participant();
			Life_Event birthEvent = new Life_Event();

			birth.setLife_event(birthEvent);
			birthEvent.setParticipation(birth);

			if (obj.get("Data di nascita") != null) {
				String date = obj.get("Data di nascita").getAsString();

				Fuzzy_Date birthDate = new Fuzzy_Date();
				birthDate.setLife_event(birthEvent);
				birthEvent.setStartDate(birthDate);

				if (date.matches("^(([0-9]{2})-[0-9]{2}-([0-9]{4}){0,1})$")) {
					date = birthDate.turnDate(date);
					birthDate.splitDate(date);
				} else if (date
						.matches("^(([0-9]{2})\\/[0-9]{2}\\/([0-9]{4}){0,1})$")) {
					date.replace("/", "-");
					date = birthDate.turnDate(date);
					birthDate.splitDate(date);
				} else {
					birthDate.setTextual_date(date);
				}
			}

			if (obj.get("Luogo di nascita") != null) {
				String location = obj.get("Luogo di nascita").getAsString();

				Location l = new Location();
				birthEvent.setLocation(l);
				l.setEvent(birthEvent);

				String out;
				try {
					out = coord.getJsonByGoogle(location);
					coord.parseGeoJson(out, birthEvent);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			db.addParticipant(birth);

			if ((obj.get("Data di morte") != null)
					|| (obj.get("Luogo di morte") != null)) {

				Participant death = new Participant();
				Life_Event deathEvent = new Life_Event();

				birth.setLife_event(deathEvent);
				birthEvent.setParticipation(death);

				if (obj.get("Data di morte") != null) {
					String date = obj.get("Data di morte").getAsString();

					Fuzzy_Date deathDate = new Fuzzy_Date();
					deathDate.setLife_event(deathEvent);
					deathEvent.setStartDate(deathDate);

					if (date.matches("^(([0-9]{2})-[0-9]{2}-([0-9]{4}){0,1})$")) {
						date = deathDate.turnDate(date);
						deathDate.splitDate(date);
					} else if (date
							.matches("^(([0-9]{2})\\/[0-9]{2}\\/([0-9]{4}){0,1})$")) {
						date.replace("/", "-");
						date = deathDate.turnDate(date);
						deathDate.splitDate(date);
					} else {
						deathDate.setTextual_date(date);
					}
				}

				if (obj.get("Luogo di morte") != null) {
					String location = obj.get("Luogo di morte").getAsString();

					Location l = new Location();
					deathEvent.setLocation(l);
					l.setEvent(deathEvent);

					String out;
					try {
						out = coord.getJsonByGoogle(location);
						coord.parseGeoJson(out, deathEvent);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				
				db.addParticipant(death);

			}

		}
		
		return p;
	}
}
