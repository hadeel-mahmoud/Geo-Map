package com.hadeel.geomap.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import com.hadeel.geomap.models.Edge;
import com.hadeel.geomap.models.Node;

public class SearchAlgorithms {

	public static ArrayList<?> AstarSearch(Node<?> source, Node<?> goal) {
		HashMap<Node<?>,Node<?>> parentMap = new HashMap<Node<?>,Node<?>>();  
		HashMap<Node<?>,Double> gScore = new HashMap<Node<?>, Double>(); 
		HashMap<Node<?>,Double> fScore = new HashMap<Node<?>, Double>(); 

		Set<Node<?>> explored = new HashSet<Node<?>>();


		PriorityQueue<Node<?>> queue = new PriorityQueue<Node<?>>(200, new Comparator<Node<?>>() {

			@Override
			public int compare(Node<?> i, Node<?> j) {
				if (i.fScores > j.fScores) {
					return 1;
				}

				else if (i.fScores < j.fScores) {
					return -1;
				}

				else {
					return 0;
				}
			}

		});

		// cost from start
		gScore.put(source, 0.0);
		queue.add(source);

		boolean found = false;
		double finalCost=0;

		while ((!queue.isEmpty()) && (!found)) {

			// the node in having the lowest f_score value
			Node<?> current = queue.poll();

			explored.add(current);

			// goal found
			if (current.value.equals(goal.value)) {
				found = true;
				break;
			}


			// check every child of current node

			for (Edge<?> e : current.adjacencies) {
				Node<?> child = e.target;
				double cost = e.cost;
				double temp_g_scores = gScore.containsKey(current) ?  gScore.get(current): Double.MAX_VALUE + cost;
				finalCost=finalCost+cost;
				
				double temp_f_scores = temp_g_scores + child.hScores;

				/*
				 * if child node has been evaluated and the newer f_score is higher, skip
				 */
				double fScores = fScore.containsKey(child)? fScore.get(child) : 0.0;

				if ((explored.contains(child)) && (temp_f_scores >= fScores)) {
					continue;
				}

				/*
				 * else if child node is not in queue or newer fScore is lower
				 */

				else if ((!queue.contains(child)) || (temp_f_scores < fScores)) {	
					parentMap.put(child, current);
					gScore.put(child, temp_g_scores);
					fScore.put(child, temp_f_scores);
					queue.add(child);

				}

			}
			//System.out.println(finalCost);
			gScore.toString();



		}



		/*
		 *  Build path
		 */
		ArrayList path = new ArrayList();
		for (Node<?> node = goal; node != null; node = parentMap.get(node)) {
			path.add(node.value);
		}
		Collections.reverse(path);
		return path;


	}
}
