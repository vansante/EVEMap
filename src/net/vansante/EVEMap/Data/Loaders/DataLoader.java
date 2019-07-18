package net.vansante.EVEMap.Data.Loaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.text.ParseException;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Data.Connection;
import net.vansante.EVEMap.Data.Constellation;
import net.vansante.EVEMap.Data.EVEObjects;
import net.vansante.EVEMap.Data.Faction;
import net.vansante.EVEMap.Data.Locations;
import net.vansante.EVEMap.Data.Region;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Data.Station;

public abstract class DataLoader {
	
	public static void readFactions(EVEObjects factions) throws IOException, ParseException {
		BufferedReader reader = null;
		URL url = DataLoader.class.getClassLoader().getResource("data/factions.dat");
		if (url == null) {
			throw new IOException("Could not find factions file.");
		}
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			Faction faction = null;
			int lineNumber = 1;
			String[] parts = null;
			int id = 0;
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts.length != 2) {
					throw new ParseException("Unexpected faction array size (" + (parts.length) + ")", lineNumber);
				}
				try {
					id = Integer.parseInt(parts[1]);
				} catch (NumberFormatException n) {
					throw new ParseException("Illegal number format", lineNumber);
				}
				faction = new Faction(id, parts[0]);
				factions.add(faction);
				lineNumber++;
			}
		} catch (IOException error) {
			throw new IOException("Could not read factions file '" + url.toString() + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void readRegions(Locations regions, EVEObjects factions) throws IOException, ParseException {
		BufferedReader reader = null;
		URL url = DataLoader.class.getClassLoader().getResource("data/regions.dat");
		if (url == null) {
			throw new IOException("Could not find regions file.");
		}
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			Region region = null;
			int lineNumber = 1;
			String[] parts = null;
			int id = 0, factionId = 0;
			double x = 0, y = 0, z = 0;
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts.length != 6) {
					throw new ParseException("Unexpected region array size (" + (parts.length) + ")", lineNumber);
				}
				try {
					id = Integer.parseInt(parts[1]);
					x = Double.parseDouble(parts[2]) / Constants.SCALE;
					y = Double.parseDouble(parts[3]) / Constants.SCALE;
					z = - Double.parseDouble(parts[4]) / Constants.SCALE;
					factionId = Integer.parseInt(parts[5]);
				} catch (NumberFormatException n) {
					throw new ParseException("Illegal number format", lineNumber);
				}
				region = new Region(id, x, y, z, parts[0], (Faction) factions.getById(factionId));
				regions.add(region);
				lineNumber++;
			}
		} catch (IOException error) {
			throw new IOException("Could not read regions file '" + url.toString() + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void readConstellations(Locations constellations, Locations regions) throws IOException, ParseException {
		BufferedReader reader = null;
		URL url = DataLoader.class.getClassLoader().getResource("data/constellations.dat");
		if (url == null) {
			throw new IOException("Could not find constellations file.");
		}
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			Constellation constellation = null;
			int lineNumber = 1;
			String[] parts = null;
			int id = 0, regionId = 0;
			double x = 0, y = 0, z = 0;
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts.length != 6) {
					throw new ParseException("Unexpected constellation array size (" + (parts.length) + ")", lineNumber);
				}
				try {
					id = Integer.parseInt(parts[1]);
					regionId = Integer.parseInt(parts[2]);
					x = Double.parseDouble(parts[3]) / Constants.SCALE;
					y = Double.parseDouble(parts[4]) / Constants.SCALE;
					z = - Double.parseDouble(parts[5]) / Constants.SCALE;
				} catch (NumberFormatException n) {
					throw new ParseException("Illegal number format", lineNumber);
				}
				constellation = new Constellation(id, x, y, z, parts[0], (Region) regions.getById(regionId));
				constellations.add(constellation);
				lineNumber++;
			}
		} catch (IOException error) {
			throw new IOException("Could not read constellations file '" + url.toString() + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void readSolarsystems(Locations solarsystems, Locations constellations) throws IOException, ParseException {
		BufferedReader reader = null;
		URL url = DataLoader.class.getClassLoader().getResource("data/solarsystems.dat");
		if (url == null) {
			throw new IOException("Could not find solarsystems file.");
		}
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			Solarsystem solarsystem = null;
			int lineNumber = 1;
			String[] parts = null;
			int id = 0, constellationId = 0, planets = 0, moons = 0, belts = 0, classification = 0, anomaly = 0;
			double x = 0, y = 0, z = 0;
			float security = 0;
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts.length != 12) {
					throw new ParseException("Unexpected solarsystem array size (" + (parts.length) + ")", lineNumber);
				}
				try {
					id = Integer.parseInt(parts[1]);
					constellationId = Integer.parseInt(parts[2]);
					x = Double.parseDouble(parts[3]) / Constants.SCALE;
					y = Double.parseDouble(parts[4]) / Constants.SCALE;
					z = - Double.parseDouble(parts[5]) / Constants.SCALE;
					security = Float.parseFloat(parts[6]);
					planets = Integer.parseInt(parts[7]);
					moons = Integer.parseInt(parts[8]);
					belts = Integer.parseInt(parts[9]);
					classification = Integer.parseInt(parts[10]);
					anomaly = Integer.parseInt(parts[11]);
				} catch (NumberFormatException n) {
					throw new ParseException("Illegal number format", lineNumber);
				}
				solarsystem = new Solarsystem(id, x, y, z, parts[0], (Constellation) constellations.getById(constellationId),
						security, planets, moons, belts, classification, anomaly);
				solarsystems.add(solarsystem);
				lineNumber++;
			}
		} catch (IOException error) {
			throw new IOException("Could not read solarsystems file '" + url.toString() + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void readConnections(List<Connection> connections, Locations solarsystems) throws IOException, ParseException {
		BufferedReader reader = null;
		URL url = DataLoader.class.getClassLoader().getResource("data/connections.dat");
		if (url == null) {
			throw new IOException("Could not find solarsystem jumps file.");
		}
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			String[] parts;
			int lineNumber = 1;
			Solarsystem solarsystem1, solarsystem2;
			Connection connection;
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts.length != 2) {
					throw new ParseException("Unexpected connection array size (" + (parts.length) + ")", lineNumber);
				}
				try {
					solarsystem1 = (Solarsystem) solarsystems.getById(Integer.parseInt(parts[0]));
					solarsystem2 = (Solarsystem) solarsystems.getById(Integer.parseInt(parts[1]));
					solarsystem1.addConnection(solarsystem2);
					solarsystem2.addConnection(solarsystem1);
					connection = new Connection(solarsystem1, solarsystem2);
					connections.add(connection);
				} catch (NumberFormatException n) {
					throw new ParseException("Illegal number format", lineNumber);
				} catch (NullPointerException npe) {
					throw new ParseException("Couldn't find solarsystem", lineNumber);
				}
				lineNumber++;
			}
		} catch (IOException error) {
			throw new IOException("Could not read solarsystem jumps file '" + url.toString() + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
	public static void readStations(EVEObjects stations, Locations solarsystems) throws IOException, ParseException {
		BufferedReader reader = null;
		URL url = DataLoader.class.getClassLoader().getResource("data/stations.dat");
		if (url == null) {
			throw new IOException("Could not find stations file.");
		}
		try {
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			Station station = null;
			Solarsystem solarsystem = null;
			int lineNumber = 1;
			String[] parts = null;
			int id = 0, solarsystemId = 0;
			while ((line = reader.readLine()) != null) {
				parts = line.split(",");
				if (parts.length != 3) {
					throw new ParseException("Unexpected station array size (" + (parts.length) + ")", lineNumber);
				}
				try {
					id = Integer.parseInt(parts[1]);
					solarsystemId = Integer.parseInt(parts[2]);
				} catch (NumberFormatException n) {
					throw new ParseException("Illegal number format", lineNumber);
				}
				try {
					solarsystem = (Solarsystem) solarsystems.getById(solarsystemId);
					station = new Station(id, parts[0], solarsystem);
					stations.add(station);
					solarsystem.addStation(station);
				} catch (NullPointerException npe) {
					throw new ParseException("Couldn't find solarsystem", lineNumber);
				}
				lineNumber++;
			}
		} catch (IOException error) {
			throw new IOException("Could not read stations file '" + url.toString() + "'.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {}
			}
		}
	}
}