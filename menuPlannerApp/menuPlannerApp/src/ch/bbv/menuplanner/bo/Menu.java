package ch.bbv.menuplanner.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Serializable{
	private static final long serialVersionUID = 1L;

	private long id;

	private String name;
	private Day day;
	final private List<CondimentPos> condimentPos = new ArrayList<CondimentPos>();

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}

	
	public Day getDay() {
		return day;
	}
	
	public void setDay(Day day) {
		this.day = day;
	}
	
	public void addPos(CondimentPos pos) {
		condimentPos.add(pos);
		pos.setMenu(this);
	}
	
	public List<CondimentPos> getCondiments() {
		return condimentPos;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
