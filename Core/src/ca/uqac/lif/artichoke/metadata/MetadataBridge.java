package ca.uqac.lif.artichoke.metadata;

import java.io.IOException;

/**
 * Interface defining methods for reading and writing a string as metadata in
 * a particular file format.
 */
public interface MetadataBridge 
{
	public String read() throws IOException;
	
	public void write(String s) throws IOException;
}
