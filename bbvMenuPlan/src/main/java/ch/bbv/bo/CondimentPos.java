package ch.bbv.bo;

import java.math.BigDecimal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class CondimentPos {
	
	@Id
	@GeneratedValue
	private long id;
	private final SimpleObjectProperty<Condiment> condiment = new SimpleObjectProperty<Condiment>();
	private final SimpleStringProperty unit = new SimpleStringProperty();
	private final SimpleStringProperty amount = new SimpleStringProperty();
	
	@ManyToOne(optional = false)
	private Menu menu;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	@Access(AccessType.PROPERTY)
	@OneToOne(cascade = {CascadeType.REMOVE}, optional = false)
	public Condiment getCondiment() {
		return condiment.getValue();
	}
	public void setCondiment(Condiment condiment) {
		this.condiment.setValue(condiment);
	}
	public SimpleObjectProperty<Condiment> condimentProperty() {
		return condiment;
	}
	@Access(AccessType.PROPERTY)
	public BigDecimal getAmount() {
		return new BigDecimal(amount.getValue()).setScale(2);
	}
	public void setAmount(BigDecimal amount) {
		this.amount.setValue(amount.toString());
	}
	public SimpleStringProperty amountProperty() {
		return amount;
	}
	@Access(AccessType.PROPERTY)
	public String getUnit() {
		return unit.getValue();
	}
	public void setUnit(String unit) {
		this.unit.setValue(unit);
	}
	public SimpleStringProperty unitProperty() {
		return unit;
	}
	public String getCondimentName() {
		return getCondiment() == null ? null : getCondiment().getName();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getAmount()).append(" ");
		sb.append(getUnit()).append(" ");
		sb.append(getCondiment());
		return sb.toString();
	}

}
