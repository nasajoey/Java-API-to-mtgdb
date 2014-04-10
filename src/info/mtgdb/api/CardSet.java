package info.mtgdb.api;

/*
The MIT License (MIT)

Copyright (c) 2014 Littlepancake Software

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents a Magic set.
 * 
 * @author Joseph Rios <joey@littlepancake.com>
 * @version 0.1
 * @since  2014-03-20
 *
 */
public class CardSet {

	/**
	 * Constructor for an empty CardSet.  All values will be unset upon instantiation.
	 */
	public CardSet() {
		if( memberTypeHash == null ) {
			createMemberTypeHash();
		}
	}

	/**
	 * Constructor for a CardSet taking a json instance to build it.
	 * 
	 * @param json JSONObject for a CardSet as described by the mtgdb.info API.
	 */
	public CardSet(JSONObject json) {
		this();
		@SuppressWarnings("unchecked")
		Set<String> set = json.keySet();
		for( String s : set ) {
			CardField cardField = memberTypeHash.get(s);
			if( cardField != null) {
				//System.out.println(s+" is a "+cardField.type);
				try {
					if( cardField.type.matches("int") ) {
						int val = json.getInt(s);
						cardField.f.set(this, val);
						//System.out.println("Set "+cardField.name+" to "+cardField.f.getInt(this));
					}
					else if( cardField.type.matches("java.lang.String") ) {
						Object o = json.get(s);
						if( o != JSONObject.NULL ) cardField.f.set(this, (String) o);
						else continue;
						//System.out.println("Set "+cardField.name+" to "+cardField.f.get(this));
					}
					else if( cardField.type.matches("java.util.Date") ) {
						SimpleDateFormat sdf = new SimpleDateFormat(Dates.dateFormatInput);
						Date date = sdf.parse(json.getString(s));

						cardField.f.set(this, date);
					}
					else if( cardField.type.matches("java.util.List") ) {
						JSONArray jsonArray = json.getJSONArray(s);
						if( s.matches("cardIds") ) {
							cardIds = new ArrayList<Integer>();
							for( int i = 0; i < jsonArray.length(); i++ ) {
								cardIds.add(jsonArray.getInt(i));
							}
						}

					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			//else System.out.println(s+" wasn't found.");
		}
	}

	private String id;
	private String name;
	private String block;
	private String description;
	private String wikipedia;

	private int common;
	private int uncommon;
	private int rare;
	private int mythicRare;
	private int basicLand;
	private int total;

	private Date releasedAt;
	private List<Integer> cardIds;

	private static HashMap<String, CardField> memberTypeHash = null;

	/* Used in reflection to access data members by name when processing JSON. */
	private class CardField {
		Field f;
		//String name;
		String type;
	}
	
	/* Populates the static HashMap of field values. */
	private void createMemberTypeHash() {
		memberTypeHash = new HashMap<String, CardField>();
		Field[] fields = getClass().getDeclaredFields();
		for( Field f : fields ) {
			CardField cf = new CardField();
			cf.type = f.getType().getCanonicalName();
			cf.f = f;
			//cf.name = f.getName();
			memberTypeHash.put(f.getName(), cf);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWikipedia() {
		return wikipedia;
	}

	public void setWikipedia(String wikipedia) {
		this.wikipedia = wikipedia;
	}

	public int getCommon() {
		return common;
	}

	public void setCommon(int common) {
		this.common = common;
	}

	public int getUncommon() {
		return uncommon;
	}

	public void setUncommon(int uncommon) {
		this.uncommon = uncommon;
	}

	public int getRare() {
		return rare;
	}

	public void setRare(int rare) {
		this.rare = rare;
	}

	public int getMythic() {
		return mythicRare;
	}

	public void setMythic(int mythic) {
		this.mythicRare = mythic;
	}

	public int getBasicLand() {
		return basicLand;
	}

	public void setBasicLand(int basicLand) {
		this.basicLand = basicLand;
	}

	public Date getReleasedAt() {
		return releasedAt;
	}

	public void setReleasedAt(Date releasedAt) {
		this.releasedAt = releasedAt;
	}

	public List<Integer> getCardIds() {
		return cardIds;
	}

	public void setCardIds(List<Integer> cardIds) {
		this.cardIds = cardIds;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
