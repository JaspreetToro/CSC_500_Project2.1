package outputstream;
import java.io.*;

public class outputstream 
{
	public static void main(String args[]) throws IOException 
	{
		FileOutputStream outputStream = new FileOutputStream("D:/sample.inp");
		String fileContent1 = "p min 6 8";	
		String lineSeparator1 = System.getProperty("line.separator");
		String fileContent2 = "c min-cost flow problem with 6 nodes and 8 arcs";
		String lineSeparator2 = System.getProperty("line.separator");
		String fileContent3 = "n 1 10";
		String lineSeparator3 = System.getProperty("line.separator");
		String fileContent4 = "c supply of 10 at node 1";
		String lineSeparator4 = System.getProperty("line.separator");
		String fileContent5 = "n 6 -10";
		String lineSeparator5 = System.getProperty("line.separator");
		String fileContent6 = "c demand of 10 at node 6";
		String lineSeparator6 = System.getProperty("line.separator");
		String fileContent7 = "c arc list follows";
		String lineSeparator7 = System.getProperty("line.separator");
		String fileContent8 = "c arc has <tail> <head> <capacity l.b.> <capacity u.b> <cost>";
		
		
 
	    
	    
	    byte[] strToBytes1 = fileContent1.getBytes();
	    byte[] strToBytes2 = fileContent2.getBytes();
	    byte[] strToBytes3 = fileContent3.getBytes();
	    byte[] strToBytes4 = fileContent4.getBytes();
	    byte[] strToBytes5 = fileContent5.getBytes();
	    byte[] strToBytes6 = fileContent6.getBytes();
	    byte[] strToBytes7 = fileContent7.getBytes();
	    byte[] strToBytes8 = fileContent8.getBytes();
	    
	    outputStream.write(strToBytes1);
	    outputStream.write(lineSeparator1.getBytes());
	    outputStream.write(strToBytes2);
	    outputStream.write(lineSeparator2.getBytes());
	    outputStream.write(strToBytes3);
	    outputStream.write(lineSeparator3.getBytes());
	    outputStream.write(strToBytes4);
	    outputStream.write(lineSeparator4.getBytes());
	    outputStream.write(strToBytes5);
	    outputStream.write(lineSeparator5.getBytes());
	    outputStream.write(strToBytes6);
	    outputStream.write(lineSeparator6.getBytes());
	    outputStream.write(strToBytes7);
	    outputStream.write(lineSeparator7.getBytes());
	    outputStream.write(strToBytes8);
	    outputStream.close();
	}
}
