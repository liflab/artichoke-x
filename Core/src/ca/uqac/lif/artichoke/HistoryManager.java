/*
    Artichoke, enforcement of document lifecycles
    Copyright (C) 2016-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.artichoke;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.FileUtils;

public class HistoryManager 
{
	MessageDigest m_digest;

	Map<String,Peer> m_peerDirectory;

	Map<String,Group> m_groupDirectory;

	Map<String,Action> m_actionDirectory;

	static String s_historySeparator = "#";

	static String s_elementSeparator = ":";

	public HistoryManager(MessageDigest digest)
	{
		super();
		m_digest = digest;
		m_peerDirectory = new HashMap<String,Peer>();
		m_groupDirectory = new HashMap<String,Group>();
		m_actionDirectory = new HashMap<String,Action>();
	}

	public void add(Peer ... peers)
	{
		for (Peer p : peers)
		{
			m_peerDirectory.put(p.getName(), p);
		}
	}

	public void add(Group ... groups)
	{
		for (Group g : groups)
		{
			m_groupDirectory.put(g.getName(), g);
		}
	}

	public void add(Action ... actions)
	{
		for (Action a : actions)
		{
			m_actionDirectory.put(a.getName(), a);
		}
	}

	public void setPeerDirectory(Map<String,Peer> directory)
	{
		m_peerDirectory = directory;
	}

	public void setActionDirectory(Map<String,Action> directory)
	{
		m_actionDirectory = directory;
	}

	public void setGroupDirectory(Map<String,Group> directory)
	{
		m_groupDirectory = directory;
	}

	public void appendAction(History history, Peer p, Action a, Group g) throws EncryptionException
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
		byte[] new_digest = m_digest.digest(digest_string);
		byte[] encrypted_digest = p.encryptDigest(new_digest);
		// Create the new history element
		HistoryElement new_he = new HistoryElement(encrypted_action, p, g, encrypted_digest);
		history.add(new_he);
	}

	public void appendAction(History history, String peer_name, String action_name, String group_name) throws EncryptionException
	{
		Peer p = m_peerDirectory.get(peer_name);
		Group g = m_groupDirectory.get(group_name);
		Action a = m_actionDirectory.get(action_name);
		appendAction(history, p, a, g);
	}

	protected byte[] concatenateDigest(byte[] last_digest, byte[] encrypted_action, Group g)
	{
		byte[] group_bytes = g.getName().getBytes();
		byte[] concatenated_bytes = new byte[last_digest.length + encrypted_action.length + group_bytes.length];
		int offset = 0;
		for (int i = 0; i < last_digest.length; i++)
		{
			concatenated_bytes[offset + i] = last_digest[i];
		}
		offset += last_digest.length;
		for (int i = 0; i < encrypted_action.length; i++)
		{
			concatenated_bytes[offset + i] = encrypted_action[i];
		}
		offset += encrypted_action.length;
		for (int i = 0; i < group_bytes.length; i++)
		{
			concatenated_bytes[offset + i] = group_bytes[i];
		}
		return concatenated_bytes;
	}

	public String serializeHistory(History history)
	{
		StringBuilder sb = new StringBuilder();
		for (HistoryElement he : history)
		{
			String s = serializeHistoryElement(he);
			sb.append(s).append(s_historySeparator);
		}
		Base64.Encoder encoder = Base64.getEncoder();
		String encoded_string = encoder.encodeToString(sb.toString().getBytes());
		return encoded_string;
	}

	protected String serializeHistoryElement(HistoryElement he)
	{
		Base64.Encoder encoder = Base64.getEncoder();
		StringBuilder sb = new StringBuilder();
		sb.append(encoder.encodeToString(he.getAction()));
		sb.append(s_elementSeparator);
		sb.append(he.getPeer().getName());
		sb.append(s_elementSeparator);
		sb.append(he.getGroup().getName());
		sb.append(s_elementSeparator);
		sb.append(encoder.encodeToString(he.getDigest()));
		return sb.toString();
	}

	public History deserializeHistory(String s)
	{
		History history = new History();
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] decoded_bytes = decoder.decode(s);
		String decoded_string = new String(decoded_bytes);
		String[] parts = decoded_string.split(s_historySeparator);
		for (String part : parts)
		{
			HistoryElement he = deserializeHistoryElement(part);
			if (he != null)
			{
				history.add(he);
			}
		}
		return history;
	}

	protected HistoryElement deserializeHistoryElement(String s)
	{
		String[] parts = s.split(s_elementSeparator);
		if (parts.length != 4)
		{
			// Error
			return null;
		}
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] encrypted_action = decoder.decode(parts[0]);
		if (!m_peerDirectory.containsKey(parts[1]))
		{
			return null;
		}
		Peer p = m_peerDirectory.get(parts[1]);
		if (!m_groupDirectory.containsKey(parts[2]))
		{
			return null;
		}
		Group g = m_groupDirectory.get(parts[2]);
		byte[] encrypted_digest = decoder.decode(parts[3]);
		HistoryElement he = new HistoryElement(encrypted_action, p, g, encrypted_digest);
		return he;
	}

	public boolean isHistoryValid(History history) throws InvalidHistoryException, EncryptionException
	{
		for (int i = history.size() - 1; i >= 1; i--)
		{
			HistoryElement current_element = history.get(i);
			HistoryElement previous_element = history.get(i-1);
			Peer current_peer = current_element.getPeer();
			byte[] decrypted_digest = current_peer.decryptDigest(current_element.getDigest());
			byte[] next_to_last_digest = previous_element.getDigest();
			byte[] encrypted_action = current_element.getAction();
			Group g = current_element.getGroup();
			byte[] digest_string = concatenateDigest(next_to_last_digest, encrypted_action, g);
			byte[] expected_digest = m_digest.digest(digest_string);
			if (!Arrays.equals(expected_digest, decrypted_digest))
			{
				// Digests don't match
				throw new InvalidHistoryException("Unexpected digest on element " + i);
			}
		}
		return true;
	}

	public boolean evaluate(History h, Policy p) throws PolicyViolationException
	{
		p.reset();
		for (HistoryElement he : h)
		{
			Group g = he.getGroup();
			String action_name;
			try
			{
				action_name = new String(g.decryptAction(he.getAction()));
			}
			catch (EncryptionException e)
			{
				throw new PolicyViolationException(e);
			}
			Action a = m_actionDirectory.get(action_name);
			p.evaluate(he.getPeer(), a, he.getGroup());
		}
		return true;
	}
	
	public Peer getPeer(String name)
	{
		if (m_peerDirectory.containsKey(name))
		{
			return m_peerDirectory.get(name);
		}
		return null;
	}
	
	public Group getGroup(String name)
	{
		if (m_groupDirectory.containsKey(name))
		{
			return m_groupDirectory.get(name);
		}
		return null;
	}
	
	public void write(FileSystem fs, EncryptionFactory factory) throws FileSystemException, IOException
	{
		fs.mkdir("peers");
		fs.chdir("peers");
		writePeerKeys(fs, factory);
		fs.popd();
		fs.mkdir("groups");
		fs.chdir("groups");
		writeGroupKeys(fs, factory);
		fs.popd();
	}
	
	public void read(FileSystem fs, EncryptionFactory factory) throws FileSystemException, IOException
	{
		fs.chdir("peers");
		readPeerKeys(fs, factory);
		fs.popd();
		fs.chdir("groups");
		readGroupKeys(fs, factory);
		fs.popd();
	}

	protected void writePeerKeys(FileSystem fs, EncryptionFactory factory) throws FileSystemException, IOException
	{
		for (Map.Entry<String,Peer> e : m_peerDirectory.entrySet())
		{
			Peer p = e.getValue();
			{
				OutputStream os = fs.writeTo(e.getKey() + ".pri");
				BufferedOutputStream bos = new BufferedOutputStream(os);
				bos.write(p.getPrivateKey().getEncoded());
				bos.close();
			}
			{
				OutputStream os = fs.writeTo(e.getKey() + ".pub");
				BufferedOutputStream bos = new BufferedOutputStream(os);
				bos.write(p.getPublicKey().getEncoded());
				bos.close();
			}
		}
	}
	
	protected void readPeerKeys(FileSystem fs, EncryptionFactory factory) throws FileSystemException, IOException
	{
		for (String pub_filename : FileUtils.ls(fs, ".", "^.*\\.pub$"))
		{
			String peer_name = pub_filename.replace(".pub", "");
			PublicKey k_pub = factory.readPublicKey(FileUtils.toBytes(fs.readFrom(pub_filename)));
			PrivateKey k_pri = null;
			String pri_filename = pub_filename.replace(".pub", ".pri");
			if (fs.isFile(pri_filename))
			{
				k_pri = factory.readPrivateKey(FileUtils.toBytes(fs.readFrom(pri_filename)));
			}
			Peer p = factory.newPeer(peer_name, k_pub, k_pri);
			m_peerDirectory.put(peer_name, p);
		}
	}
	
	protected void writeGroupKeys(FileSystem fs, EncryptionFactory factory) throws FileSystemException, IOException
	{
		for (Map.Entry<String,Group> e : m_groupDirectory.entrySet())
		{
			Group g = e.getValue();
			{
				OutputStream os = fs.writeTo(e.getKey() + ".pri");
				BufferedOutputStream bos = new BufferedOutputStream(os);
				bos.write(g.getPrivateKey().getEncoded());
				bos.close();
			}
			{
				OutputStream os = fs.writeTo(e.getKey() + ".pub");
				BufferedOutputStream bos = new BufferedOutputStream(os);
				bos.write(g.getPublicKey().getEncoded());
				bos.close();
			}
		}
	}
	
	public void readGroupKeys(FileSystem fs, EncryptionFactory factory) throws FileSystemException, IOException
	{
		for (String pub_filename : FileUtils.ls(fs, ".", "^.*\\.pub$"))
		{
			String group_name = pub_filename.replace(".pub", "");
			PublicKey k_pub = factory.readPublicKey(FileUtils.toBytes(fs.readFrom(pub_filename)));
			PrivateKey k_pri = null;
			String pri_filename = pub_filename.replace(".pub", ".pri");
			if (fs.isFile(pri_filename))
			{
				k_pri = factory.readPrivateKey(FileUtils.toBytes(fs.readFrom(pri_filename)));
			}
			Group g = factory.newGroup(group_name, k_pub, k_pri);
			m_groupDirectory.put(group_name, g);
		}
	}
}
