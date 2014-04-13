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

public class Dates {

	/* Formatters for dates.  If the format of the date from the web service API changes,
	 * then dateFormatterInput should be adjusted accordingly.
	 * 
	 * UPDATE:  Apparently these aren't thread safe.  Booooooo!
	 * They are no longer used in the project.  But I'll leave them here for historical 
	 * reference and as a mark of shame to Java for having such an innocuous class rely
	 * on non-thread safe data structures.  Boooo!
	 */
	//public final static SimpleDateFormat dateFormatterInput  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	//public final static SimpleDateFormat dateFormatterOutput = new SimpleDateFormat("yyyy-MM-dd");
	
	public final static String dateFormatInput  = "yyyy-MM-dd";
	public final static String dateFormatOutput = "yyyy-MM-dd";
}
