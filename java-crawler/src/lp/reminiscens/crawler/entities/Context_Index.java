package lp.reminiscens.crawler.entities;

public class Context_Index {

	
	private int context_index_id;
	private int decade;
	private int year;
	private double distance;
	private int coordinatesTrust;
	
	private Media_Metadata mediaMD;
	private Media media;
	private Event event;
	private City city;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public int getContext_index_id() {
		return context_index_id;
	}

	public void setContext_index_id(int context_index_id) {
		this.context_index_id = context_index_id;
	}

	public int getDecade() {
		return decade;
	}
	public void setDecade(int decade) {
		this.decade = decade;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public int isCoordinatesTrust() {
		return coordinatesTrust;
	}
	public void setCoordinatesTrust(int coordinatesTrust) {
		this.coordinatesTrust = coordinatesTrust;
	}
	public Media_Metadata getMediaMD() {
		return mediaMD;
	}
	public void setMediaMD(Media_Metadata mediaMD) {
		this.mediaMD = mediaMD;
	}
	public Media getMedia() {
		return media;
	}
	public void setMedia(Media media) {
		this.media = media;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}

}
