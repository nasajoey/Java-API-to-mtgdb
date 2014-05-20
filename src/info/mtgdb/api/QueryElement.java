package info.mtgdb.api;

public class QueryElement {

	public enum Field {
		name, description, flavor, color, 
		manacost, convertedmanacost, type, 
		subtype, power, toughness, loyalty, 
		rarity, artist, setId
	}
	
	public enum Operator {
		m, eq, not, gt, gte, lt, lte
	}
	
	private Field field;
	private Operator operator;
	private String valueStr = null;
	private int valueInt = -1;
	
	public QueryElement(Field f, Operator o, String val) {
		this.field    = f;
		this.operator = o;
		this.valueStr = val;
	}
	
	public QueryElement(Field f, Operator o, int val) {
		this.field    = f;
		this.operator = o;
		this.valueInt = val;
	}
	
	public String toString() {
		return field.name() + " " + operator.name() + " " + (valueStr == null ? valueInt : valueStr);
	}
}
