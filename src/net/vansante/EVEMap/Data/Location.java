package net.vansante.EVEMap.Data;


public abstract class Location extends EVEObject {
	public final static int TYPE_SOLARSYSTEM = 0;
	public final static int TYPE_CONSTELLATION = 1;
	public final static int TYPE_REGION = 2;
	public final static int TYPE_STATION = 3;
	
	private double x, y, z;
	
	public Location(int id, double x, double y, double z, String name) {
		super(id, name);
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public abstract int getType();
	public abstract boolean isKnown();
	public abstract boolean isUnknown();

	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
}
