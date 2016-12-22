import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.InvalidHistoryException;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.RsaFactory;


public class Test 
{

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws EncryptionException 
	 */
	public static void main(String[] args) throws NoSuchAlgorithmException, EncryptionException 
	{
		// Populate the world
		RsaFactory factory = new RsaFactory();
		MessageDigest digest = MessageDigest.getInstance("MD5");
		HistoryManager hm = new HistoryManager(digest);
		Map<String,Peer> peers = new HashMap<String,Peer>();
		peers.put("Alice", factory.newPeer("Alice"));
		peers.put("Bob", factory.newPeer("Bob"));
		peers.put("Carl", factory.newPeer("Carl"));
		Map<String,Group> groups = new HashMap<String,Group>();
		groups.put("G1", factory.newGroup("G1"));
		groups.put("G2", factory.newGroup("G2"));
		Map<String,Action> actions = new HashMap<String,Action>();
		actions.put("a", factory.newAction("a"));
		actions.put("b", factory.newAction("b"));
		actions.put("c", factory.newAction("c"));
		hm.setPeerDirectory(peers);
		hm.setActionDirectory(actions);
		hm.setGroupDirectory(groups);
		// Create a sequence
		History h = new History();
		hm.appendAction(h, "Alice", "a", "G1");
		hm.appendAction(h, "Alice", "a", "G1");
		System.out.println(h);
		System.out.println(hm.serializeHistory(h));
		boolean b = false;
		try
		{
			b = hm.isHistoryValid(h);
		}
		catch (InvalidHistoryException e)
		{
			e.printStackTrace();
		}
		if (!b)
		{
			System.out.println("History is invalid");
		}
		else
		{
			System.out.println("History is valid");
		}
		System.out.println("Done");
	}

}
