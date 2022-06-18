package examples.jpeg;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;
import ca.uqac.lif.fs.WriteZipFile;
import ca.uqac.lif.fs.memento.JpegExifMemento;

public class Populate
{
	public static void main(String[] args) throws NoSuchAlgorithmException, FileSystemException, IOException, EncryptionException, SAXException
	{
		// Populate the world
		AbcHistoryManager hm = new AbcHistoryManager();
		Map<String,Peer> peers = new HashMap<String,Peer>();
		peers.put("Alice", hm.getEncryptionFactory().newPeer("Alice"));
		peers.put("Bob", hm.getEncryptionFactory().newPeer("Bob"));
		peers.put("Carl", hm.getEncryptionFactory().newPeer("Carl"));
		Map<String,Group> groups = new HashMap<String,Group>();
		groups.put("G1", hm.getEncryptionFactory().newGroup("G1"));
		groups.put("G2", hm.getEncryptionFactory().newGroup("G2"));
		hm.setPeerDirectory(peers);
		hm.setGroupDirectory(groups);
		
		// Create a sequence
		History h = new History();
		hm.appendAction(h, "Alice", "a", "G1");
		hm.appendAction(h, "Alice", "b", "G1");
		hm.appendAction(h, "Bob", "a", "G2");
		hm.appendAction(h, "Carl", "c", "G1");
		
		// Save peer-action sequence
		FileSystem fs_out = new HardDisk(".");
		fs_out.open();
		FileProxy file = new FileProxy(fs_out, args[0]);
		JpegExifMemento bridge = new JpegExifMemento(file);
		String sh = hm.serializeHistory(h);
		bridge.write(sh);
		fs_out.close();

		// Save keyring
		FileSystem fs_save = new HardDisk(".");
		fs_save.open();
		{
			OutputStream os = fs_save.writeTo("All.zip");
			WriteZipFile zip = new WriteZipFile(os);
			zip.open();
			hm.write(zip);
			zip.close();
			os.close();
		}
		// Save individual keyrings
		saveKeyring(fs_save, hm, "Alice", "G1");
		saveKeyring(fs_save, hm, "Bob", "G2");
		saveKeyring(fs_save, hm, "Carl", "G1");
	}

	protected static void saveKeyring(FileSystem fs, HistoryManager hm, String peer_name, String group_name) throws FileSystemException, IOException
	{
		OutputStream os = fs.writeTo(peer_name + ".zip");
		WriteZipFile zip = new WriteZipFile(os);
		zip.open();
		zip.pushd("peers");
		hm.writePeerKeys(zip, peer_name);
		zip.popd();
		zip.pushd("groups");
		hm.writeGroupKeys(zip, group_name);
		zip.popd();
		zip.close();
		os.close();
	}
}
