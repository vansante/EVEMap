package net.vansante.EVEMap.Map;

import java.lang.Thread;
import java.awt.Point;
import java.util.prefs.Preferences;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Location;
import net.vansante.EVEMap.Data.Locations;
import net.vansante.EVEMap.Data.Region;
import net.vansante.EVEMap.Data.Solarsystem;

public class MapControl {
	public final static int SOLARSYSTEM_MODE_NORMAL = 0;
	public final static int SOLARSYSTEM_MODE_SECURITY = 1;
	public final static int SOLARSYSTEM_MODE_ZEROSECURITY = 2;
	public final static int SOLARSYSTEM_MODE_CLASS = 3;
	public final static int SOLARSYSTEM_MODE_REGION = 4;
	public final static int SOLARSYSTEM_MODE_FACTION = 5;
	public final static int SOLARSYSTEM_MODE_OCCUPANCY = 6;
	public final static int SOLARSYSTEM_MODE_SOVEREIGNTY = 7;
	public final static int SOLARSYSTEM_MODE_CONSOVEREIGNTY = 8;
	public final static int SOLARSYSTEM_MODE_STATIONCOUNT = 9;
	public final static int SOLARSYSTEM_MODE_PLANETCOUNT = 10;
	public final static int SOLARSYSTEM_MODE_MOONCOUNT = 11;
	public final static int SOLARSYSTEM_MODE_BELTCOUNT = 12;
	public final static int SOLARSYSTEM_MODE_JUMPS = 13;
	public final static int SOLARSYSTEM_MODE_SHIPKILLS = 14;
	public final static int SOLARSYSTEM_MODE_PODKILLS = 15;
	public final static int SOLARSYSTEM_MODE_FACTIONKILLS = 16;
	public final static int SOLARSYSTEM_MODE_ANOMALIES = 17;
	
	public final static int CONNECTION_MODE_JUMPTYPE = 0;
	public final static int CONNECTION_MODE_SECURITY = 1;
	public final static int CONNECTION_MODE_REGION = 2;
	
	public final static int SOLARSYSTEM_SHOW_NORMAL = 0;
	public final static int SOLARSYSTEM_SHOW_REGION = 1;
	public final static int SOLARSYSTEM_SHOW_UNKNOWN = 2;
	
	public final static int CONNECTION_SHOW_ALL = 0;
	public final static int CONNECTION_SHOW_HOVER = 1;
	public final static int CONNECTION_SHOW_REGION = 2;
	public final static int CONNECTION_SHOW_NONE = 3;
	
	public final static int RENDER_DIRECT = 0;
	public final static int RENDER_START_LIST = 1;
	public final static int RENDER_LIST = 2;
	public final static int RENDER_STOP_LIST = 3;
	public final static int RENDER_RESET_LIST = 4;
	
	public final static double[] KNOWN_START_VIEW = { 250, -1600, 0, 180, 0 };
	public final static double[] KNOWN_VIEW_BOUNDRIES = { -2000, -4000, -2000, 2000, 300, 2000 };

	public final static double[] UNKNOWN_START_VIEW = { -16000, -1800, -19500, 180, 0 };
	public final static double[] UNKNOWN_VIEW_BOUNDRIES = { -17500, -4000, -21000, -14500, 300, -18000 };

	
	private static MapControl instance = null;
	
	private int renderingState = RENDER_START_LIST;
		
	private MoveViewThread moveViewThread;
	private Thread flattenThread;
	
	private double viewX = 250, viewY = -5000, viewZ = 0;
	private double rotateX = 180, rotateY = 0;
	private double yScale = 1.0;
	
	private Point pickPoint = null;
	private Solarsystem hover;
	
	private boolean rotate3dEnabled = false, flattenEnabled;
	private boolean showHoverInfoEnabled = true, showJumpRangeEnabled = false;
	
	private boolean regionLabelsEnabled = true, conLabelsEnabled = true;
	private boolean solLabelsEnabled = true, routeLabelsEnabled = true;
	
	private int solarsystemMode = SOLARSYSTEM_MODE_SECURITY;
	private int connectionMode = CONNECTION_MODE_JUMPTYPE;
	
	private int showSolarsystem = SOLARSYSTEM_SHOW_NORMAL;
	private int showConnection = CONNECTION_SHOW_ALL;
	private Region showSolarsystemRegion, showConnectionRegion;
	
	private MapControl() {
		this.resetView();
	}
	public static MapControl get() {
		if (instance == null) {
			instance = new MapControl();
		}
		return instance;
	}
	public void resetView() {
		this.resetView(1);
	}
	public void resetView(int speed) {
		if (showSolarsystem == SOLARSYSTEM_SHOW_UNKNOWN) {
			this.moveView(UNKNOWN_START_VIEW[0], UNKNOWN_START_VIEW[1], UNKNOWN_START_VIEW[2], UNKNOWN_START_VIEW[3], UNKNOWN_START_VIEW[4], speed);
		} else {
			this.moveView(KNOWN_START_VIEW[0], KNOWN_START_VIEW[1], KNOWN_START_VIEW[2], KNOWN_START_VIEW[3], KNOWN_START_VIEW[4], speed);
		}
	}
	public int getRenderingState() {
		return renderingState;
	}
	public void setRenderingState(int renderingState) {
		this.renderingState = renderingState;
	}
	public Point getPickPoint() {
		return pickPoint;
	}
	public void setPickPoint(Point pickPoint) {
		this.pickPoint = pickPoint;
	}
	public Solarsystem getHover() {
		return hover;
	}
	public void setHover(Solarsystem hover) {
		this.hover = hover;
	}
	public boolean regionLabelsEnabled() {
		return regionLabelsEnabled;
	}
	public void enableRegionLabels(boolean regionLabelsEnabled) {
		this.regionLabelsEnabled = regionLabelsEnabled;
	}
	public boolean conLabelsEnabled() {
		return conLabelsEnabled;
	}
	public void enableConLabels(boolean conLabelsEnabled) {
		this.conLabelsEnabled = conLabelsEnabled;
	}
	public boolean solLabelsEnabled() {
		return solLabelsEnabled;
	}
	public void enableSolLabels(boolean solLabelsEnabled) {
		this.solLabelsEnabled = solLabelsEnabled;
	}
	public boolean routeLabelsEnabled() {
		return routeLabelsEnabled;
	}
	public void enableRouteLabels(boolean routeLabelsEnabled) {
		this.routeLabelsEnabled = routeLabelsEnabled;
	}
	public int getSolarsystemMode() {
		return solarsystemMode;
	}
	public void setSolarsystemMode(int solarsystemMode) {
		this.solarsystemMode = solarsystemMode;
		renderingState = RENDER_RESET_LIST;
	}
	public int getConnectionMode() {
		return connectionMode;
	}
	public void setConnectionMode(int connectionMode) {
		this.connectionMode = connectionMode;
		renderingState = RENDER_RESET_LIST;
	}
	public int getShowSolarsystem() {
		return showSolarsystem;
	}
	public void setShowSolarsystem(int showSolarsystem) {
		int temp = this.showSolarsystem;
		this.showSolarsystem = showSolarsystem;
		renderingState = RENDER_RESET_LIST;
		
		if (showSolarsystem == SOLARSYSTEM_SHOW_REGION) {
			this.lookAt(showSolarsystemRegion, 100);
		} else if ((showSolarsystem != SOLARSYSTEM_SHOW_UNKNOWN && temp == SOLARSYSTEM_SHOW_UNKNOWN) ||
				(showSolarsystem == SOLARSYSTEM_SHOW_UNKNOWN && temp != SOLARSYSTEM_SHOW_UNKNOWN)) {
			this.resetView(100);
		}
	}
	public int getShowConnection() {
		return showConnection;
	}
	public void setShowConnection(int showConnection) {
		this.showConnection = showConnection;
		renderingState = RENDER_RESET_LIST;
	}
	public Region getShowSolarsystemRegion() {
		return showSolarsystemRegion;
	}
	public void setShowSolarsystemRegion(Region showSolarsystemRegion) {
		this.showSolarsystemRegion = showSolarsystemRegion;
		renderingState = RENDER_RESET_LIST;
	}
	public Region getShowConnectionRegion() {
		return showConnectionRegion;
	}
	public void setShowConnectionRegion(Region showConnectionRegion) {
		this.showConnectionRegion = showConnectionRegion;
		renderingState = RENDER_RESET_LIST;
	}
	public void lookAt(Location location, int speed) {
		this.moveView(-location.getX(), location.getY() - 150, location.getZ(), 180, 0, speed);
	}
	public void zoomIn() {
		this.moveView(viewX, this.boxY(viewY - (300 * this.getAcceleration())), viewZ, rotateX, rotateY, 1);
	}
	public void zoomOut() {
		this.moveView(viewX, this.boxY(viewY + (300 * this.getAcceleration())), viewZ, rotateX, rotateY, 1);
	}
	public void moveNorth() {
		this.moveView(viewX, viewY, this.boxZ(viewZ - (100 * this.getAcceleration())), rotateX, rotateY, 1);
	}
	public void moveEast() {
		this.moveView(this.boxX(viewX + (100 * this.getAcceleration())), viewY, viewZ, rotateX, rotateY, 1);
	}
	public void moveSouth() {
		this.moveView(viewX, viewY, this.boxZ(viewZ + (100 * this.getAcceleration())), rotateX, rotateY, 1);
	}
	public void moveWest() {
		this.moveView(this.boxX(viewX - (100 * this.getAcceleration())), viewY, viewZ, rotateX, rotateY, 1);
	}
	public double getViewX() {
		return viewX;
	}
	public double boxX(double x) {
		if (showSolarsystem == SOLARSYSTEM_SHOW_UNKNOWN) {
			if (x > UNKNOWN_VIEW_BOUNDRIES[3]) {
				x = UNKNOWN_VIEW_BOUNDRIES[3];
			} else if (x < UNKNOWN_VIEW_BOUNDRIES[0]) {
				x = UNKNOWN_VIEW_BOUNDRIES[0];
			}
		} else {
			if (x > KNOWN_VIEW_BOUNDRIES[3]) {
				x = KNOWN_VIEW_BOUNDRIES[3];
			} else if (x < KNOWN_VIEW_BOUNDRIES[0]) {
				x = KNOWN_VIEW_BOUNDRIES[0];
			}
		}
		return x;
	}
	public double boxY(double y) {
		if (showSolarsystem == SOLARSYSTEM_SHOW_UNKNOWN) {
			if (y > UNKNOWN_VIEW_BOUNDRIES[4]) {
				y = UNKNOWN_VIEW_BOUNDRIES[4];
			} else if (y < UNKNOWN_VIEW_BOUNDRIES[1]) {
				y = UNKNOWN_VIEW_BOUNDRIES[1];
			}
		} else {
			if (y > KNOWN_VIEW_BOUNDRIES[4]) {
				y = KNOWN_VIEW_BOUNDRIES[4];
			} else if (y < KNOWN_VIEW_BOUNDRIES[1]) {
				y = KNOWN_VIEW_BOUNDRIES[1];
			}
		}
		return y;
	}
	public double boxZ(double z) {
		if (showSolarsystem == SOLARSYSTEM_SHOW_UNKNOWN) {
			if (z > UNKNOWN_VIEW_BOUNDRIES[5]) {
				z = UNKNOWN_VIEW_BOUNDRIES[5];
			} else if (z < UNKNOWN_VIEW_BOUNDRIES[2]) {
				z = UNKNOWN_VIEW_BOUNDRIES[2];
			}
		} else {
			if (z > KNOWN_VIEW_BOUNDRIES[5]) {
				z = KNOWN_VIEW_BOUNDRIES[5];
			} else if (z < KNOWN_VIEW_BOUNDRIES[2]) {
				z = KNOWN_VIEW_BOUNDRIES[2];
			}
		}
		return z;
	}
	public void setViewX(double viewX) {
		this.viewX = this.boxX(viewX);
	}
	public double getViewY() {
		return viewY;
	}
	public void setViewY(double viewY) {
		this.viewY = this.boxY(viewY);
	}
	public double getViewZ() {
		return viewZ;
	}
	public void setViewZ(double viewZ) {
		this.viewZ = this.boxZ(viewZ);
	}
	public double getRotateX() {
		return rotateX;
	}
	public void setRotateX(double rotateX) {
		if (rotateX > 359) {
			rotateX = 1;
		}
		if (rotateX < 1) {
			rotateX = 359;
		}
		this.rotateX = rotateX;
	}
	public double getRotateY() {
		return rotateY;
	}
	public void setRotateY(double rotateY) {
		if (rotateY > 359) {
			rotateY = 1;
		}
		if (rotateY < 1) {
			rotateY = 359;
		}
		this.rotateY = rotateY;
	}
	public double getAcceleration() {
		double acceleration = ((double) viewY / 825);
		if (acceleration >= -0.2) {
			acceleration = -0.2;
		}
		return acceleration;
	}
	public boolean rotate3dEnabled() {
		return rotate3dEnabled;
	}
	public void enableRotate3d(boolean rotate3dEnabled) {
		this.rotate3dEnabled = rotate3dEnabled;
		if (!rotate3dEnabled) {
			this.moveView(viewX, viewY, viewZ, 180, 0, 1);
		}
	}
	public boolean showHoverInfoEnabled() {
		return showHoverInfoEnabled;
	}
	public void enableShowHoverInfo(boolean showHoverInfoEnabled) {
		this.showHoverInfoEnabled = showHoverInfoEnabled;
	}
	public boolean showJumpRangeEnabled() {
		return showJumpRangeEnabled;
	}
	public void enableShowJumpRange(boolean showJumpRangeEnabled) {
		this.showJumpRangeEnabled = showJumpRangeEnabled;
	}
	public void saveSettings(Preferences preferences) {
		preferences.putBoolean(Constants.SETTING_ROTATE3D, rotate3dEnabled);
		preferences.putBoolean(Constants.SETTING_FLATTENMAP, flattenEnabled);
		preferences.putBoolean(Constants.SETTING_SHOWHOVERINFO, showHoverInfoEnabled);
		preferences.putBoolean(Constants.SETTING_SHOWJUMPRANGE, showJumpRangeEnabled);
		preferences.putBoolean(Constants.SETTING_SHOWREGIONLABELS, regionLabelsEnabled);
		preferences.putBoolean(Constants.SETTING_SHOWCONSTELLATIONLABELS, conLabelsEnabled);
		preferences.putBoolean(Constants.SETTING_SHOWSOLARSYSTEMLABELS, solLabelsEnabled);
		preferences.putBoolean(Constants.SETTING_SHOWROUTELABELS, routeLabelsEnabled);
		preferences.putInt(Constants.SETTING_SOLARSYSTEMMODE, solarsystemMode);
		preferences.putInt(Constants.SETTING_CONNECTIONMODE, connectionMode);
		preferences.putInt(Constants.SETTING_SHOWSOLARSYSTEM, showSolarsystem);
		preferences.putInt(Constants.SETTING_SHOWCONNECTION, showConnection);
		if (showSolarsystemRegion != null) {
			preferences.putInt(Constants.SETTING_SHOWSOLARSYSTEMREGION, showSolarsystemRegion.getId());
		}
		if (showConnectionRegion != null) {
			preferences.putInt(Constants.SETTING_SHOWCONNECTIONREGION, showConnectionRegion.getId());
		}
	}
	public void loadSettings(Preferences preferences, Locations regions) {
		rotate3dEnabled = preferences.getBoolean(Constants.SETTING_ROTATE3D, rotate3dEnabled);
		flattenEnabled = preferences.getBoolean(Constants.SETTING_FLATTENMAP, flattenEnabled);
		if (flattenEnabled) {
			yScale = 0;
		}
		showHoverInfoEnabled = preferences.getBoolean(Constants.SETTING_SHOWHOVERINFO, showHoverInfoEnabled);
		showJumpRangeEnabled = preferences.getBoolean(Constants.SETTING_SHOWJUMPRANGE, showJumpRangeEnabled);
		regionLabelsEnabled = preferences.getBoolean(Constants.SETTING_SHOWREGIONLABELS, regionLabelsEnabled);
		conLabelsEnabled = preferences.getBoolean(Constants.SETTING_SHOWCONSTELLATIONLABELS, conLabelsEnabled);
		solLabelsEnabled = preferences.getBoolean(Constants.SETTING_SHOWSOLARSYSTEMLABELS, solLabelsEnabled);
		routeLabelsEnabled = preferences.getBoolean(Constants.SETTING_SHOWROUTELABELS, routeLabelsEnabled);
		solarsystemMode = preferences.getInt(Constants.SETTING_SOLARSYSTEMMODE, solarsystemMode);
		connectionMode = preferences.getInt(Constants.SETTING_CONNECTIONMODE, connectionMode);
		showConnection = preferences.getInt(Constants.SETTING_SHOWCONNECTION, showConnection);
		showSolarsystemRegion = (Region) regions.getById(preferences.getInt(Constants.SETTING_SHOWSOLARSYSTEMREGION, 0));
		showConnectionRegion = (Region) regions.getById(preferences.getInt(Constants.SETTING_SHOWCONNECTIONREGION, 0));
		this.setShowSolarsystem(preferences.getInt(Constants.SETTING_SHOWSOLARSYSTEM, showSolarsystem));
	}
	public void moveView(double destX, double destY, double destZ, double destRotX, double destRotY, int speed) {
		if (moveViewThread != null) {
			moveViewThread.cancel();
		}
		moveViewThread = new MoveViewThread(destX, destY, destZ, destRotX, destRotY, speed);
		moveViewThread.start();
	}
	public double getYScale() {
		return yScale;
	}
	public void enableFlatten(boolean enabled) {
		renderingState = RENDER_STOP_LIST;
		this.flattenEnabled = enabled;
		this.startFlattenThread();
	}
	public boolean flattenEnabled() {
		return flattenEnabled;
	}
	public void startFlattenThread() {
		if (flattenThread == null || !flattenThread.isAlive()) {
			flattenThread = new Thread("Map Flattenen Thread") {
				public void run() {
					while ((flattenEnabled && yScale > 0.0d) || (!flattenEnabled && yScale < 1.0d)) {
						if (flattenEnabled) {
							yScale -= 1.0d / Constants.FLATTEN_TIME / Constants.DESIRED_FPS;
							if (yScale < 0.0d) {
								yScale = 0.0d;
							}
						} else {
							yScale += 1.0d / Constants.FLATTEN_TIME / Constants.DESIRED_FPS;
							if (yScale > 1.0d) {
								yScale = 1.0d;
							}
						}
						try {
							Thread.sleep(1000 / Constants.DESIRED_FPS);
						} catch( InterruptedException e) {
							System.out.println(e);
						}
					}
					renderingState = RENDER_START_LIST;
				}
			};
			flattenThread.start();
		}
	}
	private class MoveViewThread extends Thread {
		private double destX, destY, destZ, destRotX, destRotY;
		private int speed;
		private boolean cancel = false;
		
		public MoveViewThread(double destX, double destY, double destZ, double destRotX, double destRotY, int speed) {
			super("MoveViewThread");
			this.destX = destX;
			this.destY = destY;
			this.destZ = destZ;
			this.destRotX = destRotX;
			this.destRotY = destRotY;
			this.speed = speed;
		}
		public void cancel() {
			this.cancel = true;
		}
		public void run() {
			while (!cancel && (destX != viewX || destY != viewY || destZ != viewZ || destRotX != rotateX || destRotY != rotateY)) {
				viewX = Tools.approachValue(viewX, destX, 15 * speed);
				viewY = Tools.approachValue(viewY, destY, 60 * speed);
				viewZ = Tools.approachValue(viewZ, destZ, 15 * speed);
				rotateX = Tools.approachAngle(rotateX, destRotX, 4);
				rotateY = Tools.approachAngle(rotateY, destRotY, 4);
				try {
					Thread.sleep(1000 / Constants.DESIRED_FPS + 10);
				} catch( InterruptedException e) {
					System.out.println(e);
				}
			}
		}
	}
}
