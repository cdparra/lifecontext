package eu.reminiscens.booklet;

public class Constants {

	
	// Base URLs of our APIs and resources
	//	public static String BASE_URL = "http://localhost:4567/generalBooklet";
	public static String LIFECONTEXT_API = "http://test.reminiscens.me/lifecontext/api";
	public static String GENERAL_BOOKLET= "/generalBooklet";
	public static String MEDIA = "/media";
	public static String EVENTS = "/events";
	public static String WORKS = "/works";
	
	// Service End Points
	public static String MEDIA_SERVICE = Constants.LIFECONTEXT_API + Constants.GENERAL_BOOKLET + Constants.MEDIA;
	public static String EVENTS_SERVICE = Constants.LIFECONTEXT_API + Constants.GENERAL_BOOKLET + Constants.EVENTS;
	public static String WORKS_SERVICE = Constants.LIFECONTEXT_API + Constants.GENERAL_BOOKLET + Constants.WORKS;
	
}
