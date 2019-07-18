package net.vansante.EVEMap.UI.Dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Constants;
import net.vansante.EVEMap.Main;
import net.vansante.EVEMap.Tools;

public class About extends JDialog implements ActionListener {
	
	private final JButton closeButton;
	
	public About() {
		super(Main.get(), "About", true);
		this.setSize(180, 150);
		this.setResizable(false);
		this.setLocationRelativeTo(Main.get());
		this.setLayout(new BorderLayout());
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
				
		JLabel titleLabel = new JLabel(Constants.TITLE, JLabel.CENTER);
		titleLabel.setFont(new Font("Verdana", Font.BOLD, 15));
		JLabel versionLabel =  new JLabel("Version " + Constants.VERSION + " (build " + Constants.REVISION + ")");
		JLabel createdLabel = new JLabel("Created by:");
		createdLabel.setFont(new Font("Verdana", Font.BOLD, 13));
		JLabel creatorLabel = new JLabel("Paul van Santen");
		JLabel creatorLabel2 = new JLabel("aka AcriQuo");
		
		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(titleLabel);
		centerPanel.add(versionLabel);
		centerPanel.add(Box.createVerticalGlue());
		centerPanel.add(createdLabel);
		centerPanel.add(creatorLabel);
		centerPanel.add(creatorLabel2);
		centerPanel.add(Box.createVerticalGlue());
		
		this.add(centerPanel, BorderLayout.CENTER);
		
		closeButton = Tools.createButton("Close", null, 60, 20, this, KeyEvent.VK_C);
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout());
		southPanel.add(closeButton);
		
		this.add(southPanel, BorderLayout.SOUTH);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				closeButton.requestFocusInWindow();
			}
		});
	}
	public void actionPerformed(ActionEvent e) {
		Object event = e.getSource();
		if (event == closeButton) {
			this.setVisible(false);
		}
	}

}
