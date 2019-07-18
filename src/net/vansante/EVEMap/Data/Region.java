package net.vansante.EVEMap.Data;

import net.vansante.EVEMap.Tools;


public class Region extends Location {
	
	private final Faction faction;
	private float red, green, blue;
	
	public Region(int id, double x, double y, double z, String name, Faction faction) {
		
		super(id, x, y, z, name);
		
		this.faction = faction;
		
		float[] color = Tools.getRandomColor();
		red = color[0];
		green = color[1];
		blue = color[2];
	}
	public int getType() {
		return Location.TYPE_REGION;
	}
	public Faction getFaction() {
		return faction;
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
	public boolean isUnknown() {
		return super.getId() >= 11000000;
	}
	public boolean isKnown() {
		return super.getId() < 11000000;
	}
}