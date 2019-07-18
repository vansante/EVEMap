package net.vansante.EVEMap;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import net.vansante.EVEMap.Data.Location;

import java.lang.Math;
import java.util.*;
import java.net.URL;

public abstract class Tools {
	public static double calculateDistance(Location location1, Location location2) {
		return Tools.calculateDistance(location1.getX(), location1.getY(), location1.getZ(), location2.getX(),location2.getY(), location2.getZ());
	}
	public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2) + Math.pow((z2 - z1), 2));
	}
	public static double round(double number, int precision) {
		long temp = (long) (number * precision);
		return (double) temp / precision;
	}
	public static ImageIcon getIcon(String icon) {
		URL url = Tools.class.getClassLoader().getResource("images/" + icon);
		if (url != null) {
			return new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
		System.out.println("Warning: Could not load image '" + icon + "'");
		return null;
	}
	public static Image getImage(String image) {
		URL url = Tools.class.getClassLoader().getResource("images/" + image);
		if (url != null) {
			return Toolkit.getDefaultToolkit().getImage(url);
		}
		System.out.println("Warning: Could not load image '" + image + "'");
		return null;
	}
	public static URL getUrl(String file) {
		return Tools.class.getClassLoader().getResource(file);
	}
	public static JMenuItem createMenuItem(JComponent target, String title, String icon, ActionListener listener, int mnemonic) {
		JMenuItem item = new JMenuItem(title);
		if (icon != null) {
			item.setIcon(Tools.getIcon(icon));
		}
		if (mnemonic != 0) {
			item.setMnemonic(mnemonic);
		}
		item.addActionListener(listener);
		target.add(item);
		return item;
	}
	public static JMenu createSubMenu(JComponent target, String title, String icon) {
		JMenu item = new JMenu(title);
		if (icon != null) {
			item.setIcon(Tools.getIcon(icon));
		}
		target.add(item);
		return item;
	}
	public static JButton createButton(String title, String image, int width, int height, ActionListener listener, int mnemonic) {
		JButton button = new JButton(title);
		button.setPreferredSize(new Dimension(width, height));
		button.addActionListener(listener);
		if (mnemonic != 0) {
			button.setMnemonic(mnemonic);
		}
		if (image != null) {
			button.setIcon(Tools.getIcon(image));
		}
		return button;
	}
	public static JToggleButton createToggleButton(String title, int width, int height, ActionListener listener, int mnemonic) {
		JToggleButton button = new JToggleButton(title);
		button.setPreferredSize(new Dimension(width, height));
		button.addActionListener(listener);
		if (mnemonic != 0) {
			button.setMnemonic(mnemonic);
		}
		return button;
	}
	public static JRadioButton createRadioButton(String title, ActionListener listener, ButtonGroup group, int mnemonic) {
		JRadioButton button = new JRadioButton(title);
		button.addActionListener(listener);
		group.add(button);
		if (mnemonic != 0) {
			button.setMnemonic(mnemonic);
		}
		return button;
	}
	public static JCheckBox createCheckBox(String title, ActionListener listener, int mnemonic) {
		JCheckBox checkbox = new JCheckBox(title);
		checkbox.addActionListener(listener);
		if (mnemonic != 0) {
			checkbox.setMnemonic(mnemonic);
		}
		return checkbox;
	}
	public static JTextField createTextField(int width, int height, ActionListener listener) {
		JTextField textField = new JTextField();
		textField.setPreferredSize(new Dimension(width, height));
		if (listener != null) {
			textField.addActionListener(listener);
		}
		return textField;
	}
	public static Icon getSolarsystemIcon(final int security) {
		return new Icon() {
			public int getIconHeight() {
				return Constants.ICON_SIZE;
			}
			public int getIconWidth() {
				return Constants.ICON_SIZE;
			}
			public void paintIcon(Component c, Graphics g, int x, int y) {
				if (c.isEnabled()) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.translate(x, y);
					g2d.setColor(Constants.SEC_COLORS[security]);
					g2d.fillOval(1, 1, Constants.ICON_SIZE - 2, Constants.ICON_SIZE - 2);
					//Restore graphics object
					g2d.translate(-x, -y);
				}
			}
		};
	}
	public static String colorToHex(Color color) {
		StringBuilder hex = new StringBuilder();
		hex.append(Integer.toHexString(color.getRed()));
		if (Integer.toHexString(color.getRed()).length() < 2) {
			hex.append(0);
		}
		hex.append(Integer.toHexString(color.getGreen()));
		if (Integer.toHexString(color.getGreen()).length() < 2) {
			hex.append(0);
		}
		hex.append(Integer.toHexString(color.getBlue()));
		if (Integer.toHexString(color.getBlue()).length() < 2) {
			hex.append(0);
		}
		return hex.toString();
	}
	public static float[] getRandomColor() {
		Random random = new Random();
		float[] color = new float[] {
			0.15f + Math.abs(random.nextFloat() - 0.15f),
			0.15f + Math.abs(random.nextFloat() - 0.15f),
			0.15f + Math.abs(random.nextFloat() - 0.15f)
		};
		return color;
	}
	public static double approachValue(double current, double dest, int speed) {
		if (current < dest + speed * 1.2 && current > dest - speed * 1.2) {
			return dest;
		} else if (current < dest) {
			return current + speed;
		}
		return current - speed;
	}
	public static double approachAngle(double current, double dest, int speed) {
		if (current < dest + speed * 1.2 && current > dest - speed * 1.2) {
			return dest;
		}
		if (current - dest <= 180 && current - dest >= 0) {
			current -= speed;
		} else {
			current += speed;
		}
		if (current < 0) {
			current += 360;
		} else if (current > 360) {
			current -= 360;
		}
		return current;
	}
	/////////////////////////////////////////////////////////
	//  Bare Bones Browser Launch                          //
	//  Version 1.5                                        //
	//  December 10, 2005                                  //
	//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
	//  Example Usage:                                     //
	//     String url = "http://www.centerkey.com/";       //
	//     BareBonesBrowserLaunch.openURL(url);            //
	//  Public Domain Software -- Free to Use as You Like  //
	/////////////////////////////////////////////////////////
	public static void openURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
				openURL.invoke(null, new Object[] {url});
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else { //assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(new String[] {"which", browsers[count]}).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[] {browser, url});
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error attempting to launch web browser:\n" + e.getLocalizedMessage());
		}
	}
	public static int arraySearch(int[] array, int item) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == item) {
				return i;
			}
		}
		return -1;
	}
	public static void printArray(int[] array) {
		System.out.println("START INT ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END INT ARRAY");
	}
	public static void printArray(double[] array) {
		System.out.println("START DOUBLE ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END DOUBLE ARRAY");
	}
	public static void printArray(float[] array) {
		System.out.println("START FLOAT ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END FLOAT ARRAY");
	}
	public static void printArray(String[] array) {
		System.out.println("START STRING ARRAY");
		for (int i = 0; i < array.length; i++) {
			System.out.println(i + ":  " + array[i]);
		}
		System.out.println("END STRING ARRAY");
	}
	public static void printList(java.util.List list) {
		System.out.println("START LIST");
		for (Object object : list) {
			System.out.println(object);
		}
		System.out.println("END LIST");
	}
}
