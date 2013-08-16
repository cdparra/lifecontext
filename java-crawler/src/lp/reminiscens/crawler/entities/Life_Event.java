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
public class Life_Event extends Event {

    private BigInteger life_event_id;
    private String headline;
    private String type;
    private String text;
    private String source;
    private String source_url;
    private String locationName;
    private Timestamp last_update;
    private Timestamp contribution_date;
    private Location location;
      //private List<Participant> participations;
    private Participant participation;
   
    public Life_Event(){
    //participations=new ArrayList<Participant>();
    }
    
    public Life_Event(String source, String source_url, String headline, String startDate, String endDate, String location) {

        this.source_url = source_url;
        this.source = source;
        this.headline = headline;

    }

    public static String formatLocation(String locationUrl) {

        char[] charArray = locationUrl.toCharArray();

        for (int i = 0; i < locationUrl.length(); i++) {
            if ((charArray[i] == 'r') && (charArray[i + 1] == 'c') && (charArray[i + 2] == 'e')) {
                return locationUrl.substring(i + 4, locationUrl.length());
            }
        }

        return "Wrong location url";

    }

    
    public BigInteger getLife_event_id() {
        return life_event_id;
    }

    public void setLife_event_id(BigInteger life_event_id) {
        this.life_event_id = life_event_id;
    }


    /*public List<Participant> getParticipations() {
        return participations;
    }

    public void setParticipations(List<Participant> participations) {
        this.participations = participations;
    }*/
    @Override
    public Fuzzy_Date getStartDate() {
        return super.getStartDate();
    }

    @Override
    public void setStartDate(Fuzzy_Date startDate) {
        super.setStartDate(startDate);
    }

    @Override
    public Fuzzy_Date getEndDate() {
        return super.getEndDate();
    }

    @Override
    public void setEndDate(Fuzzy_Date endDate) {
        super.setEndDate(endDate);
    }

    
    
    public Participant getParticipation() {
        return participation;
    }

    public void setParticipation(Participant participation) {
        this.participation = participation;
    }
    
    public Timestamp getContribution_date() {
        return contribution_date;
    }

    public void setContribution_date(Timestamp contribution_date) {
        this.contribution_date = contribution_date;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Timestamp getLast_update() {
        return last_update;
    }

    public void setLast_update(Timestamp last_update) {
        this.last_update = last_update;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSource() {
        return source;
    }

    public String getSource_url() {
        return source_url;
    }

    public String getType() {
        return type;
    }

}
