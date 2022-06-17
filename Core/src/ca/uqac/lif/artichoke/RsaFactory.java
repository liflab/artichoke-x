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

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class RsaFactory implements EncryptionFactory
{
	public RsaFactory()
	{
		super();
	}

	@Override
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
	
	@Override
	public Peer newPeer(String name, PublicKey k_public, PrivateKey k_private)
	{
		Cipher c;
		try {
			c = Cipher.getInstance("RSA");
			Peer p = new Peer(name, c, k_public, k_private);
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
	
	@Override
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
	
	@Override
	public Group newGroup(String name, PublicKey k_pub, PrivateKey k_pri)
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
	
	@Override
	public Action newAction(String name)
	{
		return new Action(name);
	}
	
	@Override
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

	@Override
	public PrivateKey readPrivateKey(byte[] contents)
	{
		try
		{
			KeyFactory kf = KeyFactory.getInstance("RSA");
			KeySpec keyspec = new PKCS8EncodedKeySpec(contents);
			return kf.generatePrivate(keyspec);
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidKeySpecException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PublicKey readPublicKey(byte[] contents)
	{
		try
		{
			KeyFactory kf = KeyFactory.getInstance("RSA");
			KeySpec keyspec = new X509EncodedKeySpec(contents);
			return kf.generatePublic(keyspec);
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidKeySpecException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
