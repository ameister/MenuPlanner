package ch.bbv.bo;

import java.util.List;

import com.google.common.collect.Lists;

public class Menu {
	private String name;
	final private List<CondimentPos> condimentPos = Lists.newArrayList();
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addPos(CondimentPos pos) {
		condimentPos.add(pos);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
