package net.vansante.EVEMap.Data;

import java.util.*;

import net.vansante.EVEMap.Tools;

public class Locations extends ArrayList<Location> {

	public Locations(int size) {
		super(size);
	}
	public List<Location> searchByName(String name) {
		LinkedList<Location> results = new LinkedList<Location>();
		name = name.toLowerCase();
		for (Location location : this) {
			if (location.getName().toLowerCase().indexOf(name) != -1) {
				results.add(location);
			}
		}
		return results;
	}
	public Location getById(int id) {
		for (Location location : this) {
			if (location.getId() == id) {
				 return location;
			}
		}
		return null;
	}
	public List<Solarsystem> getSolarsystemsByDistance(Location search, double distance) {
		LinkedList<Solarsystem> results = new LinkedList<Solarsystem>();
		double locationDistance;
		for (Location location : this) {
			locationDistance = Tools.calculateDistance(location, search);
			if (locationDistance < distance && search != location) {
				results.add((Solarsystem) location);
			}
		}
		return results;
	}
	public List<Location> getByDistance(double x, double y, double z, double distance) {
		LinkedList<Location> results = new LinkedList<Location>();
		double locationDistance;
		for (Location location : this) {
			locationDistance = Tools.calculateDistance(location.getX(), location.getY(), location.getZ(), x, y, z);
			if (locationDistance < distance) {
				results.add(location);
			}
		}
		return results;
	}
	public Object[] toKnownArray() {
		LinkedList<Location> results = new LinkedList<Location>();
		for (Location location : this) {
			if (location.isKnown()) {
				results.add(location);
			}
		}
		return results.toArray();
	}
}
