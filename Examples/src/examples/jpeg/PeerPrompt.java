package examples.jpeg;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.artichoke.Action;
import ca.uqac.lif.artichoke.EncryptionException;
import ca.uqac.lif.artichoke.Group;
import ca.uqac.lif.artichoke.History;
import ca.uqac.lif.artichoke.HistoryElement;
import ca.uqac.lif.artichoke.InvalidHistoryException;
import ca.uqac.lif.artichoke.Peer;
import ca.uqac.lif.artichoke.Policy;
import ca.uqac.lif.artichoke.PolicyViolationException;
import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;
import ca.uqac.lif.fs.memento.JpegExifMemento;
import examples.jpeg.AnsiPrinter.Color;

public class PeerPrompt
{
	public static void main(String[] args) throws FileSystemException, IOException, NoSuchAlgorithmException, InvalidHistoryException, EncryptionException
	{
		// Standard streams with ANSI colors
		AnsiPrinter out = new AnsiPrinter(System.out);
		AnsiPrinter err = new AnsiPrinter(System.err);
		
		// Get filename
		String filename = args[0];

		// Read settings
		String peer_config = args[1];
		String peer_name = getPeerName(args[1]);
		if (peer_name == null)
		{
			err.println("No peer configuration found");
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
			hm.printDecrypted(h, out);
			System.exit(0);
		}

		// Verify validity of sequence
		if (action.compareToIgnoreCase("verify") == 0)
		{
			if (hm.isHistoryValid(h))
			{
				out.setForegroundColor(Color.GREEN);
				out.println("History is valid");
				out.resetColors();
			}
			else
			{
				out.setForegroundColor(Color.RED);
				out.println("History is not valid");
				out.resetColors();
			}
			System.exit(0);
		}

		// Dumps the raw metadata
		if (action.compareToIgnoreCase("dump") == 0)
		{
			out.println(raw_dump);
			System.exit(0);
		}

		// Appends an action to the sequence
		if (action.compareToIgnoreCase("append") == 0)
		{
			String action_name = args[3];
			String group_name = args[4];
			hm.appendAction(h, hm.getPeer(peer_name), hm.getAction(action_name), hm.getGroup(group_name));
			save(hm, h, filename);
		}
		
		// "Maliciously" alters an event of the sequence
		if (action.compareToIgnoreCase("malicious") == 0)
		{
			String malicious_action = args[3];
			if (malicious_action.compareToIgnoreCase("modify") == 0)
			{
				int index = Integer.parseInt(args[4].trim());
				if (index >= h.size() || index < 0)
				{
					err.println("Out of bounds");
					System.exit(1);
				}
				HistoryElement elem = h.get(index);
				String to_modify = args[5];
				String value = args[6];
				switch (to_modify)
				{
				case "action": {
					hm.alterAction(h, index, elem.getPeer(), hm.getAction(value), elem.getGroup());
					break; }
				case "peer": {
					Group g = elem.getGroup();
					hm.alterAction(h, index, hm.getPeer(value), hm.getAction(new String(g.decryptAction(elem.getAction()))), elem.getGroup());
					break; }
				case "group": {
					Group g = elem.getGroup();
					hm.alterAction(h, index, elem.getPeer(), hm.getAction(new String(g.decryptAction(elem.getAction()))), hm.getGroup(value));
					break; }
				default:
					System.err.println("Cannot modify");
					System.exit(1);
				}
				save(hm, h, filename);
			}
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
			case "twob":
				p = new NobodyTwoB();
				break;
			}
			if (p == null)
			{
				err.println("No such policy");
				System.exit(1);
			}
			try
			{
				boolean b = hm.evaluate(h, p);
				out.println("Policy " + p + " is " + (b ? "satisfied" : "violated"));
			}
			catch (PolicyViolationException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected static void save(AbcHistoryManager hm, History h, String filename) throws FileSystemException
	{
		FileSystem fs_out = new HardDisk(".");
		fs_out.open();
		FileProxy file = new FileProxy(fs_out, filename);
		JpegExifMemento bridge = new JpegExifMemento(file);
		String sh = hm.serializeHistory(h);
		bridge.write(sh);
		fs_out.close();
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
