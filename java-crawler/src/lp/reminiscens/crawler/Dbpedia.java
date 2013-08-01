package lp.reminiscens.crawler;

import com.hp.hpl.jena.query.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashSet;
import lp.reminiscens.crawler.entities.Event;
import lp.reminiscens.crawler.entities.Fuzzy_Date;
import lp.reminiscens.crawler.entities.Life_Event;
import lp.reminiscens.crawler.entities.Location;
import lp.reminiscens.crawler.entities.Media_Metadata;
import lp.reminiscens.crawler.entities.Participant;
import lp.reminiscens.crawler.entities.Person;

public class Dbpedia {

	Collection<Event> events;
	Collection<Participant> lives;
	Collection<Media_Metadata> mediaMDs;
	private String sparqlService = "http://dbpedia.org/sparql";
	final static String ENGLISH = "en";
	final static String ITALIAN = "it";

	YoutubeSearcher youtube;
	CoordSearcher coord;

	public Dbpedia() {

		events = new HashSet<Event>();
		lives = new HashSet<Participant>();
		mediaMDs = new HashSet<Media_Metadata>();

		coord = new CoordSearcher();

	}

	public String formatDate(String date) {
		if (date != null) {
			String formattedDate = date.substring(0, 10);
			return formattedDate;
		} else {
			return null;
		}
	}

	public String formatStringLocale(String string) {
		if (string != null) {
			String formattedString = string.substring(0, string.length() - 3);
			return formattedString;
		} else {
			return null;
		}
	}

	public String formatLocationToUrl(String location) {

		if (location != null) {
			return location.replaceAll(" ", "_");
		} else {
			return null;
		}
	}

	public void lookUpMediaMM(String language, String type) {

		if (!mediaMDs.isEmpty()) {
			mediaMDs.clear();
		}

		String sparql = null;

		if (type.equals("ALBUM")) {
			sparql = "SELECT *"
					+ "WHERE {"
					+ "?work a <http://dbpedia.org/ontology/Album> ."
					+ "?work <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
					+ "?work <http://dbpedia.org/ontology/abstract> ?descr ."
					+ "?work <http://purl.org/dc/terms/subject> <http://dbpedia.org/resource/Category:Italian-language_albums> ."
					+ "{"
					+ "?work <http://dbpedia.org/ontology/releaseDate> ?relDate ."
					+ "}" + "UNION {"
					+ "?work <http://dbpedia.org/property/released> ?relDate_1"
					+ "}" + "FILTER (lang(?title)='" + language
					+ "' && lang(?descr)='" + language + "')" + "}";
		} else if (type.equals("BOOK")) {
			sparql = "SELECT *"
					+ "WHERE {"
					+ "?work a <http://dbpedia.org/ontology/Book> ."
					+ "?work <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
					+ "?work <http://dbpedia.org/ontology/abstract> ?descr ."
					+ "{"
					+ "?work <http://dbpedia.org/property/pubDate> ?relDate ."
					+ "}" + "UNION {"
					+ "?work <http://dbpedia.org/property/released> ?relDate_1"
					+ "}" + "FILTER (lang(?title)='" + language
					+ "' && lang(?descr)='" + language + "')" + "}";
		} else if (type.equals("SONG")) {
			sparql = "SELECT *"
					+ "WHERE {"
					+ "?work a <http://dbpedia.org/ontology/Song> ."
					+ "?work <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
					+ "?work <http://dbpedia.org/ontology/abstract> ?descr ."
					+ "?work <http://dbpedia.org/ontology/artist> ?author ."
					+ "?work <http://dbpedia.org/ontology/genre> ?genre ."
					+ "{"
					+ "?work <http://dbpedia.org/ontology/releaseDate> ?relDate ."
					+ "}" + "UNION {"
					+ "?work <http://dbpedia.org/property/released> ?relDate_1"
					+ "}" + "FILTER (lang(?title)='" + language
					+ "' && lang(?descr)='" + language + "')" + "}";

			youtube = new YoutubeSearcher();

		} else if (type.equals("FILM")) {
			sparql = "SELECT *"
					+ "WHERE {"
					+ "?work a <http://dbpedia.org/ontology/Film> ."
					+ "?work <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
					+ "?work <http://dbpedia.org/ontology/abstract> ?descr ."
					+ "{"
					+ "?work <http://dbpedia.org/ontology/releaseDate> ?relDate ."
					+ "}" + "UNION {"
					+ "?work <http://dbpedia.org/property/released> ?relDate_1"
					+ "}" + "FILTER (lang(?title)='" + language
					+ "' && lang(?descr)='" + language + "')" + "}";
		}

		ParameterizedSparqlString paramQuery = new ParameterizedSparqlString(
				sparql);

		Query query = paramQuery.asQuery();

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlService, query);
		ResultSet results = qexec.execSelect();

		String workAttribute = "work";
		String releaseDateAttribute = "relDate";
		String releaseDateAttribute_1 = "relDate_1";
		String titleAttribute = "title";
		String descrAttribute = "descr";
		String genreAttribute = "genre";
		String authorAttribute ="author";
		String description = null;
		String genre = null;
		String author = null;

		String work_url = null;
		String releaseDate = null;
		String title = null;
		Fuzzy_Date start;

		Media_Metadata mediaMD = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			work_url = qs.get(workAttribute).toString();
			if (qs.get(releaseDateAttribute) == null) {
				releaseDate = qs.get(releaseDateAttribute_1).toString();
			} else {
				releaseDate = qs.get(releaseDateAttribute).toString();
			}
			if (releaseDate.substring(0, 4).matches("^([1-2][0-9][0-9][0-9])")
					&& releaseDate.length() > 4) {
				releaseDate = formatDate(releaseDate);
			}

			title = qs.get(titleAttribute).toString();
			description = qs.get(descrAttribute).toString();

			System.out.println(releaseDate);
			System.out.println(title);
			System.out.println(description);

			if (releaseDate.substring(0, 4).matches("^([1-2][0-9][0-9][0-9])")) {

				if (Integer.parseInt(releaseDate.substring(0, 4)) > 1900) {

					mediaMD = new Media_Metadata();
					mediaMD.setSource("dbpedia");

					title = formatStringLocale(title);
					if (title.length() > 255) {
						mediaMD.setTitle(title.substring(0, 99));
					} else {
						mediaMD.setTitle(title);
					}

					description = formatStringLocale(description);
					if (description.length() > 65535) {
						mediaMD.setDescription(description.substring(0, 65534));
					} else {
						mediaMD.setDescription(description);
					}

					mediaMD.setType(type);
					mediaMD.setSource_url(work_url);

					start = new Fuzzy_Date();
					start.setSeasonLimits();
					start.splitDate(releaseDate);
					mediaMD.setReleaseDate(start);
					start.setMediaMD(mediaMD);

					mediaMD.setLocale(language);

					if (type.equals("SONG")) {
						try {

							author = qs.get(authorAttribute).toString();
							author = mediaMD.formatAttribute(author);
							mediaMD.setAuthor(author);
							
							genre = qs.get(genreAttribute).toString();
							genre = mediaMD.formatAttribute(genre);
							mediaMD.setTags(genre);
							
							if (author == null) {
								mediaMD.setResource_url(youtube.getVideoUrl(mediaMD
										.getTitle(),""));
							} else {
								mediaMD.setResource_url(youtube.getVideoUrl(mediaMD
										.getTitle(),author));
							}

							
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					mediaMDs.add(mediaMD);
				}
			}
		}
		qexec.close();
	}

	public void lookUpEvents(String fromStartDate, String toStartDate,
			String locationName, String language) {

		String sparql = "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>"
				+ "SELECT *"
				+ "WHERE {"
				+ "?event a <http://dbpedia.org/ontology/Event> ."
				+ "?event <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
				+ "?event <http://dbpedia.org/ontology/abstract> ?desc ."
				+ "?event <http://dbpedia.org/ontology/startDate> ?startDate ."
				+ "OPTIONAL {"
				+ "?event <http://dbpedia.org/ontology/location> ?location ."
				+ "?event <http://dbpedia.org/ontology/endDate> ?endDate ."
				+ "}" + "FILTER((?startDate >= '" + fromStartDate
				+ "'^^xsd:date) && (?startDate <= '" + toStartDate
				+ "'^^xsd:date)" + "&& (lang(?title)='" + language
				+ "') && (lang(?desc)='" + language + "'))" + "}";

		ParameterizedSparqlString paramQuery = new ParameterizedSparqlString(
				sparql);

		Query query = paramQuery.asQuery();

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlService, query);
		ResultSet results = qexec.execSelect();

		String eventAttribute = "event";
		String startDateAttribute = "startDate";
		String titleAttribute = "title";
		String locationAttribute = "location";
		String endDateAttribute = "endDate";
		String descrAttribute = "desc";
		String endDate = null;
		String locationUrl = null;
		String description = null;

		String event_url = null;
		String startDate = null;
		String title = null;
		Location location;
		Fuzzy_Date start;
		Fuzzy_Date end;

		Event event = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			event_url = qs.get(eventAttribute).toString();
			startDate = qs.get(startDateAttribute).toString();
			startDate = formatDate(startDate);
			title = qs.get(titleAttribute).toString();
			description = qs.get(descrAttribute).toString();
			if (qs.get(endDateAttribute) != null) {
				endDate = qs.get(endDateAttribute).toString();
				endDate = formatDate(endDate);
			}

			if (qs.get(locationAttribute) != null) {
				locationUrl = qs.get(locationAttribute).toString();
			}

			event = new Event();
			event.setSource("dbpedia");
			description = formatStringLocale(description);
			if (description.length() > 65535) {
				event.setText(description.substring(0, 65534));
			} else {
				event.setText(description);
			}

			title = formatStringLocale(title);
			if (title.length() > 99) {
				event.setHeadline(title.substring(0, 99));
			} else {
				event.setHeadline(title);
			}
			event.setType("GENERAL");
			event.setSource_url(event_url);

			start = new Fuzzy_Date();
			start.setSeasonLimits();
			start.splitDate(startDate);
			start.setEvent(event);
			event.setStartDate(start);

			if (endDate != null) {
				end = new Fuzzy_Date();
				end.setSeasonLimits();
				end.splitDate(endDate);
				end.setEvent(event);
				event.setEndDate(end);
			}
			if (locationUrl != null) {
				location = new Location();
				location.setTextual(Event.formatLocation(locationUrl));
				location.setEvent(event);
				location.setAccuracy(3);
				event.setLocation(location);
			}
			event.setLocale(language);

			events.add(event);
		}
		qexec.close();
	}

	public void lookUpSpaceMissions(String fromStartDate, String toStartDate,
			String locationName, String language) {

		String sparql = "SELECT DISTINCT *"
				+ "WHERE {"
				+ "?event a <http://dbpedia.org/ontology/SpaceMission> ."
				+ "?event <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
				+ "?event <http://dbpedia.org/ontology/abstract> ?descr ."
				+ "?event <http://dbpedia.org/ontology/launchDate> ?launchDate ."
				+ "?event <http://dbpedia.org/ontology/landingDate> ?landingDate ."
				+ "OPTIONAL {"
				+ "?event <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat ."
				+ "?event <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lon ."
				+ "}" + "FILTER (lang(?title)='" + language
				+ "' && lang(?descr)='" + language + "')" + "}";

		ParameterizedSparqlString paramQuery = new ParameterizedSparqlString(
				sparql);

		Query query = paramQuery.asQuery();

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlService, query);
		ResultSet results = qexec.execSelect();

		String eventAttribute = "event";
		String launchDateAttribute = "launchDate";
		String landingDateAttribute = "landingDate";
		String titleAttribute = "title";
		// String locationAttribute = "location";
		String descrAttribute = "descr";
		String latAttribute = "lat";
		String lonAttribute = "lon";
		String landingDate = null;
		String description = null;
		String lat = null;
		String lon = null;

		String event_url = null;
		String launchDate = null;
		String title = null;
		Location location;
		Fuzzy_Date start;
		Fuzzy_Date end;

		Event event = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			event_url = qs.get(eventAttribute).toString();
			launchDate = qs.get(launchDateAttribute).toString();
			launchDate = formatDate(launchDate);
			landingDate = qs.get(landingDateAttribute).toString();
			landingDate = formatDate(landingDate);
			title = qs.get(titleAttribute).toString();
			description = qs.get(descrAttribute).toString();

			try {
				lat = qs.get(latAttribute).toString();
			} catch (Exception e) {
			}
			try {
				lon = qs.get(lonAttribute).toString();
			} catch (Exception e) {
			}

			event = new Event();
			event.setSource("dbpedia");
			description = formatStringLocale(description);
			if (description.length() > 65535) {
				event.setText(description.substring(0, 65534));
			} else {
				event.setText(description);
			}

			title = formatStringLocale(title);
			if (title.length() > 99) {
				event.setHeadline(title.substring(0, 99));
			} else {
				event.setHeadline(title);
			}
			event.setType("SPACE_MISSION");
			event.setSource_url(event_url);

			start = new Fuzzy_Date();
			start.setSeasonLimits();
			start.splitDate(launchDate);
			start.setEvent(event);
			event.setStartDate(start);

			end = new Fuzzy_Date();
			end.setSeasonLimits();
			end.splitDate(landingDate);
			end.setEvent(event);
			event.setEndDate(end);

			event.setLocale(language);

			if (lat != null && lon != null) {

				System.out.println(lat);
				System.out.println(lon);

				if (lat.indexOf('^') != -1) {
					lat = lat.substring(0, lat.indexOf('^'));
				}

				if (lon.indexOf('^') != -1) {
					lon = lon.substring(0, lon.indexOf('^'));
				}

				location = new Location();
				location.setEvent(event);
				event.setLocation(location);

				location.setLat(lat);
				location.setLon(lon);
				location.setCoordinates_trust(1);
				location.setGoogled(true);

				String json;
				try {
					json = coord.getReverseJsonByGoogle(lat, lon);
					coord.parseReverseGeoJson(json, event);
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				}

			}

			events.add(event);
		}
		qexec.close();
	}

	public void lookUpSportEvents(String fromStartDate, String toStartDate,
			String locationName, String language) {

		String sparql = "SELECT DISTINCT *"
				+ "WHERE {"
				+ "?event a <http://dbpedia.org/ontology/SportsEvent> ."
				+ "?event <http://www.w3.org/2000/01/rdf-schema#label> ?title ."
				+ "?event <http://dbpedia.org/ontology/abstract> ?descr ."
				+ "?event <http://dbpedia.org/ontology/date> ?date ."
				+ "?event <http://dbpedia.org/ontology/city> ?city."
				+ "FILTER (lang(?title)='" + language + "' && lang(?descr)='"
				+ language + "')" + "}";

		ParameterizedSparqlString paramQuery = new ParameterizedSparqlString(
				sparql);

		Query query = paramQuery.asQuery();

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlService, query);
		ResultSet results = qexec.execSelect();

		String eventAttribute = "event";
		String dateAttribute = "date";
		String titleAttribute = "title";
		String cityAttribute = "city";
		String descrAttribute = "descr";
		String city = null;

		String event_url = null;
		String date = null;
		String title = null;
		String description = null;
		Location location;
		Fuzzy_Date start;
		Fuzzy_Date end;

		Event event = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			event_url = qs.get(eventAttribute).toString();
			date = qs.get(dateAttribute).toString();
			date = formatDate(date);
			title = qs.get(titleAttribute).toString();
			description = qs.get(descrAttribute).toString();
			city = qs.get(cityAttribute).toString();

			event = new Event();
			event.setSource("dbpedia");
			description = formatStringLocale(description);
			if (description.length() > 65535) {
				event.setText(description.substring(0, 65534));
			} else {
				event.setText(description);
			}

			title = formatStringLocale(title);
			System.out.println(title);
			if (title.length() > 99) {
				event.setHeadline(title.substring(0, 99));
			} else {
				event.setHeadline(title);
			}
			event.setType("SPORT_EVENT");
			event.setSource_url(event_url);

			start = new Fuzzy_Date();
			start.setSeasonLimits();
			start.splitDate(date);
			start.setEvent(event);
			event.setStartDate(start);

			location = new Location();
			location.setTextual(Event.formatLocation(city));
			location.setEvent(event);
			location.setAccuracy(3);
			event.setLocation(location);

			event.setLocale(language);

			events.add(event);
		}

		qexec.close();
	}

	public void lookUpPeople(String fromBirthDate, String toBirthDate,
			String locationName, String language) {

		if (!lives.isEmpty()) {
			lives.clear();
		}
		System.out.println(locationName);
		String sparql = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "PREFIX owl: <http://dbpedia.org/ontology/> "
				+ "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#> "
				+ "PREFIX dbprop:  <http://dbpedia.org/property/> "
				+ "SELECT *" + "WHERE { " + "?person a owl:Person . "
				+ "?person foaf:givenName ?nome . "
				+ "?person foaf:surname ?cognome . "
				+ "?person owl:abstract ?descr . "
				+ "?person owl:birthDate ?birthDate . "
				+ "?person owl:birthPlace ?birthPlace . "
				+ "OPTIONAL {?person owl:deathDate ?deathDate . "
				+ "?person owl:deathPlace ?deathPlace .} "
				+ "?person owl:birthPlace  <http://dbpedia.org/resource/"
				+ formatLocationToUrl(locationName) + "> . "
				// +
				// "FILTER((?birthDate >= '?from'^^xsd:date) && (?birthDate <= '?to'^^xsd:date) "
				// + "&& (lang(?descr)='?language')) "
				+ "FILTER (lang(?descr)='" + language + "') " + "}";

		ParameterizedSparqlString paramQuery = new ParameterizedSparqlString(
				sparql);

		Query query = paramQuery.asQuery();

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlService, query);
		ResultSet results = qexec.execSelect();

		String personAttribute = "person";
		String nameAttribute = "nome";
		String surnameAttribute = "cognome";
		String descrAttribute = "descr";
		String birthDateAttribute = "birthDate";
		String deathDateAttribute = "deathDate";
		String birthPlaceAttribute = "birthPlace";
		String deathPlaceAttribute = "deathPlace";

		String person_url = null;
		String firstname = null;
		String lastname = null;
		String description = null;
		String birthDateString = null;
		String deathDateString = null;
		String birthPlaceString = null;
		String deathPlaceString = null;
		Participant wasBorn = null;
		Participant died = null;
		Life_Event birth = null;
		Life_Event death = null;
		;
		Location birthPlace;
		Location deathPlace;
		Fuzzy_Date birthDate;
		Fuzzy_Date deathDate;

		Person person = null;
		while (results.hasNext()) {
			QuerySolution qs = results.next();
			person_url = qs.get(personAttribute).toString();
			firstname = qs.get(nameAttribute).toString();
			firstname = formatStringLocale(firstname);
			lastname = qs.get(surnameAttribute).toString();
			lastname = formatStringLocale(lastname);
			description = qs.get(descrAttribute).toString();
			description = formatStringLocale(description);

			person = new Person();

			wasBorn = new Participant();
			wasBorn.setFocus(true);
			wasBorn.setStatus("CONFIRMED");

			birth = new Life_Event();
			birthDate = new Fuzzy_Date();

			birthDateString = qs.get(birthDateAttribute).toString();
			birthDateString = formatDate(birthDateString);
			birthDate.splitDate(birthDateString);
			birthDate.setEvent(birth);
			birth.setStartDate(birthDate);

			birth.setParticipation(wasBorn);
			birth.setType("birth");
			wasBorn.setLife_event(birth);
			wasBorn.setPerson(person);
			//person.setBirth(wasBorn);

			if (qs.get(deathDateAttribute) != null) {
				died = new Participant();
				died.setFocus(true);
				died.setStatus("CONFIRMED");

				death = new Life_Event();
				deathDate = new Fuzzy_Date();

				deathDateString = qs.get(deathDateAttribute).toString();
				deathDateString = formatDate(deathDateString);
				deathDate.splitDate(deathDateString);
				deathDate.setEvent(death);
				death.setStartDate(deathDate);
				death.setParticipation(died);
				death.setType("death");
				died.setLife_event(death);
				died.setPerson(person);
				//person.setDeath(died);
			}

			birthPlace = new Location();
			birthPlaceString = qs.get(birthPlaceAttribute).toString();
			birthPlaceString = Person.formatLocation(birthPlaceString);
			birthPlace.setTextual(birthPlaceString);
			birthPlace.setEvent(birth);
			birth.setLocation(birthPlace);

			if (qs.get(deathPlaceAttribute) != null && death != null) {
				deathPlace = new Location();
				deathPlaceString = qs.get(deathPlaceAttribute).toString();
				deathPlaceString = Person.formatLocation(deathPlaceString);
				deathPlace.setTextual(deathPlaceString);
				deathPlace.setEvent(death);
				death.setLocation(deathPlace);
			}

			person.setFirstName(firstname);
			person.setLastName(lastname);
			person.setFamous_for(description);
			person.setSource("dbpedia");
			person.setSource_url(person_url);
			person.setCreator_type("SYSTEM");
			person.setLocale(language);

			lives.add(wasBorn);
			if (died != null) {
				lives.add(died);
			}
		}

		qexec.close();
	}
}
