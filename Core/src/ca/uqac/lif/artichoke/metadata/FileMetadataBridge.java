package ca.uqac.lif.artichoke.metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.FileUtils;

public abstract class FileMetadataBridge implements MetadataBridge
{
	/**
	 * The file from which to extract the metadata.
	 */
	/*@ non_null @*/ protected final FileProxy m_file;
	
	/**
	 * A byte array preserving the contents of the file the last time
	 * it was read.
	 * 
	 */
	/*@ non_null @*/ protected byte[] m_fileContents;
	
	public FileMetadataBridge(FileProxy file)
	{
		super();
		m_file = file;
		m_fileContents = new byte[0];
	}
	
	@Override
	public String read() throws IOException
	{
		fetchFileContents();
		try 
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(m_fileContents);
			String s = read(bais);
			bais.close();
			return s;
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void write(String s) throws IOException
	{
		fetchFileContents();
		try 
		{
			OutputStream os = m_file.writeTo();
			write(s, os);
			os.close();
		} 
		catch (FileSystemException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void fetchFileContents() throws IOException
	{
		InputStream is;
		try 
		{
			is = m_file.readFrom();
			m_fileContents = FileUtils.toBytes(is);
			is.close();
		} 
		catch (FileSystemException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected abstract String read(InputStream is) throws IOException;
	
	protected abstract void write(String s, OutputStream os) throws IOException;

}
