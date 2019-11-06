package MigrationPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	static HashMap<Integer, List<Integer>> topoMap = new HashMap<Integer, List<Integer>>();
	public static int mbDistance;
	public static int firstMB;
	public static int lastMB;
	public static int migration;


	@SuppressWarnings("unused")
	public static void main(String[] args) {

		//call other class to begin generating the file. Pass over user input.
		//k,rc,vmpairs,mbs,frequency,migration
		int[] inputs = SDN.GetInput();

		String path = "C:/Users/angel/Desktop/";
		//Set up for testing
		//SetUpMinCost(4,4,2,2,2,1);
		
		//generate mcf_migration.inp file
		SetUpMinCost(inputs[0],inputs[1],inputs[2],inputs[3],inputs[4],inputs[5], path);
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

		PrintParameters(numPods, resCap,vmPairLocation,MBsLocation,frequencies,migCoe,path);
		//generate mcf_migration.inp file	
		MCFGenerator.GenerateFile(topoMap, k,path);
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
		for(int i = 0; i< vmPair.size(); i++) {//loop through all vms.
			if(i%2== 0) {
				j =i+1;
			}
			else {
				j=i-1;
			}
			freIndex = (int)Math.floor((i/2));
			topoMap.put(vmPair.get(i), GetCostAllPosiblePmLocs( frequencies.get(freIndex) , vmPair.get(i), vmPair.get(j)));
		}
		return;
	}

	private static List<Integer> GetCostAllPosiblePmLocs(int fre, int vmI,int vmJ) {//gets the cost for all possible new vm locations.

		List<Integer> list = new ArrayList<Integer>();

		for(int i = 0; i< numPms; i++){ //loop through all possible migration locations of vm
			if(i != vmI) {//cannot migrate to its own location
				list.add(GetTotalCost(vmI,i,fre,vmJ));
			}
		}
		return list;
	}


	private static Integer GetTotalCost(int vm1, int vm2,int fre,int vmJ) 
	{
		int temp = CommCost(vm1,vm2,fre);
		int temp1= MigCost(vm2,vmJ);
		return  temp + temp1 ;
	}


	private static int MigCost(int vm1, int vm2) {

		return migration*SDN.ShorestDistance(k, vm1, vm2);
	}

	private static int CommCost(int fisrtVM, int secondVm,int fre) {

		int temp = SDN.ShorestDistance(k, fisrtVM,firstMB );
		int temp2 = SDN.ShorestDistance(k, secondVm,lastMB );

		return (temp+temp2+ mbDistance)*fre;
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
				if(Collections.frequency(list, result) == 0 )
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