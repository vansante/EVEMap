package net.vansante.EVEMap.Route;

import java.util.*;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Solarsystem;

public class Pathfinder {
	
	private final Set<Solarsystem> settledSystems = new HashSet<Solarsystem>(5390);
	private final Map<Solarsystem, Integer> shortestDistances = new HashMap<Solarsystem, Integer>(5390);
	private final Map<Solarsystem, Solarsystem> predecessors = new HashMap<Solarsystem, Solarsystem>(5390);
	private final Queue<Solarsystem> unsettledSystems = new PriorityQueue<Solarsystem>(5390, new ShortestDistanceComparator());
	
	private boolean cancel;
	private Solarsystem start, destination;
	
	private int minSec;
	private int maxSec;
	private boolean preferStations;
	private List<Solarsystem> avoidList;
	private boolean jumpRoute;
	private int shipType;
	private double jumpRange;
	
	public Pathfinder(Solarsystem start, Solarsystem destination, int minSec, int maxSec, boolean preferStations, List<Solarsystem> avoidList) {
		this.start = start;
		this.destination = destination;
		this.minSec = minSec;
		this.maxSec = maxSec;
		this.avoidList = avoidList;
		this.jumpRoute = false;
		
		this.calculate();
	}
	public Pathfinder(Solarsystem start, Solarsystem destination, int minSec, int maxSec, boolean preferStations,
			List<Solarsystem> avoidList, double jumpRange, int shipType) {
		this.start = start;
		this.destination = destination;
		this.minSec = minSec;
		this.maxSec = maxSec;
		this.avoidList = avoidList;
		this.jumpRoute = true;
		this.jumpRange = jumpRange;
		this.shipType = shipType;
		
		this.calculate();
	}
	public void cancel() {
		this.cancel = true;
	}
	public List<Waypoint> getWaypoints() {
		LinkedList<Waypoint> list = new LinkedList<Waypoint>();
		Waypoint currentWaypoint, previousWaypoint = null;
		Solarsystem currentSolarsystem = destination;
		while (start != currentSolarsystem) {
			currentWaypoint = new Waypoint(currentSolarsystem, previousWaypoint);
			list.add(0, currentWaypoint);
			currentSolarsystem = this.getPredecessor(currentSolarsystem);
			previousWaypoint = currentWaypoint;
		}
		return list;
	}
	public void calculate() {
		settledSystems.clear();
		unsettledSystems.clear();
		shortestDistances.clear();
		predecessors.clear();
		// add source
		this.setShortestDistance(start, 0);
		unsettledSystems.add(start);
		
		this.findRoute();
	}
	private boolean isSettled(Solarsystem solarsystem) {
	    return settledSystems.contains(solarsystem);
	}
	private void setShortestDistance(Solarsystem solarsystem, int distance) {
		shortestDistances.put(solarsystem, distance);
		unsettledSystems.add(solarsystem);
	}
	public int getShortestDistance(Solarsystem solarsystem) {
	    Integer distance = shortestDistances.get(solarsystem);
	    if (distance == null) {
	    	return Integer.MAX_VALUE;
	    }
	    return distance;
	}
	private void setPredecessor(Solarsystem a, Solarsystem b) {
	    predecessors.put(a, b);
	}
	public Solarsystem getPredecessor(Solarsystem solarsystem) {
	    return predecessors.get(solarsystem);
	}
	private Solarsystem extractMin() {
	    return unsettledSystems.poll();
	}
	private int getDistance(Solarsystem start, Solarsystem end) {
		int distance = 0;
		if (Tools.arraySearch(Constants.ROUTE_IGNORE_REGIONS, end.getRegion().getId()) >= 0) {
			distance += 200000000;
		}
		if (jumpRoute && shipType == Constants.JUMP_BRIDGE_ID
				&& (end.getSecurityType() > Solarsystem.SEC_00 || end.getRegion().getFaction() != null)) {
			distance += 200000000;
		}
		if (jumpRoute && end.getSecurityType() > Solarsystem.SEC_04) {
			distance += 200000000;
		}
		if (avoidList.indexOf(end) >= 0) {
			distance += 100000000;
		}
		if (end.getSecurityType() < minSec) {
			distance += (minSec - end.getSecurityType()) * 1000000;
		} else if (end.getSecurityType() > maxSec) {
			distance += (end.getSecurityType() - maxSec) * 1000000;
		}
		if (preferStations && end.getNumberOfStations() < 1) {
			distance += 4000;
		}
		if (jumpRoute) {
			distance += Tools.calculateDistance(start, end);
		}
		distance += 400;
		return distance;
	}
	private void relaxNeighbours(Solarsystem solarsystem) {
		List<Solarsystem> neighbours = null;
		if (jumpRoute) {
			neighbours = Main.get().getSolarsystems().getSolarsystemsByDistance(solarsystem, jumpRange);
		} else {
			neighbours = solarsystem.getConnections();
		}
		for (Solarsystem neighbour : neighbours) {
			// skip node already settled
			if (this.isSettled(neighbour)) {
				continue;
			}
			int shortestDistance = this.getShortestDistance(solarsystem) + this.getDistance(solarsystem, neighbour);
			if (shortestDistance < this.getShortestDistance(neighbour)) {
				// assign new shortest distance and mark unsettled
				this.setShortestDistance(neighbour, shortestDistance);
				
				// assign predecessor in shortest path
				this.setPredecessor(neighbour, solarsystem);
			}
		}
	}
	private void findRoute() {
	    while (!unsettledSystems.isEmpty() && !cancel) {
	        // get the node with the shortest distance
	    	Solarsystem solarsystem = this.extractMin();

	        // destination reached, stop
	        if (solarsystem == destination) {
	        	break;
	        }
	        settledSystems.add(solarsystem);
	        this.relaxNeighbours(solarsystem);
	    }
	}
	private class ShortestDistanceComparator implements Comparator<Solarsystem> {
		public int compare(Solarsystem left, Solarsystem right) {
			int shortestDistanceLeft = getShortestDistance(left);
			int shortestDistanceRight = getShortestDistance(right);
			
			if (shortestDistanceLeft > shortestDistanceRight) {
				return 1;
			} else if (shortestDistanceLeft < shortestDistanceRight) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
