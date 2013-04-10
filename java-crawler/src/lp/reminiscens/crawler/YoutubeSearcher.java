/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lp.reminiscens.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Nicola
 */
public class YoutubeSearcher {

    private final String youtubeUrlString = "https://gdata.youtube.com/feeds/api/videos?q=";
    private final String youtubeUrlParameterString = "&max-results=2&v=2&alt=jsonc&key=";
    private final String youtubeKeyString = "AI39si7gAsH_3JEgM1dX11kMLFNN8qLlFe-JuAcpz6KCuQ3gKxxzSi0kw7LmTE0znLnkrLi99pIpKNyAwR9A6GUO2u-nrVkXKA";

    public YoutubeSearcher() {
    }

    public static void main(String[] a) throws MalformedURLException, IOException {
        YoutubeSearcher y = new YoutubeSearcher();

        System.out.println(URLEncoder.encode("One in a Million (Guns N' Roses)", "UTF-8"));
        System.out.println(y.getVideoUrl("One in a Million (Guns N' Roses)"));
    }

//the function only looks for the first video in the list (actually it looks just for one video)
    public String getVideoUrl(String keyword) throws MalformedURLException, IOException {

        String urlString = youtubeUrlString + URLEncoder.encode(keyword, "UTF-8") + youtubeUrlParameterString + youtubeKeyString;
        System.out.println(urlString);
        URL url = new URL(urlString);

        URLConnection conn = url.openConnection();
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);

        IOUtils.copy(conn.getInputStream(), output);

        output.close();

        return parseJSON(output.toString());

    }

    public String parseJSON(String json) {

        String url = null;

        try {
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jObj = jelement.getAsJsonObject();
            System.out.println(jObj);
            jObj = jObj.getAsJsonObject("data");
            System.out.println(jObj);
            JsonArray items = jObj.getAsJsonArray("items");
            System.out.println(items);
            url = items.get(0).getAsJsonObject().get("content").getAsJsonObject().get("5").getAsString();

        } catch (Exception e) {
            try {
                JsonElement jelement = new JsonParser().parse(json);
                JsonObject jObj = jelement.getAsJsonObject();
                jObj = jObj.getAsJsonObject("data");
                JsonArray items = jObj.getAsJsonArray("items");
                url = items.get(1).getAsJsonObject().get("content").getAsJsonObject().get("5").getAsString();
            } catch (Exception ex) {
                return "PROBLEMA";
            }
        }
        return url;
    }
}
