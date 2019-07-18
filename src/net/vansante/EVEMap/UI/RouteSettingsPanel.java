package net.vansante.EVEMap.UI;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Location;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Map.MapControl;
import net.vansante.EVEMap.Route.Route;
import net.vansante.EVEMap.Route.RouteListener;
import net.vansante.EVEMap.Route.Waypoint;
import net.vansante.EVEMap.UI.Menus.SolarsystemMenu;

public class RouteSettingsPanel extends JPanel implements ActionListener, MouseListener, ListSelectionListener, RouteListener {
	
	private final JRadioButton typeGate, typeJump;
	private final ButtonGroup typeGroup;
	
	private final JPanel jumpRangePanel, fuelUsagePanel;
	private final JComboBox rangeShipSelect, rangeCalibrationSelect;
	private final JLabel rangeShipLabel, rangeCalibrationLabel;
	
	private final JComboBox fuelConservationSelect, fuelJumpFreighterSelect, fuelRaceSelect;
	private final JLabel fuelConservationLabel, fuelJumpFreighterLabel, fuelRaceLabel;
	
	private final JComboBox minSecSelect, maxSecSelect;
	
	private final JCheckBox preferStationsCheck;
	
	private final DefaultListModel avoidModel;
	private final JList avoidList;
	private final JButton avoidListRemove, avoidListClear;
	
	private boolean ignoreActions = true;
	
	public RouteSettingsPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel typePanel = Sidebar.createBorderedPanel(this, "Route type", Constants.SIDEBAR_WIDTH - 10, 60);
		typeGroup = new ButtonGroup();
		typeGate = Tools.createRadioButton("Gates", this, typeGroup, KeyEvent.VK_G);
		typeGate.setSelected(true);
		typeJump = Tools.createRadioButton("Jumpdrive", this, typeGroup, KeyEvent.VK_J);
		typePanel.add(typeGate);
		typePanel.add(typeJump);
		
		jumpRangePanel = Sidebar.createBorderedPanel(this, null, Constants.SIDEBAR_WIDTH - 10, 70);
		jumpRangePanel.setLayout(new GridLayout(2, 2, 2, 2));
		rangeShipSelect = new JComboBox(Constants.JUMP_SHIP_TYPES);
		rangeShipSelect.addActionListener(this);
		rangeCalibrationSelect = new JComboBox(Constants.SKILL_LEVELS);
		rangeCalibrationSelect.addActionListener(this);
		rangeShipLabel = new JLabel("Ship type:");
		rangeCalibrationLabel = new JLabel("Jump calibration skill:");
		jumpRangePanel.add(rangeShipLabel);
		jumpRangePanel.add(rangeShipSelect);
		jumpRangePanel.add(rangeCalibrationLabel);
		jumpRangePanel.add(rangeCalibrationSelect);
		
		fuelUsagePanel = Sidebar.createBorderedPanel(this, null, Constants.SIDEBAR_WIDTH - 10, 90);
		fuelUsagePanel.setLayout(new GridLayout(3, 2, 2, 2));
		fuelConservationSelect = new JComboBox(Constants.SKILL_LEVELS);
		fuelConservationSelect.addActionListener(this);
		fuelJumpFreighterSelect = new JComboBox(Constants.SKILL_LEVELS);
		fuelJumpFreighterSelect.addActionListener(this);
		fuelRaceSelect = new JComboBox(Constants.SHIP_RACES);
		fuelRaceSelect.addActionListener(this);
		fuelConservationLabel = new JLabel("Jump conservation skill:");
		fuelJumpFreighterLabel = new JLabel("Jump freighter skill:");
		fuelRaceLabel = new JLabel("Jump freighter race:");
		fuelUsagePanel.add(fuelConservationLabel);
		fuelUsagePanel.add(fuelConservationSelect);
		fuelUsagePanel.add(fuelJumpFreighterLabel);
		fuelUsagePanel.add(fuelJumpFreighterSelect);
		fuelUsagePanel.add(fuelRaceLabel);
		fuelUsagePanel.add(fuelRaceSelect);
		
		JPanel securityPanel = Sidebar.createBorderedPanel(this, "Security status", Constants.SIDEBAR_WIDTH - 10, 70);
		securityPanel.setLayout(new GridLayout(2, 2, 2, 2));
		minSecSelect = new JComboBox(Solarsystem.SECURITY_LEVELS);
		minSecSelect.addActionListener(this);
		maxSecSelect = new JComboBox(Solarsystem.SECURITY_LEVELS);
		maxSecSelect.addActionListener(this);
		maxSecSelect.setSelectedIndex(10);
		JLabel minSecLabel = new JLabel("Minimum security:");
		JLabel maxSecLabel = new JLabel("Maximum security:");
		securityPanel.add(minSecLabel);
		securityPanel.add(minSecSelect);
		securityPanel.add(maxSecLabel);
		securityPanel.add(maxSecSelect);
		
		JPanel otherOptionsPanel = Sidebar.createBorderedPanel(this, "Other options", Constants.SIDEBAR_WIDTH - 10, 60);
		preferStationsCheck = Tools.createCheckBox("Prefer solarsystems with station", this, KeyEvent.VK_S);
		otherOptionsPanel.add(preferStationsCheck);
		
		JPanel avoidListPanel = Sidebar.createBorderedPanel(this, "Avoid solarsystem list", Constants.SIDEBAR_WIDTH - 10, 450, true);
		avoidListPanel.setLayout(new BorderLayout(2, 2));
		avoidModel = new DefaultListModel();
		avoidList = new JList(avoidModel) {
			public String getToolTipText(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index >= 0) {
					return ((Solarsystem) avoidModel.getElementAt(index)).getToolTip();
				}
				return null;
			}
		};
		avoidList.addMouseListener(this);
		avoidList.addListSelectionListener(this);
		avoidList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, object, index, isSelected, cellHasFocus);
				label.setIcon(Tools.getSolarsystemIcon(((Solarsystem) object).getSecurityType()));
				return label;
			}
		});
		avoidList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane resultListScroll = new JScrollPane(avoidList);
		avoidListRemove = Tools.createButton("Remove", "avoid.png", 100, 25, this, KeyEvent.VK_R);
		avoidListRemove.setEnabled(false);
		avoidListClear = Tools.createButton("Clear list", "clear.png", 100, 25, this, KeyEvent.VK_C);
		JPanel southPanel = new JPanel();
		southPanel.add(avoidListRemove);
		southPanel.add(avoidListClear);
		avoidListPanel.add(resultListScroll, BorderLayout.CENTER);
		avoidListPanel.add(southPanel, BorderLayout.SOUTH);

		this.updateSettings();
		this.updateJumpRange();
		this.updateFuelUsage();
		
		Route.get().addRouteListener(this);
		this.avoidListUpdated(Route.get().getAvoidList());
		ignoreActions = false;
	}
	public void valueChanged(ListSelectionEvent lse) {
		boolean enabled = avoidList.getSelectedIndex() != -1;
		avoidListRemove.setEnabled(enabled);
	}
	public void updateSettings() {
		typeGate.setSelected(!Route.get().getJumpRoute());
		typeJump.setSelected(Route.get().getJumpRoute());
		rangeShipSelect.setSelectedIndex(Route.get().getShipType());
		rangeCalibrationSelect.setSelectedIndex(Route.get().getJumpCalibration());
		fuelConservationSelect.setSelectedIndex(Route.get().getJumpConservation());
		fuelJumpFreighterSelect.setSelectedIndex(Route.get().getJumpFreighterSkill());
		fuelRaceSelect.setSelectedIndex(Route.get().getJumpFreighterRace());
		minSecSelect.setSelectedIndex(Route.get().getMinSecurity());
		maxSecSelect.setSelectedIndex(Route.get().getMaxSecurity());
		preferStationsCheck.setSelected(Route.get().getPreferStations());
		this.enableJumpRangePanel(typeJump.isSelected(), rangeShipSelect.getSelectedIndex() == Constants.JUMP_BRIDGE_ID);
		this.enableFuelUsagePanel(typeJump.isSelected(), rangeShipSelect.getSelectedIndex() == Constants.JUMP_FREIGHTER_ID);
	}
	public void avoidListUpdated(List<Solarsystem> avoidList) {
		avoidModel.clear();
		for (Solarsystem solarsystem : Route.get().getAvoidList()) {
			avoidModel.addElement(solarsystem);
		}
	}
	public void waypointsUpdated(List<Waypoint> waypoints) {}
	public void enableJumpRangePanel(boolean enabled, boolean jumpBridge) {
		rangeShipSelect.setEnabled(enabled);
		rangeShipLabel.setEnabled(enabled);
		rangeCalibrationSelect.setEnabled(enabled && !jumpBridge);
		rangeCalibrationLabel.setEnabled(enabled && !jumpBridge);
	}
	public void enableFuelUsagePanel(boolean enabled, boolean jumpFreighter) {
		fuelConservationSelect.setEnabled(enabled);
		fuelConservationLabel.setEnabled(enabled);
		fuelJumpFreighterSelect.setEnabled(enabled && jumpFreighter);
		fuelJumpFreighterLabel.setEnabled(enabled && jumpFreighter);
		fuelRaceSelect.setEnabled(enabled && jumpFreighter);
		fuelRaceLabel.setEnabled(enabled && jumpFreighter);
	}
	public void updateJumpRange() {
		double range = Route.get().getJumpRange() / Constants.LIGHTYEAR * Constants.SCALE + 0.00000000001;
		jumpRangePanel.setBorder(BorderFactory.createTitledBorder("Jump range (" + Tools.round(range, 1000) + " lightyears)"));
	}
	public void updateFuelUsage() {
		fuelUsagePanel.setBorder(BorderFactory.createTitledBorder("Fuel usage (" + Route.get().getFuelUsage() + " isotopes per lightyear)"));
	}
	public void maybeShowPopup(MouseEvent me) {
		if (me.isPopupTrigger()) {
			int index = avoidList.locationToIndex(me.getPoint());
			if (index >= 0) {
				SolarsystemMenu menu = new SolarsystemMenu((Solarsystem) avoidModel.get(index));
				menu.show(me.getComponent(), me.getX(), me.getY());
			}
		}
	}
	public void mousePressed(MouseEvent me) {
		this.maybeShowPopup(me);
	}
	public void mouseReleased(MouseEvent me) {
		this.maybeShowPopup(me);
	}
	public void mouseEntered(MouseEvent me) {}
	public void mouseClicked(MouseEvent me) {
		if (me.getClickCount() == 2) {
			int index = avoidList.locationToIndex(me.getPoint());
			MapControl.get().lookAt((Location) avoidModel.get(index), 1);
		}
	}
	public void mouseExited(MouseEvent me) {}
	public void actionPerformed(ActionEvent e) {
		if (ignoreActions) {
			return;
		}
		Object event = e.getSource();
		if (event == typeGate) {
			Route.get().setJumpRoute(false);
			this.enableJumpRangePanel(false, false);
			this.enableFuelUsagePanel(false, false);
			this.updateJumpRange();
		} else if (event == typeJump) {
			Route.get().setJumpRoute(true);
			this.enableJumpRangePanel(true, rangeShipSelect.getSelectedIndex() == Constants.JUMP_BRIDGE_ID);
			this.enableFuelUsagePanel(true, rangeShipSelect.getSelectedIndex() == Constants.JUMP_FREIGHTER_ID);
		} else if (event == rangeShipSelect) {
			Route.get().setShipType(rangeShipSelect.getSelectedIndex());
			this.updateJumpRange();
			this.updateFuelUsage();
			this.enableJumpRangePanel(true, rangeShipSelect.getSelectedIndex() == Constants.JUMP_BRIDGE_ID);
			this.enableFuelUsagePanel(rangeShipSelect.getSelectedIndex() != Constants.JUMP_BRIDGE_ID,
					rangeShipSelect.getSelectedIndex() == Constants.JUMP_FREIGHTER_ID);
		} else if (event == rangeCalibrationSelect) {
			Route.get().setJumpCalibration(rangeCalibrationSelect.getSelectedIndex());
			this.updateJumpRange();
		} else if (event == fuelConservationSelect) {
			Route.get().setJumpConservation(fuelConservationSelect.getSelectedIndex());
			this.updateFuelUsage();
		} else if (event == fuelJumpFreighterSelect) {
			Route.get().setJumpFreighterSkill(fuelJumpFreighterSelect.getSelectedIndex());
			this.updateFuelUsage();
		} else if (event == fuelRaceSelect) {
			Route.get().setJumpFreighterRace(fuelRaceSelect.getSelectedIndex());
			this.updateFuelUsage();
		} else if (event == minSecSelect) {
			Route.get().setMinSecurity(minSecSelect.getSelectedIndex());
		} else if (event == maxSecSelect) {
			Route.get().setMaxSecurity(maxSecSelect.getSelectedIndex());
		} else if (event == preferStationsCheck) {
			Route.get().setPreferStations(preferStationsCheck.isSelected());
		} else if (event == avoidListRemove) {
			Route.get().removeFromAvoidList(avoidList.getSelectedIndex());
		} else if (event == avoidListClear) {
			Route.get().clearAvoidList();
		}
	}
}