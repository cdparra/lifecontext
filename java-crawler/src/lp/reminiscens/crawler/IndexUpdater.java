package lp.reminiscens.crawler;

import java.math.BigInteger;
import java.util.List;

import lp.reminiscens.crawler.entities.Event;
import lp.reminiscens.crawler.entities.Media;
import lp.reminiscens.crawler.entities.Media_Metadata;
import lp.reminiscens.crawler.entities.Person;

public class IndexUpdater {

	Database db;

	public IndexUpdater() {

		db = new Database();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IndexUpdater updater = new IndexUpdater();

		// updater.indexMedia();
		// updater.indexEvents();
		// updater.indexWorks();
		updater.indexFamousPeople();

	}

	public void indexMedia() {

		List<Media> mediaList = db.getMedia();

		for (Media m : mediaList) {

			try {

				System.out
						.println("Valuto media con title = " + m.getCaption());

				double lat = Double.parseDouble(m.getLocation().getLat());
				double lon = Double.parseDouble(m.getLocation().getLon());

				Object[] city = db.getClosestCity(lat, lon);
				System.out.println("City: " + city[0]);
				System.out.println("Distance: " + city[1]);

				boolean result = db.addMediaToIndex(Integer.parseInt(m
						.getTakenDate().getDecade()), Integer.parseInt(m
						.getTakenDate().getYear()), (Double) city[1], m
						.getMedia_id(), (Integer) city[0], m.getLocation()
						.getCoordinates_trust());

				if (result) {
					System.out.println("A posto");
				} else {
					System.out.println("No no!");
				}

			} catch (Exception e) {
			}

		}
	}

	public void indexEvents() {

		List<Event> eventList = db.getEvents();

		for (Event e : eventList) {

			try {

				System.out.println("Valuto evento con title = "
						+ e.getHeadline());

				double lat = Double.parseDouble(e.getLocation().getLat());
				double lon = Double.parseDouble(e.getLocation().getLon());

				Object[] city = db.getClosestCity(lat, lon);
				System.out.println("City: " + city[0]);
				System.out.println("Distance: " + city[1]);

				boolean result = db.addEventToIndex(Integer.parseInt(e
						.getStartDate().getDecade()), Integer.parseInt(e
						.getStartDate().getYear()), (Double) city[1], e
						.getEvent_id(), (Integer) city[0], e.getLocation()
						.getCoordinates_trust());

				if (result) {
					System.out.println("A posto");
				} else {
					System.out.println("No no!");
				}

			} catch (Exception ex) {
				System.out
						.println("Errore: indice non aggiornato per la voce attuale");
				ex.printStackTrace();
			}

		}
	}

	public void indexFamousPeople() {

		List<Person> peopleList = db.getFamousPeople();

		boolean result = false;

		for (Person p : peopleList) {

			try {

				System.out.println("Valuto person con nome = "
						+ p.getFullName());

				if (p.getBirthPlace() != null) {
					if (p.getBirthPlace().getLat() != null
							&& p.getBirthPlace().getLon() != null) {
						if (p.getBirthDate().getDecade() != null
								&& p.getBirthDate().getYear() != null) {

							System.out.println("Nato");

							double lat = Double.parseDouble(p.getBirthPlace()
									.getLat());
							double lon = Double.parseDouble(p.getBirthPlace()
									.getLon());

							Object[] city = db.getClosestCity(lat, lon);
							System.out.println("City: " + city[0]);
							System.out.println("Distance: " + city[1]);

							result = db
									.addFamousPersonToIndex(Integer.parseInt(p
											.getBirthDate().getDecade()),
											Integer.parseInt(p.getBirthDate()
													.getYear()),
											(Double) city[1], p.getPerson_id(),
											(Integer) city[0], p
													.getBirthPlace()
													.getCoordinates_trust());
						}
					}

				}

				if (p.getDeathPlace() != null) {
					if (p.getDeathPlace().getLat() != null
							&& p.getDeathPlace().getLon() != null) {
						if (p.getDeathDate().getDecade() != null
								&& p.getDeathDate().getYear() != null) {

							System.out.println("Morto");

							double lat = Double.parseDouble(p.getDeathPlace()
									.getLat());
							double lon = Double.parseDouble(p.getDeathPlace()
									.getLon());

							Object[] city = db.getClosestCity(lat, lon);
							System.out.println("City: " + city[0]);
							System.out.println("Distance: " + city[1]);

							result = db
									.addFamousPersonToIndex(Integer.parseInt(p
											.getDeathDate().getDecade()),
											Integer.parseInt(p.getDeathDate()
													.getYear()),
											(Double) city[1], p.getPerson_id(),
											(Integer) city[0], p
													.getDeathPlace()
													.getCoordinates_trust());
						}
					}
				}

				if (result) {
					System.out.println("A posto");
				} else {
					System.out.println("No no!");
				}

			} catch (Exception ex) {
				System.out
						.println("Errore: indice non aggiornato per la voce attuale");
				ex.printStackTrace();
			}

		}
	}

	public void indexWorks() {

		List<Media_Metadata> workList = db.getMDs();

		for (Media_Metadata w : workList) {

			try {

				System.out.println("Valuto work con title = " + w.getTitle());

				boolean result = db.addWorkToIndex(
						Integer.parseInt(w.getReleaseDate().getDecade()),
						Integer.parseInt(w.getReleaseDate().getYear()),
						w.getMedia_metadata_id());

				if (result) {
					System.out.println("A posto");
				} else {
					System.out.println("No no!");
				}

			} catch (Exception ex) {
				System.out
						.println("Errore: indice non aggiornato per la voce attuale");
				ex.printStackTrace();
			}

		}
	}

	public static double distFrom(float lat1, float lng1, float lat2, float lng2) {
		double earthRadius = 6371;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;

		return dist;

	}

}
