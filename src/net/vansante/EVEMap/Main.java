package net.vansante.EVEMap;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.media.opengl.GLCapabilities;

import net.vansante.EVEMap.Data.Connection;
import net.vansante.EVEMap.Data.Constellation;
import net.vansante.EVEMap.Data.EVEObject;
import net.vansante.EVEMap.Data.EVEObjects;
import net.vansante.EVEMap.Data.Location;
import net.vansante.EVEMap.Data.Locations;
import net.vansante.EVEMap.Data.Region;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Data.Loaders.APILoader;
import net.vansante.EVEMap.Data.Loaders.DataLoader;
import net.vansante.EVEMap.Map.MapControl;
import net.vansante.EVEMap.Map.MapView;
import net.vansante.EVEMap.Route.Route;
import net.vansante.EVEMap.UI.Sidebar;
import net.vansante.EVEMap.UI.Dialogs.About;

public class Main extends JFrame implements WindowListener {
	
	private static Main instance;
	
	private final Preferences preferences;
	private final APILoader apiLoader;
	
	private final Sidebar sidebar;
	private final MapView mapView; 
	
	private final List<Connection> connections;
	private final Locations solarsystems;
	private final Locations constellations;
	private final Locations regions;
	private final EVEObjects factions;
	private final EVEObjects stations;
	private final EVEObjects alliances;
	
	private final Route route;
	
	private GraphicsDevice graphicsDevice;
	private boolean fullscreen = false;
	
	public Main(GraphicsDevice graphicsDevice, String[] args) {
		super(graphicsDevice.getDefaultConfiguration());
		instance = this;
		
		this.graphicsDevice = graphicsDevice;
		this.setTitle(Constants.TITLE + " " + Constants.VERSION);
		
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setSize(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
		this.setResizable(true);
		this.setIconImage(Tools.getImage("icon.png"));
		
		this.addWindowListener(this);
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		
		ToolTipManager.sharedInstance().setInitialDelay(2000);
		ToolTipManager.sharedInstance().setDismissDelay(10000);
		ToolTipManager.sharedInstance().setReshowDelay(4000);
		
		preferences = Preferences.userNodeForPackage(this.getClass());
		
		connections = new LinkedList<Connection>();
		solarsystems = new Locations(7930);
		constellations = new Locations(1110);
		regions = new Locations(100);
		factions = new EVEObjects(24);
		stations = new EVEObjects(5300);
		alliances = new EVEObjects(500);
		
		try {
			DataLoader.readFactions(factions);
	    	DataLoader.readRegions(regions, factions);
			DataLoader.readConstellations(constellations, regions);
			DataLoader.readSolarsystems(solarsystems, constellations);
	 		DataLoader.readConnections(connections, solarsystems);
			DataLoader.readStations(stations, solarsystems);
		} catch (Exception e) {
	 		System.out.println(e);
			JOptionPane.showMessageDialog(Main.this, "There was an error loading the EVE static data:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	 		Main.this.exit();
	 	}
	    
	    MapControl.get().loadSettings(preferences, regions);
	    
	    route = Route.get();
	    route.loadSettings();

	    GLCapabilities capabilities = new GLCapabilities();
		capabilities.setDoubleBuffered(true);
		capabilities.setHardwareAccelerated(true);
		
		mapView = new MapView(capabilities);
		
		sidebar = new Sidebar();
		
		this.getContentPane().setLayout(new BorderLayout(0, 0));
		
		this.getContentPane().add(sidebar, BorderLayout.WEST);
		this.getContentPane().add(mapView, BorderLayout.CENTER);
		
		apiLoader = new APILoader(solarsystems, factions, alliances, stations);
		
		this.setVisible(true);
	}
	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception ex) {
	    	System.out.println("Could not set look and feel");
	    }
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		// Instantiate this class
		new Main(graphicsDevice, args);
	}
	public static Main get() {
		return instance;
	}
	public void exit() {
		if (mapView != null) {
			mapView.stop();
		}
		if (route != null) {
			route.saveSettings();
			route.stop();
			MapControl.get().saveSettings(preferences);
		}
		if (apiLoader != null) {
			apiLoader.stopUpdateThread();
		}
		dispose();
		System.exit(0);
	}
	public void showAbout() {
		About about = new About();
		about.setVisible(true);
	}
	public void toggleSidebar() {
		sidebar.setVisible(!sidebar.isVisible());
		this.getContentPane().repaint();
		mapView.setKeyListenerEnabled(!sidebar.isVisible());
	}
	public void toggleFullscreen() {
		fullscreen = !fullscreen;
		if (fullscreen) {
			this.dispose();
			this.setUndecorated(true);
			graphicsDevice.setFullScreenWindow(this);
			this.setVisible(true);
		} else {
			this.dispose();
			this.setUndecorated(false);
			graphicsDevice.setFullScreenWindow(null);
			this.setVisible(true);
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		this.getContentPane().validate();
		MapControl.get().setRenderingState(MapControl.RENDER_RESET_LIST);
	}
	public GraphicsDevice[] getGraphicsDevices() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}
	public GraphicsDevice getCurrentGraphicsDevice() {
		return graphicsDevice;
	}
	public void setCurrentGraphicsDevice(int index) {
		graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[index];
	}
	public void windowClosing(WindowEvent e) {
		// When the close window button is pressed, shut down the program.
		this.exit();
	}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	public Iterator<Location> getSolIterator() {
		return solarsystems.iterator();
	}
	public Iterator<Connection> getConnectionIterator() {
		return connections.iterator();
	}
	public Iterator<Location> getConIterator() {
		return constellations.iterator();
	}
	public Iterator<Location> getRegionIterator() {
		return regions.iterator();
	}
	public Iterator<EVEObject> getFactionIterator() {
		return factions.iterator();
	}
	public Iterator<EVEObject> getAllianceIterator() {
		return alliances.iterator();
	}
	public Iterator<EVEObject> getStationIterator() {
		return stations.iterator();
	}
	public Locations getSolarsystems() {
		return solarsystems;
	}
	public List<Connection> getConnections() {
		return connections;
	}
	public Locations getConstellations() {
		return constellations;
	}
	public Locations getRegions() {
		return regions;
	}
	public EVEObjects getFactions() {
		return factions;
	}
	public EVEObjects getAlliances() {
		return alliances;
	}
	public EVEObjects getStations() {
		return stations;
	}
	public Solarsystem getSolarsystem(int index) {
		return (Solarsystem) solarsystems.get(index);
	}
	public Constellation getConstellation(int index) {
		return (Constellation) constellations.get(index);
	}
	public Region getRegion(int index) {
		return (Region) regions.get(index);
	}
	public Preferences getPreferences() {
		return preferences;
	}
}
