package ch.bbv.bo;

import java.math.BigDecimal;

public class CondimentPos {
	private Condiment condiment;
	private BigDecimal amount;
	private String unit;
	
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
