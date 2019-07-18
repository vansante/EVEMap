package net.vansante.EVEMap.Data;


public class Station extends EVEObject {

	private Solarsystem solarsystem;
	
	public Station(int id, String name, Solarsystem solarsystem) {
		super(id, name);
		this.solarsystem = solarsystem;
	}
	public Solarsystem getSolarsystem() {
		return solarsystem;
	}
}
