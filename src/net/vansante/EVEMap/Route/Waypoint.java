package net.vansante.EVEMap.Route;

import net.vansante.EVEMap.Data.Solarsystem;

public class Waypoint {
	
	private Waypoint next;
	private Solarsystem solarsystem;
	
	public Waypoint(Solarsystem solarsystem) {
		this.solarsystem = solarsystem;
	}
	public Waypoint(Solarsystem solarsystem, Waypoint next) {
		this.solarsystem = solarsystem;
		this.next = next;
	}
	public String toString() {
		return solarsystem.toString();
	}
	public Solarsystem getSolarsystem() {
		return solarsystem;
	}
	public void setSolarsystem(Solarsystem solarsystem) {
		this.solarsystem = solarsystem;
	}
	public Waypoint getNext() {
		return next;
	}
	public boolean hasNext() {
		return next != null;
	}
	public void setNext(Waypoint next) {
		this.next = next;
	}
}
