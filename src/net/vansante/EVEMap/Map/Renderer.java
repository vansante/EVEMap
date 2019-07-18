package net.vansante.EVEMap.Map;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Connection;
import net.vansante.EVEMap.Data.Constellation;
import net.vansante.EVEMap.Data.Location;
import net.vansante.EVEMap.Data.Region;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Route.Waypoint;
import net.vansante.EVEMap.Route.Route;

import com.sun.opengl.util.*;
import com.sun.opengl.util.texture.*;

import java.util.*;
import java.nio.*;
import java.io.IOException;

public class Renderer implements GLEventListener {
	
	private final GLU glu;
	private final GLUT glut;
	private final MapControl control;
	
	private double width, height;
	private boolean isReady = false;
	private boolean fullGraphics = false;
	private boolean pointParameters = false, pointSprite = false;
	
	private int solarsystemListId, connectionListId;
	private Texture solarsystemTexture;
	
	private float pointSize = 1.0f;
	private float maxPointSize = 1.0f;
	private float selectPointSize = 1.0f;
	
	private FloatBuffer matrixBuffer = BufferUtil.newFloatBuffer(16);
	private float[] matrix = new float[16];
	
	private IntBuffer selectBuffer = BufferUtil.newIntBuffer(40);
	private int[] selection = new int[40];
	
	private Iterator<Location> locationIterator;
	
	public Renderer(int width, int height) {
		this.control = MapControl.get();
		this.fullGraphics = Main.get().getPreferences().getBoolean(Constants.SETTING_FULLGRAPHICS, false);
		
		this.glu = new GLU();
		this.glut = new GLUT();
	}
	public void stop(GL gl) {
		this.deleteLists(gl);
	}
	public void display(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		if (!isReady) {
			this.doInit(gl);
			this.setupView(gl);
		}
		if (!isReady) {
			return;
		}
		// clear the buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// set the modelview
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glPushMatrix();
		this.transRotate(gl);
		
		if (control.rotate3dEnabled()) {
			this.getViewMatrix(gl);
		}
		
		gl.glPointSize(pointSize);
		gl.glLineWidth(1f);
		
		if (control.getRenderingState() == MapControl.RENDER_STOP_LIST || control.getRenderingState() == MapControl.RENDER_RESET_LIST) {
			this.deleteLists(gl);
		}
		if (control.getRenderingState() == MapControl.RENDER_LIST) {
			gl.glCallList(connectionListId);
			gl.glCallList(solarsystemListId);
		} else if (control.getRenderingState() == MapControl.RENDER_DIRECT || control.getRenderingState() == MapControl.RENDER_STOP_LIST) {
			this.drawConnections(gl, false);
			this.drawSolarsystems(gl, false);
		}
		if (control.getRenderingState() == MapControl.RENDER_START_LIST || control.getRenderingState() == MapControl.RENDER_RESET_LIST) {
			this.drawConnections(gl, true);
			this.drawSolarsystems(gl, true);
			control.setRenderingState(MapControl.RENDER_LIST);
		}
		if (control.getRenderingState() == MapControl.RENDER_STOP_LIST) {
			control.setRenderingState(MapControl.RENDER_DIRECT);
		}
		
		gl.glPointSize(selectPointSize);
		gl.glLineWidth(3f);
		
		this.drawRoute(gl);
		this.drawSolarsystemLabels(gl);
		this.drawConstellationLabels(gl);
		this.drawRegionLabels(gl);
		this.drawHover(gl);
		
		gl.glLineWidth(1f);
		gl.glPopMatrix();
		
		this.draw2D(gl);
		
		if (control.getPickPoint() != null) {
			this.pickSolarsystem(gl);
		}
		
		gl.glFlush();
	}
	private void getViewMatrix(GL gl) {
		gl.glGetFloatv(GL.GL_MODELVIEW_MATRIX, matrixBuffer);
		matrixBuffer.get(matrix);
		matrixBuffer.clear();
	}
	private float[] convertCoordinates(float[] coords, double x, double y, double z) {
		coords[0] = ((float) x) * matrix[0] + ((float) y) * matrix[4] + ((float) z) * matrix[8] + matrix[12];
		coords[1] = ((float) x) * matrix[1] + ((float) y) * matrix[5] + ((float) z) * matrix[9] + matrix[13];
		coords[2] = ((float) x) * matrix[2] + ((float) y) * matrix[6] + ((float) z) * matrix[10] + matrix[14];
		return coords;
	}
	private void deleteLists(GL gl) {
		gl.glDeleteLists(solarsystemListId, 1);
		gl.glDeleteLists(connectionListId, 1);
	}
	private void draw2D(GL gl) {
		// set up the ortho view for 2d drawing
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glOrtho(0, width, height, 0, -1, 1);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		this.drawHoverInfo(gl);

		// set the view back
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glEnable(GL.GL_DEPTH_TEST);
	}
	private void drawSolarsystems(GL gl, boolean generateList) {
		if (generateList) {
			solarsystemListId = gl.glGenLists(1);
			gl.glNewList(solarsystemListId, GL.GL_COMPILE_AND_EXECUTE);
		}
		this.enablePointSprite(gl);
		gl.glBegin(GL.GL_POINTS);
		locationIterator = Main.get().getSolIterator();
		Solarsystem solarsystem;
		int temp;
		while (locationIterator.hasNext()) {
			solarsystem = (Solarsystem) locationIterator.next();
			if ((control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_UNKNOWN && solarsystem.isKnown())
					|| (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_NORMAL && solarsystem.isUnknown())
					|| (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION
					&& (solarsystem.getRegion() != control.getShowSolarsystemRegion() || solarsystem.isUnknown()))) {
				continue;
			}
			if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_NORMAL) {
				this.drawRegularDot(gl, solarsystem, Constants.STAR_COLOR[0], Constants.STAR_COLOR[1], Constants.STAR_COLOR[2]);
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SECURITY) {
				temp = solarsystem.getSecurityType();
				this.drawRegularDot(gl, solarsystem, Constants.SEC_COLORS3F[temp][0], Constants.SEC_COLORS3F[temp][1], Constants.SEC_COLORS3F[temp][2]);
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_ZEROSECURITY) {
				temp = solarsystem.getZeroSecurityType();
				if (temp >= 0) {
					this.drawRegularDot(gl, solarsystem, Constants.SEC_COLORS3F[temp][0], Constants.SEC_COLORS3F[temp][1], Constants.SEC_COLORS3F[temp][2]);
				}
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_CLASS) {
				temp = solarsystem.getClassification();
				this.drawRegularDot(gl, solarsystem, Constants.CLASS_COLORS3F[temp - 1][0], Constants.CLASS_COLORS3F[temp - 1][1], Constants.CLASS_COLORS3F[temp - 1][2]);
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_REGION) {
				this.drawRegularDot(gl, solarsystem, solarsystem.getRegion().getRed(), solarsystem.getRegion().getGreen(), solarsystem.getRegion().getBlue());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_FACTION) {
				if (solarsystem.getRegion().getFaction() != null) {
					this.drawRegularDot(gl, solarsystem, solarsystem.getRegion().getFaction().getRed(), solarsystem.getRegion().getFaction().getGreen(), solarsystem.getRegion().getFaction().getBlue());
				}
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_OCCUPANCY) {
				if (solarsystem.getOccupant() != null) {
					this.drawRegularDot(gl, solarsystem, solarsystem.getOccupant().getRed(), solarsystem.getOccupant().getGreen(), solarsystem.getOccupant().getBlue());
				}
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SOVEREIGNTY) {
				if (solarsystem.getSovereignty() != null) {
					this.drawRegularDot(gl, solarsystem, solarsystem.getSovereignty().getRed(), solarsystem.getSovereignty().getGreen(), solarsystem.getSovereignty().getBlue());
				}
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_CONSOVEREIGNTY) {
				if (solarsystem.getConstellation().getSovereignty() != null) {
					this.drawRegularDot(gl, solarsystem, solarsystem.getConstellation().getSovereignty().getRed(), solarsystem.getConstellation().getSovereignty().getGreen(), solarsystem.getConstellation().getSovereignty().getBlue());
				}
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_STATIONCOUNT) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_STATIONCOUNT, solarsystem.getNumberOfStations());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PLANETCOUNT) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_PLANETCOUNT, solarsystem.getPlanets());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_MOONCOUNT) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_MOONCOUNT, solarsystem.getMoons());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_BELTCOUNT) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_BELTCOUNT, solarsystem.getBelts());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_JUMPS) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_JUMPS, solarsystem.getJumps());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SHIPKILLS) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_SHIPKILLS, solarsystem.getShipKills());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PODKILLS) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_PODKILLS, solarsystem.getPodKills());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_FACTIONKILLS) {
				this.drawSizedDot(gl, solarsystem, 0, Constants.DOT_MAX_FACTIONKILLS, solarsystem.getFactionKills());
			} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_ANOMALIES) {
				temp = solarsystem.getAnomaly();
				if (temp != 0) {
					this.drawRegularDot(gl, solarsystem, Constants.CLASS_COLORS3F[temp][0], Constants.CLASS_COLORS3F[temp][1], Constants.CLASS_COLORS3F[temp][2]);
				}
			}
		}
		gl.glEnd();
		this.disablePointSprite(gl);
		if (generateList) {
			gl.glEndList();
		}
	}
	private void drawConnections(GL gl, boolean generateList) {
		if (control.getShowConnection() == MapControl.CONNECTION_SHOW_NONE) {
			connectionListId = gl.glGenLists(1);
			gl.glNewList(connectionListId, GL.GL_COMPILE_AND_EXECUTE);
			gl.glEndList();
			return;
		}
		Solarsystem solarsystem1, solarsystem2;
		int temp;
		if (generateList) {
			connectionListId = gl.glGenLists(1);
			gl.glNewList(connectionListId, GL.GL_COMPILE_AND_EXECUTE);
		}
		gl.glBegin(GL.GL_LINES);
		for (Connection connection : Main.get().getConnections()) {
			solarsystem1 = connection.getSolarsystem1();
			solarsystem2 = connection.getSolarsystem2();
			if ((control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION // Check if its in a non displayed solarsystem region
					&& solarsystem1.getRegion() != control.getShowSolarsystemRegion()
					&& solarsystem2.getRegion() != control.getShowSolarsystemRegion())
					|| (control.getShowConnection() == MapControl.CONNECTION_SHOW_REGION // Check if its in a non displayed connection region
					&& solarsystem1.getRegion() != control.getShowConnectionRegion()
					&& solarsystem2.getRegion() != control.getShowConnectionRegion())
					|| (control.getShowConnection() == MapControl.CONNECTION_SHOW_HOVER // Check if it is hovered if that is on
					&& (control.getHover() == null || (solarsystem1.getRegion() != control.getHover().getRegion()
					&& solarsystem2.getRegion() != control.getHover().getRegion())))) {
				continue;
			}
			if (control.getConnectionMode() == MapControl.CONNECTION_MODE_JUMPTYPE) {
				if (solarsystem1.getRegion() != solarsystem2.getRegion()) {
					temp = Constants.CONNECTION_REGION;
				} else if (solarsystem1.getConstellation() != solarsystem2.getConstellation()) {
					temp = Constants.CONNECTION_CONSTELLATION;
				} else {
					temp = Constants.CONNECTION_SOLARSYSTEM;
				}
				gl.glColor3f(Constants.CONNECTION_COLORS[temp][0] * 0.3f, Constants.CONNECTION_COLORS[temp][1] * 0.3f, Constants.CONNECTION_COLORS[temp][2] * 0.3f);
				gl.glVertex3d(solarsystem1.getX(), solarsystem1.getY() * control.getYScale(), solarsystem1.getZ());
				gl.glVertex3d(solarsystem2.getX(), solarsystem2.getY() * control.getYScale(), solarsystem2.getZ());
			} else if (control.getConnectionMode() == MapControl.CONNECTION_MODE_SECURITY) {
				temp = solarsystem1.getSecurityType();
				gl.glColor3f(Constants.SEC_COLORS3F[temp][0] * 0.3f, Constants.SEC_COLORS3F[temp][1] * 0.3f, Constants.SEC_COLORS3F[temp][2] * 0.3f);
				gl.glVertex3d(solarsystem1.getX(), solarsystem1.getY() * control.getYScale(), solarsystem1.getZ());
				temp = solarsystem2.getSecurityType();
				gl.glColor3f(Constants.SEC_COLORS3F[temp][0] * 0.3f, Constants.SEC_COLORS3F[temp][1] * 0.3f, Constants.SEC_COLORS3F[temp][2] * 0.3f);
				gl.glVertex3d(solarsystem2.getX(), solarsystem2.getY() * control.getYScale(), solarsystem2.getZ());
			} else if (control.getConnectionMode() == MapControl.CONNECTION_MODE_REGION) {
				gl.glColor3f(solarsystem1.getRegion().getRed() * 0.3f, solarsystem1.getRegion().getGreen() * 0.3f, solarsystem1.getRegion().getBlue() * 0.3f);
				gl.glVertex3d(solarsystem1.getX(), solarsystem1.getY() * control.getYScale(), solarsystem1.getZ());
				gl.glColor3f(solarsystem2.getRegion().getRed() * 0.3f, solarsystem2.getRegion().getGreen() * 0.3f, solarsystem2.getRegion().getBlue() * 0.3f);
				gl.glVertex3d(solarsystem2.getX(), solarsystem2.getY() * control.getYScale(), solarsystem2.getZ());
			}
		}
		gl.glEnd();
		if (generateList) {
			gl.glEndList();
		}
	}
	private void drawRoute(GL gl) {
		if (Route.get().getNumberOfWaypoints() == 0
				|| control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_UNKNOWN) {
			return;
		}
		gl.glColor3f(Constants.ROUTE_COLOR[0] * 0.5f, Constants.ROUTE_COLOR[1] * 0.5f, Constants.ROUTE_COLOR[2] * 0.5f);
		gl.glBegin(GL.GL_LINE_STRIP);
		for (Waypoint waypoint : Route.get()) {
			gl.glVertex3d(waypoint.getSolarsystem().getX(), waypoint.getSolarsystem().getY() * control.getYScale(), waypoint.getSolarsystem().getZ());
		}
		gl.glEnd();
		this.enablePointSprite(gl);
		gl.glColor3f(Constants.ROUTE_COLOR[0], Constants.ROUTE_COLOR[1], Constants.ROUTE_COLOR[2]);
		gl.glBegin(GL.GL_POINTS);
		for (Waypoint waypoint : Route.get()) {
			gl.glVertex3d(waypoint.getSolarsystem().getX(), waypoint.getSolarsystem().getY() * control.getYScale(), waypoint.getSolarsystem().getZ());
		}
		gl.glEnd();
		this.disablePointSprite(gl);
		if (control.routeLabelsEnabled()) {
			for (Waypoint waypoint: Route.get()) {
				this.drawText3D(gl, waypoint.getSolarsystem().getX() + 0.5, waypoint.getSolarsystem().getY() * control.getYScale(),
					waypoint.getSolarsystem().getZ() - 0.5, GLUT.BITMAP_HELVETICA_10, waypoint.getSolarsystem().getName());
			}
		}
	}
	private void drawHover(GL gl) {
		if (control.getHover() == null) {
			return;
		}
		Solarsystem solarsystem = control.getHover();
		if (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION && 
				solarsystem.getRegion() != control.getShowSolarsystemRegion()) {
			return;
		}
		this.enablePointSprite(gl);
		gl.glColor3f(Constants.SELECTION_COLOR[0], Constants.SELECTION_COLOR[1], Constants.SELECTION_COLOR[2]);
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex3d(solarsystem.getX(), solarsystem.getY() * control.getYScale(), solarsystem.getZ());
		gl.glEnd();
		if (control.showJumpRangeEnabled()) {
			gl.glPointSize(selectPointSize / 2.5f);
			gl.glBegin(GL.GL_POINTS);
			List<Solarsystem> solarsystems = Main.get().getSolarsystems().getSolarsystemsByDistance(solarsystem, Route.get().getJumpRange());
			for (Solarsystem currentSolarsystem : solarsystems) {
				gl.glVertex3d(currentSolarsystem.getX(), currentSolarsystem.getY() * control.getYScale(), currentSolarsystem.getZ());
			}
			gl.glPointSize(selectPointSize);
			gl.glEnd();
		}
		this.disablePointSprite(gl);
		this.drawText3D(gl, solarsystem.getX() + 4, solarsystem.getY() * control.getYScale(), solarsystem.getZ(), GLUT.BITMAP_HELVETICA_12, solarsystem.getName());
		if (control.showJumpRangeEnabled() && !control.flattenEnabled()) {
			gl.glColor4f(Constants.SELECTION_COLOR[0], Constants.SELECTION_COLOR[1], Constants.SELECTION_COLOR[2], 0.1f);
			gl.glPushMatrix();
			gl.glTranslated(solarsystem.getX(), solarsystem.getY(), solarsystem.getZ());
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			glut.glutSolidSphere(Route.get().getJumpRange(), 30, 30);
			gl.glDisable(GL.GL_BLEND);
			gl.glPopMatrix();
		}
	}
	private void drawSolarsystemLabels(GL gl) {
		if (!control.solLabelsEnabled() || (!control.rotate3dEnabled() && control.getViewY() < Constants.SOLARSYSTEM_NAMES_LEVEL)) {
			return;
		}
		locationIterator = Main.get().getSolIterator();
		Solarsystem solarsystem;
		double distance = 0;
		float[] coords = new float[3];
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		while (locationIterator.hasNext()) {
			solarsystem = (Solarsystem) locationIterator.next();
			if (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION && 
					solarsystem.getRegion() != control.getShowSolarsystemRegion()) {
				continue;
			}
			if (control.rotate3dEnabled()) {
				coords = this.convertCoordinates(coords, solarsystem.getX(), solarsystem.getY() * control.getYScale(), solarsystem.getZ());
				distance = Tools.calculateDistance(coords[0], coords[1], coords[2], 0, 0, 0);
			} else {
				distance = Tools.calculateDistance(-solarsystem.getX(), solarsystem.getY() * control.getYScale(), solarsystem.getZ(),
						control.getViewX(), control.getViewY(), control.getViewZ());
			}
			if (distance < Constants.SOLARSYSTEM_NAMES_DISTANCE) {
				this.drawText3D(gl, solarsystem.getX() + 0.5, solarsystem.getY() * control.getYScale(), solarsystem.getZ() - 0.5, GLUT.BITMAP_HELVETICA_10, solarsystem.getName());
			}
		}
	}
	private void drawConstellationLabels(GL gl) {
		if (!control.conLabelsEnabled() || (!control.rotate3dEnabled() && (control.getViewY() < Constants.CONSTELLATION_NAMES_LEVEL ||
				control.getViewY() > Constants.SOLARSYSTEM_NAMES_LEVEL))) {
			return;
		}
		double distance;
		float[] coords = new float[3];
		gl.glColor3f(0.75f, 0.75f, 0.75f);
		for (Location constellation : Main.get().getConstellations()) {
			if ((control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION 
					&& ((Constellation)constellation).getRegion() != control.getShowSolarsystemRegion())
					|| (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_UNKNOWN && constellation.isKnown())
					|| (control.getShowSolarsystem() != MapControl.SOLARSYSTEM_SHOW_UNKNOWN && constellation.isUnknown())) {
				continue;
			}
			if (control.rotate3dEnabled()) {
				coords = this.convertCoordinates(coords, constellation.getX(), constellation.getY() * control.getYScale(), constellation.getZ());
				distance = Tools.calculateDistance(coords[0], coords[1], coords[2], 0, 0, 0);
			} else {
				distance = Tools.calculateDistance(-constellation.getX(), constellation.getY() * control.getYScale(), constellation.getZ(),
						control.getViewX(), control.getViewY(), control.getViewZ());
			}
			
			if (distance < Constants.CONSTELLATION_NAMES_DISTANCE && distance > Constants.SOLARSYSTEM_NAMES_LEVEL) {
				this.drawText3D(gl, constellation.getX(), constellation.getY() * control.getYScale(), constellation.getZ(), GLUT.BITMAP_HELVETICA_12, constellation.getName());
			}
		}
	}
	private void drawRegionLabels(GL gl) {
		if (!control.regionLabelsEnabled() || control.getViewY() > Constants.CONSTELLATION_NAMES_LEVEL) {
			return;
		}
		gl.glColor3f(0.85f, 0.85f, 0.85f);
		for (Location region : Main.get().getRegions()) {
			if ((control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION
					&& (Region)region != control.getShowSolarsystemRegion())
					|| (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_UNKNOWN && region.isKnown())
					|| (control.getShowSolarsystem() != MapControl.SOLARSYSTEM_SHOW_UNKNOWN && region.isUnknown())) {
				continue;
			}
			this.drawText3D(gl, region.getX(), region.getY() * control.getYScale(), region.getZ(), GLUT.BITMAP_HELVETICA_18, region.getName());
		}	
	}
	private void drawHoverInfo(GL gl) {
		if (!control.showHoverInfoEnabled() || control.getHover() == null || control.getHover().getType() != Location.TYPE_SOLARSYSTEM) {
			return;
		}
		Solarsystem solarsystem = (Solarsystem) control.getHover();
		
		if (control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION && solarsystem.getRegion() != control.getShowSolarsystemRegion()) {
			return;
		}
		
		gl.glColor3f(0.95f, 0.95f, 0.95f);
		this.drawText2D(gl, 10, 20, GLUT.BITMAP_HELVETICA_18, solarsystem.getName());
		int i = 3;
		this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Constellation:");
		this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Region:");
		this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Security status:");
		if (solarsystem.getRegion().getFaction() != null) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Faction:");
		} else if (solarsystem.getSovereignty() != null) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Sovereignty:");
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Sovereignty level:");
		}
		if (solarsystem.getConstellation().getSovereignty() != null) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Constellation sov.:");
		}
		this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Connected to:");
		i += solarsystem.getNumberOfConnections();
		
		if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_CLASS) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Class:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_OCCUPANCY) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Occupant:");
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Contested:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_STATIONCOUNT) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Station count:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PLANETCOUNT) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Planet count:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_MOONCOUNT) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Moon count:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_BELTCOUNT) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Belt count:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_JUMPS) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Jumps:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SHIPKILLS) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Ship kills:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PODKILLS) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Pod kills:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_FACTIONKILLS) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Faction kills:");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_ANOMALIES) {
			this.drawText2D(gl, 10, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Anomaly:");
		}
		
		i = 3;
		gl.glColor3f(0.8f, 0.8f, 0.8f);

		this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, solarsystem.getConstellation().getName());
		this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, solarsystem.getRegion().getName());
		this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getSecurity());
		if (solarsystem.getRegion().getFaction() != null) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, solarsystem.getRegion().getFaction().getName());
		} else if (solarsystem.getSovereignty() != null) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, solarsystem.getSovereignty().getName());
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getSovereigntyLevel());
		}
		if (solarsystem.getConstellation().getSovereignty() != null) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getConstellation().getSovereignty().getName());
		}
		for (Solarsystem neighbour : solarsystem.getConnections()) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, neighbour.getName());
		}
		i++;
		
		if  (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_CLASS) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getClassification() + " (" + solarsystem.getClassText() + ")");
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_OCCUPANCY) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getOccupant());
			if (solarsystem.isContested()) {
				this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "Yes");
			} else {
				this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "No");
			}
		} else if  (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_STATIONCOUNT) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getNumberOfStations());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PLANETCOUNT) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getPlanets());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_MOONCOUNT) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getMoons());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_BELTCOUNT) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getBelts());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_JUMPS) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getJumps());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SHIPKILLS) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getShipKills());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PODKILLS) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getPodKills());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_FACTIONKILLS) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getFactionKills());
		} else if (control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_ANOMALIES) {
			this.drawText2D(gl, 120, 16 * i++, GLUT.BITMAP_HELVETICA_12, "" + solarsystem.getAnomalyText());
		}
	}
	private void pickSolarsystem(GL gl) {	
		int viewport[] = new int[4];
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glSelectBuffer(100, selectBuffer);
		gl.glRenderMode(GL.GL_SELECT);
		gl.glInitNames();
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluPickMatrix((double) control.getPickPoint().getX(), (double) (viewport[3] - control.getPickPoint().getY()), 10.0, 10.0, viewport, 0);
		this.setupCamera(gl);
		this.transRotate(gl);
		control.setPickPoint(null);
		gl.glPushName(0);
		int i = 0;
		for (Location solarsystem : Main.get().getSolarsystems()) {
			gl.glLoadName(i);
			gl.glBegin(GL.GL_POINTS);
			gl.glVertex3d(solarsystem.getX(), solarsystem.getY() * control.getYScale(), solarsystem.getZ());
			gl.glEnd();
			i++;
		}
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glFlush();
		gl.glPopMatrix();
		int hits = gl.glRenderMode(GL.GL_RENDER);
		selectBuffer.get(selection);
		selectBuffer.clear();
		if (hits > 0) {
			control.setHover((Solarsystem) Main.get().getSolarsystems().get(selection[3]));
		} else {
			control.setHover(null);
		}
	}
	private void drawText2D(GL gl, double x, double y, int font, String text) {
		gl.glRasterPos2d(x, y);
		glut.glutBitmapString(font, text);
	}
	private void drawText3D(GL gl, double x, double y, double z, int font, String text) {
		gl.glRasterPos3d(x, y, z);
		glut.glutBitmapString(font, text);
	}
	private void drawRegularDot(GL gl, Location location, float r, float g, float b) {
		gl.glColor3f(r, g, b);
		gl.glVertex3d(location.getX(), location.getY() * control.getYScale(), location.getZ());
	}
	private void drawSizedDot(GL gl, Location location, int min, int max, int value) {
		if (value <= 0) {
			return;
		}
		int index = (int) (12 * (float) value / (max - min));
		if (index > 11) {
			index = 11;
		}
		gl.glColor3f(Constants.SCALE_COLORS3F[index][0], Constants.SCALE_COLORS3F[index][1], Constants.SCALE_COLORS3F[index][2]);
		gl.glVertex3d(location.getX(), location.getY() * control.getYScale(), location.getZ());
	}
	private void enablePointSprite(GL gl) {
		if (pointSprite) {
			solarsystemTexture.bind();
			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL.GL_POINT_SPRITE_ARB);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
			gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);
		}
	}
	private void disablePointSprite(GL gl) {
		if (pointSprite) {
			gl.glDisable(GL.GL_BLEND);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glDisable(GL.GL_POINT_SPRITE_ARB);
			gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_FALSE);
			solarsystemTexture.disable();
		}
	}
	private void doInit(GL gl) {	
		control.resetView();
		this.doGlInit(gl);
		glu.gluPerspective(60.0f, width / height, 1.0f, 500.0f);
		isReady = true;
	}
	private void doGlInit(GL gl) {
		// set bg to black - and rendering to flat
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL.GL_POINT_SMOOTH);
		gl.glDisable(GL.GL_DITHER);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		gl.glHint(GL.GL_POINT_SMOOTH_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_ALWAYS);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
		float[] distanceScaling = null;
		float min = 1.0f, threshold = 1.0f;
		
		// Point size settings
		if (fullGraphics && gl.isExtensionAvailable("GL_ARB_point_parameters") && gl.isFunctionAvailable("glPointParameterfvARB") && gl.isFunctionAvailable("glPointParameterfARB")) {
			// Get the max point size.
			FloatBuffer pointBuffer = BufferUtil.newFloatBuffer(4);
			gl.glGetFloatv(GL.GL_POINT_SIZE_MAX_ARB, pointBuffer);
			maxPointSize = pointBuffer.get(0);
			pointSize = maxPointSize - 1;
			if (pointSize > 10.0f) {
				pointSize = 10.0f;
			}
			selectPointSize = pointSize * 2;
			if (selectPointSize >= maxPointSize) {
				selectPointSize = maxPointSize - 1;
			}
			distanceScaling =  new float[] { 1.0f, 0.0f, 0.00001f };
			threshold = 1.0f;
			min = 2.0f;
			pointParameters = true;
		} else {
			pointSize = 4f;
			selectPointSize = 8f;
		}
		if (pointParameters && gl.isExtensionAvailable("GL_ARB_point_sprite") && gl.isFunctionAvailable("glTexEnvf")) {
			// Create texture
			try {
				solarsystemTexture = TextureIO.newTexture(Tools.getUrl("images/solarsystem.png"), false, "");
				solarsystemTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				solarsystemTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
				gl.glTexEnvf(GL.GL_POINT_SPRITE_ARB, GL.GL_COORD_REPLACE_ARB, GL.GL_TRUE);
			
				pointSize = maxPointSize - 1;
				if (pointSize > 30.0f) {
					pointSize = 30.0f;
				}
				selectPointSize = pointSize * 5;
				if (selectPointSize >= maxPointSize) {
					selectPointSize = maxPointSize - 1;
				}
				distanceScaling =  new float[] { 1.0f, 0.000009f, 0.000009f };
				threshold = 2.0f;
				min = 3.0f;
				pointSprite = true;
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}
		if (pointParameters) {
			gl.glPointParameterfvARB(GL.GL_POINT_DISTANCE_ATTENUATION_ARB, distanceScaling, 0);
			gl.glPointParameterfARB(GL.GL_POINT_FADE_THRESHOLD_SIZE_ARB, threshold);
			gl.glPointParameterfARB(GL.GL_POINT_SIZE_MIN_ARB, min);
			gl.glPointParameterfARB(GL.GL_POINT_SIZE_MAX_ARB, maxPointSize);
		}
	}
	private void transRotate(GL gl) {
		gl.glTranslated(0, control.getViewY(), 0);
		gl.glRotated(control.getRotateX(), 0, 0, 1);
		gl.glRotated(control.getRotateY(), 1, 0, 0);
		gl.glTranslated(control.getViewX(), 0, -control.getViewZ());
	}
	private void setupView(GL gl) {
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		this.setupCamera(gl);
	}
	private void setupCamera(GL gl) {
		glu.gluPerspective(45.0f, width / height, 5.0, 800000.0);
		glu.gluLookAt(0, 1, 0, 0, 0, 0, 0, 0, 1);
	}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		this.width = (double) width;
		this.height = (double) height;
		// get the GL object	
		GL gl = drawable.getGL();
		// set up the projection (lens for camera)
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, width, height);
		this.setupView(gl);
		this.doGlInit(gl);
	}
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {}
	public void init(GLAutoDrawable drawable) {}
}