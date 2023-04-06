package storage;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public class Tuple implements Cloneable, ITuple, Serializable {

	private Vector<Cell> cells;
	private Object primaryKey; // used in binary search within the same page

	public Tuple() {
		cells = new Vector<>();
	}

	@Override
	public void addCell(Cell c) {
		this.cells.add(c);
	}
	
	public Vector<Cell> getCells() {
		return cells;
	}

	public Object getPrimaryKey() {
		return primaryKey;
	}

	@Override
	public void setPrimaryKey(Object PK) {
		this.primaryKey = PK;
	}

	protected Tuple getCopy() {
		Tuple copy = null;
		try {
			copy = (Tuple) this.clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return copy;
	}

}
