package net.vansante.EVEMap.Data;

import net.vansante.EVEMap.Tools;

public class Alliance extends EVEObject {
	
	private String ticker;
	private float red, green, blue;
	
	public Alliance(int id, String name, String ticker) {
		super(id, name);
		this.ticker = ticker;
		
		float[] color = Tools.getRandomColor();
		red = color[0];
		green = color[1];
		blue = color[2];
	}
	public String getTicker() {
		return ticker;
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
