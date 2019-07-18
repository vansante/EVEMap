package net.vansante.EVEMap.Data;

public class Connection {
	
	private Solarsystem solarsystem1, solarsystem2;
	
	public Connection(Solarsystem solarsystem1, Solarsystem solarsystem2) {
		this.solarsystem1 = solarsystem1;
		this.solarsystem2 = solarsystem2;
	}
	public Solarsystem getSolarsystem1() {
		return solarsystem1;
	}
	public Solarsystem getSolarsystem2() {
		return solarsystem2;
	}
	public Solarsystem getDestination(Solarsystem source) {
		if (source == solarsystem1) {
			return solarsystem2;
		} else if (source == solarsystem2) {
			return solarsystem1;
		}
		return null;
	}
}