package com.hadeel.geomap.models;

public class Node<T> {
	public final T value;
    public double gScores = Double.MAX_VALUE;
    public final double hScores;
    public double fScores = 0;
    public Edge<T>[] adjacencies;

    public Node(T val, double hVal){
            value = val;
            hScores = hVal;
    }

    public String toString(){
            return value.toString();
    }
}
