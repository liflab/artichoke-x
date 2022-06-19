package examples.jpeg;

import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryElement;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.RsaFactory;
import examples.jpeg.AnsiPrinter.Color;

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
	
	public void alterAction(History history, int index, Peer p, Action a, Group g) throws EncryptionException
	{
		byte[] last_digest = new byte[]{0};
		if (!history.isEmpty())
		{
			HistoryElement he = history.get(history.size() - 1);
			last_digest = he.getDigest();
		}
		// Encrypt action and compute digest
		byte[] encrypted_action = g.encryptAction(a);
		byte[] digest_string = concatenateDigest(last_digest, encrypted_action, g);
		byte[] new_digest = getDigest().digest(digest_string);
		byte[] encrypted_digest = p.encryptDigest(new_digest);
		// Create the new history element
		HistoryElement new_he = new HistoryElement(encrypted_action, p, g, encrypted_digest);
		history.set(index, new_he);
	}
	
	public void insertAction(History history, int index, Peer p, Action a, Group g) throws EncryptionException
	{
		byte[] last_digest = new byte[]{0};
		if (!history.isEmpty())
		{
			HistoryElement he = history.get(history.size() - 1);
			last_digest = he.getDigest();
		}
		// Encrypt action and compute digest
		byte[] encrypted_action = g.encryptAction(a);
		byte[] digest_string = concatenateDigest(last_digest, encrypted_action, g);
		byte[] new_digest = getDigest().digest(digest_string);
		byte[] encrypted_digest = p.encryptDigest(new_digest);
		// Create the new history element
		HistoryElement new_he = new HistoryElement(encrypted_action, p, g, encrypted_digest);
		history.add(index, new_he);
	}
	
	public void printDecrypted(History h, AnsiPrinter ps)
	{
		for (HistoryElement he : h)
		{
			Group g = he.getGroup();
			String action_name = "?";
			try
			{
				String name = new String(g.decryptAction(he.getAction()));
				Action a = m_actionDirectory.get(name);
				action_name = a.getName();
			}
			catch (EncryptionException e)
			{
				action_name = "X";
			}
			ps.print("(");
			ps.setForegroundColor(Color.YELLOW);
			ps.print(he.getPeer());
			ps.resetColors();
			ps.print(",");
			if (action_name.compareTo("X") == 0)
			{
				ps.setBackgroundColor(Color.RED);
			}
			else
			{
				ps.setForegroundColor(Color.LIGHT_CYAN);
			}
			ps.print(action_name);
			ps.resetColors();
			ps.print(",");
			ps.setForegroundColor(Color.LIGHT_PURPLE);
			ps.print(he.getGroup());
			ps.resetColors();
			ps.print(")");
		}
	}
	
	public byte[] concatenateDigest(byte[] last_digest, byte[] encrypted_action, Group g)
	{
		return super.concatenateDigest(last_digest, encrypted_action, g);
	}
	
	public MessageDigest getDigest()
	{
		return m_digest;
	}
}
