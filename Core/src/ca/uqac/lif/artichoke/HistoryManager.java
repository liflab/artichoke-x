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

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class HistoryManager 
{
	MessageDigest m_digest;
	
	Map<String,Peer> m_peerDirectory;
	
	Map<String,Group> m_groupDirectory;
	
	Map<String,Action> m_actionDirectory;
	
	static String s_historySeparator = "|";
	
	static String s_elementSeparator = "?";
	
	public HistoryManager(MessageDigest digest)
	{
		super();
		m_digest = digest;
		m_peerDirectory = new HashMap<String,Peer>();
		m_groupDirectory = new HashMap<String,Group>();
		m_actionDirectory = new HashMap<String,Action>();
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
}
