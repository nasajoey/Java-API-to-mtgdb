package info.mtgdb.api;

import java.util.ArrayList;

public class ComplexQuery {

	private ArrayList<QueryElement> elements;
	private int start = -1;
	private int limit = 0;
	
	public ComplexQuery() {
		elements = new ArrayList<QueryElement>();
	}
	
	public void addQueryElement(QueryElement qe) {
		elements.add(qe);
	}
	
	public void setStart(int s) {
		this.start = s;
	}
	
	public void setLimit(int l) {
		this.limit = l;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		int numElements = elements.size();
		for( int i = 0; i < numElements; i++ ) {
			sb.append(elements.get(i).toString());
			if( i < numElements - 1 ) {
				sb.append(" and ");
			}
		}
		if(start > 0) {
			sb.append("&start="+start);
		}
		
		if( limit > 0 ) {
			sb.append("&limit="+limit);
		}
		
		return sb.toString();
	}
	
}
