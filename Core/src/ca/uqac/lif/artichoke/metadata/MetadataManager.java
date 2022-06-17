package ca.uqac.lif.artichoke.metadata;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.InvalidHistoryException;

public class MetadataManager 
{
	protected HistoryManager m_manager;
	
	/**
	 * The key used to store the peer-action sequence
	 */
	public static final String s_keyName = "arthis";
	
	/**
	 * Reads metadata from a document.
	 * @param is An input stream open on the document to read metadata from
	 * @return The metadata object
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	/*@ non_null @*/ public static Metadata readMetadata(InputStream is) throws IOException, TikaException, SAXException
	{
		Parser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		ParseContext context = new ParseContext();
		parser.parse(is, handler, metadata, context);
		return metadata;
	}
	
	public MetadataManager(HistoryManager manager)
	{
		super();
		m_manager = manager;
	}
	
	public void appendAction(Metadata mdata, String peer_name, String action_name, String group_name) throws EncryptionException
	{
		String encoded_string = mdata.get(s_keyName);
		if (encoded_string == null)
		{
			encoded_string = "";
		}
		History h = m_manager.deserializeHistory(encoded_string);
		m_manager.appendAction(h, peer_name, action_name, group_name);
		String new_encoded_string = m_manager.serializeHistory(h);
		mdata.set(s_keyName, new_encoded_string);
	}
	
	public boolean isHistoryValid(Metadata mdata) throws InvalidHistoryException, EncryptionException
	{
		String encoded_string = mdata.get(s_keyName);
		if (encoded_string == null || encoded_string.isEmpty())
		{
			return true;
		}
		History h = m_manager.deserializeHistory(encoded_string);
		return m_manager.isHistoryValid(h);
	}

}
