package ch.bbv.menuplanner.bo;

import java.io.Serializable;

public class ListPos implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int pos;
	private String name;
	
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return getName();
	}
}
