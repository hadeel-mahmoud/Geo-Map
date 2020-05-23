package com.hadeel.geomap.main;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import net.java.html.boot.fx.FXBrowsers;
import net.java.html.leaflet.LatLng;
import net.java.html.leaflet.Map;
import net.java.html.leaflet.TileLayer;
import net.java.html.leaflet.TileLayerOptions;

public class MapView extends StackPane {
	private final WebView webView;
	private Map map;

	public MapView() {
		// we define a regular JavaFX WebView that DukeScript can use for rendering
		webView = new WebView();
		getChildren().add(webView);

		// FXBrowsers loads the associated page into the WebView and runs our
		// code.

		FXBrowsers.load(webView, MapView.class.getResource("/pages/index.html"), new Runnable() {

			@Override
			public void run() {
				// Here we define that the map is rendered to a div with id="map"
				// in our index.html.
				// This can only be done after the page is loaded and the context is
				// initialized.
				map = new Map("map");

				// from here we just use the Leaflet API to show some stuff on the map
				map.setView(new LatLng(31.9038, 35.2034), 12);
				map.addLayer(new TileLayer(
						"http://{s}.tile.thunderforest.com/cycle/{z}/{x}/{y}.png?apikey=b9c099f3099a4be1abc55714d1d35441",
						new TileLayerOptions().setAttribution(
								"Map data &copy; <a href='http://www.thunderforest.com/opencyclemap/'>OpenCycleMap</a> contributors, "
										+ "<a href='http://creativecommons.org/licenses/by-sa/2.0/'>CC-BY-SA</a>, "
										+ "Imagery © <a href='http://www.thunderforest.com/'>Thunderforest</a>")
								.setMaxZoom(18).setId("eppleton.ia9c2p12")));

			}
		});
	}

	public Map getMap() {
		return map;
	}

	public WebView getWebView() {
		return webView;
	}

}
