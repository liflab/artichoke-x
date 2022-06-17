package ca.uqac.lif.artichoke.metadata;

import java.io.IOException;

import org.junit.Test;

import ca.uqac.lif.fs.FileProxy;
import ca.uqac.lif.fs.FileSystem;
import ca.uqac.lif.fs.FileSystemException;
import ca.uqac.lif.fs.HardDisk;

public class JpegExifMetadataBridgeTest
{
	@Test
	public void testRead1() throws IOException, FileSystemException
	{
		FileSystem fs = new HardDisk("/tmp");
		fs.open();
		FileProxy file = new FileProxy(fs, "Oli.jpg");
		JpegExifMetadataBridge bridge = new JpegExifMetadataBridge(file);
		System.out.println(bridge.read());
		fs.close();
	}
	
	@Test
	public void testWrite1() throws IOException, FileSystemException
	{
		FileSystem fs = new HardDisk("/tmp");
		fs.open();
		FileProxy file = new FileProxy(fs, "Oli.jpg");
		JpegExifMetadataBridge bridge = new JpegExifMetadataBridge(file);
		bridge.write("blabla afjfj");
		fs.close();
	}
}
