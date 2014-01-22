package ch.bbv.menuplanner.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CondimentPos implements Serializable{
	private static final long serialVersionUID = 1L;
	private static DecimalFormat df = new DecimalFormat("#.##");
	
	private long id;
	private Condiment condiment;
	private String unit;
	private BigDecimal amount;
	
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
		return amount.setScale(2);
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getAmoutFormatted() {
		return df.format(getAmount());
	}

	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getCondimentName() {
		return getCondiment() == null ? null : getCondiment().getName();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getAmoutFormatted()).append(" ");
		sb.append(getUnit()).append(" ");
		sb.append(getCondiment());
		return sb.toString();
	}

}
