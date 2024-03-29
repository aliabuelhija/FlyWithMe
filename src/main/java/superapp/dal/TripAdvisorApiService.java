package superapp.dal;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class TripAdvisorApiService {
	private static final String BASE_URL = "https://tripadvisor16.p.rapidapi.com/api/v1/hotels/searchHotels";
	private final static String apiKey = "1508b32a23mshb391c27c71c2452p1158a4jsn52862a30834d";
	private final static String apiHost = "tripadvisor16.p.rapidapi.com";

	public enum ItineraryType {
		ONE_WAY, ROUND_TRIP
	}

	public enum ClassOfService {
		ECONOMY, BUSINESS, FIRST_CLASS
	}

	public int getLocationId(String query) {
		OkHttpClient client = new OkHttpClient();

		String encodedQuery = "";
		try {
			encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		Request request = new Request.Builder()
				.url("https://tripadvisor16.p.rapidapi.com/api/v1/hotels/searchLocation?query=" + encodedQuery).get()
				.addHeader("X-RapidAPI-Key", apiKey).addHeader("X-RapidAPI-Host", apiHost).build();

		try {
			Response response = client.newCall(request).execute();
			String responseString = response.body().string();
			JsonElement responseJson = JsonParser.parseString(responseString);
			String geoId = responseJson.getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject()
					.get("geoId").getAsString();
			String numericGeoId = geoId.split(";")[1];
			return Integer.parseInt(numericGeoId);
		} catch (IOException | JsonParseException | NullPointerException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public String getHotels(int locationId, int adults, int rooms, int nights, String checkin, String checkout,
			String sort) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date checkinDate;
		Date checkoutDate;
		try {
			checkinDate = format.parse(checkin);
			checkoutDate = format.parse(checkout);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

		HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL).newBuilder();
		urlBuilder.addQueryParameter("geoId", String.valueOf(locationId));
		urlBuilder.addQueryParameter("adults", String.valueOf(adults));
		urlBuilder.addQueryParameter("rooms", String.valueOf(rooms));
		urlBuilder.addQueryParameter("nights", String.valueOf(nights));
		urlBuilder.addQueryParameter("checkIn", format.format(checkinDate));
		urlBuilder.addQueryParameter("checkOut", format.format(checkoutDate));
		urlBuilder.addQueryParameter("currency", "USD");
		urlBuilder.addQueryParameter("sort", sort);

		String url = urlBuilder.build().toString();

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder().url(url).addHeader("X-RapidAPI-Key", apiKey)
				.addHeader("X-RapidAPI-Host", apiHost).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				return "Error: " + response.message();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAirport(String query) {

		OkHttpClient client = new OkHttpClient();
		String encodedQuery = null;
		try {
			encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		Request request = new Request.Builder()
				.url("https://tripadvisor16.p.rapidapi.com/api/v1/flights/searchAirport?query=" + encodedQuery).get()
				.addHeader("X-RapidAPI-Key", apiKey)
				.addHeader("X-RapidAPI-Host",apiHost).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				return "Error: " + response.message();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String searchFlights(String sourceAirportCode, String destinationAirportCode, String date, String returnDate,
			String itineraryType, int numAdults, int numSeniors, String classOfService) {
		OkHttpClient client = new OkHttpClient();

		HttpUrl.Builder urlBuilder = HttpUrl.parse("https://tripadvisor16.p.rapidapi.com/api/v1/flights/searchFlights")
				.newBuilder();
		urlBuilder.addQueryParameter("sourceAirportCode", sourceAirportCode);
		urlBuilder.addQueryParameter("destinationAirportCode", destinationAirportCode);
		urlBuilder.addQueryParameter("date", date);
		if (returnDate != null && !returnDate.isEmpty()) {
			urlBuilder.addQueryParameter("returnDate", returnDate);
		}
		urlBuilder.addQueryParameter("itineraryType", itineraryType);
		urlBuilder.addQueryParameter("sortOrder","PRICE");
		urlBuilder.addQueryParameter("numAdults", String.valueOf(numAdults));
		urlBuilder.addQueryParameter("numSeniors", String.valueOf(numSeniors));
		urlBuilder.addQueryParameter("classOfService", classOfService);
		urlBuilder.addQueryParameter("currencyCode", "USD");

		String url = urlBuilder.build().toString();

		Request request = new Request.Builder().url(url).get()
				.addHeader("X-RapidAPI-Key", apiKey)
				.addHeader("X-RapidAPI-Host", apiHost).build();

		try (Response response = client.newCall(request).execute()) {
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				return "Error: " + response.message();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
