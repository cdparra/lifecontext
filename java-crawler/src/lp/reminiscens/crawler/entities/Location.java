/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler.entities;

import java.math.BigInteger;

/**
 *
 * @author Nicola.Parrello
 */
public class Location {

    private BigInteger location_id;
    private Integer accuracy;
    private Integer coordinates_trust;
    private Boolean googled;
    private String textual;
    private String name;
    private String lat;
    private String lon;
    private String continent;
    private String country;
    private String city;
    private String region;
    private String locale;
    private Media photo;
    private Event event;
    private Person person;

    public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Location() {
    	googled = false;
    }

    public Location(String name) {
        this.name = name;
        accuracy = 3;
    }

    public BigInteger getLocation_id() {
        return location_id;
    }

    
    public void setLocation_id(BigInteger location_id) {
        this.location_id = location_id;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getCoordinates_trust() {
        return coordinates_trust;
    }

    public void setCoordinates_trust(Integer coordinates_trust) {
        this.coordinates_trust = coordinates_trust;
    }

    public Boolean isGoogled() {
        return googled;
    }

    public void setGoogled(Boolean googled) {
        this.googled = googled;
    }
    
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getCity() {
        return city;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTextual() {
        return textual;
    }

    public void setTextual(String textual) {
        this.textual = textual;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public Media getPhoto() {
        return photo;
    }

    public void setPhoto(Media photo) {
        this.photo = photo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
