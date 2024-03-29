package superapp.logic.objects;

public class Location {

	private double lat;
	private double lng;

// Constructors
	public Location() {
		this.lng = 0.0;
		this.lat = 0.0;
	}

	public Location(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

// Gets
	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

// Sets
	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

// To String
	@Override
	public String toString() {
		return "[lat=" + lat + ", lng=" + lng + "]";
	}
}