package MigrationPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.*;

public class MCF_Migration {

	public static int k;
	public static int numPms;									
	public static int numEdges;	
	public static int numAggs;  
	public static int edgeStart;									
	public static int edgeEnd;							
	public static int aggStart;
	//static HashMap<Integer, List<Integer>> topoMap = new HashMap<Integer, List<Integer>>();
	public static int mbDistance;
	public static int firstMB; 
	public static int lastMB;
	public static int migration;
	public static List<Node> Nodes = new ArrayList<Node>();;


	@SuppressWarnings("unused")
	public static void main(String[] args) {

		//call other class to begin generating the file. Pass over user input.
		//k,rc,vmpairs,mbs,frequency,migration
		int[] inputs = SDN.GetInput();

		Node nodeZero = new Node(0,1,null);
		Nodes.add(nodeZero);

		String path = "";
		//Set up for testing
		//SetUpMinCost(4,4,2,2,2,1);

		//generate mcf_migration.inp file
		SetUpMinCost(inputs[0],inputs[1],inputs[2],inputs[3],inputs[4],inputs[5], path);
		k = 0;
	}

	//									4,2,2,4,2,1
	public static void SetUpMinCost(int numPods,int resCap, int vmPairs,int boxes, int frequency,int migCoe, String path) {

		k = numPods;
		migration = migCoe;
		numPms = (int)(Math.pow(k, 3)/4);	//		2 			16										
		numEdges = (int)(Math.pow(k, 2)/2);	// 		2			8
		numAggs = (int)(Math.pow(k, 2)/2);  //		2			8
		edgeStart = numPms ;												// 2			16
		edgeEnd = numPms + numEdges -1;									// 3			23
		aggStart = edgeEnd +1;	

		List<Integer> vmPairLocation = VMPairsLocation(vmPairs,resCap);
		List<Integer> MBsLocation = MBsLocation(boxes);
		mbDistance = MBDistance(MBsLocation);
		List<Integer> frequencies = GetFrequencies(vmPairs, frequency);

		migCost(vmPairLocation, frequencies);
		CreatePmNodes(vmPairLocation.size());

		PrintParameters(numPods, resCap,vmPairLocation,MBsLocation,frequencies,migCoe,path);
		//generate mcf_migration.inp file	

		Node endNode = new Node();
		endNode.NodeID = vmPairLocation.size() + numPms + 1;
		endNode.Cost = -1;
		endNode.VMM = null;
		Nodes.add(endNode);

		MCFGenerator.GenerateFile(Nodes, k, vmPairs*2,path,resCap);
		
	}

	private static void CreatePmNodes(int numVM) {

		int NodeIndex = numVM +1;
		for(int i = 1; i <= numPms;i++) {
			Node tempNode = new Node();
			tempNode.NodeID = NodeIndex +  i;
			tempNode.Cost = 1;
			tempNode.VMM = null;
		}

	}

	private static void PrintParameters(int numPods, int resCap, List<Integer> vmPairLocation,
			List<Integer> mBsLocation, List<Integer> frequencies, int migCoe,String path) {
		FileOutputStream outputStream;
		try {

			outputStream = new FileOutputStream(path + "Parameters.txt");

			//k
			String k = "k = " + numPods+ "\r\n";
			byte[] kBytes = k.getBytes();
			outputStream.write(kBytes);

			//resCap
			String rc = "ResCap = " + resCap+ "\r\n";
			byte[] rcBytes = rc.getBytes();
			outputStream.write(rcBytes);

			//vms
			String vm = "VM Locations: " + vmPairLocation.toString()+ "\r\n";
			byte[] vmBytes = vm.getBytes();
			outputStream.write(vmBytes);

			//mbs
			String mb = "MB Locations: " + mBsLocation.toString()+ "\r\n";
			byte[] mbBytes = mb.getBytes();
			outputStream.write(mbBytes);

			//f
			String f = "Frequencies: " + frequencies.toString()+ "\r\n";
			byte[] fBytes = f.getBytes();
			outputStream.write(fBytes);

			//migCoe
			String mc = "Migration Coeficient = " + migCoe+ "\r\n";
			byte[] mcBytes = mc.getBytes();
			outputStream.write(mcBytes);

			outputStream.close();
		}catch(Exception e){
			System.out.println("IOException has been thrown.\n" + e.getMessage() );
		}	
	}

	private static void migCost(List<Integer> vmPair,List<Integer> frequencies)
	{
		//we need to figure out which frequency needs to be sent over.

		int freIndex = 0;
		int j;
		boolean firstOrLast = true; // print out the first which is sourceVm and the last which is primeVm
		for(int i = 0; i< vmPair.size(); i++) {//loop through all vms.

			if(i == vmPair.size()-1)
				firstOrLast = true;
			freIndex = (int)Math.floor((i/2));

			VmMigration tempVmm = new VmMigration();
			tempVmm.VmSource = vmPair.get(i);
			if(i%2== 0) {
				j =i+1;
				tempVmm.IsSourceVm= true;
			}
			else {
				j=i-1;
				tempVmm.IsSourceVm = false;
			}
			tempVmm.VmEnd = vmPair.get(j);
			tempVmm.PossibleCosts = GetCostAllPosiblePmLocs( frequencies.get(freIndex) , vmPair.get(i),
					vmPair.get(j), tempVmm.IsSourceVm ,firstOrLast);
			tempVmm.Frequency = frequencies.get(freIndex);
			tempVmm.MigrationCost = migration;

			firstOrLast = false;
			Nodes.add(new Node(i+1,1,tempVmm));

		}
		return;
	}

	private static List<Integer> GetCostAllPosiblePmLocs(int fre, int vmI,int vmJ,boolean isSourceVm,boolean fol) {//gets the cost for all possible new vm locations.

		List<Integer> list = new ArrayList<Integer>();
		int total = 0;

		for(int i = 0; i< numPms; i++){ //loop through all possible migration locations of vm
			if(i == 1) {
				fol = false;
			}
			if (isSourceVm) {
				total = GetTotalCostSource(vmI,i,fre,vmJ,fol);
				if(fol)
					System.out.println("Total Cost = " + total);	
				list.add(total);
			}
			else{
				total = GetTotalCostEnd(vmI,i,fre,vmJ,fol);
				if(fol)
					System.out.println("Total Cost = " + total);	
				list.add(total);
			}		
		}
		return list;
	}


	private static Integer GetTotalCostSource(int vm, int pm,int fre,int vmJ,boolean fol) 
	{
		int temp1= MigCost(vm,pm);
		int temp = CommCost(firstMB,pm,fre);
		if(fol) {
			System.out.println("----------Source Vm-------------");	
			System.out.println("Migration Cost = " + temp1);	
			System.out.println("Vm = " + vm);	
			System.out.println("Pm = " + pm);
			System.out.println("Migration Coe = " + migration);
			System.out.println("Communication Cost = " + temp);	
			System.out.println("Mb = " + firstMB);	
			System.out.println("Pm = " + pm);
			System.out.println("Frequecny = " + fre);
		}
		return  temp + temp1 ;
	}

	private static Integer GetTotalCostEnd(int vm, int pm,int fre,int vmJ,boolean fol) 
	{
		int temp1= MigCost(vm,pm);
		int temp = CommCost(lastMB,pm,fre);
		if(fol) {
			System.out.println("----------End Vm-------------");	
			System.out.println("Migration Cost = " + temp1);	
			System.out.println("Vm = " + vm);	
			System.out.println("Pm = " + pm);
			System.out.println("Migration Coe = " + migration);
			System.out.println("Communication Cost = " + temp);	
			System.out.println("Mb = " + lastMB);	
			System.out.println("Pm = " + pm);
			System.out.println("Frequecny = " + fre);
		}
		return  temp + temp1 ;
	}


	private static int MigCost(int vm, int pm) {

		return migration*SDN.ShorestDistance(k, vm, pm);
	}

	private static int CommCost(int mb, int pm,int fre) {

		int temp = SDN.ShorestDistance(k, mb,pm );

		return (temp)*fre;
	}

	private static int MBDistance(List<Integer> mb) {
		int temp=0;

		for(int i =0; i < mb.size()-1;i++) {
			temp += SDN.ShorestDistance(k, mb.get(i),mb.get(i+1) );
		}

		firstMB = mb.get(0);
		lastMB = mb.get(mb.size()-1);

		return temp;
	}

	@SuppressWarnings("unused")
	private static int GeneralCost(List<Integer> vmPair) {

		int temp = SDN.ShorestDistance(k, vmPair.get(0),firstMB );
		int temp2 = SDN.ShorestDistance(k, vmPair.get(1),lastMB );

		return temp+temp2;
	}

	private static List<Integer> GetFrequencies(int vmPairs, int frequency) {

		List<Integer> list = new ArrayList<Integer>();

		Random r = new Random();

		for(int i = 0;i < vmPairs;i++) {

			int result = r.nextInt(frequency-1) + 1;
			list.add(result);
		}
		return list;
	}

	private static List<Integer> MBsLocation( int boxes) {
		//number of each nodes   				//if 	k = 2		k=4
		int numPms = (int)(Math.pow(k, 3)/4);	//		2 			16										
		int numEdges = (int)(Math.pow(k, 2)/2);	// 		2			8
		int numAggs = (int)(Math.pow(k, 2)/2);  //		2			8

		//start and end points of each type of node   						//if k = 2		k=4
		int edgeStart = numPms;												// 2			16
		int edgeEnd = numPms + numEdges -1;									// 3			23
		int aggStart = edgeEnd +1;											// 4			24
		int aggEnd = aggStart + numAggs -1;									// 5			31


		List<Integer> list = new ArrayList<Integer>();

		Random r = new Random();

		for(int i = 0;i < boxes;i++) {

			int result = r.nextInt(aggEnd-edgeStart) + edgeStart;
			list.add(result);
		}

		return list;
	}

	private static List<Integer> VMPairsLocation(int vmPairs,int resCap){

		int numPms = (int)Math.pow(k, 3)/4;
		List<Integer> list = new ArrayList<Integer>();
		boolean proceed = true;
		Random r = new Random();

		for(int i = 0;i < vmPairs * 2;i++) {
			proceed = true;
			while(proceed) {	
				int result = r.nextInt(numPms-0) + 0;
				if(Collections.frequency(list, result) <= resCap )
				{
					list.add(result);
					proceed = false;
				}
			}

			//			if( Collections.frequency(list, result) > resCap ) {
			//				result = r.nextInt(numPms-0) + 0;
			//				list.set(i, result);
			//			}
		}

		return list;
	}
}