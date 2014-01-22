package ch.bbv.menuplanner.bo;

import java.io.Serializable;


public class Condiment implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	
	private String name;
	private String barcode;

	public Condiment(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	@Override
	public String toString() {
		return getName();
	}

}
