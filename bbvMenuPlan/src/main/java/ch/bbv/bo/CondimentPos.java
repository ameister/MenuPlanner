package ch.bbv.bo;

import java.math.BigDecimal;

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
	@OneToOne
	private Condiment condiment;
	private BigDecimal amount;
	private String unit;
	@ManyToOne
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

	public Condiment getCondiment() {
		return condiment;
	}
	public void setCondiment(Condiment condiment) {
		this.condiment = condiment;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
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
