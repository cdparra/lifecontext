package lp.reminiscens.crawler;

import java.util.List;

import lp.reminiscens.crawler.entities.City;
import lp.reminiscens.crawler.entities.Context_Index;
import lp.reminiscens.crawler.entities.Media;

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

		updater.indexMedia();

	}

	public void indexMedia() {

		List<Media> mediaList = db.getMedia();

		for (Media m : mediaList) {

			// Context_Index cRow = new Context_Index();

			try {

				System.out.println("Valuto media con title = " +
				 m.getCaption());

				// cRow.setMedia(m);

				// cRow.setDecade(Integer.parseInt(m.getTakenDate().getDecade()));
				// cRow.setYear(Integer.parseInt(m.getTakenDate().getYear()));

				double lat = Double.parseDouble(m.getLocation().getLat());
				double lon = Double.parseDouble(m.getLocation().getLon());

				Object[] city = db.getClosestCity(lat, lon);
				System.out.println("City: " + city[0]);
				System.out.println("Distance: "+ city[1]);

				// cRow.setDistance((Double) city[1]);

				// System.out.println("La città più vicina è " +
				// c.getCity_name() + "con id = " + c.getCity_id());

				// cRow.setCity(db.getCityById((Integer) city[0]));

				// cRow.setCoordinatesTrust(m.getLocation().getCoordinates_trust());

				boolean result = db.addMediaToIndex_with_ids(Integer.parseInt(m
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
