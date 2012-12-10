package ch.bbv.bo;

import java.util.List;

import com.google.common.collect.Lists;

public class ShoppingList {
	
	private List<ListPos> pos = Lists.newArrayList();
	
	public List<ListPos> getPos() {
		return pos;
	}
	
	public void addPos(ListPos position) {
		pos.add(position);
	}

	public boolean isContained(String name) {
		for (ListPos pos : getPos()) {
			String posName = pos.getName();
			if(name == null && posName == null || posName.equals(name)) {
				return true;
			}
		}
		return false;
	}

}
