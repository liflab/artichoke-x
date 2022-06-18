package examples.jpeg;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.InvalidHistoryException;
import ca.uqac.lif.artichoke.Policy;
import ca.uqac.lif.artichoke.PolicyViolationException;
import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;
import ca.uqac.lif.fs.memento.JpegExifMemento;

public class PeerPrompt
{
	public static void main(String[] args) throws FileSystemException, IOException, NoSuchAlgorithmException, InvalidHistoryException, EncryptionException
	{
		// Get filename
		String filename = args[0];

		// Read settings
		String peer_config = args[1];
		String peer_name = getPeerName(args[1]);
		if (peer_name == null)
		{
			System.err.println("No peer configuration found");
			System.exit(1);
		}
		AbcHistoryManager hm = new AbcHistoryManager();
		hm.readFromZip(peer_config);

		// Read document
		History h = null;
		String raw_dump = "";
		{
			FileSystem fs_in = new HardDisk(".");
			fs_in.open();
			FileProxy file = new FileProxy(fs_in, filename);
			JpegExifMemento bridge = new JpegExifMemento(file);
			raw_dump = bridge.read();
			h = hm.deserializeHistory(raw_dump);
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

		// Dumps the raw metadata
		if (action.compareToIgnoreCase("dump") == 0)
		{
			System.out.println(raw_dump);
			System.exit(0);
		}

		// Appends an action to the sequence
		if (action.compareToIgnoreCase("append") == 0)
		{
			String action_name = args[3];
			String group_name = args[4];
			hm.appendAction(h, hm.getPeer(peer_name), hm.getAction(action_name), hm.getGroup(group_name));
			FileSystem fs_out = new HardDisk(".");
			fs_out.open();
			FileProxy file = new FileProxy(fs_out, filename);
			JpegExifMemento bridge = new JpegExifMemento(file);
			String sh = hm.serializeHistory(h);
			bridge.write(sh);
			fs_out.close();
		}

		// Evaluates a lifecycle property on the sequence
		if (action.compareToIgnoreCase("check") == 0)
		{
			String policy_name = args[3];
			Policy p = null;
			switch (policy_name)
			{
			case "abc":
				p = new AbcPolicy();
				break;
			case "carl":
				p = new CarlNoA();
				break;
			}
			if (p == null)
			{
				System.err.println("No such policy");
				System.exit(1);
			}
			try
			{
				boolean b = hm.evaluate(h, p);
				System.out.println("Policy " + p + " is " + (b ? "satisfied" : "violated"));
			}
			catch (PolicyViolationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected static String getPeerName(String filename)
	{
		Pattern pat = Pattern.compile("\\b([^/]*?)\\.zip$");
		Matcher mat = pat.matcher(filename);
		if (mat.find())
		{
			return mat.group(1);
		}
		return null;
	}
}
