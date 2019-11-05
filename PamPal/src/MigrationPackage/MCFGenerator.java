package MigrationPackage;

import java.util.HashMap;
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
	public static void GenerateFile(HashMap<Integer, List<Integer>> topoMap, int minCost)
	{	
		
	}
}