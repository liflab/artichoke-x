import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.xml.sax.SAXException;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.RsaFactory;
import ca.uqac.lif.artichoke.metadata.JpegExifMetadataBridge;
import ca.uqac.lif.artichoke.metadata.MetadataManager;
import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;

public class Populate {

	public static void main(String[] args) throws NoSuchAlgorithmException, FileSystemException, IOException, EncryptionException, TikaException, SAXException
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
		// Save peer-action sequence
		FileSystem fs_out = new HardDisk("/tmp");
		fs_out.open();
		FileProxy file = new FileProxy(fs_out, "Oli.jpg");
		JpegExifMetadataBridge bridge = new JpegExifMetadataBridge(file);
		String s = hm.serializeHistory(h);
		System.out.println(s);
		bridge.write(s);
		fs_out.close();
		
		// Save keyring
		FileSystem fs_save = new HardDisk("/tmp/artitest");
		fs_save.open();
		hm.write(fs_save, factory);
		fs_save.close();
		
	}

}
