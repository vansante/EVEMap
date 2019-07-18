package net.vansante.EVEMap.Data;

import java.util.*;


public class EVEObjects extends ArrayList<EVEObject> {

	public EVEObjects(int size) {
		super(size);
	}
	public List<EVEObject> searchByName(String name) {
		LinkedList<EVEObject> results = new LinkedList<EVEObject>();
		name = name.toLowerCase();
		for (EVEObject object : this) {
			if (object.getName().toLowerCase().indexOf(name) != -1) {
				results.add(object);
			}
		}
		return results;
	}
	public EVEObject getById(int id) {
		for (EVEObject object : this) {
			if (object.getId() == id) {
				 return object;
			}
		}
		return null;
	}
}
