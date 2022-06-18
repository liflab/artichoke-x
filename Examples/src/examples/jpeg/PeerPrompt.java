package examples.jpeg;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.InvalidHistoryException;
import ca.uqac.lif.artichoke.metadata.JpegExifMetadataBridge;
import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;

public class PeerPrompt {

	public static void main(String[] args) throws FileSystemException, IOException, NoSuchAlgorithmException, InvalidHistoryException, EncryptionException
	{
		// Get filename
		String filename = args[0];

		// Read settings
		String peer_name = args[1];
		AbcHistoryManager hm = new AbcHistoryManager();
		hm.readFromZip("/tmp/artitest/" + peer_name + ".zip");

		// Read document
		History h = null;
		{
			FileSystem fs_in = new HardDisk("/");
			fs_in.open();
			FileProxy file = new FileProxy(fs_in, filename);
			JpegExifMetadataBridge bridge = new JpegExifMetadataBridge(file);
			h = hm.deserializeHistory(bridge.read());
			fs_in.close();
		}

		// Do something
		String action = args[2];

		// Print sequence
		if (action.compareToIgnoreCase("show") == 0)
		{
			hm.printDecrypted(h, System.out);
			System.exit(0);
		}

		// Verify validity of sequence
		if (action.compareToIgnoreCase("verify") == 0)
		{
			if (hm.isHistoryValid(h))
			{
				System.out.println("History is valid");
			}
			else
			{
				System.out.println("History is not valid");
			}
			System.exit(0);
		}

		// Verify validity of sequence
		if (action.compareToIgnoreCase("append") == 0)
		{
			String action_name = args[3];
			String group_name = args[4];
			hm.appendAction(h, hm.getPeer(peer_name), hm.getAction(action_name), hm.getGroup(group_name));
			FileSystem fs_out = new HardDisk("/");
			fs_out.open();
			FileProxy file = new FileProxy(fs_out, filename);
			JpegExifMetadataBridge bridge = new JpegExifMetadataBridge(file);
			String sh = hm.serializeHistory(h);
			bridge.write(sh);
			fs_out.close();
		}
	}
}
