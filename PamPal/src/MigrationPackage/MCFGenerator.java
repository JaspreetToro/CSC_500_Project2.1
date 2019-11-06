package MigrationPackage;

import java.util.HashMap;
import java.io.*;
import java.util.List;
import java.util.Map;

public class MCFGenerator {

	/*
	k: number of ports each switch has, or number of PODs in this fattree data center;
	l: number of VM pairs that are randomly placed onto the PMs initially
	m: number of middleboxes, of different type, in the data center
	rc: the initial resource capacity of each PM
	lambda: the communication frequency of each VM pair is a random number between [0, lambda]
	mu: migration coefficient
	 */
	public static void GenerateFile(HashMap<Integer, List<Integer>> topoMap, int k,String path) {

		try {

			int numVMs = topoMap.size();
			int numPms = (int)(Math.pow(k, 3)/4);
			int numNodes = numVMs + numPms + 2;
			int arcs = numVMs * 15;
			int supply = 10;

			FileOutputStream outputStream = new FileOutputStream(path + "sample.inp");
			String fileContent1 = "p min " + numNodes + " " + arcs + " \r\n";	
			String fileContent2 = "c min-cost flow problem with " + numNodes + " nodes and " + arcs + " arcs\r\n";
			String fileContent3 = "n 1 " + supply + "\r\n";
			String fileContent4 = "c supply of " + supply + " at node 1\r\n";
			String fileContent5 = "n " + numNodes + " -" + supply + "\r\n";
			String fileContent6 = "c demand of " + supply + " at node " + numNodes + "\r\n";
			String fileContent7 = "c arc list follows\r\n";
			String fileContent8 = "c arc has <tail> <head> <capacity l.b.> <capacity u.b> <cost>\r\n";	    

			byte[] strToBytes1 = fileContent1.getBytes();
			byte[] strToBytes2 = fileContent2.getBytes();
			byte[] strToBytes3 = fileContent3.getBytes();
			byte[] strToBytes4 = fileContent4.getBytes();
			byte[] strToBytes5 = fileContent5.getBytes();
			byte[] strToBytes6 = fileContent6.getBytes();
			byte[] strToBytes7 = fileContent7.getBytes();
			byte[] strToBytes8 = fileContent8.getBytes();

			outputStream.write(strToBytes1);
			outputStream.write(strToBytes2);
			outputStream.write(strToBytes3);
			outputStream.write(strToBytes4);
			outputStream.write(strToBytes5);
			outputStream.write(strToBytes6);
			outputStream.write(strToBytes7);
			outputStream.write(strToBytes8);

			String temp;
			int vmIndex = 0 ;
			int j = 0;
			for(Map.Entry<Integer, List<Integer>> entry : topoMap.entrySet()) {
				for(int i = 0; i < entry.getValue().size(); i++) {
					
					if(vmIndex == entry.getKey()) {
						vmIndex++;
					}

					int n = entry.getValue().get(j);
					temp = "a" + " " + entry.getKey() + " " + vmIndex + " " + "0" + " " + SDN.ShorestDistance(k, entry.getKey(), vmIndex)+ " " + n + "\r\n";
					byte[] tempBytes = temp.getBytes();
					outputStream.write(tempBytes);
					vmIndex++;
					j++;
				}
				j=0;
				vmIndex=0;
			}

			outputStream.close();
		}catch(Exception e){
			System.out.println("IOException has been thrown.\n" + e.getMessage() );
		}
	}
}