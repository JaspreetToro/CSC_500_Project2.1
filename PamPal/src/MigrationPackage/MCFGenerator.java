package MigrationPackage;

import java.io.*;
import java.util.List;

public class MCFGenerator {

	/*
	k: number of ports each switch has, or number of PODs in this fattree data center;
	l: number of VM pairs that are randomly placed onto the PMs initially
	m: number of middleboxes, of different type, in the data center
	rc: the initial resource capacity of each PM
	lambda: the communication frequency of each VM pair is a random number between [0, lambda]
	mu: migration coefficient
	 */
	public static void GenerateFile(List<Node> nodes, int k,int numVMs, String path,int r) {

		try {

			int numPms = (int)(Math.pow(k, 3)/4);
			int totalNodes = numVMs + numPms + 2;//6 + 16 
			int arcs = (numVMs * numPms) + numVMs + numPms;//edges and arcs
			int supply = r;

			FileOutputStream outputStream = new FileOutputStream(path + "sample.inp");
			String fileContent1 = "p min " + totalNodes + " " + arcs + " \r\n";	
			String fileContent2 = "c min-cost flow problem with " + totalNodes + " nodes and " + arcs + " arcs\r\n";
			String fileContent3 = "n 0 " + supply + "\r\n";
			String fileContent4 = "c supply of " + supply + " at node 0\r\n";
			String fileContent5 = "n " + (totalNodes-1) + " -" + supply + "\r\n";
			String fileContent6 = "c demand of " + supply + " at node " + (totalNodes-1) + "\r\n";
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


			//			c arc has <tail> <head> <capacity l.b.> <capacity u.b> <cost>
			//			a 1 2 0 4  1
			
			//lines that involve source node
			for(int i = 1; i <= numVMs;i++) {//vms = 4 , 4 lines
				String s = "a" + " " + nodes.get(0).NodeID + " " + i + " " + "0" + " " + "1" + " " + "1" + "\r\n";
				byte[] sBytes = s.getBytes();
				outputStream.write(sBytes);
			}
			
			//lines from each vm to each pm
			int pmIndex = numVMs+1;
			int listIndex = 0;
			for(int i = 1; i <= numVMs;i++) {//k = 4;vms =4, 64 lines
				for(int j = pmIndex;j < pmIndex+numPms;j++) {
					String s = "a" + " " + nodes.get(i).NodeID + " " + j + " " + "0" + " " + "1" + " " + nodes.get(i).VMM.PossibleCosts.get(listIndex) + "\r\n";
					byte[] sBytes = s.getBytes();
					outputStream.write(sBytes);
					listIndex++;
				}
				listIndex = 0;
			}
			
			//lines from each pm to endNode
			for(int i = pmIndex; i < numPms+pmIndex;i++) {//k=4, lines = 16
				String s = "a" + " " + i + " " + (totalNodes-1) + " " + "0" + " " + supply + " " + "1" + "\r\n";
				byte[] sBytes = s.getBytes();
				outputStream.write(sBytes);
			}

			//			String temp;
			//			int vmIndex = 0 ;
			//			int j = 0;
			//			for(Map.Entry<Integer, List<Integer>> entry : topoMap.entrySet()) {
			//				for(int i = 0; i < entry.getValue().size(); i++) {
			//
			//					int n = entry.getValue().get(j);
			//					temp = "a" + " " + entry.getKey() + " " + vmIndex + " " + "0" + " " + SDN.ShorestDistance(k, entry.getKey(), vmIndex)+ " " + n + "\r\n";
			//					byte[] tempBytes = temp.getBytes();
			//					outputStream.write(tempBytes);
			//					vmIndex++;
			//					j++;
			//				}
			//				j=0;
			//				vmIndex=0;
			//			}

			outputStream.close();
		}catch(Exception e){
			System.out.println("IOException has been thrown.\n" + e.getMessage() );
		}
	}
}