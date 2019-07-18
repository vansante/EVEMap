package net.vansante.EVEMap.UI.Menus;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Map.MapControl;

public class Menubar extends JMenuBar implements ActionListener {
	
	private final JMenu menuFile, menuView, menuSelectDevice, menuScale, menuNavigate, menuHelp;
	private final JMenuItem mExit, mHideSidebar, mFullscreen, mZoomIn, mZoomOut, mResetView;
	private final JMenuItem mMoveNorth, mMoveEast, mMoveSouth, mMoveWest, mAbout, mVisitSite, mVisitThread;
	
	private final JCheckBox mGraphicsEnabled;
	
	private ButtonGroup deviceGroup;
	private JRadioButton[] deviceOptions;
	
	public Menubar() {
		super();
		
		menuFile = this.createMenu(this, "File", KeyEvent.VK_F);
		mExit = this.createMenuItem(menuFile, "Exit", null, KeyEvent.VK_E, 3, KeyEvent.VK_F4);
		
		menuView = this.createMenu(this, "View", KeyEvent.VK_V);
		mHideSidebar = this.createMenuItem(menuView, "Hide sidebar", null, KeyEvent.VK_S, 1, KeyEvent.VK_ESCAPE);
		mFullscreen = this.createMenuItem(menuView, "Fullscreen", null, KeyEvent.VK_F, 3, KeyEvent.VK_ENTER);
		menuView.addSeparator();
		mGraphicsEnabled = Tools.createCheckBox("Enable full graphics", this, KeyEvent.VK_G);
		mGraphicsEnabled.setSelected(Main.get().getPreferences().getBoolean(Constants.SETTING_FULLGRAPHICS, false));
		menuView.add(mGraphicsEnabled);
		menuView.addSeparator();		
		menuSelectDevice = this.createMenu(menuView, "Select fullscreen monitor", 0);
		
		menuScale = this.createMenu(this, "Scale", KeyEvent.VK_S);
		mZoomIn = this.createMenuItem(menuScale, "Zoom in", "zoom_in.png", KeyEvent.VK_I, 1, KeyEvent.VK_ADD);
		mZoomOut = this.createMenuItem(menuScale, "Zoom out", "zoom_out.png", KeyEvent.VK_O, 1, KeyEvent.VK_SUBTRACT);
		mResetView = this.createMenuItem(menuScale, "Reset view", "zoom.png", KeyEvent.VK_R, 1, KeyEvent.VK_MULTIPLY);

		menuNavigate = this.createMenu(this, "Navigate", KeyEvent.VK_N);
		mMoveNorth = this.createMenuItem(menuNavigate, "Move north", "arrow_up.png", KeyEvent.VK_N, 1, KeyEvent.VK_UP);
		mMoveEast = this.createMenuItem(menuNavigate, "Move east", "arrow_right.png", KeyEvent.VK_E, 1, KeyEvent.VK_RIGHT);
		mMoveSouth = this.createMenuItem(menuNavigate, "Move south", "arrow_down.png", KeyEvent.VK_S, 1, KeyEvent.VK_DOWN);
		mMoveWest = this.createMenuItem(menuNavigate, "Move west", "arrow_left.png", KeyEvent.VK_W, 1, KeyEvent.VK_LEFT);
		
		menuHelp = this.createMenu(this, "Help", KeyEvent.VK_H);
		mVisitSite = this.createMenuItem(menuHelp, "Visit EVEMap site", null, KeyEvent.VK_A, 0, 0);
		mVisitThread = this.createMenuItem(menuHelp, "Visit EVEMap's EVE-Online thread", null, KeyEvent.VK_A, 0, 0);
		mAbout = this.createMenuItem(menuHelp, "About", null, KeyEvent.VK_A, 0, 0);
		
		this.updateMonitorMenu();
	}
	public void updateMonitorMenu() {
		menuSelectDevice.removeAll();
		deviceGroup = new ButtonGroup();
		GraphicsDevice[] devices = Main.get().getGraphicsDevices();
		deviceOptions = new JRadioButton[devices.length];
		for (int i = 0; i < devices.length; i++) {
			deviceOptions[i] = new JRadioButton(devices[i].getIDstring());
			deviceOptions[i].addActionListener(this);
			if (Main.get().getCurrentGraphicsDevice() == devices[i]) {
				deviceOptions[i].setSelected(true);
			}
			deviceGroup.add(deviceOptions[i]);
			menuSelectDevice.add(deviceOptions[i]);
		}
	}
	public JMenu createMenu(JComponent target, String text, int mnemonic) {
		JMenu menu = new JMenu(text);
		menu.setMnemonic(mnemonic);
		target.add(menu);
		return menu;
	}
	public JMenuItem createMenuItem(JComponent target, String text, String icon, int mnemonic, int acceleratorUse, int accelerator) {
		JMenuItem menuItem = new JMenuItem(text, mnemonic);
		menuItem.addActionListener(this);
		if (icon != null) {
			menuItem.setIcon(Tools.getIcon(icon));
		}
		if (acceleratorUse == 1) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, 0));
		} else if (acceleratorUse == 2) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, ActionEvent.CTRL_MASK));
		} else if (acceleratorUse == 3) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(accelerator, ActionEvent.ALT_MASK));
		}
		target.add(menuItem);
		return menuItem;
	}
	public void actionPerformed(ActionEvent e) {
		// Get the source menu item
		Object event = e.getSource();
		// Do the action for the menu item
		if (event == mExit) {
			Main.get().exit();
		} else if (event == mHideSidebar) {
			Main.get().toggleSidebar();
		} else if (event == mFullscreen) {
			Main.get().toggleFullscreen();
		} else if (event == mGraphicsEnabled) {
			Main.get().getPreferences().putBoolean(Constants.SETTING_FULLGRAPHICS, mGraphicsEnabled.isSelected());
			JOptionPane.showMessageDialog(Main.get(), "Toggling this option requires a program restart to take effect.", "Notice", JOptionPane.INFORMATION_MESSAGE);
		} else if (event == mZoomIn) {
			MapControl.get().zoomIn();
		} else if (event == mZoomOut) {
			MapControl.get().zoomOut();
		} else if (event == mResetView) {
			MapControl.get().resetView();
		} else if (event == mMoveNorth) {
			MapControl.get().moveNorth();
		} else if (event == mMoveEast) {
			MapControl.get().moveEast();
		} else if (event == mMoveSouth) {
			MapControl.get().moveSouth();
		} else if (event == mMoveWest) {
			MapControl.get().moveWest();
		} else if (event == mVisitSite) {
			Tools.openURL(Constants.WEBSITE_URL);
		} else if (event == mVisitThread) {
			Tools.openURL(Constants.EVE_ONLINE_THREAD_URL);
		} else if (event == mAbout) {
			Main.get().showAbout();
		} else {
			for (int i = 0; i < deviceOptions.length; i++) {
				if (event == deviceOptions[i]) {
					Main.get().setCurrentGraphicsDevice(i);
				}
			}
		}
	}
}
