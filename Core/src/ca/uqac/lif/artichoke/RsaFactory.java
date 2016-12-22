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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class RsaFactory
{
	public RsaFactory()
	{
		super();
	}

	/**
	 * Creates a new peer and generates a key pair
	 * @param name
	 * @return
	 */
	public Peer newPeer(String name)
	{
		Cipher c;
		try {
			c = Cipher.getInstance("RSA");
			Peer p = new Peer(name, c);
			p.setKeyPair(generateKeyPair());
			return p;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Creates a new group and generates a key pair
	 * @param name
	 * @return
	 */
	public Group newGroup(String name)
	{
		Cipher c;
		try {
			c = Cipher.getInstance("RSA");
			Group g = new Group(name, c);
			g.setKeyPair(generateKeyPair());
			return g;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Action newAction(String name)
	{
		return new Action(name);
	}
	
	public KeyPair generateKeyPair()
	{
		try {
			return KeyPairGenerator.getInstance("RSA").generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
