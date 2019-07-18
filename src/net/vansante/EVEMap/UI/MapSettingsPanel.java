package net.vansante.EVEMap.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Map.MapControl;

public class MapSettingsPanel extends JPanel implements ActionListener {
	
	private final MapControl control;
	
	private final JToggleButton rotate3DButton, flattenButton;
	
	private final JCheckBox regionLabels, conLabels, solLabels, routeLabels;
	
	private final JCheckBox showHoverInfo, showHoverJumpRange;
	
	private final ButtonGroup solModeGroup;
	private final JRadioButton solModeNormal, solModeSecurity, solModeZeroSecurity, solModeClass, solModeRegion, solModeFaction, solModeOccupancy, solModeSov, solModeConSov;
	private final JRadioButton solModeStationCount, solModePlanetCount, solModeMoonCount, solModeBeltCount;
	private final JRadioButton solModeJumps, solModePodKills, solModeShipKills, solModeFactionKills, solModeAnomalies;
	
	private final ButtonGroup conModeGroup;
	private final JRadioButton conModeJumpType, conModeSecurity, conModeRegion;
	
	private final ButtonGroup solShowGroup;
	private final JRadioButton solShowNormal, solShowUnknown, solShowRegion;
	private final JComboBox solShowRegionSelect;
	
	private final ButtonGroup conShowGroup;
	private final JRadioButton conShowAll, conShowHover, conShowNone, conShowRegion;
	private final JComboBox conShowRegionSelect;
	
	private boolean ignoreActions = true;
	
	public MapSettingsPanel() {
		super();
		this.control = MapControl.get();
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel settingsPanel = Sidebar.createBorderedPanel(this, "Settings", Constants.SIDEBAR_WIDTH - 10, 55);
		rotate3DButton = Tools.createToggleButton("3D Rotate", 100, 20, this, KeyEvent.VK_3);
		flattenButton = Tools.createToggleButton("Flatten", 100, 20, this, KeyEvent.VK_F);
		settingsPanel.add(rotate3DButton);
		settingsPanel.add(flattenButton);
		
		JPanel labelsPanel = Sidebar.createBorderedPanel(this, "Labels", Constants.SIDEBAR_WIDTH - 10, 70);
		labelsPanel.setLayout(new GridLayout(2, 2, 1, 1));
		regionLabels = Tools.createCheckBox("Region", this, KeyEvent.VK_R);
		conLabels = Tools.createCheckBox("Constellation", this, KeyEvent.VK_C);
		solLabels = Tools.createCheckBox("Solarsystem", this, KeyEvent.VK_S);
		routeLabels = Tools.createCheckBox("Route", this, KeyEvent.VK_O);
		labelsPanel.add(regionLabels);
		labelsPanel.add(conLabels);
		labelsPanel.add(solLabels);
		labelsPanel.add(routeLabels);
		
		JPanel hoverPanel = Sidebar.createBorderedPanel(this, "Mouse hover", Constants.SIDEBAR_WIDTH - 10, 50);
		hoverPanel.setLayout(new GridLayout(1, 2, 1, 1));
		showHoverInfo = Tools.createCheckBox("Show hover info", this, KeyEvent.VK_I);
		showHoverJumpRange = Tools.createCheckBox("Show jumprange", this, KeyEvent.VK_J);
		hoverPanel.add(showHoverInfo);
		hoverPanel.add(showHoverJumpRange);
		
		JPanel solarsystemShowPanel = Sidebar.createBorderedPanel(this, "Show solarsystems", Constants.SIDEBAR_WIDTH - 10, 80);
		solarsystemShowPanel.setLayout(new GridLayout(2, 2, 1, 1));
		solShowGroup = new ButtonGroup();
		solShowNormal = Tools.createRadioButton("Normal space", this, solShowGroup, KeyEvent.VK_N);
		solShowNormal.setSelected(true);
		solShowUnknown = Tools.createRadioButton("Unknown space", this, solShowGroup, KeyEvent.VK_U);
		solShowRegion = Tools.createRadioButton("From region:", this, solShowGroup, 0);
		solShowRegionSelect = new JComboBox(Main.get().getRegions().toKnownArray());
		solShowRegionSelect.addActionListener(this);
		JPanel solShowRegionSelectPanel = new JPanel();
		solShowRegionSelectPanel.add(solShowRegionSelect);
		solarsystemShowPanel.add(solShowNormal);
		solarsystemShowPanel.add(solShowUnknown);
		solarsystemShowPanel.add(solShowRegion);
		solarsystemShowPanel.add(solShowRegionSelectPanel);
		
		JPanel connectionShowPanel = Sidebar.createBorderedPanel(this, "Show connections", Constants.SIDEBAR_WIDTH - 10, 104);
		connectionShowPanel.setLayout(new GridLayout(3, 2, 1, 1));
		conShowGroup = new ButtonGroup();
		conShowAll = Tools.createRadioButton("All", this, conShowGroup, KeyEvent.VK_A);
		conShowAll.setSelected(true);
		conShowHover = Tools.createRadioButton("Hovered regions", this, conShowGroup, KeyEvent.VK_V);
		conShowNone = Tools.createRadioButton("None", this, conShowGroup, KeyEvent.VK_E);
		conShowRegion = Tools.createRadioButton("From region:", this, conShowGroup, 0);
		conShowRegionSelect = new JComboBox(Main.get().getRegions().toKnownArray());
		conShowRegionSelect.addActionListener(this);
		JPanel conShowRegionSelectPanel = new JPanel();
		conShowRegionSelectPanel.add(conShowRegionSelect);
		connectionShowPanel.add(conShowAll);
		connectionShowPanel.add(conShowHover);
		connectionShowPanel.add(conShowNone);
		connectionShowPanel.add(new JPanel());
		connectionShowPanel.add(conShowRegion);
		connectionShowPanel.add(conShowRegionSelectPanel);
		
		JPanel solarsystemModePanel = Sidebar.createBorderedPanel(this, "Color solarsystems by", Constants.SIDEBAR_WIDTH - 10, 180);
		solarsystemModePanel.setLayout(new GridLayout(9, 2, 1, 1));
		solModeGroup = new ButtonGroup();
		solModeNormal = Tools.createRadioButton("Actual color", this, solModeGroup, KeyEvent.VK_C);
		solModeNormal.setSelected(true);
		solModeSecurity = Tools.createRadioButton("Security status", this, solModeGroup, KeyEvent.VK_Y);
		solModeZeroSecurity = Tools.createRadioButton("0.0 Security", this, solModeGroup, KeyEvent.VK_0);
		solModeClass = Tools.createRadioButton("Class", this, solModeGroup, KeyEvent.VK_L);
		solModeRegion = Tools.createRadioButton("Region", this, solModeGroup, 0);
		solModeFaction = Tools.createRadioButton("Faction", this, solModeGroup, 0);
		solModeOccupancy = Tools.createRadioButton("Occupancy", this, solModeGroup, 0);
		solModeSov = Tools.createRadioButton("Sovereignty", this, solModeGroup, 0);
		solModeConSov = Tools.createRadioButton("Constellation sov.", this, solModeGroup, 0);
		solModeStationCount = Tools.createRadioButton("Station count", this, solModeGroup, 0);
		solModePlanetCount = Tools.createRadioButton("Planet count", this, solModeGroup, 0);
		solModeMoonCount = Tools.createRadioButton("Moon count", this, solModeGroup, 0);
		solModeBeltCount = Tools.createRadioButton("Belt count", this, solModeGroup, 0);
		solModeJumps = Tools.createRadioButton("Jumps", this, solModeGroup, 0);
		solModePodKills = Tools.createRadioButton("Pod kills", this, solModeGroup, 0);
		solModeShipKills = Tools.createRadioButton("Ship kills", this, solModeGroup, 0);
		solModeFactionKills = Tools.createRadioButton("Faction kills", this, solModeGroup, 0);
		solModeAnomalies = Tools.createRadioButton("Anomalies", this, solModeGroup, 0);
		solarsystemModePanel.add(solModeNormal);
		solarsystemModePanel.add(solModeSecurity);
		solarsystemModePanel.add(solModeZeroSecurity);
		solarsystemModePanel.add(solModeClass);
		solarsystemModePanel.add(solModeRegion);
		solarsystemModePanel.add(solModeFaction);
		solarsystemModePanel.add(solModeOccupancy);
		solarsystemModePanel.add(solModeSov);
		solarsystemModePanel.add(solModeConSov);
		solarsystemModePanel.add(solModeStationCount);
		solarsystemModePanel.add(solModePlanetCount);
		solarsystemModePanel.add(solModeMoonCount);
		solarsystemModePanel.add(solModeBeltCount);
		solarsystemModePanel.add(solModeJumps);
		solarsystemModePanel.add(solModePodKills);
		solarsystemModePanel.add(solModeShipKills);
		solarsystemModePanel.add(solModeFactionKills);
		solarsystemModePanel.add(solModeAnomalies);
		
		JPanel connectionModePanel = Sidebar.createBorderedPanel(this, "Color connections by", Constants.SIDEBAR_WIDTH - 10, 70);
		connectionModePanel.setLayout(new GridLayout(2, 2, 1, 1));
		conModeGroup = new ButtonGroup();
		conModeJumpType = Tools.createRadioButton("Jump type", this, conModeGroup, 0);
		conModeJumpType.setSelected(true);
		conModeSecurity = Tools.createRadioButton("Security status", this, conModeGroup, 0);
		conModeRegion = Tools.createRadioButton("Region", this, conModeGroup, 0);
		connectionModePanel.add(conModeJumpType);
		connectionModePanel.add(conModeSecurity);
		connectionModePanel.add(conModeRegion);
		
		this.add(Box.createVerticalGlue());
		this.updateSettings();
		
		ignoreActions = false;
	}
	public void updateSettings() {
		showHoverInfo.setSelected(control.showHoverInfoEnabled());
		showHoverJumpRange.setSelected(control.showJumpRangeEnabled());
		regionLabels.setSelected(control.regionLabelsEnabled());
		conLabels.setSelected(control.conLabelsEnabled());
		solLabels.setSelected(control.solLabelsEnabled());
		routeLabels.setSelected(control.routeLabelsEnabled());
		rotate3DButton.setSelected(control.rotate3dEnabled());
		flattenButton.setSelected(control.flattenEnabled());
		regionLabels.setSelected(control.regionLabelsEnabled());
		conLabels.setSelected(control.conLabelsEnabled());
		solLabels.setSelected(control.solLabelsEnabled());
		
		solModeNormal.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_NORMAL);
		solModeSecurity.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SECURITY);
		solModeZeroSecurity.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_ZEROSECURITY);
		solModeClass.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_CLASS);
		solModeRegion.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_REGION);
		solModeFaction.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_FACTION);
		solModeOccupancy.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_OCCUPANCY);
		solModeSov.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SOVEREIGNTY);
		solModeConSov.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_CONSOVEREIGNTY);
		solModeStationCount.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_STATIONCOUNT);
		solModePlanetCount.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PLANETCOUNT);
		solModeMoonCount.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_MOONCOUNT);
		solModeBeltCount.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_BELTCOUNT);
		solModeJumps.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_JUMPS);
		solModePodKills.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_PODKILLS);
		solModeShipKills.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_SHIPKILLS);
		solModeFactionKills.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_FACTIONKILLS);
		solModeFactionKills.setSelected(control.getSolarsystemMode() == MapControl.SOLARSYSTEM_MODE_ANOMALIES);
		
		conModeSecurity.setSelected(control.getConnectionMode() == MapControl.CONNECTION_MODE_SECURITY);
		conModeJumpType.setSelected(control.getConnectionMode() == MapControl.CONNECTION_MODE_JUMPTYPE);
		conModeRegion.setSelected(control.getConnectionMode() == MapControl.CONNECTION_MODE_REGION);
		
		solShowNormal.setSelected(control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_NORMAL);
		solShowUnknown.setSelected(control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_UNKNOWN);
		solShowRegion.setSelected(control.getShowSolarsystem() == MapControl.SOLARSYSTEM_SHOW_REGION);
		solShowRegionSelect.setSelectedItem(control.getShowSolarsystemRegion());
		
		conShowAll.setSelected(control.getShowConnection() == MapControl.CONNECTION_SHOW_ALL);
		conShowHover.setSelected(control.getShowConnection() == MapControl.CONNECTION_SHOW_HOVER);
		conShowNone.setSelected(control.getShowConnection() == MapControl.CONNECTION_SHOW_NONE);
		conShowRegion.setSelected(control.getShowConnection() == MapControl.CONNECTION_SHOW_REGION);
		conShowRegionSelect.setSelectedItem(control.getShowConnectionRegion());
	}
	public void actionPerformed(ActionEvent e) {
		if (ignoreActions) {
			return;
		}
		Object event = e.getSource();
		if (event == rotate3DButton) {
			control.enableRotate3d(rotate3DButton.isSelected());
		} else if (event == flattenButton) {
			control.enableFlatten(flattenButton.isSelected());
		} else if (event == regionLabels) {
			control.enableRegionLabels(regionLabels.isSelected());
		} else if (event == conLabels) {
			control.enableConLabels(conLabels.isSelected());
		} else if (event == solLabels) {
			control.enableSolLabels(solLabels.isSelected());
		} else if (event == routeLabels) {
			control.enableRouteLabels(routeLabels.isSelected());
		} else if (event == showHoverInfo) {
			control.enableShowHoverInfo(showHoverInfo.isSelected());
		} else if (event == showHoverJumpRange) {
			control.enableShowJumpRange(showHoverJumpRange.isSelected());
		} else if (event == solModeNormal) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_NORMAL);
		} else if (event == solModeSecurity) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_SECURITY);
		} else if (event == solModeZeroSecurity) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_ZEROSECURITY);
		} else if (event == solModeClass) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_CLASS);
		} else if (event == solModeRegion) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_REGION);
		} else if (event == solModeFaction) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_FACTION);
		} else if (event == solModeOccupancy) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_OCCUPANCY);
		} else if (event == solModeSov) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_SOVEREIGNTY);
		} else if (event == solModeConSov) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_CONSOVEREIGNTY);
		} else if (event == solModeStationCount) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_STATIONCOUNT);
		} else if (event == solModePlanetCount) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_PLANETCOUNT);
		} else if (event == solModeMoonCount) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_MOONCOUNT);
		} else if (event == solModeBeltCount) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_BELTCOUNT);
		} else if (event == solModeJumps) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_JUMPS);
		} else if (event == solModeShipKills) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_SHIPKILLS);
		} else if (event == solModePodKills) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_PODKILLS);
		} else if (event == solModeFactionKills) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_FACTIONKILLS);
		} else if (event == solModeAnomalies) {
			control.setSolarsystemMode(MapControl.SOLARSYSTEM_MODE_ANOMALIES);
		} else if (event == conModeJumpType) {
			control.setConnectionMode(MapControl.CONNECTION_MODE_JUMPTYPE);
		} else if (event == conModeSecurity) {
			control.setConnectionMode(MapControl.CONNECTION_MODE_SECURITY);
		} else if (event == conModeRegion) {
			control.setConnectionMode(MapControl.CONNECTION_MODE_REGION);
		} else if (event == solShowNormal) {
			control.setShowSolarsystem(MapControl.SOLARSYSTEM_SHOW_NORMAL);
		} else if (event == solShowUnknown) {
			control.setShowSolarsystem(MapControl.SOLARSYSTEM_SHOW_UNKNOWN);
		} else if (event == solShowRegion) {
			control.setShowSolarsystem(MapControl.SOLARSYSTEM_SHOW_REGION);
			if (solShowRegionSelect.getSelectedIndex() >= 0) {
				control.setShowSolarsystemRegion(Main.get().getRegion(solShowRegionSelect.getSelectedIndex()));
			}
		} else if (event == solShowRegionSelect) {
			solShowRegion.setSelected(true);
			control.setShowSolarsystemRegion(Main.get().getRegion(solShowRegionSelect.getSelectedIndex()));
			control.setShowSolarsystem(MapControl.SOLARSYSTEM_SHOW_REGION);
		} else if (event == conShowAll) {
			control.setShowConnection(MapControl.CONNECTION_SHOW_ALL);
		} else if (event == conShowHover) {
			control.setShowConnection(MapControl.CONNECTION_SHOW_HOVER);
		} else if (event == conShowNone) {
			control.setShowConnection(MapControl.CONNECTION_SHOW_NONE);
		} else if (event == conShowRegion) {
			control.setShowConnection(MapControl.CONNECTION_SHOW_REGION);
			if (conShowRegionSelect.getSelectedIndex() >= 0) {
				control.setShowConnectionRegion(Main.get().getRegion(conShowRegionSelect.getSelectedIndex()));
			}
		} else if (event == conShowRegionSelect) {
			conShowRegion.setSelected(true);
			control.setShowConnectionRegion(Main.get().getRegion(conShowRegionSelect.getSelectedIndex()));
			control.setShowConnection(MapControl.CONNECTION_SHOW_REGION);
		}
	}
}