package ch.bbv.menuplanner.bo;

public class ShoppingListKey {
	
	private final int year;
	private final int week;

	public ShoppingListKey(int week, int year) {
		this.week = week;
		this.year = year;
	}
	
	public ShoppingListKey(String key) {
		String[] parsedKey = key.split("-");
		this.week = Integer.parseInt(parsedKey[0]);
		this.year = Integer.parseInt(parsedKey[1]);
	}

	public int getYear() {
		return year;
	}

	public int getWeek() {
		return week;
	}
	
	public String getKey() {
		return week + "-" + year;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Woche").append(" ").append(week).append(" ");
		sb.append("Jahr").append(" ").append(year);
		return sb.toString();
	}

}
