package net.vansante.EVEMap.Data;

public class Constellation extends Location {
	
	private final Region region;
	
	private Alliance sovereignty;
	private Solarsystem capital;

	public Constellation(int id, double x, double y, double z, String name, Region region) {
		
		super(id, x, y, z, name);
		this.region = region;
	}
	public int getType() {
		return Location.TYPE_CONSTELLATION;
	}
	public String toString() {
		return this.getName() + " / " + region.getName();
	}
	public Region getRegion() {
		return region;
	}
	public Alliance getSovereignty() {
		return sovereignty;
	}
	public void setSovereignty(Alliance sovereignty) {
		this.sovereignty = sovereignty;
	}
	public Solarsystem getCapital() {
		return capital;
	}
	public void setConstellationCapital(Solarsystem capital) {
		this.capital = capital;
	}
	public boolean isUnknown() {
		return super.getId() >= 21000000;
	}
	public boolean isKnown() {
		return super.getId() < 21000000;
	}
}
