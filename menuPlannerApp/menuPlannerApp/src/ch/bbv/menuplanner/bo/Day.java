package ch.bbv.menuplanner.bo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Day implements Serializable {
	private static final long serialVersionUID = 1L;

	private static DateFormat df = new SimpleDateFormat("EEEE d.MM.y", Locale.getDefault());
	
	private final long id;
	private final Date date;
	private final List<Menu> menus = new ArrayList<Menu>();
	
	public Day(long id, Date date) {
		this.id = id;
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}
	
	public List<Menu> getMenus() {
		return menus;
	}
	
	public void addMenu(Menu menu) {
		menus.add(menu);
	}
	
	@Override
	public String toString() {
		return getDate() != null ? df.format(getDate()) : "?";
	}

	public void addAllMenus(List<Menu> menus) {
		this.menus.addAll(menus);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Day other = (Day) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}
