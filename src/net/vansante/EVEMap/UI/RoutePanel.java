package net.vansante.EVEMap.UI;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import net.vansante.EVEMap.Tools;
import net.vansante.EVEMap.Data.Solarsystem;
import net.vansante.EVEMap.Route.Route;
import net.vansante.EVEMap.Route.RouteListener;
import net.vansante.EVEMap.Route.Waypoint;
import net.vansante.EVEMap.UI.Menus.SolarsystemMenu;
import net.vansante.EVEMap.UI.Menus.WaypointMenu;

import java.util.*;

public class RoutePanel extends JPanel implements ActionListener, MouseListener, TreeSelectionListener, RouteListener {
	
	private final Route route;
	private final JTree tree;
	private final WaypointModel treeModel;
	private final JButton removeWaypointButton, clearRouteButton;
	
	public RoutePanel() {
		super();
		this.route = Route.get();
		
		this.setLayout(new BorderLayout(2, 2));
		
		treeModel = new WaypointModel();
		tree = new JTree(treeModel) {
			public String getToolTipText(MouseEvent e) {
				TreePath treePath = getPathForLocation(e.getX(), e.getY());
				if (treePath != null && treePath.getPathCount() == 2) {
					return route.getWaypointToolTip((Waypoint) treePath.getPathComponent(1));
				} else if (treePath != null && treePath.getPathCount() == 3) {
					return ((Solarsystem) treePath.getPathComponent(2)).getToolTip();
				}
				return null;
			}
		};
		tree.setToolTipText("");
		tree.setCellRenderer(new WaypointTreeRenderer());
		tree.addMouseListener(this);
		tree.addTreeSelectionListener(this);
		tree.setEditable(false);
		
		JScrollPane treeScroll = new JScrollPane(tree);
		Dimension size = tree.getPreferredScrollableViewportSize();
		size.width = Short.MAX_VALUE;
		treeScroll.setMaximumSize(size);
		this.add(treeScroll, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		removeWaypointButton = Tools.createButton("Remove waypoint", "removewaypoint.png", 140, 25, this, KeyEvent.VK_R);
		removeWaypointButton.setEnabled(false);
		clearRouteButton = Tools.createButton("Clear route", "clear.png", 110, 25, this, KeyEvent.VK_C);
		clearRouteButton.setEnabled(false);
		southPanel.add(removeWaypointButton);
		southPanel.add(clearRouteButton);
		this.add(southPanel, BorderLayout.SOUTH);
		
		Route.get().addRouteListener(this);
	}
	public void avoidListUpdated(List<Solarsystem> avoidList) {}
	public void waypointsUpdated(List<Waypoint> waypoints) {
		treeModel.fireTreeStructureChanged();
		boolean enabled = route.getNumberOfWaypoints() > 0;
		clearRouteButton.setEnabled(enabled);
		removeWaypointButton.setEnabled(!tree.isSelectionEmpty());
	}
	public void valueChanged(TreeSelectionEvent tse) {
		boolean enabled = tse.getPath().getPathCount() == 2;
		removeWaypointButton.setEnabled(enabled);
	}
	public void maybeShowPopup(MouseEvent me) {
		if (me.isPopupTrigger()) {
			TreePath treePath = tree.getPathForLocation(me.getX(), me.getY());
			if (treePath != null && treePath.getPathCount() == 2) {
				WaypointMenu menu = new WaypointMenu((Waypoint) treePath.getPathComponent(1));
				menu.show(me.getComponent(), me.getX(), me.getY());
			} else if (treePath != null && treePath.getPathCount() == 3) {
				SolarsystemMenu menu = new SolarsystemMenu((Solarsystem) treePath.getPathComponent(2));
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
	public void mouseClicked(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == removeWaypointButton) {
			Waypoint waypoint = (Waypoint) tree.getSelectionPath().getLastPathComponent();
			Route.get().removeWaypoint(waypoint);
		} else if (event == clearRouteButton) {
			Route.get().clearRoute();
		}
	}
	private class WaypointModel implements TreeModel {
		private Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();

		public WaypointModel() {}
		
		public Object getChild(Object parent, int index) {
			if (parent.equals(route)) {
				return route.getWaypoint(index);
			} else if (parent instanceof Waypoint) {
				return route.getWaypointChild((Waypoint) parent, index).getSolarsystem();
			}
			return null;
		}
		public int getChildCount(Object parent) {
			if (parent.equals(route)) {
				return route.getNumberOfWaypoints();
			} else if (parent instanceof Waypoint) {
				return route.getWaypointChildSize((Waypoint) parent);
			}
			return 0;
		}
		public int getIndexOfChild(Object parent, Object child) {
			if (parent.equals(route)) {
				return route.indexOfWaypoint((Waypoint) parent);
			} else if (parent instanceof Waypoint) {
				return route.indexOfWaypointChild((Waypoint) parent, (Waypoint) child);
			}
			return -1;
		}
		public Object getRoot() {
			return route;
		}
		public boolean isLeaf(Object node) {
			if (node.equals(route)) {
				return false;
			} else if (node instanceof Waypoint && route.getWaypointChildSize((Waypoint) node) != 0) {
				return false;
			}
			return true;
		}
		public void addTreeModelListener(TreeModelListener l) {
			treeModelListeners.addElement(l);
		}
		public void removeTreeModelListener(TreeModelListener l) {
			treeModelListeners.removeElement(l);
		}
		protected void fireTreeStructureChanged() {
			TreeModelEvent e = new TreeModelEvent(this, new Object[] {route});
			for (TreeModelListener tml : treeModelListeners) {
				tml.treeStructureChanged(e);
			}
		}
		public void valueForPathChanged(TreePath path, Object newValue) {}
	}
	private class WaypointTreeRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object object, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			super.getTreeCellRendererComponent(tree, object, sel, expanded, leaf, row, hasFocus);
			if (object.equals(treeModel.getRoot())) {
				setIcon(Tools.getIcon("waypoint.png"));
			} else if (object instanceof Waypoint) {
				setIcon(Tools.getSolarsystemIcon(((Waypoint) object).getSolarsystem().getSecurityType()));
				setText(route.getWaypointString((Waypoint) object));
			} else if (object instanceof Solarsystem) {
				setIcon(Tools.getSolarsystemIcon(((Solarsystem) object).getSecurityType()));
			}
			return this;
		}
	}
}
