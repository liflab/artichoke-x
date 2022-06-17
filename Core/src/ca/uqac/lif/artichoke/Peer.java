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

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Peer 
{	
	private final String m_name;
	
	private PrivateKey m_privateKey;
	
	private PublicKey m_publicKey;
	
	private Cipher m_cipher;
	
	public Peer(String name, Cipher c)
	{
		super();
		m_name = name;
		m_cipher = c;
	}
	
	public Peer(String name, Cipher c, PublicKey k_public, PrivateKey k_private)
	{
		super();
		m_name = name;
		m_cipher = c;
		m_publicKey = k_public;
		m_privateKey = k_private;
	}
	
	public void set(PublicKey k)
	{
		m_publicKey = k;
	}
	
	public void set(PrivateKey k)
	{
		m_privateKey = k;
	}
	
	public void setKeyPair(KeyPair pair)
	{
		m_privateKey = pair.getPrivate();
		m_publicKey = pair.getPublic();
	}
	
	public void setKeyPair(PrivateKey private_key, PublicKey public_key)
	{
		m_privateKey = private_key;
		m_publicKey = public_key;
	}
	
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * Gets the peer's private key.
	 * @return The key
	 */
	public PrivateKey getPrivateKey()
	{
		return m_privateKey;
	}
	
	/**
	 * Gets the peer's public key.
	 * @return The key
	 */
	public PublicKey getPublicKey()
	{
		return m_publicKey;
	}
	
	public byte[] encryptDigest(byte[] digest) throws EncryptionException
	{
		try
		{
			m_cipher.init(Cipher.ENCRYPT_MODE, m_privateKey);
			byte[] encrypted = m_cipher.doFinal(digest);
			return encrypted;
		} 
		catch (InvalidKeyException e) 
		{
			throw new EncryptionException(e);
		} 
		catch (IllegalBlockSizeException e) 
		{
			throw new EncryptionException(e);
		} 
		catch (BadPaddingException e)
		{
			throw new EncryptionException(e);
		}
	}
	
	public byte[] decryptDigest(byte[] b) throws EncryptionException
	{
		try
		{
			m_cipher.init(Cipher.DECRYPT_MODE, m_publicKey);
			byte[] decrypted = m_cipher.doFinal(b);
			return decrypted;
		} 
		catch (InvalidKeyException e) 
		{
			throw new EncryptionException(e);
		} 
		catch (IllegalBlockSizeException e) 
		{
			throw new EncryptionException(e);
		} 
		catch (BadPaddingException e)
		{
			throw new EncryptionException(e);
		}
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}
}
