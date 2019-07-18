package net.vansante.EVEMap.Data;

public class EVEObject {
	private int id;
	private String name;
	
	public EVEObject(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String toString() {
		return name;
	}
}
