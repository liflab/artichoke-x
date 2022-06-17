import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryManager;
import ca.uqac.lif.artichoke.InvalidHistoryException;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.RsaFactory;
import ca.uqac.lif.artichoke.metadata.JpegExifMetadataBridge;
import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;

public class Read 
{
	public static void main(String[] args) throws NoSuchAlgorithmException, FileSystemException, IOException, InvalidHistoryException, EncryptionException
	{
		// Read settings
		RsaFactory factory = new RsaFactory();
		MessageDigest digest = MessageDigest.getInstance("MD5");
		HistoryManager hm = new HistoryManager(digest);
		FileSystem fs = new HardDisk("/tmp/artitest");
		fs.open();
		hm.read(fs, factory);
		fs.close();
		Peer alice = hm.getPeer("Alice");
		Group g = hm.getGroup("G1");

		// Retrieve sequence from file
		FileSystem fs_in = new HardDisk("/tmp");
		fs_in.open();
		FileProxy file = new FileProxy(fs_in, "Oli.jpg");
		JpegExifMetadataBridge bridge = new JpegExifMetadataBridge(file);
		String s = bridge.read();
		System.out.println(s);
		History h = hm.deserializeHistory(s);
		fs_in.close();
		System.out.println(h);
		if (hm.isHistoryValid(h))
		{
			System.out.println("History is valid");
		}
		else
		{
			System.out.println("History is not valid");
		}
	}

}
