package MigrationPackage;
import java.util.List;

public class VmMigration {
	
	int VmSource;
	int VmEnd;
	List<Integer> PossibleCosts;//nodes that represent each PM
	int Frequency;
	int MigrationCost;
	boolean IsSourceVm;
	
	public VmMigration(int vms, int vme,List<Integer> pc, int f, int mc, boolean isv) {
		this.VmSource = vms;
		this.VmEnd = vme;
		this.PossibleCosts = pc;
		this.Frequency = f;
		this.MigrationCost = mc;
		this.IsSourceVm = isv;
	}
	public VmMigration() {
		// TODO Auto-generated constructor stub
	}

	//get set issourcevm
	public boolean getIsVmSource() {
		return IsSourceVm;
	}

	public void setIsVmSource(boolean isSourceVm) {
		IsSourceVm = isSourceVm;
	}

	//get set vmsource
	public int getVmSource() {
		return VmSource;
	}

	public void setVmSource(int vmSource) {
		VmSource = vmSource;
	}

	//get set vmend
	public int getVmEnd() {
		return VmEnd;
	}

	public void setVmEnd(int vmEnd) {
		VmEnd = vmEnd;
	}

	//get set PossibleCostss
	public List<Integer> getPossibleCosts() {
		return PossibleCosts;
	}

	public void setPossibleCosts(List<Integer> possibleCosts) {
		this.PossibleCosts = possibleCosts;
	}

	public int getFrequency() {
		return Frequency;
	}

	public void setFrequency(int frequency) {
		Frequency = frequency;
	}

	public int getMigrationCost() {
		return MigrationCost;
	}

	public void setMigrationCost(int migrationCost) {
		MigrationCost = migrationCost;
	}
}