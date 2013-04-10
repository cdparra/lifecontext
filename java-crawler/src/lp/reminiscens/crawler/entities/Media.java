/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler.entities;

/**
 *
 * @author Nicola.Parrello
 */
import java.sql.Timestamp;

public class Media {

    private Integer media_id;
    private String media_url;
    private String media_type;
    private String caption;
    private String text;
    private String source;
    private String source_url;
    private Timestamp last_update;
    private Integer is_public;
    private Location location;
    private Fuzzy_Date takenDate;
    private String locale;
    private String owner;
    private String flickr_photo_id;
    private String secret;

    
    public Media () {}
   
    public Media(String flickr_photo_id, String owner, String caption, String secret) {
        this.setFlickr_photo_id(flickr_photo_id);
        this.setOwner(owner);
        this.setCaption(dbformatTitle(caption));
        this.setMedia_type("photo");
        this.setSource("flickr");
        this.setSecret(secret);
        this.source_url = "http://www.flickr.com/photos/" + owner + "/" + flickr_photo_id + "/";
        this.is_public = 1;
    }
    
        public String dbformatTitle(String caption) {

        return caption.replaceAll("'", "\\\\'");

    }
        
         public String toSmallPhoto(String url) {
        char[] charArray = url.toCharArray();
        charArray[url.length() - 5] = 's';
        return String.valueOf(charArray);
    }
         
             public void setMedia_url(String farm, String server, String id, String secret) {

        media_url =
                "http://farm" + farm + ".static.flickr.com/"
                + server + "/" + id + "_" + secret + "_z.jpg";
    }

    public String getSecret() {
        return secret;
    }

    public Fuzzy_Date getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Fuzzy_Date takenDate) {
        this.takenDate = takenDate;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }

    
    


    public Integer getMedia_id() {
        return media_id;
    }

    public void setMedia_id(Integer media_id) {
        this.media_id = media_id;
    }

    public Integer getIs_public() {
        return is_public;
    }

    public void setIs_public(Integer is_public) {
        this.is_public = is_public;
    }
    
    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setMedia_url(String media_url) {
        this.media_url = media_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public Timestamp getLast_update() {
        return last_update;
    }

    public void setLast_update(Timestamp last_update) {
        this.last_update = last_update;
    }


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFlickr_photo_id() {
        return flickr_photo_id;
    }

    public void setFlickr_photo_id(String flickr_photo_id) {
        this.flickr_photo_id = flickr_photo_id;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getMedia_url() {
        return media_url;
    }



    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
