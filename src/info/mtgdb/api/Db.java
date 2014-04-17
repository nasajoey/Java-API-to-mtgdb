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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.*;

/**
 * This is the primary API class. It allows for querying the mtgdb.info database for cards and sets.
 * 
 * @author Joseph Rios <joey@littlepancake.com>
 * @version 0.1
 * @since  2014-03-20
 *
 */
public class Db {

	public static String API_URL = "https://api.mtgdb.info";

	/* Make this class un-instantiatable. */
	private Db() {

	}

	/** 
	 * This method would be for development purposes only, perhaps to point to
	 * a local version of the service.
	 * 
	 * @param url A string representation of a complete URL.
	 */
	public static void setApiUrl(String url) {
		API_URL = url;
	}

	/**
	 * This method will allow the user to supply an arbitrary URL string from which
	 * he or she expects to receive a card.  If new API calls are added to mtgdb.info
	 * in the future, this method would be the way to get data from those new calls
	 * while waiting for a proper update to this Java API.
	 * 
	 * @param url String
	 * @return
	 */
	public static Card getCardFromUrl(String url) {
		Card card = null;
		JSONObject root = getObject(url);
		card = new Card(root);

		return card;
	}

	/**
	 * Same as {@link getCardFromUrl} but for CardSet objects instead of Cards.
	 * @param url
	 * @return
	 */
	public static CardSet getSetFromUrl(String url) {
		CardSet cardSet = null;

		JSONObject root = getObject(url);
		cardSet = new CardSet(root);

		return cardSet;
	}

	/**
	 * Same as {@link getCardFromUrl} but for ArrayList<Card> objects instead of Cards.
	 * @param url
	 * @return
	 */
	public static ArrayList<Card> getCardsFromUrl(String url) {
		ArrayList<Card> cards = new ArrayList<Card>();
		
		JSONArray ja = getArray(url);

		/* Process the JSON to create the list of cards. */
		for( int i = 0; i < ja.length(); i++ ) {
			Card card = new Card(ja.getJSONObject(i));
			if( card != null ) cards.add(card);
		}

		return cards;
	}

	/** 
	 * Get a random card from a particular set.
	 * 
	 * @param setId The id of the set from which to retrieve a random card.  If setId is null, then return a completely random card.
	 * @return Card
	 */
	public static Card getRandom(String setId) {
		String url = setId == null ? API_URL+"/cards/random" : API_URL+"/sets/"+setId+"/cards/random";
		return getCardFromUrl(url);
	}

	/**
	 * Get a completely random Card.
	 * 
	 * @return Card
	 */
	public static Card getRandom() {
		return getRandom(null);
	}

	/**
	 * Get a card from a multiverse id.
	 * 
	 * @param id The multiverse id of a card.
	 * @return A {@link Card} referenced by id.
	 */
	public static Card getCard(int id) {
		String url = API_URL+"/cards/"+id;
		return getCardFromUrl(url);
	}
	
	/*
	Get a card in a set

	Get http://api.mtgdb.info/sets/[id]/cards/[setNumber] 
	This is by the card number in the set not multiverse Id
	 */
	
	/**
	 * Get a card from a set based on its number within that set.
	 * 
	 * @param set The set id
	 * @param id The id of a card within set.
	 * @return A {@link Card} referenced by id.
	 */
	public static Card getCardWithinSet(String set, int id) {
		String url = API_URL+"/sets/"+set+"/cards/"+id;
		return getCardFromUrl(url);
	}

	/**
	 * 
	 * @param setId The String setId of the set.
	 * @return A {@link CardSet}.
	 */
	public static CardSet getSet(String setId)
	{
		String url = API_URL+"/sets/"+setId;
		return getSetFromUrl(url);
	}

	/**
	 * Retrieve set information based upon their set ids.
	 * 
	 * @param ids An {@link ArrayList} of 3-character set ids.
	 * @return An {@link ArrayList} of {@link CardSet} objects.
	 */
	public static ArrayList<CardSet> getSets(ArrayList<String> ids) {
		StringBuilder sb = new StringBuilder();
		ArrayList<CardSet> cardSetArray = new ArrayList<CardSet>();
		for( String s : ids ) {
			sb.append(s+","); // Trailing commas don't seem to hurt API call.

		}

		String url = API_URL+"/sets/"+sb.toString();
		JSONArray ja = getArray(url);
		for( int i = 0; i < ja.length(); i++ ) {
			CardSet cs = new CardSet(ja.getJSONObject(i));
			if( cs != null ) cardSetArray.add(cs);
		}

		return cardSetArray;
	}
	/**
	 * Request all sets from the database.
	 * 
	 * @return ArrayList<CardSet> of all sets in the database.
	 */
	public static ArrayList<CardSet> getAllSets() {
		ArrayList<CardSet> cardSetArray = new ArrayList<CardSet>();
		String url = API_URL+"/sets/";
		JSONArray ja = getArray(url);
		for( int i = 0; i < ja.length(); i++ ) {
			CardSet cs = new CardSet(ja.getJSONObject(i));
			if( cs != null ) cardSetArray.add(cs);
		}

		return cardSetArray;
	}

	/**
	 * Retrieve cards based upon their multiverse ids.
	 * 
	 * @param multiverseIds A {@link List} of multiverse ids
	 * @return An {@link ArrayList} of {@link Card} objects corresponding to the ids.
	 */
	public static ArrayList<Card> getCards(ArrayList<Integer> multiverseIds) {
		StringBuilder sb = new StringBuilder();

		/* Make a list of the ids to fetch. */
		for( Integer i : multiverseIds ) {
			sb.append(i+",");
		}
		/* Remove trailing ',' */
		if( multiverseIds.size() > 0 ) sb.deleteCharAt(sb.length()-1);
		String url = API_URL+"/cards/"+sb.toString();
		
		return getCardsFromUrl(url);

	}

	/**
	 * Gets cards in a set.  You can start and end to page through the card sets.  This uses the card number.
	 * 
	 * @param setId String set name (3 characters)
	 * @param start Beginning index of cards in this set to fetch.
	 * @param end Ending index of cards in this set to fetch.
	 * @return ArrayList<Card> of Card objects.
	 */
	public static ArrayList<Card> getSetCards(String setId, int start, int end) {
		if( end < start || start <= 0 ) return null;
		
		StringBuilder sb = new StringBuilder();
		sb.append(API_URL);
		sb.append("/sets/");
		sb.append(setId);
		sb.append("/cards/?start=");
		sb.append(start);
		sb.append("&end=");
		sb.append(end);

		return getCardsFromUrl(sb.toString());
	}
	
	/**
	 * Get all versions of a card with the supplied name.
	 * 
	 * @param name The name of the card.
	 * @return A {@link ArrayList} of {@link Card} objects.
	 */
	public static ArrayList<Card> getCards(String name) {
		String url = API_URL+"/cards/"+name;
		return getCardsFromUrl(url);
	}

	public static ArrayList<Card> getSetCards(String setName) {
		StringBuilder sb = new StringBuilder();
		sb.append(API_URL);
		sb.append("/sets/");
		sb.append(setName);
		sb.append("/cards/");
		return getCardsFromUrl(sb.toString());
		
	}

	public static ArrayList<Card> getCards(Set<String> fields) {
		StringBuilder sb = new StringBuilder();
		for( String s : fields ) {
			sb.append(s+",");			
		}
		if( fields.size() > 0 ) sb.deleteCharAt(sb.length()-1);
		String url = API_URL+"/cards/?fields="+sb.toString();
		return getCardsFromUrl(url);
	}

	/**
	 * Returns a list of cards matching the supplied query string.  Note that it will not filter for
	 * size, so a generic or empty query string will return many, many cards.  Non alphanumberic characters
	 * are removed prior to querying (with the exception of the space and '-' characters).
	 * 
	 * @param searchText String representing the text to search for.
	 * @return ArrayList<Card> matching the query text.
	 */
	public static ArrayList<Card> searchCards(String searchText) {
		searchText = searchText.replaceAll("[^A-Za-z0-9 -]", "");
		String url = API_URL+"/search/"+searchText;
		return getCardsFromUrl(url);
	}

	/**
	 * Gets the entire card database. Useful creating a local copy. This method may take up to a few minutes to run.
	 * 
	 * @return An {@link ArrayList} of {@link Card} objects.
	 */
	public static ArrayList<Card> getCards() {
		ArrayList<Card> cards = new ArrayList<Card>();
		String url = API_URL+"/cards/";
		return getCardsFromUrl(url);
	}

	/**
	 * 
	 * @param filters HashMap<String, String> containing a mapping of keys to values.  For example colors and black.
	 * @return ArrayList
	 */
	public static ArrayList<Card> filterCards(HashMap<String, String> filters) {
		StringBuilder sb = new StringBuilder();
		sb.append(API_URL+"/cards/?");
		for( String key : filters.keySet()) {
			String value = filters.get(key);
			sb.append(key+"="+value+"&");
		}
		if( filters.size() > 0 ) sb.deleteCharAt(sb.length()-1);
		return getCardsFromUrl(sb.toString());
	}

	private static JSONObject getObject(String url) {
		JSONObject ja;
		try {
			URL ur = new URL(url);
			JSONTokener tokener = new JSONTokener(ur.openStream());
			ja = new JSONObject(tokener);
		} catch (JSONException e) {
			System.err.println("Problem with the JSON?  That isn't good.");
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			System.err.println("Check your URL for correctness.");
			System.err.println("'"+url+"' is malformed for some reason.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Problem opening an input stream from the URL: "+url);
			e.printStackTrace();
			return null;
		}

		return ja;
	}

	private static JSONArray getArray(String url) {
		JSONArray ja;
		try {
			URL ur = new URL(url);
			JSONTokener tokener = new JSONTokener(ur.openStream());
			ja = new JSONArray(tokener);
		} catch (JSONException e) {
			System.err.println("Problem with the JSON?  That isn't good.");
			e.printStackTrace();
			return null;
		} catch (MalformedURLException e) {
			System.err.println("Check your URL for correctness.");
			System.err.println("'"+url+"' is malformed for some reason.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("Problem opening an input stream from the URL: "+url);
			e.printStackTrace();
			return null;
		}

		return ja;
	}

}
