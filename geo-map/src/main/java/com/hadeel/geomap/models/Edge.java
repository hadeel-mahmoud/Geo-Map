package com.hadeel.geomap.models;

public class Edge<T> {

	public final double cost;
	public final Node<T> target;

	public Edge(Node<T> targetNode, double costVal) {
		target = targetNode;
		cost = costVal;
	}
}
