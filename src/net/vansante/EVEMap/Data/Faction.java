package net.vansante.EVEMap.Data;

import net.vansante.EVEMap.Tools;

public class Faction extends EVEObject {
	
	private float red, green, blue;
	
	public Faction(int id, String name) {
		super(id, name);
	
		float[] color = Tools.getRandomColor();
		red = color[0];
		green = color[1];
		blue = color[2];
	}
	public float getRed() {
		return red;
	}
	public void setRed(float red) {
		this.red = red;
	}
	public float getGreen() {
		return green;
	}
	public void setGreen(float green) {
		this.green = green;
	}
	public float getBlue() {
		return blue;
	}
	public void setBlue(float blue) {
		this.blue = blue;
	}
}
