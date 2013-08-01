/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler.entities;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 *
 * @author Nicola.Parrello
 */
public class Person {

    private BigInteger person_id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String famous_for;
    private String source;
    private String source_url;
    private String gender;
    private Location deathPlace;
    private Fuzzy_Date deathDate;
    private String picture_url;
    private Location birthPlace;
    private Fuzzy_Date birthDate;
    private Timestamp last_update;
    private String creator_type;
    private String locale;

    public static String formatLocation(String locationUrl) {

        if (locationUrl!=null) {
        String location=locationUrl.substring(28);
        location.replaceFirst("_", " ");
                return location;
        }
        else {
            return null;
        }
        
    }

    public Person() {
    }

    public BigInteger getPerson_id() {
        return person_id;
    }

    public void setPerson_id(BigInteger person_id) {
        this.person_id = person_id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    

    public Location getDeathPlace() {
		return deathPlace;
	}

	public void setDeathPlace(Location deathPlace) {
		this.deathPlace = deathPlace;
	}

	public Fuzzy_Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Fuzzy_Date deathDate) {
		this.deathDate = deathDate;
	}

	public Location getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(Location birthPlace) {
		this.birthPlace = birthPlace;
	}

	public Fuzzy_Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Fuzzy_Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getLocale() {
        return locale;
    }

    public Timestamp getLast_update() {
        return last_update;
    }

    public String getCreator_type() {
        return creator_type;
    }

    public void setCreator_type(String creator_type) {
        this.creator_type = creator_type;
    }

    public void setLast_update(Timestamp last_update) {
        this.last_update = last_update;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFamous_for() {
        return famous_for;
    }

    public void setFamous_for(String famous_for) {
        this.famous_for = famous_for;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }
}
