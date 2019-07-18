package net.vansante.EVEMap.UI.Menus;

import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Map.MapControl;
import net.vansante.EVEMap.Route.Waypoint;
import net.vansante.EVEMap.Route.Route;

public class WaypointMenu extends JPopupMenu implements ActionListener {
	
	private final Waypoint waypoint;
	
	private final JMenuItem mRemove, mClearRoute;
	private final JMenu menuSolarsystem;
	
	private final JMenuItem mShowOnMap, mAddWaypoint;
	
	public WaypointMenu(Waypoint waypoint) {
		super("Menu");
		this.waypoint = waypoint;
		
		mRemove = Tools.createMenuItem(this, "Remove waypoint", "removewaypoint.png", this, KeyEvent.VK_R);
		
		menuSolarsystem = Tools.createSubMenu(this, "Solarsystem", "");
		
		mShowOnMap = Tools.createMenuItem(menuSolarsystem, "Show on map", "zoom.png", this, KeyEvent.VK_M);
		mAddWaypoint = Tools.createMenuItem(menuSolarsystem, "Add waypoint", "waypoint.png", this, KeyEvent.VK_W);
		
		this.addSeparator();
		
		mClearRoute = Tools.createMenuItem(this, "Clear route", "clear.png", this, KeyEvent.VK_C);
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mRemove) {
			Route.get().removeWaypoint(waypoint);
		} else if (event == mClearRoute) {
			Route.get().clearRoute();
		} else if (event == mShowOnMap) {
			MapControl.get().lookAt(waypoint.getSolarsystem(), 1);
		} else if (event == mAddWaypoint) {
			Route.get().addWaypoint(waypoint.getSolarsystem());
		}
	}
}
