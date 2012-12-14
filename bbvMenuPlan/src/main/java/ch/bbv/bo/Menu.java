package ch.bbv.bo;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@Entity
public class Menu {
	
	@Id
	@GeneratedValue
	private long id;
	private String name;
	@ManyToOne
	private Weekday day;
	@OneToMany(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
	final private List<CondimentPos> condimentPos = Lists.newArrayList();
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Weekday getDay() {
		return day;
	}
	
	public void setDay(Weekday day) {
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

	public String getTickerText() {
		return getName() + ": " + Joiner.on(", ").join(getCondiments());
	}
	
}
