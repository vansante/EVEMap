package net.vansante.EVEMap.Data.Loaders;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Data.Alliance;
import net.vansante.EVEMap.Data.EVEObjects;
import net.vansante.EVEMap.Data.Faction;
import net.vansante.EVEMap.Data.Location;
import net.vansante.EVEMap.Data.Locations;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Data.Station;
import net.vansante.EVEMap.Map.MapControl;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import java.io.IOException;
import java.util.*;
import org.jdom.*;
import java.text.*;
import javax.swing.JOptionPane;

public class APILoader {
	private final Locations solarsystems;
	private final EVEObjects factions, alliances, stations;
	
	private long killsTime = 0, jumpsTime = 0;
	private long alliancesTime = 0, stationsTime = 0;
	private long sovTime = 0, occuTime = 0;
	
	private UpdateThread updateThread;
	
	private boolean firstRun = true;
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	
	public final static String EVE_API_BASE_URL = "http://api.eve-online.com";
	public final static String EVE_API_KILLS_URL = "/map/Kills.xml.aspx";
	public final static String EVE_API_JUMPS_URL = "/map/Jumps.xml.aspx";
	public final static String EVE_API_STATIONS_URL = "/eve/ConquerableStationList.xml.aspx";
	public final static String EVE_API_ALLIANCES_URL = "/eve/AllianceList.xml.aspx";
	public final static String EVE_API_SOVEREIGNTY_URL = "/map/Sovereignty.xml.aspx";
	public final static String EVE_API_OCCUPANCY_URL = "/map/FacWarSystems.xml.aspx";
	
	public APILoader(Locations solarsystems, EVEObjects factions, EVEObjects alliances, EVEObjects stations) {
		this.solarsystems = solarsystems;
		this.factions = factions;
		this.alliances = alliances;
		this.stations = stations;
		
		this.startUpdateThread();
	}
	public long getCacheTime(Document xml) {
		long currentTime, cacheTime = 0;
		try {
			currentTime = dateFormat.parse(xml.getRootElement().getChild("currentTime").getValue()).getTime();
			cacheTime = dateFormat.parse(xml.getRootElement().getChild("cachedUntil").getValue()).getTime();
			cacheTime += (System.currentTimeMillis() - currentTime) + 10000;
		} catch (ParseException pe) {
			System.out.println(pe);
		}
		return cacheTime;
	}
	public boolean loadKills() throws IOException, JDOMException {
		if (System.currentTimeMillis() < killsTime) {
			return false;
		}
		this.resetKills();
		Document xml = new SAXBuilder().build(EVE_API_BASE_URL + EVE_API_KILLS_URL);
		
		Iterator iterator = xml.getRootElement().getChild("result").getChild("rowset").getChildren().iterator();
		Element element;
		Solarsystem solarsystem;
		while (iterator.hasNext()) {
			element = (Element) iterator.next();
			solarsystem = (Solarsystem) solarsystems.getById(element.getAttribute("solarSystemID").getIntValue());
			if (solarsystem != null) {
				solarsystem.setShipKills(element.getAttribute("shipKills").getIntValue());
				solarsystem.setPodKills(element.getAttribute("podKills").getIntValue());
				solarsystem.setFactionKills(element.getAttribute("factionKills").getIntValue());
			}
		}
		killsTime = this.getCacheTime(xml);
		return true;
	}
	public void resetKills() {
		if (firstRun) {
			return;
		}
		Solarsystem solarsystem;
		Iterator<Location> iterator = solarsystems.iterator();
		while (iterator.hasNext()) {
			solarsystem = (Solarsystem) iterator.next();
			solarsystem.setFactionKills(0);
			solarsystem.setPodKills(0);
			solarsystem.setFactionKills(0);
		}
	}
	public boolean loadJumps() throws IOException, JDOMException {
		if (System.currentTimeMillis() < jumpsTime) {
			return false;
		}
		this.resetJumps();
		Document xml = new SAXBuilder().build(EVE_API_BASE_URL + EVE_API_JUMPS_URL);

		Iterator iterator = xml.getRootElement().getChild("result").getChild("rowset").getChildren().iterator();
		Element nextElement;
		Solarsystem solarsystem;
		while (iterator.hasNext()) {
			nextElement = (Element) iterator.next();
			solarsystem = (Solarsystem) solarsystems.getById(nextElement.getAttribute("solarSystemID").getIntValue());
			if (solarsystem != null) {
				solarsystem.setJumps(nextElement.getAttribute("shipJumps").getIntValue());
			}
		}
		jumpsTime = this.getCacheTime(xml);
		return true;
	}
	public void resetJumps() {
		if (firstRun) {
			return;
		}
		Solarsystem solarsystem;
		Iterator<Location> iterator = solarsystems.iterator();
		while (iterator.hasNext()) {
			solarsystem = (Solarsystem) iterator.next();
			solarsystem.setJumps(0);
		}
	}
	public boolean loadAlliances() throws IOException, JDOMException {
		if (System.currentTimeMillis() < alliancesTime) {
			return false;
		}
		Document xml = new SAXBuilder().build(EVE_API_BASE_URL + EVE_API_ALLIANCES_URL);

		Iterator iterator = xml.getRootElement().getChild("result").getChild("rowset").getChildren().iterator();
		Element element;
		Alliance alliance;
		while (iterator.hasNext()) {
			element = (Element) iterator.next();
			alliance = new Alliance(
				element.getAttribute("allianceID").getIntValue(),
				element.getAttributeValue("name"),
				element.getAttributeValue("shortName")
			);
			alliances.add(alliance);
		}
		alliancesTime = this.getCacheTime(xml);
		return true;
	}
	public boolean loadStations() throws IOException, JDOMException {
		if (System.currentTimeMillis() < stationsTime) {
			return false;
		}
		Document xml = new SAXBuilder().build(EVE_API_BASE_URL + EVE_API_STATIONS_URL);

		Iterator iterator = xml.getRootElement().getChild("result").getChild("rowset").getChildren().iterator();
		Element element;
		Solarsystem solarsystem;
		Station station;
		while (iterator.hasNext()) {
			element = (Element) iterator.next();
			solarsystem = (Solarsystem) solarsystems.getById(element.getAttribute("solarSystemID").getIntValue());
			if (solarsystem != null) {
				station = new Station(
					element.getAttribute("stationID").getIntValue(),
					element.getAttributeValue("stationName"),
					solarsystem
				);
				stations.add(station);
				solarsystem.addStation(station);
			}
		}
		stationsTime = this.getCacheTime(xml);
		return true;
	}
	public boolean loadSovereignty() throws IOException, JDOMException {
		if (System.currentTimeMillis() < sovTime) {
			return false;
		}
		Document xml = new SAXBuilder().build(EVE_API_BASE_URL + EVE_API_SOVEREIGNTY_URL);

		Iterator iterator = xml.getRootElement().getChild("result").getChild("rowset").getChildren().iterator();
		Element element;
		Alliance alliance;
		Solarsystem solarsystem;
		while (iterator.hasNext()) {
			element = (Element) iterator.next();
			solarsystem = (Solarsystem) solarsystems.getById(element.getAttribute("solarSystemID").getIntValue());
			if (solarsystem != null) {
				if (element.getAttribute("allianceID").getIntValue() != 0) {
					alliance = (Alliance) alliances.getById(element.getAttribute("allianceID").getIntValue());
					solarsystem.setSovereignty(alliance);
					solarsystem.setSovereigntyLevel(element.getAttribute("sovereigntyLevel").getIntValue());
					if (element.getAttribute("sovereigntyLevel").getIntValue() == 4) {
						solarsystem.getConstellation().setConstellationCapital(solarsystem);
					}
				}
				if (element.getAttribute("constellationSovereignty").getIntValue() != 0) {
					alliance = (Alliance) alliances.getById(element.getAttribute("constellationSovereignty").getIntValue());
					solarsystem.getConstellation().setSovereignty(alliance);
				}
			}
		}
		sovTime = this.getCacheTime(xml);
		return true;
	}
	public boolean loadOccupancy() throws IOException, JDOMException {
		if (System.currentTimeMillis() < occuTime) {
			return false;
		}
		Document xml = new SAXBuilder().build(EVE_API_BASE_URL + EVE_API_OCCUPANCY_URL);

		Iterator iterator = xml.getRootElement().getChild("result").getChild("rowset").getChildren().iterator();
		Element element;
		Faction faction;
		Solarsystem solarsystem;
		while (iterator.hasNext()) {
			element = (Element) iterator.next();
			solarsystem = (Solarsystem) solarsystems.getById(element.getAttribute("solarSystemID").getIntValue());
			if (solarsystem != null) {
				if (element.getAttribute("occupyingFactionID").getIntValue() != 0) {
					faction = (Faction) factions.getById(element.getAttribute("occupyingFactionID").getIntValue());
					solarsystem.setOccupant(faction);
				} else {
					solarsystem.setOccupant(solarsystem.getRegion().getFaction());
				}
				solarsystem.setContested(element.getAttribute("contested").getBooleanValue());
			}
		}
		occuTime = this.getCacheTime(xml);
		return true;
	}
	public void startUpdateThread() {
		if (updateThread != null && updateThread.isAlive()) {
			return;
		}
		updateThread = new UpdateThread();
		updateThread.start();
	}
	public void stopUpdateThread() {
		if (updateThread != null && updateThread.isAlive()) {
			updateThread.cancel();
		}
	}
	private class UpdateThread extends Thread {
		private boolean cancel = false;
		private int errorCount = 0;
		public void cancel() {
			this.cancel = true;
		}
		public void run() {
			while (!cancel) {
				try {
					this.update(APILoader.this.loadKills());
					this.update(APILoader.this.loadJumps());
					if (firstRun) {
						this.update(APILoader.this.loadAlliances());
						this.update(APILoader.this.loadStations());
						this.update(APILoader.this.loadSovereignty());
						this.update(APILoader.this.loadOccupancy());
						firstRun = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
					errorCount++;
					if (errorCount >= Constants.API_ATTEMPTS) {
						this.cancel();
						JOptionPane.showMessageDialog(Main.get(), "There was an error retrieving EVE API data.\nEVEMap will stop attempting to download API data.", "Warning", JOptionPane.WARNING_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(Main.get(), "There was an error retrieving EVE API data.", "Warning", JOptionPane.WARNING_MESSAGE);
					}
				}
				try {
					Thread.sleep(60000);
				} catch( InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		private void update(boolean updated) {
			if (MapControl.get().getRenderingState() == MapControl.RENDER_LIST) {
				MapControl.get().setRenderingState(MapControl.RENDER_RESET_LIST);
			}
		}
	}
//	private void printXML(Document xml) {
//		try {
//			org.jdom.output.XMLOutputter outputter = new org.jdom.output.XMLOutputter();
//			outputter.output(xml, System.out);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
