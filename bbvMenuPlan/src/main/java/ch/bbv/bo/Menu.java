package ch.bbv.bo;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@Entity
public class Menu {
	
	@Id
	@GeneratedValue
	private long id;
	@Transient
	private final SimpleStringProperty nameProperty = new SimpleStringProperty();
	@ManyToOne
	private Weekday day;
	@OneToMany(cascade = {CascadeType.REMOVE}, fetch = FetchType.EAGER)
	final private List<CondimentPos> condimentPos = Lists.newArrayList();

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		nameProperty.setValue(name);
	}
	
	@Access(AccessType.PROPERTY)
	public String getName() {
		return nameProperty.getValue();
	}
	
	public StringProperty nameProperty() {
		return nameProperty;
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
