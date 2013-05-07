package eu.reminiscens.booklet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class BookletServlet
 */
@WebServlet(name = "booklet", urlPatterns = { "/booklet" })
public class BookletServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public BookletServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/json");

		String decade = request.getParameter("decade");
		String lat = request.getParameter("lat");
		String lon = request.getParameter("lon");

		PrintWriter out = response.getWriter();
		try {

//			String mediaUrl = "http://localhost:4567/generalBooklet/media?decade="
			String mediaUrl = "http://test.reminiscens.me/lifecontext/api/generalBooklet/media?decade="
					+ decade + "&lat=" + lat + "&lon=" + lon;
			String eventsUrl = "http://localhost:4567/generalBooklet/events?decade="
					+ decade + "&lat=" + lat + "&lon=" + lon;
			String worksUrl = "http://localhost:4567/generalBooklet/works?decade="
					+ decade;
			
			System.out.println("Loading Media");
			
			String outMediaJson = getJson(mediaUrl);
			
			System.out.println("Media loaded");
			
			System.out.println("Loading Events");
			
			String outEventsJson = getJson(eventsUrl);
			
			System.out.println("Events loaded");
			
			System.out.println("Loading Works");
			
			String outWorksJson = getJson(worksUrl);
			
			System.out.println("Works loaded");

			out.print("[" + outMediaJson + "," + outEventsJson + "," + outWorksJson + "]");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public String getJson(String qString) {

		String qResult = null;

		System.out.println(qString);

		HttpClient httpClient = new DefaultHttpClient();

		HttpGet httpGet = new HttpGet(qString);

		try {

			HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

			if (httpEntity != null) {

				InputStream inputStream = httpEntity.getContent();
				Reader in = new InputStreamReader(inputStream);
				BufferedReader bufferedreader = new BufferedReader(in);
				StringBuilder stringBuilder = new StringBuilder();
				String stringReadLine = null;

				while ((stringReadLine = bufferedreader.readLine()) != null) {

					stringBuilder.append(stringReadLine + "\n");
				}

				qResult = stringBuilder.toString();

				System.out.println(qResult);

			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return qResult;

	}

}
