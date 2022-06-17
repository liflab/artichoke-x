package ca.uqac.lif.artichoke;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface EncryptionFactory
{
	public KeyPair generateKeyPair();
	
	/**
	 * Creates a new group and generates a key pair
	 * @param name
	 * @return
	 */
	public Group newGroup(String name);
	
	public Group newGroup(String name, PublicKey k_public, PrivateKey k_private);
	
	/**
	 * Creates a new peer and generates a key pair
	 * @param name
	 * @return
	 */
	public Peer newPeer(String name);
	
	public Peer newPeer(String name, PublicKey k_public, PrivateKey k_private);
	
	public Action newAction(String name);
	
	public PrivateKey readPrivateKey(byte[] contents);
	
	public PublicKey readPublicKey(byte[] contents);
}
