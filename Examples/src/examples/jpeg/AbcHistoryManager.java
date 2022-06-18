package examples.jpeg;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.RsaFactory;

public class AbcHistoryManager extends HistoryManager
{
	public AbcHistoryManager() throws NoSuchAlgorithmException
	{
		super(MessageDigest.getInstance("MD5"), new RsaFactory());
		Map<String,Action> actions = new HashMap<String,Action>();
		actions.put("a", m_encryptionFactory.newAction("a"));
		actions.put("b", m_encryptionFactory.newAction("b"));
		actions.put("c", m_encryptionFactory.newAction("c"));
		setActionDirectory(actions);
	}
}
