package net.vansante.EVEMap;

import java.awt.Color;

public abstract class Constants {
	
	public final static String TITLE = "EVEMap";
	public final static String VERSION = "1.4";
	public final static int REVISION = 100;
	
	public final static int FRAME_WIDTH = 1024;
	public final static int FRAME_HEIGHT = 768;
	
	public final static int SIDEBAR_WIDTH = 300;
	
	public final static int DESIRED_FPS = 40;
	
	public final static int FLATTEN_TIME = 1;
	
	public final static int API_ATTEMPTS = 2;
	
	public final static double SCALE = 4.8445284569785e14;
	public final static double LIGHTYEAR = 9.460730472580e15;
	
	public final static float SOLARSYSTEM_NAMES_LEVEL = -250.0f;
	public final static float CONSTELLATION_NAMES_LEVEL = -450.0f;
	
	public final static float SOLARSYSTEM_NAMES_DISTANCE = 280.0f;
	public final static float CONSTELLATION_NAMES_DISTANCE = 500.0f;
		
	public final static String[] SKILL_LEVELS = {"Level 0", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5"};
	public final static String[] JUMP_SHIP_TYPES = {"Black Ops", "Capital Industrial", "Carrier", "Dreadnought", "Jump Freighter", "Mothership","POS Jump Bridge", "Titan"};
	public final static double[] JUMP_SHIP_TYPE_RANGES = { 2.0, 5.0, 6.5, 5.0, 5.0, 4.0, 5.0, 3.5 };
	public final static int[] JUMP_SHIP_FUEL_USAGE = { 400, 1000, 1000, 1000, 1000, 3000, 500, 1000 };
	public final static double JUMP_FUEL_VOLUME = 0.15;
	
	public final static int JUMP_BRIDGE_ID = 6;
	
	public final static int JUMP_FREIGHTER_ID = 4;
	public final static int[] JUMP_SHIP_FREIGHTER_FUEL_USAGE = { 2900, 3300, 3100, 2700 };
	public final static String[] SHIP_RACES = {"Amarr", "Caldari", "Gallente", "Minmatar"};
	
	public final static int[] ROUTE_IGNORE_REGIONS = { 10000019, 10000017, 10000004	};
	
	public final static int DOT_MAX_STATIONCOUNT = 10;
	public final static int DOT_MAX_PLANETCOUNT = 15;
	public final static int DOT_MAX_MOONCOUNT = 100;
	public final static int DOT_MAX_BELTCOUNT = 25;
	public final static int DOT_MAX_JUMPS = 300;
	public final static int DOT_MAX_SHIPKILLS = 30;
	public final static int DOT_MAX_PODKILLS = 20;
	public final static int DOT_MAX_FACTIONKILLS = 600;
	
	public final static int ICON_SIZE = 10;
	
	public final static float[] STAR_COLOR = new float[] {0.4f, 0.4f, 0.6f};
	public final static float[] SELECTION_COLOR = new float[] {0.9f, 0.9f, 0.9f};
	
	public final static float[] ROUTE_COLOR = new float[] {1.0f, 0.8f, 0.5f};
	
	public final static int CONNECTION_REGION = 0;
	public final static int CONNECTION_CONSTELLATION = 1;
	public final static int CONNECTION_SOLARSYSTEM = 2;
	
	public final static String WEBSITE_URL = "http://evemap.vansante.net";
	public final static String EVE_ONLINE_THREAD_URL = "http://myeve.eve-online.com/ingameboard.asp?a=topic&threadID=764169";
	
	public final static String SETTING_FULLGRAPHICS = "FullGraphicsEnabled";
	
	public final static String SETTING_JUMPDRIVE_ROUTE = "JumpdriveRoute";
	public final static String SETTING_SHIPTYPE = "ShipType";
	public final static String SETTING_JUMPCALIBRATION = "JumpCalibration";
	public final static String SETTING_JUMPCONSERVATION = "JumpConservation";
	public final static String SETTING_JUMPFREIGHTER_SKILL = "JumpFreighterSkill";
	public final static String SETTING_JUMPFREIGHTER_RACE = "JumpFreighterRace";
	public final static String SETTING_MINIMUM_SECURITY = "MinimumSecurity";
	public final static String SETTING_MAXIMUM_SECURITY = "MaximumSecurity";
	public final static String SETTING_PREFER_STATIONS = "PreferStations";
	public final static String SETTING_AVOID_LIST = "AvoidSystemList";
	public final static String SETTING_WAYPOINT_LIST = "WaypointList";
	
	public final static String SETTING_ROTATE3D = "Rotate3D";
	public final static String SETTING_FLATTENMAP = "FlattenMap";
	public final static String SETTING_SHOWHOVERINFO = "ShowHoverInfo";
	public final static String SETTING_SHOWJUMPRANGE = "ShowJumpRange";
	public final static String SETTING_SHOWREGIONLABELS = "ShowRegionLabels";
	public final static String SETTING_SHOWCONSTELLATIONLABELS = "ShowConstellationLabels";
	public final static String SETTING_SHOWSOLARSYSTEMLABELS = "ShowSolarsystemLabels";
	public final static String SETTING_SHOWROUTELABELS = "ShowRouteLabels";
	public final static String SETTING_SOLARSYSTEMMODE = "SolarsystemMode";
	public final static String SETTING_CONNECTIONMODE = "ConnectionMode";
	public final static String SETTING_SHOWSOLARSYSTEM = "ShowSolarsystems";
	public final static String SETTING_SHOWCONNECTION = "ShowConnections";
	public final static String SETTING_SHOWSOLARSYSTEMREGION = "ShowSolarsystemRegion";
	public final static String SETTING_SHOWCONNECTIONREGION = "ShowConnectionRegion";
	
	public final static float[][] CONNECTION_COLORS = new float[][] {
		{0.8f, 0.0f, 0.8f},
		{0.8f, 0.0f, 0.0f},
		{0.0f, 0.0f, 0.8f}
	};
	
	public final static Color[] SEC_COLORS = {
		new Color(192, 0, 0),
		new Color(216, 24, 0),
		new Color(240, 48, 0),
		new Color(240, 72, 0),
		new Color(216, 96, 0),
		new Color(240, 240, 0),
		new Color(72, 216, 0),
		new Color(72, 216, 24),
		new Color(24, 240, 48),
		new Color(0, 240, 120),
		new Color(47, 239, 239)
	};
	public final static float[][] SEC_COLORS3F = {
		{SEC_COLORS[0].getRed() / 255.0f, SEC_COLORS[0].getGreen() / 255.0f, SEC_COLORS[0].getBlue() / 255.0f},
		{SEC_COLORS[1].getRed() / 255.0f, SEC_COLORS[1].getGreen() / 255.0f, SEC_COLORS[1].getBlue() / 255.0f},
		{SEC_COLORS[2].getRed() / 255.0f, SEC_COLORS[2].getGreen() / 255.0f, SEC_COLORS[2].getBlue() / 255.0f},
		{SEC_COLORS[3].getRed() / 255.0f, SEC_COLORS[3].getGreen() / 255.0f, SEC_COLORS[3].getBlue() / 255.0f},
		{SEC_COLORS[4].getRed() / 255.0f, SEC_COLORS[4].getGreen() / 255.0f, SEC_COLORS[4].getBlue() / 255.0f},
		{SEC_COLORS[5].getRed() / 255.0f, SEC_COLORS[5].getGreen() / 255.0f, SEC_COLORS[5].getBlue() / 255.0f},
		{SEC_COLORS[6].getRed() / 255.0f, SEC_COLORS[6].getGreen() / 255.0f, SEC_COLORS[6].getBlue() / 255.0f},
		{SEC_COLORS[7].getRed() / 255.0f, SEC_COLORS[7].getGreen() / 255.0f, SEC_COLORS[7].getBlue() / 255.0f},
		{SEC_COLORS[8].getRed() / 255.0f, SEC_COLORS[8].getGreen() / 255.0f, SEC_COLORS[8].getBlue() / 255.0f},
		{SEC_COLORS[9].getRed() / 255.0f, SEC_COLORS[9].getGreen() / 255.0f, SEC_COLORS[9].getBlue() / 255.0f},
		{SEC_COLORS[10].getRed() / 255.0f, SEC_COLORS[10].getGreen() / 255.0f, SEC_COLORS[10].getBlue() / 255.0f}
	};
	public final static float[][] SCALE_COLORS3F = {
		{ 0.0f, 0.0f, 1.0f },
		{ 0.0f, 0.5f, 1.0f },
		{ 0.0f, 0.8f, 1.0f },
		{ 0.0f, 1.0f, 1.0f },
		{ 0.0f, 1.0f, 0.6f },
		{ 0.0f, 1.0f, 0.0f },
		{ 0.5f, 0.9f, 0.0f },
		{ 0.8f, 0.9f, 0.0f },
		{ 1.0f, 1.0f, 0.0f },
		{ 1.0f, 0.8f, 0.0f },
		{ 1.0f, 0.5f, 0.0f },
		{ 1.0f, 0.0f, 0.0f }
	};
	public final static float[][] CLASS_COLORS3F = {
		{ 0.75f, 0.75f, 0.0f },
		{ 0.0f, 0.7f, 0.7f },
		{ 0.0f, 0.5f, 0.75f },
		{ 0.85f, 0.0f, 0.75f },
		{ 0.55f, 0.3f, 0.50f },
		{ 0.35f, 0.0f, 0.55f },
		{ 0.0f, 1.0f, 0.0f },
		{ 1.0f, 0.6f, 0.25f },
		{ 0.8f, 0.0f, 0.0f }
	};
}