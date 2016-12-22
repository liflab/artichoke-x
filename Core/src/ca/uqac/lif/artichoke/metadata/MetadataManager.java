package ca.uqac.lif.artichoke.metadata;

import org.apache.tika.metadata.Metadata;

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
