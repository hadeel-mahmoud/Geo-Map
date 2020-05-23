package com.hadeel.geomap.models;

public class Address {
	private final int cityNumber;
	private final String name;
	private final double lat;
	private final double lng;

	public Address(int cityNumber, String name, double lat, double lng) {
		this.cityNumber = cityNumber;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}

	public int getCityNumber() {
		return cityNumber;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	@Override
	public String toString() {
		return name;
	}

}
