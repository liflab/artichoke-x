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

public class HistoryElement 
{
	private final byte[] m_encryptedAction;
	
	private final Peer m_peer;
	
	private final Group m_group;
	
	private final byte[] m_digest;
	
	public HistoryElement(byte[] action, Peer p, Group g, byte[] digest)
	{
		super();
		m_encryptedAction = action;
		m_peer = p;
		m_group = g;
		m_digest = digest;
	}
	
	public byte[] getAction()
	{
		return m_encryptedAction;
	}
	
	public Peer getPeer()
	{
		return m_peer;
	}
	
	public Group getGroup()
	{
		return m_group;
	}
	
	public byte[] getDigest()
	{
		return m_digest;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(firstBytes(m_encryptedAction, 5)).append(",");
		sb.append(m_peer).append(",").append(m_group).append(",");
		sb.append(firstBytes(m_digest, 5)).append(")");
		return sb.toString();
	}
	
	protected static StringBuilder firstBytes(byte[] array, int max_size)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Math.min(array.length, max_size); i++)
		{
			sb.append(String.format("%02X", array[i]));
		}
		return sb;
	}

}
