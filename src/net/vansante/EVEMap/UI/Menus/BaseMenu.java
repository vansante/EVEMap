package net.vansante.EVEMap.UI.Menus;

import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Map.MapControl;
import net.vansante.EVEMap.Route.Route;

public class BaseMenu extends JPopupMenu implements ActionListener {
	
	private final JMenuItem mResetView, mZoomIn, mZoomOut, mClearRoute;
	
	public BaseMenu() {
		super("Menu");
		
		mZoomIn = Tools.createMenuItem(this, "Zoom in", "zoom_in.png", this, KeyEvent.VK_I);
		mZoomOut = Tools.createMenuItem(this, "Zoom out", "zoom_out.png", this, KeyEvent.VK_O);
		mResetView = Tools.createMenuItem(this, "Reset view", "zoom.png", this, KeyEvent.VK_R);
		this.addSeparator();
		mClearRoute = Tools.createMenuItem(this, "Clear route", "clear.png", this, KeyEvent.VK_C);
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mZoomIn) {
			MapControl.get().zoomIn();
		} else if (event == mZoomOut) {
			MapControl.get().zoomOut();
		} else if (event == mResetView) {
			MapControl.get().resetView();
		} else if (event == mClearRoute) {
			Route.get().clearRoute();
		}
	}
}
