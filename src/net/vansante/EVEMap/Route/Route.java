package net.vansante.EVEMap.Route;

import java.util.*;
import java.lang.Math;
import java.lang.Thread;

import javax.swing.JOptionPane;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Solarsystem;

public class Route implements Iterable<Waypoint>, Iterator<Waypoint> {
	
	private static Route instance;
	
	private Pathfinder pathfinder;
	
	private PathfindThread pathfindThread;
	private final LinkedList<Solarsystem> waypointQueue;
	private boolean cancelPathfinding = false;
	
	private final ArrayList<Solarsystem> avoidList;
	private final ArrayList<Waypoint> waypoints;
	
	private final ArrayList<RouteListener> listeners;
	
	private Waypoint currentWaypoint;
	
	private int shipType, jumpCalibration, jumpConservation, jumpFreighterSkill, jumpFreighterRace;
	private boolean jumpRoute = false;
	private int minSec = Solarsystem.SEC_00;
	private int maxSec = Solarsystem.SEC_10;
	private double jumpRange;
	private int fuelUsage;
	private boolean preferStations = false;
	
	private Route() {
		this.avoidList = new ArrayList<Solarsystem>(10);
		this.waypoints = new ArrayList<Waypoint>(10);
		
		this.listeners = new ArrayList<RouteListener>(2);
		
		this.pathfindThread = new PathfindThread();
		this.waypointQueue = new LinkedList<Solarsystem>();
	}
	public static Route get() {
		if (instance == null) {
			instance = new Route();
		}
		return instance;
	}
	public synchronized void addRouteListener(RouteListener listener) {
		 listeners.add(listener);
	}
	public synchronized void removeRouteListener(RouteListener listener) {
		listeners.remove(listener);
	}
	public synchronized void waypointsUpdated() {
		for (RouteListener listener : listeners) {
			listener.waypointsUpdated(waypoints);
		}
	}
	public synchronized void avoidListUpdated() {
		for (RouteListener listener : listeners) {
			listener.avoidListUpdated(avoidList);
		}
	}
	public void addWaypoint(Solarsystem solarsystem) {
		if (solarsystem.isUnknown() || Tools.arraySearch(Constants.ROUTE_IGNORE_REGIONS, solarsystem.getRegion().getId()) >= 0) {
			JOptionPane.showMessageDialog(Main.get(), "You can't add a waypoint from this region.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (this.getLastWaypoint() == null || (waypointQueue.isEmpty() && this.getLastWaypoint().getSolarsystem() != solarsystem)
					|| (!waypointQueue.isEmpty() && waypointQueue.getLast() != solarsystem)) {
			waypointQueue.add(solarsystem);
			if (pathfindThread == null || !pathfindThread.isAlive()) {
				pathfindThread = new PathfindThread();
				pathfindThread.start();
			}
		}
	}
	public Waypoint getLastWaypoint() {
		if (waypoints.size() == 0) {
			return null;
		}
		return waypoints.get(waypoints.size() - 1);
	}
	public void removeWaypoint(Waypoint waypoint) {
		waypoints.remove(waypoint);
		this.recalculate();
	}
	public void clearRoute() {
		waypoints.clear();
		waypointQueue.clear();
		cancelPathfinding = true;
		this.waypointsUpdated();
	}
	public Waypoint getWaypoint(int index) {
		return waypoints.get(index);
	}
	public int getNumberOfWaypoints() {
		return waypoints.size();
	}
	public Waypoint getWaypointChild(Waypoint waypoint, int child) {
		for (int i = 0; i <= child; i++) {
			waypoint = waypoint.getNext();
		}
		return waypoint;
	}
	public int getWaypointChildSize(Waypoint waypoint) {
		int index = waypoints.indexOf(waypoint);
		if (index == waypoints.size() - 1) {
			return 0;
		}
		Waypoint end = waypoints.get(index + 1);
		int i = 0;
		while (waypoint != end) {
			waypoint = waypoint.getNext();
			i++;
		}
		return i;
	}
	public int getTotalWaypointSize() {
		int i = 0;
		for (Waypoint waypoint : this) {
			i++;
		}
		return i;
	}
	public int indexOfWaypoint(Waypoint waypoint) {
		return waypoints.indexOf(waypoint);
	}
	public int indexOfWaypointChild(Waypoint parent, Waypoint child) {
		int i = 0;
		while (parent != child) {
			parent = parent.getNext();
			i++;
		}
		return i;
	}
	public void recalculate() {
		List<Solarsystem> waypointsCopy = new LinkedList<Solarsystem>();
		for (Waypoint waypoint : waypoints) {
			waypointsCopy.add(waypoint.getSolarsystem());
		}
		waypointsCopy.addAll(waypointQueue);
		this.clearRoute();
		for (Solarsystem solarsystem : waypointsCopy) {
			this.addWaypoint(solarsystem);
		}
	}
	public Iterator<Waypoint> iterator() {
		currentWaypoint = null;
		return this;
	}
	public Waypoint next() {
		if (currentWaypoint == null) {
			currentWaypoint = waypoints.get(0);
		} else {
			currentWaypoint = currentWaypoint.getNext();
		}
		return currentWaypoint;
	}
	public boolean hasNext() {
		if (currentWaypoint != null) {
			return currentWaypoint.getNext() != null;
		}
		return waypoints.size() > 0;
	}
	public void remove() {}
	public double getWaypointLightyears(Waypoint waypoint) {
		int size = this.getWaypointChildSize(waypoint);
		double distance = 0;
		Waypoint previous = null;
		for (int i = 0; i <= size; i++) {
			if (previous != null) {
				distance += Tools.calculateDistance(previous.getSolarsystem(), waypoint.getSolarsystem());
			}
			previous = waypoint;
			waypoint = waypoint.getNext();
		}
		return distance;
	}
	public void addToAvoidList(Solarsystem solarsystem) {
		if (solarsystem.isUnknown() || Tools.arraySearch(Constants.ROUTE_IGNORE_REGIONS, solarsystem.getRegion().getId()) >= 0) {
			JOptionPane.showMessageDialog(Main.get(), "You can't avoid a solarsystem from this region.", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		avoidList.add(solarsystem);
		this.avoidListUpdated();
		this.recalculate();
	}
	public void removeFromAvoidList(int index) {
		avoidList.remove(index);
		this.avoidListUpdated();
		this.recalculate();
	}
	public void removeFromAvoidList(Solarsystem solarsystem) {
		this.removeFromAvoidList(avoidList.indexOf(solarsystem));
	}
	public void clearAvoidList() {
		avoidList.clear();
		this.avoidListUpdated();
		this.recalculate();
	}
	public List<Solarsystem> getAvoidList() {
		return avoidList;
	}
	public boolean inAvoidList(Solarsystem solarsystem) {
		return avoidList.indexOf(solarsystem) >= 0;
	}
	public int getFuelUsage() {
		return fuelUsage;
	}
	public void calculateFuelUsage() {
		int usage = 0;
		if (shipType == Constants.JUMP_FREIGHTER_ID) {
			usage = Constants.JUMP_SHIP_FREIGHTER_FUEL_USAGE[jumpFreighterRace];
			// Subtract jump freighter skill
			usage = (int) (usage * (1 - (jumpFreighterSkill * 0.10)));
		} else if (shipType == Constants.JUMP_BRIDGE_ID) {
			this.fuelUsage = Constants.JUMP_SHIP_FUEL_USAGE[Constants.JUMP_BRIDGE_ID];
			return;
		} else {
			usage = Constants.JUMP_SHIP_FUEL_USAGE[shipType];
		}
		// Subtract conservation skill
		usage = (int) (usage * (1 - (jumpConservation * 0.10)));
		this.fuelUsage = usage;
	}
	public void calculateJumpRange() {
		double range = Constants.JUMP_SHIP_TYPE_RANGES[shipType];
		if (shipType != Constants.JUMP_BRIDGE_ID) {
			range = range * (1 + (jumpCalibration * 0.25));
		}
		range = range * Constants.LIGHTYEAR / Constants.SCALE;
		jumpRange = range;
		this.recalculate();
	}
	public boolean getJumpRoute() {
		return jumpRoute;
	}
	public void setJumpRoute(boolean jumpRoute) {
		this.jumpRoute = jumpRoute;
		this.recalculate();
	}
	public void setJumpRange(double jumpRange) {
		this.jumpRange = jumpRange;
		this.recalculate();
	}
	public double getJumpRange() {
		return jumpRange;
	}
	public void setMinSecurity(int min) {
		this.minSec = min;
		this.recalculate();
	}
	public int getMinSecurity() {
		return minSec;
	}
	public void setMaxSecurity(int max) {
		this.maxSec = max;
		this.recalculate();
	}
	public int getMaxSecurity() {
		return maxSec;
	}
	public void setPreferStations(boolean preferStations) {
		this.preferStations = preferStations;
		this.recalculate();
	}
	public boolean getPreferStations() {
		return preferStations;
	}
	public int getShipType() {
		return shipType;
	}
	public void setShipType(int shipType) {
		this.shipType = shipType;
		this.calculateFuelUsage();
		this.calculateJumpRange();
	}
	public int getJumpCalibration() {
		return jumpCalibration;
	}
	public void setJumpCalibration(int jumpCalibration) {
		this.jumpCalibration = jumpCalibration;
		this.calculateJumpRange();
	}
	public int getJumpConservation() {
		return jumpConservation;
	}
	public void setJumpConservation(int jumpConservation) {
		this.jumpConservation = jumpConservation;
		this.calculateFuelUsage();
	}
	public int getJumpFreighterSkill() {
		return jumpFreighterSkill;
	}
	public void setJumpFreighterSkill(int jumpFreighterSkill) {
		this.jumpFreighterSkill = jumpFreighterSkill;
		this.calculateFuelUsage();
	}
	public int getJumpFreighterRace() {
		return jumpFreighterRace;
	}
	public void setJumpFreighterRace(int jumpFreighterRace) {
		this.jumpFreighterRace = jumpFreighterRace;
		this.calculateFuelUsage();
	}
	public void saveSettings() {
		Main.get().getPreferences().putBoolean(Constants.SETTING_JUMPDRIVE_ROUTE, jumpRoute);
		Main.get().getPreferences().putInt(Constants.SETTING_SHIPTYPE, shipType);
		Main.get().getPreferences().putInt(Constants.SETTING_JUMPCALIBRATION, jumpCalibration);
		Main.get().getPreferences().putInt(Constants.SETTING_JUMPCONSERVATION, jumpConservation);
		Main.get().getPreferences().putInt(Constants.SETTING_JUMPFREIGHTER_SKILL, jumpFreighterSkill);
		Main.get().getPreferences().putInt(Constants.SETTING_JUMPFREIGHTER_RACE, jumpFreighterRace);
		Main.get().getPreferences().putInt(Constants.SETTING_MINIMUM_SECURITY, minSec);
		Main.get().getPreferences().putInt(Constants.SETTING_MAXIMUM_SECURITY, maxSec);
		Main.get().getPreferences().putBoolean(Constants.SETTING_PREFER_STATIONS, preferStations);
		StringBuilder avoidStringb = new StringBuilder();
		for (Solarsystem solarsystem : avoidList) {
			avoidStringb.append(solarsystem.getId()).append(",");
		}
		String avoidString = "";
		if (avoidStringb.length() > 1) {
			avoidString = avoidStringb.substring(0, avoidStringb.length() - 1);
		}
		Main.get().getPreferences().put(Constants.SETTING_AVOID_LIST, avoidString);
		StringBuilder waypointStringb = new StringBuilder();
		for (Waypoint waypoint : waypoints) {
			waypointStringb.append(waypoint.getSolarsystem().getId()).append(",");
		}
		for (Solarsystem solarsystem : waypointQueue) {
			waypointStringb.append(solarsystem.getId()).append(",");
		}
		String waypointString = "";
		if (waypointStringb.length() > 1) {
			waypointString = waypointStringb.substring(0, waypointStringb.length() - 1);
		}
		Main.get().getPreferences().put(Constants.SETTING_WAYPOINT_LIST, waypointString);
	}
	public void loadSettings() {
		jumpRoute = Main.get().getPreferences().getBoolean(Constants.SETTING_JUMPDRIVE_ROUTE, jumpRoute);
		shipType = Main.get().getPreferences().getInt(Constants.SETTING_SHIPTYPE, shipType);
		jumpCalibration = Main.get().getPreferences().getInt(Constants.SETTING_JUMPCALIBRATION, jumpCalibration);
		jumpConservation = Main.get().getPreferences().getInt(Constants.SETTING_JUMPCONSERVATION, jumpConservation);
		jumpFreighterSkill = Main.get().getPreferences().getInt(Constants.SETTING_JUMPFREIGHTER_SKILL, jumpFreighterSkill);
		jumpFreighterRace = Main.get().getPreferences().getInt(Constants.SETTING_JUMPFREIGHTER_RACE, jumpFreighterRace);
		minSec = Main.get().getPreferences().getInt(Constants.SETTING_MINIMUM_SECURITY, minSec);
		maxSec = Main.get().getPreferences().getInt(Constants.SETTING_MAXIMUM_SECURITY, maxSec);
		preferStations = Main.get().getPreferences().getBoolean(Constants.SETTING_PREFER_STATIONS, preferStations);
		String[] avoidStrings = Main.get().getPreferences().get(Constants.SETTING_AVOID_LIST, "").split(",");
		Solarsystem solarsystem;
		for (int i = 0; i < avoidStrings.length; i++) {
			try {
				solarsystem = (Solarsystem) Main.get().getSolarsystems().getById(Integer.parseInt(avoidStrings[i]));
				if (solarsystem != null) {
					this.addToAvoidList(solarsystem);
				}
			} catch (NumberFormatException e) {}
		}
		String[] waypointStrings = Main.get().getPreferences().get(Constants.SETTING_WAYPOINT_LIST, "").split(",");
		for (int i = 0; i < waypointStrings.length; i++) {
			try {
				solarsystem = (Solarsystem) Main.get().getSolarsystems().getById(Integer.parseInt(waypointStrings[i]));
				if (solarsystem != null) {
					this.addWaypoint(solarsystem);
				}
			} catch (NumberFormatException e) {}
		}
		this.calculateFuelUsage();
		this.calculateJumpRange();
	}
	public String toString() {
		int jumps = this.getTotalWaypointSize();
		if (jumps > 0) {
			jumps -= 1;
		}
		StringBuilder string = new StringBuilder("Route (");
		string.append(waypoints.size()).append(" waypoints, ").append(jumps).append(" jumps)");
		return string.toString();
	}
	public String getWaypointString(Waypoint waypoint) {
		int size = this.getWaypointChildSize(waypoint);
		StringBuilder string = new StringBuilder(waypoint.getSolarsystem().toString());
		string.append(" (").append(size).append(" jumps)");
		return string.toString();
	}
	public String getWaypointToolTip(Waypoint waypoint) {
		StringBuilder tooltip = new StringBuilder();
		int size = this.getWaypointChildSize(waypoint);
		tooltip.append("<html><head><title>Waypoint</head><body><h3>Waypoint</h3>");
		tooltip.append("<p>").append(size).append(" jumps</p><br>");
		double distance, volume;
		int fuel;
		if (jumpRoute) {
			distance = this.getWaypointLightyears(waypoint) / Constants.LIGHTYEAR * Constants.SCALE;
			fuel = (int) Math.ceil(distance * fuelUsage);
			volume = Tools.round(fuel * Constants.JUMP_FUEL_VOLUME, 1000);
			tooltip.append("<p>").append(Tools.round(distance, 1000)).append(" lightyears, ").append(fuel).append(" isotopes, ").append(volume).append(" m3</p><br>");
		}
		tooltip.append("<table><tr height='15'><td> </td><td>Route:</td><td></td></tr>");
		Waypoint previous = null, current = waypoint;
		for (int i = 0; i <= size; i++) {
			tooltip.append("<tr height='15'><td bgcolor='#");
			tooltip.append(Tools.colorToHex(Constants.SEC_COLORS[current.getSolarsystem().getSecurityType()]));
			tooltip.append("'>  </td><td>");
			tooltip.append(i + 1).append(". ").append(current.getSolarsystem().getName());
			if (jumpRoute && previous != null) {
				distance = Tools.calculateDistance(previous.getSolarsystem(), current.getSolarsystem()) / Constants.LIGHTYEAR * Constants.SCALE;
				fuel = (int) Math.ceil(distance * fuelUsage);
				volume = Tools.round(fuel * Constants.JUMP_FUEL_VOLUME, 1000);
				tooltip.append("</td><td>(").append(Tools.round(distance, 1000)).append(" lightyears, ").append(fuel).append(" isotopes, ").append(volume).append(" m3)");
			}
			tooltip.append("</td></tr>");
			previous = current;
			current = current.getNext();
		}
		tooltip.append("</td></tr></table>");
		tooltip.append("</body></html>");
		return tooltip.toString();
	}
	public void stop() {
		cancelPathfinding = true;
		if (pathfinder != null) {
			pathfinder.cancel();	
		}
	}
	private class PathfindThread extends Thread {
		public PathfindThread() {
			super("Pathfind Thread");
		}
		public void run() {
			Solarsystem solarsystem;
			while (!cancelPathfinding  && !waypointQueue.isEmpty()) {
				solarsystem = waypointQueue.peek();
				if (waypoints.size() == 0) {
					Waypoint waypoint = new Waypoint(solarsystem);
					waypoints.add(waypoint);
					waypointQueue.poll();
				} else {
					Waypoint start = getLastWaypoint();
					if (jumpRoute) {
						pathfinder = new Pathfinder(start.getSolarsystem(), solarsystem, minSec, maxSec, preferStations, avoidList, jumpRange, shipType);
					} else {
						pathfinder = new Pathfinder(start.getSolarsystem(), solarsystem, minSec, maxSec, preferStations, avoidList);
					}
					if (!cancelPathfinding) {
						List<Waypoint> list = pathfinder.getWaypoints();
						start.setNext(list.get(0));
						waypoints.add(list.get(list.size() - 1));
						waypointQueue.poll();
					}
				}
				Route.this.waypointsUpdated();
			}
			cancelPathfinding = false;
			if (!waypointQueue.isEmpty()) {
				this.run();
			}
		}
	}
}
