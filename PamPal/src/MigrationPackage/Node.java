package MigrationPackage;

public class Node {

	// Instance Variables 
	int NodeID; 
	int Cost; 
	VmMigration VMM; 

	// Constructor Declaration of Class 
	public Node(int nodeID, int cost, VmMigration vmm) 
	{ 
		this.NodeID = nodeID; 
		this.Cost = cost; 
		this.VMM = vmm; 
	} 

	public Node() {
	}

	// get set nodeid 
	public int getNodeID() 
	{ 
		return NodeID; 
	} 
	public void setNodeID(int n) {
		NodeID = n;
	}

	// get set cost 
	public int getCost() 
	{ 
		return Cost; 
	} 

	public void setCost(int n) {
		Cost = n;
	}

	// get set vmm
	public VmMigration getVMM() 
	{ 
		return VMM; 
	} 

	public void setVMM(VmMigration vm) {
		VMM = vm;
	}
}
