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

import org.json.*;

/**
 * This class represents a single Magic card.
 * 
 * @author Joseph Rios <joey@littlepancake.com>
 * @version 0.1
 * @since  2014-03-20
 *
 */
public class Card {

	/* All non-List data members are initialized to these static values.
	 * When retrieving values for data members later on in your code,
	 * you might want to check against these values before playing
	 * with the data.
	 */
	
	/**
	 * Default value assigned to each int data member.
	 */
	public static int    INT_VALUE_NOT_SET  = -1;
	/**
	 * Default value assigned to each String data member.
	 */
	public static String STR_VALUE_NOT_SET  = "Value not set.";
	/**
	 * Value that will be printed if Date value is not set when
	 * attempting to print a Date data member.
	 */
	public static String DATE_VALUE_NOT_SET = "Date not set.";
	
	/*
	 * This static HashMap will be checked upon instantiation of any Card object.
	 * If it is null, it will be populated as a map from the field name to an
	 * instance of CardField.  This will ease the computational burden of checking
	 * the JSON keys.  Instead of a series of String matching 'if' statements, we
	 * will be able to use reflection and access the fields.  Since it is a static
	 * HashMap, it should be populated more often than not within your running
	 * code.  We can think of it as doing the 'if' statements for field names just
	 * once rather than everytime we create a new Card from JSON.
	 * 
	 * If any future fields are added to the API, they should be added to this class
	 * with the SAME NAME as the web service API JSON response.  If those fields are
	 * ints or Strings, nothing else needs to be done.  If they are arrays, then a
	 * little more work needs to be done.  Use the existing arrays (rulings, for example)
	 * to help writing new array processing code.
	 */
	private static HashMap<String, CardField> memberTypeHash = null;
	
	/* Formatters for dates.  If the format of the date from the web service API changes,
	 * then dateFormatterInput should be adjusted accordingly.
	 */
	private final SimpleDateFormat dateFormatterInput  = new SimpleDateFormat(Dates.dateFormatInput);
	private final SimpleDateFormat dateFormatterOutput = new SimpleDateFormat(Dates.dateFormatOutput);

	/* Used in reflection to access data members by name when processing JSON. */
	private class CardField {
		Field f;
		//String name;
		String type;
	}

	/**
	 * Creates new Card object with default values.
	 */
	public Card() {
		if( memberTypeHash == null ) {
			createMemberTypeHash();
		}
	}

	/**
	 * Construct Card from JSON text. If the JSON doesn't describe a 
	 * Card, the only indication would likely be all of the data members
	 * of the new Card containing default values.
	 * 
	 * @param json A {@link JSONObject} that hopefully describes a Card.
	 */
	public Card(JSONObject json) {
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
					}
					else if( cardField.type.matches("boolean") ) {
						boolean val = json.getBoolean(s);
						cardField.f.set(this, val);
					}
					else if( cardField.type.matches("java.lang.String") ) {
						Object o = json.get(s);
						if( o != JSONObject.NULL ) cardField.f.set(this, (String) o);
						else continue;
					}
					else if( cardField.type.matches("java.util.Date") ) {
						Date date = dateFormatterInput.parse(json.getString(s));						
						cardField.f.set(this, date);
					}
					else if( cardField.type.matches("java.util.List") ) {
						JSONArray jsonArray = json.getJSONArray(s);
						if( s.matches("colors") ) {
							colors = new ArrayList<String>();
							for( int i = 0; i < jsonArray.length(); i++ ) {
								colors.add(jsonArray.getString(i));
							}
						}
						else if( s.matches("rulings") ) {
							rulings = new ArrayList<Ruling>();
							for( int i = 0; i < jsonArray.length(); i++ ) {
								Ruling r = new Ruling(jsonArray.getJSONObject(i));
								rulings.add(r);
							}
						}
						else if( s.matches("formats") ) {
							formats = new ArrayList<Format>();
							for( int i = 0; i < jsonArray.length(); i++ ) {
								Format f = new Format(jsonArray.getJSONObject(i));
								formats.add(f);
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
	
	/* Populates the static HashMap of field values. */
	private void createMemberTypeHash() {
		memberTypeHash = new HashMap<String, CardField>();
		Field[] fields = getClass().getDeclaredFields();
		for( Field f : fields ) {
			CardField cf = new CardField();
			cf.type = f.getType().getCanonicalName();
			cf.f = f;
			memberTypeHash.put(f.getName(), cf);
		}
	}

	private int id					= INT_VALUE_NOT_SET;
	private int relatedCardId		= INT_VALUE_NOT_SET;
	private int setNumber			= INT_VALUE_NOT_SET;	
	private int power				= INT_VALUE_NOT_SET;
	private int toughness			= INT_VALUE_NOT_SET;
	private int loyalty				= INT_VALUE_NOT_SET;
	private int convertedManaCost	= INT_VALUE_NOT_SET;

	private String name				= STR_VALUE_NOT_SET;
	private String description		= STR_VALUE_NOT_SET;
	private String flavor			= STR_VALUE_NOT_SET;
	private String manaCost			= STR_VALUE_NOT_SET;
	private String cardSetName		= STR_VALUE_NOT_SET;
	private String type				= STR_VALUE_NOT_SET;
	private String rarity			= STR_VALUE_NOT_SET;
	private String artist			= STR_VALUE_NOT_SET;
	private String cardSetId		= STR_VALUE_NOT_SET;
	private String searchName		= STR_VALUE_NOT_SET;
	private String subType			= STR_VALUE_NOT_SET;

	private List<String> colors		= null;	
	private List<Ruling> rulings	= null;
	private List<Format> formats	= null;

	private Date releasedAt			= null;
	
	private boolean token			= false;

	/**
	 * @return String of the URL to retrieve this Card object's image.
	 */
	public String getCardImageUrl() {
		return "https://api.mtgdb.info/content/card_images/"+id+".jpeg";
	}

	/**
	 * @return String of the URL to retrieve this Card object's image in high resolution.
	 */
	public String getHiResImageUrl() {
		return "https://api.mtgdb.info/content/hi_res_card_images/"+id+".jpeg";
	}

	/**
	 * 
	 * @return The multiverse id of this Card.
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return int representing a related card id.
	 */
	public int getRelatedCardId() {
		return relatedCardId;
	}

	/**
	 * 
	 * @param relatedCardId int
	 */
	public void setRelatedCardId(int relatedCardId) {
		this.relatedCardId = relatedCardId;
	}

	/**
	 * 
	 * @return The set number that includes this Card.
	 */
	public int getSetNumber() {
		return setNumber;
	}

	/**
	 * 
	 * @param setNumber
	 */
	public void setSetNumber(int setNumber) {
		this.setNumber = setNumber;
	}

	/**
	 * 
	 * @return String of this Card's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return String of this Card's description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return String of the flavor text for this Card.
	 */
	public String getFlavor() {
		return flavor;
	}

	/**
	 * 
	 * @param flavor
	 */
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	/**
	 * 
	 * @return List<String> of the colors of this Card.
	 */
	public List<String> getColors() {
		return colors;
	}

	/**
	 * 
	 * @param colors
	 */
	public void setColors(List<String> colors) {
		this.colors = colors;
	}

	/**
	 * 
	 * @return String mana cost.
	 */
	public String getManaCost() {
		return manaCost;
	}

	/**
	 * 
	 * @param manaCost
	 */
	public void setManaCost(String manaCost) {
		this.manaCost = manaCost;
	}

	/**
	 * 
	 * @return int Converted mana cost.
	 */
	public int getConvertedMonaCost() {
		return convertedManaCost;
	}

	/**
	 * 
	 * @param convertedMonaCost
	 */
	public void setConvertedMonaCost(int convertedMonaCost) {
		this.convertedManaCost = convertedMonaCost;
	}

	/**
	 * 
	 * @return String set name.
	 */
	public String getCardSetName() {
		return cardSetName;
	}

	/**
	 * 
	 * @param cardSetName
	 */
	public void setCardSetName(String cardSetName) {
		this.cardSetName = cardSetName;
	}

	/**
	 * @return String the Card type.
	 */
	public String getType() {
		return type;
	}

	
	
	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return String the card subtype.
	 */
	public String getSubType() {
		return subType;
	}

	/**
	 * @param subType
	 */
	public void setSubType(String subType) {
		this.subType = subType;
	}

	/**
	 * @return int The power of this card.
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @param power
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/**
	 * @return int the Toughness of this card.
	 */
	public int getToughness() {
		return toughness;
	}

	/**
	 * @param toughness
	 */
	public void setToughness(int toughness) {
		this.toughness = toughness;
	}

	/**
	 * @return int The Loyalty of this Card.
	 */
	public int getLoyalty() {
		return loyalty;
	}

	/**
	 * @param loyalty
	 */
	public void setLoyalty(int loyalty) {
		this.loyalty = loyalty;
	}

	/**
	 * @return String rarity.
	 */
	public String getRarity() {
		return rarity;
	}

	/**
	 * @param rarity
	 */
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	/**
	 * @return String artist name.
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return String The set ID.
	 */
	public String getCardSetId() {
		return cardSetId;
	}

	/**
	 * @param cardSetId
	 */
	public void setCardSetId(String cardSetId) {
		this.cardSetId = cardSetId;
	}

	/**
	 * @return List<Ruling> of any rulings associated with this Card.
	 */
	public List<Ruling> getRulings() {
		return rulings;
	}

	/**
	 * @param rulings
	 */
	public void setRulings(List<Ruling> rulings) {
		this.rulings = rulings;
	}

	/**
	 * @return List<Format> of all Format that are legal for this card.
	 */
	public List<Format> getFormats() {
		return formats;
	}

	/**
	 * @param formats
	 */
	public void setFormats(List<Format> formats) {
		this.formats = formats;
	}

	/**
	 * @return Date release Date for this Card.
	 */
	public Date getReleasedAt() {
		return releasedAt;
	}
	
	/**
	 * @return String release date as a formatted string.
	 */
	public String getReleasedAtString() {
		if( releasedAt == null ) return DATE_VALUE_NOT_SET;
		
		return dateFormatterOutput.format(releasedAt);
	}

	/**
	 * @param releasedAt
	 */
	public void setReleasedAt(Date releasedAt) {
		this.releasedAt = releasedAt;
	}

	/**
	 * @return String search name.
	 */
	public String getSearchName() {
		return searchName;
	}

	/**
	 * @param searchName
	 */
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public boolean isToken() {
		return token;
	}

	public void setToken(boolean isToken) {
		this.token = isToken;
	}

}
