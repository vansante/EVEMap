package net.vansante.EVEMap.UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Location;
import net.vansante.EVEMap.Data.Locations;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Map.MapControl;
import net.vansante.EVEMap.Route.Route;
import net.vansante.EVEMap.UI.Menus.SolarsystemMenu;

public class SearchPanel extends JPanel implements ActionListener, MouseListener, ListSelectionListener {
	
	private final JTextField searchField;
	private final JButton searchButton, showOnMapButton, addWaypointButton;
	private final JList resultList;
	private final DefaultListModel resultModel;
	private final Locations results;
	
	public SearchPanel() {
		super();
		
		results = new Locations(3);
		
		this.setLayout(new BorderLayout(2, 2));
		
		JPanel northPanel = new JPanel(new BorderLayout(2, 2));
		searchField = Tools.createTextField(150, 25, this);
		searchButton = Tools.createButton("Search", null, 100, 25, this, KeyEvent.VK_S);

		northPanel.add(searchField, BorderLayout.CENTER);
		northPanel.add(searchButton, BorderLayout.EAST);
		
		this.add(northPanel, BorderLayout.NORTH);
		
		resultModel = new DefaultListModel();
		resultList = new JList(resultModel) {
			public String getToolTipText(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				if (index >= 0) {
					return ((Solarsystem) resultModel.getElementAt(index)).getToolTip();
				}
				return null;
			}
		};
		resultList.addMouseListener(this);
		resultList.addListSelectionListener(this);
		resultList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
				JLabel label = (JLabel) super.getListCellRendererComponent(list, object, index, isSelected, cellHasFocus);
				label.setIcon(Tools.getSolarsystemIcon(((Solarsystem) object).getSecurityType()));
				return label;
			}
		});
		resultList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane resultListScroll = new JScrollPane(resultList);
		
		this.add(resultListScroll, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		showOnMapButton = Tools.createButton("Show on map", "zoom.png", 120, 25, this, KeyEvent.VK_S);
		showOnMapButton.setEnabled(false);
		addWaypointButton = Tools.createButton("Add waypoint", "waypoint.png", 120, 25, this, KeyEvent.VK_A);
		addWaypointButton.setEnabled(false);
		southPanel.add(showOnMapButton);
		southPanel.add(addWaypointButton);
		
		this.add(southPanel, BorderLayout.SOUTH);
	}
	public void valueChanged(ListSelectionEvent lse) {
		boolean enabled = resultList.getSelectedIndex() != -1;
		showOnMapButton.setEnabled(enabled);
		addWaypointButton.setEnabled(enabled);
	}
	public void maybeShowPopup(MouseEvent me) {
		if (me.isPopupTrigger()) {
			int index = resultList.locationToIndex(me.getPoint());
			if (index >= 0) {
				SolarsystemMenu menu = new SolarsystemMenu((Solarsystem) resultModel.get(index));
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
			int index = resultList.locationToIndex(me.getPoint());
			MapControl.get().lookAt((Location) resultModel.get(index), 1);
		}
	}
	public void mouseExited(MouseEvent me) {}
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == searchField || event == searchButton) {
			results.clear();
			resultModel.clear();
			results.addAll(Main.get().getSolarsystems().searchByName(searchField.getText()));
			
			for (Location result : results) {
				resultModel.addElement(result);
			}
		} else if (event == showOnMapButton) {
			MapControl.get().lookAt(results.get(resultList.getSelectedIndex()), 1);
		} else if (event == addWaypointButton) {
			Route.get().addWaypoint((Solarsystem)results.get(resultList.getSelectedIndex()));
		}
	}
}