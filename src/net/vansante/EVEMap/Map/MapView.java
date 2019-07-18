package net.vansante.EVEMap.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.UI.Menus.BaseMenu;
import net.vansante.EVEMap.UI.Menus.SolarsystemMenu;

import com.sun.opengl.util.FPSAnimator;

public class MapView extends GLCanvas implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	private final FPSAnimator animator;
	private final Renderer renderer;
	private final MapControl control;
	
	private final Cursor crosshairCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
	private final Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
	private final Cursor moveCursor = new Cursor(Cursor.MOVE_CURSOR);
	private final Cursor zoomCursor = new Cursor(Cursor.S_RESIZE_CURSOR);
	
	private int mouseButtonsPressed = 0;
	private int startX, startY;
	
	private boolean keyListenerEnabled = false;
	
	public MapView(GLCapabilities capabilities) {
		super(capabilities);
		
		this.animator = new FPSAnimator(this, Constants.DESIRED_FPS);
		this.renderer = new Renderer(this.getWidth(), this.getHeight());
		this.control = MapControl.get();
		
		this.addGLEventListener(renderer);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);
		this.addKeyListener(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				animator.start();
			}
		});
	}
	public void setKeyListenerEnabled(boolean enabled) {
		this.keyListenerEnabled = enabled;
	}
	public void keyPressed(KeyEvent ke) {
		if (!keyListenerEnabled) {
			return;
		}
		ke.consume();
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
			Main.get().toggleSidebar();
		} else if (ke.getKeyCode() == KeyEvent.VK_ENTER && ke.isAltDown()) {
			Main.get().toggleFullscreen();
		}
	}
	public void keyReleased(KeyEvent ke) {}
	public void keyTyped(KeyEvent ke) {}
	public void maybeShowPopup(MouseEvent me) {
		if (me.isMetaDown()) {
			if (control.getHover() != null) {
				SolarsystemMenu menu = new SolarsystemMenu(control.getHover());
				menu.show(me.getComponent(), me.getX(), me.getY());
			} else {
				BaseMenu menu = new BaseMenu();
				menu.show(me.getComponent(), me.getX(), me.getY());
			}
		}
	}
	public void mouseClicked(MouseEvent me) {
		this.maybeShowPopup(me);
	}
	public void mouseEntered(MouseEvent me) {
		this.setCursor(crosshairCursor);
	}
	public void mouseExited(MouseEvent me) {
		this.setCursor(defaultCursor);
	}
	public void mousePressed(MouseEvent me) {
		startX = me.getX();
		startY = me.getY();
		mouseButtonsPressed++;
	}
	public void mouseReleased(MouseEvent me) {
		mouseButtonsPressed--;
	}
	public void mouseMoved(MouseEvent me) {
		if (control.getShowConnection() == MapControl.CONNECTION_SHOW_HOVER) {
			control.setRenderingState(MapControl.RENDER_DIRECT);
		}
		this.setCursor(crosshairCursor);
		control.setPickPoint(me.getPoint());
		mouseButtonsPressed = 0;
	}
	public void mouseDragged(MouseEvent me) {
		if (mouseButtonsPressed == 1) {
			this.setCursor(moveCursor);
			if (me.isMetaDown() || !control.rotate3dEnabled()) {
				control.setViewX(control.getViewX() - ((me.getX() - startX) * control.getAcceleration()));
				control.setViewZ(control.getViewZ() + ((me.getY() - startY) * -control.getAcceleration()));
			} else {
				control.setRotateX(control.getRotateX() + ((double) (me.getX() - startX) / 5));
				control.setRotateY(control.getRotateY() + ((double) (me.getY() - startY) / 5));
			}
		} else if (mouseButtonsPressed == 2) {
			control.setViewY(control.getViewY() - ((me.getY() - startY) * 2 * control.getAcceleration()));
			this.setCursor(zoomCursor);
		}
		startX = me.getX();
		startY = me.getY();
	}
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		control.setViewY(control.getViewY() + mwe.getWheelRotation() * -100 * control.getAcceleration());
	}
	public void stop() {
		renderer.stop(this.getGL());
	}
}
