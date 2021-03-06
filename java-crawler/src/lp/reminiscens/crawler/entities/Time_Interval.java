/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler.entities;

import java.sql.Date;

/**
 *
 * @author Nicola.Parrello
 */
public class Time_Interval {

    private Integer time_interval_id;
    private Integer is_fuzzy;
    private String start_date;
    private String end_date;
    private String duration_unit;
    private String duration_amount;
    private Fuzzy_Date startdate;
    private Fuzzy_Date enddate;
    private Media photo;
    private Event event;
    private Media_Metadata mediaMD;

    public Time_Interval() {
        duration_unit = "days";
    }

    public static void main(String[] args) {
        Date d3 = new Date(1352048409 * 1000L);
        System.out.println(d3);
        System.out.println(System.currentTimeMillis());
    }
    
    public Integer getTime_interval_id() {
        return time_interval_id;
    }

    public void setTime_interval_id(Integer time_interval_id) {
        this.time_interval_id = time_interval_id;
    }

    public Integer getIs_fuzzy() {
        return is_fuzzy;
    }

    public void setIs_fuzzy(Integer is_fuzzy) {
        this.is_fuzzy = is_fuzzy;
    }
    
    public Media_Metadata getMediaMD() {
        return mediaMD;
    }

    public void setMediaMD(Media_Metadata mediaMD) {
        this.mediaMD = mediaMD;
    }

    public static String formatDate(String date) {
        return date.substring(0, 10);
    }

    public String getDuration_amount() {
        return duration_amount;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setDuration_amount(String duration_amount) {
        this.duration_amount = duration_amount;
    }

    public String getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(String duration_unit) {
        this.duration_unit = duration_unit;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public Media getPhoto() {
        return photo;
    }

    public void setPhoto(Media photo) {
        this.photo = photo;
    }

    public Fuzzy_Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Fuzzy_Date enddate) {
        this.enddate = enddate;
    }

    public Fuzzy_Date getStartdate() {
        return startdate;
    }

    public void setStartdate(Fuzzy_Date startdate) {
        this.startdate = startdate;
    }
}
