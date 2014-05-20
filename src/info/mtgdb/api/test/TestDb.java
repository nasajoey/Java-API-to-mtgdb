package info.mtgdb.api.test;

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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import info.mtgdb.api.Card;
import info.mtgdb.api.CardSet;
import info.mtgdb.api.ComplexQuery;
import info.mtgdb.api.Db;
import info.mtgdb.api.QueryElement;
import info.mtgdb.api.QueryElement.Field;
import info.mtgdb.api.QueryElement.Operator;

import org.junit.Test;
import org.junit.Ignore;

/**
 * @author Joseph Rios <joey@littlepancake.com>
 * @version 0.1
 * @since  2014-03-31
 *
 */
public class TestDb {

	@Ignore
	@Test
	public void testGetCard() {
		Card card = Db.getCard(14456);
		assertEquals("Card id should be 14456.", 14456, card.getId());
		
		//card = Db.getCard(38277);
		//System.out.println("Card name = "+card.getName());
	}
	
	@Test
	public void testComplexQuery() {
		ComplexQuery cq = new ComplexQuery();
		QueryElement qe = new QueryElement(Field.convertedmanacost, Operator.eq, 4);
		cq.addQueryElement(qe);
		qe = new QueryElement(Field.type, Operator.m, "'Creature'");
		cq.addQueryElement(qe);
		qe = new QueryElement(Field.color, Operator.eq, "blue");
		cq.addQueryElement(qe);
		qe = new QueryElement(Field.power, Operator.gte, 5);
		cq.addQueryElement(qe);
		//cq.setLimit(3);
		
		ArrayList<Card> cards = Db.getCardsByComplexQuery(cq);
		assertTrue("Color should be blue.", cards.get(0).getColors().contains("blue"));
		assertTrue("Power should be >= 5.", cards.get(0).getPower() >= 5);
		assertTrue("Should have at least 13 matches.", cards.size() >= 13);
		
		cq = new ComplexQuery();
		qe = new QueryElement(Field.type, Operator.m, "'Sorcery'");
		cq.addQueryElement(qe);
		qe = new QueryElement(Field.color, Operator.eq, "green");
		cq.addQueryElement(qe);
		qe = new QueryElement(Field.color, Operator.eq, "red");
		cq.addQueryElement(qe);
		qe = new QueryElement(Field.convertedmanacost, Operator.lt, 5);
		cq.addQueryElement(qe);
		cards = Db.getCardsByComplexQuery(cq);
		//int i = 0;
		//for( Card c : cards ) System.out.println((i++)+": "+c.getName());
		assertTrue("Color should contain green.", cards.get(0).getColors().contains("green"));
		assertTrue("Color should contain red.", cards.get(0).getColors().contains("red"));
		assertTrue("Power should be >= 5.", cards.get(0).getConvertedMonaCost() < 5);
		assertTrue("Should have at least 29 matches.", cards.size() >= 29);
	}
	
	@Ignore
	@Test
	public void testGetCardWithingSet() {
		Card card = Db.getCardWithinSet("INV", 12);
		assertNotNull("Card is null?", card);
		assertEquals("Card cost should be 2WW.", "2WW", card.getManaCost());
		assertEquals("Card type should be 'creature'.", "Creature", card.getType());
		assertEquals("Card toughness should be 2.", 2, card.getToughness());
		
		card = Db.getCardWithinSet("TSP", 88);
		assertNotNull("Card is null?", card);
		assertEquals("Card cost should be 1U.", "1U", card.getManaCost());
		assertEquals("Card type should be 'Instant'.", "Instant", card.getType());
		assertEquals("Card toughness should be 0.", 0, card.getToughness());
	}
	
	@Test
	public void testGetRandom() {
		Card card = Db.getRandom();
		assertNotNull("We didn't retrieve a random card.", card);
		
		card = Db.getRandom("ALA");
		assertNotNull("We didn't retrieve a random card from ALA.", card);
		assertEquals("Card was not from ALA.", "ALA", card.getCardSetId());
	}

	@Test
	public void testGetCardArrayByIds() {
		ArrayList<Integer> multiverseIds = new ArrayList<Integer>();
		multiverseIds.add(1);
		multiverseIds.add(2);
		ArrayList<Card> cards = Db.getCards(multiverseIds);
		assertEquals("Should have retrieved two cards.", 2, cards.size());
	}
	
	@Test
	public void testGetSet() {
		CardSet cardSet = Db.getSet("10E");
		System.out.println("10E cards: "+cardSet.getCardIds().size());
		assertEquals("The set should be 10E.", "10E", cardSet.getId());
		assertEquals("The number of cards in 10E should be 368.", 368, cardSet.getTotal());
	}
	
	@Test
	public void testGetSetCards() {
		ArrayList<Card> cards = Db.getSetCards("10E");
		assertEquals("There should be 368 cards in 10E.", 368, cards.size());
	}
	
	@Test
	public void testGetSetsArray() {
		ArrayList<String> setIds = new ArrayList<String>();
		setIds.add("10e");//"10e","all","ths"
		setIds.add("all");
		setIds.add("ths");
		
		ArrayList<CardSet> cardSets = Db.getSets(setIds);
		assertEquals("There should be 3 sets.", 3, cardSets.size());
	}
	
	@Test
	public void testGetCardsBySetRange() {
		ArrayList<Card> cards = Db.getSetCards("10E", 1, 10);
		assertEquals("There should be 10 cards.", 10, cards.size());
	}
	
	@Test
	public void testGetCardsBySetRangeNull() {
		ArrayList<Card> cards = Db.getSetCards("10E", 11, 10);
		assertNull("Should be null due to bad range.",cards);
	}
	
	@Test
	public void testGetAllSets() {
		ArrayList<CardSet> cardSets = Db.getAllSets();
		//System.out.println("num sets = "+cardSets.size());
		assertTrue("There should have been waaaaay more sets.", cardSets.size() > 20);
	}
	
	@Ignore("This test takes a few minutes to complete, so don't run it often.")
	@Test
	public void testGetAllCards() {
		ArrayList<Card> cards = Db.getCards();
		//System.out.println("num cards = "+cards.size());
		assertTrue("There should have been waaaaaay more cards.", cards.size() > 1000);
	}
	
	@Ignore
	@Test
	public void testSearchCards() {
		ArrayList<Card> cards = Db.searchCards("shock");
		assertTrue("There should have been more cards when searching for 'shock'.", cards.size() > 1);
		
		cards = Db.searchCards("coward");
		//for( Card c : cards ) {
			//System.out.println(c.getName());
		//}
		assertNotNull("Didn't retrieve and cards via search.", cards);
	}
	
	@Ignore
	@Test
	public void testSearchCardsCleaningQuery() {
		ArrayList<Card> cards = Db.searchCards("shock");
		ArrayList<Card> cardsB = Db.searchCards("s!h@o%c)k++~`");
		//System.out.println("num cards = "+cards.size());
		//System.out.println("num cards2 = "+cardsB.size());
		assertTrue("Should have retrieved the same number of cards", cards.size() == cardsB.size());
	}
	
	@Ignore
	@Test
	public void testFilterArtist() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("artist", "Jennifer Law");
		ArrayList<Card> cards = Db.filterCards(map);
		assertTrue("Should only be two cards by Jennifer Law.", cards.size() == 2);
	}
	
	@Ignore
	@Test
	public void testFilterArtistAndColors() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("artist", "Jesper Myrfors");
		map.put("colors", "blue");
		ArrayList<Card> cards = Db.filterCards(map);
		assertTrue("Should only be 16 blue cards by Jesper Myrfors.", cards.size() == 16);
	}

}

/*


    [Test()]
    public void Test_get_cards_by_name ()
    {
        Card [] cards = mtginfo.GetCards ("ankh of mishra");
        System.Console.WriteLine (cards.Length.ToString());
        Assert.GreaterOrEqual (cards.Length,1);
    }

    [Test()]
    public void Test_get_cards_by_name_no_match ()
    {
        Card [] cards = null;

        try
        {
            cards = mtginfo.GetCards ("");
        }
        catch(Exception e)
        {
            Assert.IsNull (cards);
        }
    }


}

*/