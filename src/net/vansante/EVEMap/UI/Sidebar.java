package net.vansante.EVEMap.UI;

import java.awt.*;
import javax.swing.*;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.UI.Menus.Menubar;

public class Sidebar extends JPanel {
	
	private final Menubar menu;
	private final SearchPanel search;
	private final RoutePanel route;
	private final RouteSettingsPanel routeSettings;
	private final MapSettingsPanel mapSettings;
	
	private final JTabbedPane tabPane;
	
	public Sidebar() {
		this.setMinimumSize(new Dimension(Constants.SIDEBAR_WIDTH, 500));
		this.setMaximumSize(new Dimension(Constants.SIDEBAR_WIDTH, 500));
		this.setLayout(new BorderLayout());
		
		menu = new Menubar(); 
		this.add(menu, BorderLayout.NORTH);
		
		search = new SearchPanel();
		route = new RoutePanel();
		routeSettings = new RouteSettingsPanel();
		mapSettings = new MapSettingsPanel();
		
		tabPane = new JTabbedPane();
		tabPane.addTab("Map settings", mapSettings);
		tabPane.addTab("Route settings", routeSettings);
		tabPane.addTab("Route", route);
		tabPane.addTab("Search", search);
		
		this.add(tabPane, BorderLayout.CENTER);
	}
	public RoutePanel getRoutePanel() {
		return route;
	}
	public RouteSettingsPanel getRouteSettingsPanel() {
		return routeSettings;
	}
	public static JPanel createBorderedPanel(JPanel target, String title, int width, int height) {
		return createBorderedPanel(target, title, width, height, false);
	}
	public static JPanel createBorderedPanel(JPanel target, String title, int width, int height, boolean scale) {
		JPanel panel = new JPanel(new GridLayout(1, 1, 1, 1));
		if (title != null) {
			panel.setBorder(BorderFactory.createTitledBorder(title));
		}
		if (!scale) {
			panel.setMinimumSize(new Dimension(width, height));
			panel.setMaximumSize(new Dimension(width, height));
			panel.setPreferredSize(new Dimension(width, height));
		}
		target.add(panel);
		JPanel subPanel = new JPanel();
		panel.add(subPanel);
		return subPanel;
	}
}
