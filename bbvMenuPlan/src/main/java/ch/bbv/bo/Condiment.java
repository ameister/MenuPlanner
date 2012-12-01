package ch.bbv.bo;

import javax.persistence.Entity;

@Entity
public class Condiment {
	
	private String name;
	private String barcode;
	
	
	public Condiment(String name) {
		this.name = name;
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
