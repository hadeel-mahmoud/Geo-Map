package com.hadeel.geomap.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.hadeel.geomap.models.Address;
import com.hadeel.geomap.models.CoordinatesAggregated;
import com.hadeel.geomap.models.Edge;
import com.hadeel.geomap.models.Node;
import com.hadeel.geomap.search.SearchAlgorithms;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import net.java.html.boot.fx.FXBrowsers;
import net.java.html.leaflet.ILayer;
import net.java.html.leaflet.LatLng;
import net.java.html.leaflet.PolyLine;

public class App extends Application {

	static ArrayList<Address> addressList = new ArrayList<Address>();
	static ArrayList<CoordinatesAggregated> coordinatesAggregatedList = new ArrayList<CoordinatesAggregated>();
	static ArrayList<Node<Address>> graph = new ArrayList<Node<Address>>();
	static ObservableList<Address> from = FXCollections.observableArrayList();
	static final ComboBox<Address> FROMcomboBox = new ComboBox<Address>(from);
	static final ComboBox<Address> TOcomboBox = new ComboBox<Address>(from);
	static Button button = new Button("Find shortest distance");
	static ListView<Address> listView = new ListView<>();
	static final MapView map = new MapView();
	static ILayer layer = null;

	@Override
	public void start(Stage stage) throws Exception {

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(map);


		FROMcomboBox.setPrefWidth(250);
		FROMcomboBox.setPrefHeight(40);
		TOcomboBox.setPrefWidth(250);
		TOcomboBox.setPrefHeight(40);

		TOcomboBox.setPromptText("To");
		FROMcomboBox.setPromptText("From");

		String distanceMesg="Total Distance: %f";
		Label totalDistanceLabel=new Label();


		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Address from = FROMcomboBox.getValue();
				System.out.println(from);

				Address to = TOcomboBox.getValue();
				System.out.println(to);


				List<Address> path = (List<Address>) SearchAlgorithms.AstarSearch(graph.get(from.getCityNumber()), graph.get(to.getCityNumber()));
				double totalDistance=0.0;
				for (int i = 0; i < path.size()-1; i++) 
				{
					totalDistance+= Utils.distance(path.get(i).getLat(),
							path.get(i+1).getLat(),
							path.get(i).getLng(),
							path.get(i+1).getLng());	
				}
				totalDistanceLabel.setText(String.format(distanceMesg, totalDistance));
				System.out.println("Total distance: "+totalDistance);
				listView.getItems().clear();
				listView.getItems().addAll(path);

				System.out.println("Path: " + path);

				FXBrowsers.runInBrowser(map.getWebView(), new Runnable() {
					@Override
					public void run() {

						if (layer != null) {
							map.getMap().removeLayer(layer);

						}
						LatLng[] latlngArray = new LatLng[path.size()];

						for (int i = 0; i < latlngArray.length; i++) 
						{
							latlngArray[i] = new LatLng(path.get(i).getLat(), path.get(i).getLng());
						}

						layer = new PolyLine(latlngArray);

						map.getMap().addLayer(layer);
						LatLng posFrom = new LatLng(from.getLat(), from.getLng());
						LatLng posTo = new LatLng(to.getLat(),to.getLng());

						map.getMap().setView(posFrom);
						map.getMap().openPopup("Here is " + from, posFrom);

						map.getMap().setView(posTo);
						map.getMap().openPopup("Here is " + to, posTo);

					}
				});

			}
		});

		VBox box1 = new VBox();
		box1.getChildren().addAll(FROMcomboBox, TOcomboBox, button, listView,totalDistanceLabel);
		box1.setPadding(new Insets(15, 12, 15, 12));
		box1.setSpacing(10);
		borderPane.setLeft(box1);



		stage.setMaximized(true);
		Scene scene = new Scene(borderPane);

		stage.setTitle("Hadeel - Geo Map Shortest Path");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) throws FileNotFoundException {
		readCoordinates();
		readCoordinatesAggregated();
		buildGraph();

		launch(args);
	}

	private static void buildGraph() {

		for (Address address : addressList) {
			Node<Address> addrNode = new Node<Address>(address, 0);
			graph.add(addrNode);
			from.add(address);

		}

		for (Node<Address> node : graph) {

			ArrayList<Edge<Address>> edges = new ArrayList<Edge<Address>>();
			Address curr = node.value;

			for (CoordinatesAggregated item : coordinatesAggregatedList) 
			{

				if (item.edge1 == node.value.getCityNumber()) 
				{
					Node<Address> targetNode = graph.get(item.edge2);
					double costVal = Utils.distance(curr.getLat(), targetNode.value.getLat(), curr.getLng(),
							targetNode.value.getLng());
					edges.add(new Edge<Address>(targetNode, costVal));

				} 
				else if (item.edge2 == node.value.getCityNumber()) {

					Node<Address> targetNode = graph.get(item.edge1);
					double costVal = Utils.distance(curr.getLat(), targetNode.value.getLat(), curr.getLng(),
							targetNode.value.getLng());
					edges.add(new Edge<Address>(targetNode, costVal));
				}
			}
			node.adjacencies = edges.toArray(new Edge[0]);
			// System.out.println(node);
		}

	}

	private static void readCoordinates() throws FileNotFoundException {
		File myObj = new File(".\\src\\main\\resources\\data\\coordinates.txt");
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			if (data.startsWith("#") || data.trim() == "") {
				continue;
			}

			String[] row = data.split(",");
			addressList.add(new Address(Integer.parseInt(row[0].trim()), row[1].trim(),
					Double.parseDouble(row[2].trim()), Double.parseDouble(row[3].trim())));


		}

		myReader.close();

	}

	private static void readCoordinatesAggregated() throws FileNotFoundException {
		File myObj = new File(".\\src\\main\\resources\\data\\coordinatesAggregated.txt");
		Scanner myReader = new Scanner(myObj);
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			if (data.startsWith("#") || data.trim() == "") {
				continue;
			}
			String[] row = data.split(",");
			coordinatesAggregatedList
			.add(new CoordinatesAggregated(Integer.parseInt(row[0].trim()), Integer.parseInt(row[1].trim())));
		}
		myReader.close();

	}
}