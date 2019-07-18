package net.vansante.EVEMap.Data;

import java.util.*;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Tools;

public class Solarsystem extends Location {
	
	public final static String[] SECURITY_LEVELS = {"0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0"};
	public final static String[] SYSTEM_CLASSES = {"Unknown", "Unknown", "Unknown", "Dangerous unknown", "Dangerous unknown", "Deadly unknown", "High security", "Low security", "Null security"};
	public final static String[] WORMHOLE_ANOMALIES = {"None", "Black Hole", "Cataclysmic Variable", "Magnetar", "Pulsar", "Red Giant", "Wolf Rayet"};
	
	public final static int SEC_10 = 10;
	public final static int SEC_09 = 9;
	public final static int SEC_08 = 8;
	public final static int SEC_07 = 7;
	public final static int SEC_06 = 6;
	public final static int SEC_05 = 5;
	public final static int SEC_04 = 4;
	public final static int SEC_03 = 3;
	public final static int SEC_02 = 2;
	public final static int SEC_01 = 1;
	public final static int SEC_00 = 0;
	
	private final Constellation constellation;
	private final List<Solarsystem> connections;
	private final List<Station> stations;
	
	private final float security;
	private final int planets, moons, belts;
	
	private int jumps = 0, shipKills = 0, podKills = 0, factionKills = 0;

	private int sovereigntyLevel;
	private Alliance sovereignty;
	
	private Faction occupant;
	private boolean contested;
	
	private int classification, anomaly;
	
	public Solarsystem(int id, double x, double y, double z, String name, Constellation constellation,
			float security, int planets, int moons, int belts, int classification, int anomaly) {
		
		super(id, x, y, z, name);
		this.constellation = constellation;
		this.security = security;
		this.planets = planets;
		this.moons = moons;
		this.belts = belts;
		this.classification = classification;
		this.anomaly = anomaly;
		
		this.connections = new LinkedList<Solarsystem>();
		this.stations = new LinkedList<Station>();
	}
	public void addConnection(Solarsystem solarsystem) {
		connections.add(solarsystem);
	}
	public Solarsystem getConnection(int index) {
		return connections.get(index);
	}
	public Iterator<Solarsystem> getConnectionIterator() {
		return connections.iterator();
	}
	public List<Solarsystem> getConnections() {
		return connections;
	}
	public int getNumberOfConnections() {
		return connections.size();
	}
	public void addStation(Station station) {
		stations.add(station);
	}
	public Iterator<Station> getStationIterator() {
		return stations.iterator();
	}
	public List<Station> getStations() {
		return stations;
	}
	public int getNumberOfStations() {
		return stations.size();
	}
	public int getSecurityType() {
		if (security >= 0.95) {
			return SEC_10;
		} else if (security >= 0.85 && security < 0.95) {
			return SEC_09;
		} else if (security >= 0.75 && security < 0.85) {
			return SEC_08;
		} else if (security >= 0.65 && security < 0.75) {
			return SEC_07;
		} else if (security >= 0.55 && security < 0.65) {
			return SEC_06;
		} else if (security >= 0.45 && security < 0.55) {
			return SEC_05;
		} else if (security >= 0.35 && security < 0.45) {
			return SEC_04;
		} else if (security >= 0.25 && security < 0.35) {
			return SEC_03;
		} else if (security >= 0.15 && security < 0.25) {
			return SEC_02;
		} else if (security >= 0.00 && security < 0.15) {
			return SEC_01;
		}
		return SEC_00;
	}
	public int getZeroSecurityType() {
		if (security > 0.00) {
			return -1;
		} else if (security <= 0.00 && security > -0.05) {
			return SEC_10;
		} else if (security <= -0.05 && security > -0.15) {
			return SEC_08;
		} else if (security <= -0.15 && security > -0.25) {
			return SEC_08;
		} else if (security <= -0.25 && security > -0.35) {
			return SEC_07;
		} else if (security <= -0.35 && security > -0.45) {
			return SEC_06;
		} else if (security <= -0.45 && security > -0.55) {
			return SEC_05;
		} else if (security <= -0.55 && security > -0.65) {
			return SEC_04;
		} else if (security <= -0.65 && security > -0.75) {
			return SEC_03;
		} else if (security <= -0.75 && security > -0.85) {
			return SEC_02;
		} else if (security <= -0.85 && security > -0.95) {
			return SEC_01;
		}
		return SEC_00;
	}
	public String toString() {
		return this.getName() + " / " + constellation.getName() + " / " + constellation.getRegion().getName();
	}
	public String getToolTip() {
		StringBuilder tooltip = new StringBuilder();
		tooltip.append("<html><head><title>");
		tooltip.append(this.getName());
		tooltip.append("</title></head><body><h3>");
		tooltip.append(this.getName());
		tooltip.append("</h3><table><tr><td>Constellation:</td><td>");
		tooltip.append(this.getConstellation().getName());
		tooltip.append("</td></tr><tr><td>Region:</td><td>");
		tooltip.append(this.getRegion().getName());
		tooltip.append("</td></tr><tr><td>Security status:</td><td>");
		tooltip.append("<table><tr><td>");
		tooltip.append(this.getSecurity());
		tooltip.append("</td><td> &nbsp;&nbsp; </td><td bgcolor='#");
		tooltip.append(Tools.colorToHex(Constants.SEC_COLORS[this.getSecurityType()]));
		tooltip.append("'> &nbsp; </td></tr></table>");
		if (this.isUnknown()) {
			tooltip.append("</td></tr><tr><td>Class:</td><td>");
			tooltip.append(this.getClassification()).append(" (").append(this.getClassText()).append(")");
			tooltip.append("</td></tr><tr><td>Anomaly:</td><td>");
			tooltip.append(this.getAnomalyText());
		}
		
		if (this.getRegion().getFaction() != null) {
			tooltip.append("</td></tr><tr><td>Faction:</td><td>");
			tooltip.append(this.getRegion().getFaction().getName());
		}
		if (this.getSovereignty() != null) {
			tooltip.append("</td></tr><tr><td>Sovereignty:</td><td>");
			tooltip.append(this.getSovereignty().getName());
		}
		if (this.getConstellation().getSovereignty() != null) {
			tooltip.append("</td></tr><tr><td>Constellation sovereignty:</td><td>");
			tooltip.append(this.getConstellation().getSovereignty().getName());
		}
		tooltip.append("</td></tr><tr><td>Number of planets:</td><td>");
		tooltip.append(planets);
		tooltip.append("</td></tr><tr><td>Number of moons:</td><td>");
		tooltip.append(moons);
		tooltip.append("</td></tr><tr><td>Number of belts:</td><td>");
		tooltip.append(belts);
		if (this.isKnown()) {
			tooltip.append("</td></tr><tr><td valign='top'><br><br>Connected solarsystems:</td><td><br><br>");
			for (Solarsystem neighbour : connections) {
				tooltip.append(neighbour.getName()).append("<br>");
			}
			tooltip.append("</td></tr><tr><td valign='top'><br><br>Stations:</td><td><br><br>");
			for (Station station : stations) {
				tooltip.append(station.getName()).append("<br>");
			}
		}
		tooltip.append("</td></tr></table>");
		tooltip.append("</body></html>");
		return tooltip.toString();
	}
	public int getType() {
		return Location.TYPE_SOLARSYSTEM;
	}
	public float getSecurity() {
		int temp = (int) (security * 1000);
		return (float) temp / 1000;
	}
	public float getSecurityRaw() {
		return security;
	}
	public int getPlanets() {
		return planets;
	}
	public int getMoons() {
		return moons;
	}
	public int getBelts() {
		return belts;
	}
	public Constellation getConstellation() {
		return constellation;
	}
	public Region getRegion() {
		return constellation.getRegion();
	}
	public int getJumps() {
		return jumps;
	}
	public void setJumps(int jumps) {
		this.jumps = jumps;
	}
	public int getShipKills() {
		return shipKills;
	}
	public void setShipKills(int shipKills) {
		this.shipKills = shipKills;
	}
	public int getPodKills() {
		return podKills;
	}
	public void setPodKills(int podKills) {
		this.podKills = podKills;
	}
	public int getFactionKills() {
		return factionKills;
	}
	public void setFactionKills(int factionKills) {
		this.factionKills = factionKills;
	}
	public int getSovereigntyLevel() {
		return sovereigntyLevel;
	}
	public void setSovereigntyLevel(int sovereigntyLevel) {
		this.sovereigntyLevel = sovereigntyLevel;
	}
	public Alliance getSovereignty() {
		return sovereignty;
	}
	public void setSovereignty(Alliance sovereignty) {
		this.sovereignty = sovereignty;
	}
	public Faction getOccupant() {
		return occupant;
	}
	public void setOccupant(Faction occupant) {
		this.occupant = occupant;
	}
	public boolean isContested() {
		return contested;
	}
	public void setContested(boolean contested) {
		this.contested = contested;
	}
	public int getClassification() {
		return classification;
	}
	public String getClassText() {
		return SYSTEM_CLASSES[classification - 1];
	}
	public int getAnomaly() {
		return anomaly;
	}
	public String getAnomalyText() {
		return WORMHOLE_ANOMALIES[anomaly];
	}
	public boolean isUnknown() {
		return classification < 7;
	}
	public boolean isKnown() {
		return classification > 6;
	}
}