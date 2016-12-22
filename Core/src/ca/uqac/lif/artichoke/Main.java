package ca.uqac.lif.artichoke;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

public class Main 
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String filename = "";
		AutoDetectParser parser = new AutoDetectParser();
	    BodyContentHandler handler = new BodyContentHandler();
	    Metadata metadata = new Metadata();

	}

}
