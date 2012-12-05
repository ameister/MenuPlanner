package ch.bbv.bo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Condiment {
	
	@Id
	@GeneratedValue
	private long id;
	
	private String name;
	private String barcode;

	public Condiment(String name) {
		this.name = name;
	}

	public Condiment() {
		// for JPA
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
