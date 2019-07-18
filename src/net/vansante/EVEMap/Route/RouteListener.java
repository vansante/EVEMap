package net.vansante.EVEMap.Route;

import java.util.List;

import net.vansante.EVEMap.Data.Solarsystem;

public interface RouteListener {
	
	public void waypointsUpdated(List<Waypoint> waypoints);
	
	public void avoidListUpdated(List<Solarsystem> avoidList);
}
