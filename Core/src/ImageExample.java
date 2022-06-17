import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.RsaFactory;
import ca.uqac.lif.artichoke.metadata.MetadataManager;

public class ImageExample 
{

	public static void main(String[] args) throws IOException, SAXException, TikaException, NoSuchAlgorithmException
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

		File file = new File("/tmp/Oli.jpg");
		FileInputStream inputstream = new FileInputStream(file);
		Metadata m = MetadataManager.readMetadata(inputstream);

		//MetadataManager man = new MetadataManager();


		//getting the list of all meta data elements 
		String[] metadataNames = m.names();

		for(String name : metadataNames) {		        
			System.out.println(name + ": " + m.get(name));
		}
	}
}
