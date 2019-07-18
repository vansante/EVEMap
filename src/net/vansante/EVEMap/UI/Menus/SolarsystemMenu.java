package net.vansante.EVEMap.UI.Menus;

import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Map.MapControl;
import net.vansante.EVEMap.Route.Route;

public class SolarsystemMenu extends JPopupMenu implements ActionListener {

	private final Solarsystem solarsystem;
	
	private final JMenuItem mShowOnMap, mAddWaypoint;
	
	private JMenuItem mAddAvoidList, mRemoveAvoidList;
	
	public SolarsystemMenu(Solarsystem solarsystem) {
		super(solarsystem.getName());
		this.solarsystem = solarsystem;
		
		mShowOnMap = Tools.createMenuItem(this, "Show on map", "zoom.png", this, KeyEvent.VK_M);
		mAddWaypoint = Tools.createMenuItem(this, "Add waypoint", "waypoint.png", this, KeyEvent.VK_W);
				
		if (Route.get().inAvoidList(solarsystem)) {
			mRemoveAvoidList = Tools.createMenuItem(this, "Remove from avoid list", "avoid.png", this, KeyEvent.VK_A);
		} else {
			mAddAvoidList = Tools.createMenuItem(this, "Add to avoid list", "avoid.png", this, KeyEvent.VK_A);
		}
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mShowOnMap) {
			MapControl.get().lookAt(solarsystem, 1);
		} else if (event == mAddWaypoint) {
			Route.get().addWaypoint(solarsystem);
		} else if (event == mRemoveAvoidList) {
			Route.get().removeFromAvoidList(solarsystem);
		} else if (event == mAddAvoidList) {
			Route.get().addToAvoidList(solarsystem);
		}
	}
}
