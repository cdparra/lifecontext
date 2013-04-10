/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler.entities;

/**
 *
 * @author Nicola
 */
public class Media_Metadata {

    private Integer media_metadata_id;
    private String title;
    private String description;
    private String type;
    private String source;
    private String source_url;
    private String locale;
    private String resource_url;
    private Fuzzy_Date releaseDate;

    public Media_Metadata() {
    }

    public Integer getMedia_metadata_id() {
        return media_metadata_id;
    }

    public void setMedia_metadata_id(Integer media_metadata_id) {
        this.media_metadata_id = media_metadata_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Fuzzy_Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Fuzzy_Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
