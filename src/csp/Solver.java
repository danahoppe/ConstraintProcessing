package csp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import abscon.instance.components.PIntensionConstraint;

public abstract class Solver {
	
	private Instance problem;
	protected int cc;
	protected int first_cc;
	
	protected int nv;
	protected int bt;

	protected int first_nv;
	protected int first_bt;
		
	protected double cpu_time;
	protected double first_cpu_time;
	private double setup_time;
	
	protected int solutions;
	
	private String variable_ordering_heuristic;
	private String var_static_dynamic;
	private String value_ordering_heuristic;
	private String value_static_dynamic;
	
	private int fval;
	private double iSize;
	private double fSize;
	private double fEffect;
	
	private List<Arc> arcs;
	public List<Variable> current_path;
	
	
	//Constructor
	public Solver(Instance problem) {
		this.problem = problem;
		this.arcs = new LinkedList<Arc>();
		this.setup_time = System.currentTimeMillis();
	}
	

	public boolean CHECK(Variable VarA, int ValA, Variable VarB, int ValB){
		List<Constraint> constraint = new ArrayList<Constraint>();
		
		//Determine whether constraint exists between the variables
		for(int i = 0; i < VarA.getConstraints().size(); i++) {
			Constraint constraintA = VarA.getConstraints().get(i);
			
			for(int j = 0; j < VarB.getConstraints().size(); j++) {
				Constraint constraintB = VarB.getConstraints().get(j);
				if(constraintA.equals(constraintB)) {
					constraint.add(VarA.getConstraints().get(i));
					this.cc++;
				}
			}
		}
		
		//Determine whether (ValA,ValB) exists		
		//Constraint(s) found
		if(constraint.size()>0){			
			//For each constraint found between the variables
			//System.out.println(constraint.size());
			for(int c = 0; c < constraint.size(); c++) {
				boolean support_found = false;
				boolean conflict_found = false;
				
				constraint.get(c).resetConflicts();
				//Extension Constraint
				if(constraint.get(c) instanceof ExtensionConstraint){
					ExtensionConstraint extConstraint = (ExtensionConstraint)constraint.get(c);
					String definition = extConstraint.getDefinition();
					//System.out.println(definition);
					int [][] tuples = extConstraint.getTuples();
					
					//For all values of the constraint
					for(int k = 0; k < tuples.length; k++) {
						if(extConstraint.getVars().get(0)==VarA) {
							if((tuples[k][0] == ValA)&&(tuples[k][1] == ValB)) {
								if(definition.equals("conflicts")) {
									//System.out.println(VarA.getName() + ": " + ValA +" is in conflict with " + VarB.getName() + ": " +ValB);
									conflict_found = true;
								}
								else if(definition.equals("supports")) {
									//System.out.println(VarA.getName() + ": " + ValA +" is supporting " + VarB.getName() + ": " + ValB);
									support_found = true;
								}
							}
						}
						else if(extConstraint.getVars().get(0)==VarB) {
							if((tuples[k][0] == ValB)&&(tuples[k][1] == ValA)) {
								if(definition.equals("conflicts")) {
									//System.out.println(VarA.getName() + ": " + ValA +" is in conflict with " + VarB.getName() + ": " +ValB);
									conflict_found = true;
								}
								else if(definition.equals("supports")) {
									//System.out.println(VarA.getName() + ": " + ValA +" is supporting " + VarB.getName() + ": " + ValB);
									support_found = true;
								}
							}
						}
					}
					
					if(definition.equals("conflicts")){
						if(conflict_found) {
							//System.out.println("Constraint conflicted");
							constraint.get(c).incrementConflicts();
							return false;
						}
					}
					else if(definition.equals("supports")) {
						if(!support_found) {
							//System.out.println("Constraint not supported");
							constraint.get(c).incrementConflicts();
							return false;
						}
					}
	
				}
				
				//Intension Constraint
				else if(constraint.get(c) instanceof IntensionConstraint) {
					IntensionConstraint intConstraint = (IntensionConstraint)constraint.get(c);
					PIntensionConstraint pIntConstraint = (PIntensionConstraint) intConstraint.getRef();
					int[] tuple = new int[] {ValA,ValB};
					
					if(pIntConstraint.computeCostOf(tuple) == 0) {
						support_found = true;
					}
					else {
						constraint.get(c).incrementConflicts();
						return false;
					}
				}
				
			}			
			//No conflicts found for conflict constraints and at least one support found for support constraints
			return true;
		}
		//No Constraint - Universal Constraint
		else {
			//System.out.println("Universal Constraint!");
			return true;
		}
	}
	
	public boolean CHECK_UNARY(Variable var, int val) {
		List<Constraint> constraints = var.getConstraints();
		//For each constraint on this variable
		for(int c = 0; c < constraints.size(); c++) {
			Constraint currCon = constraints.get(c);
			currCon.resetConflicts();
			//Check if unary found
			if(currCon.getVars().size() == 1) {
				//Extension Unary Constraint
				if(currCon instanceof ExtensionConstraint) {
					//Get tuples for unary
					int [][] tuples = ((ExtensionConstraint) currCon).getTuples();
					
					//check if value found in unary
					for(int v = 0; v < tuples.length; v++) {
						if(val==tuples[v][0]) {
							return true;
						}
						else {
							currCon.incrementConflicts();
							return false;
						}
					}					
				}
				else if(currCon instanceof IntensionConstraint) {
					IntensionConstraint intConstraint = (IntensionConstraint)currCon;
					PIntensionConstraint pIntConstraint = (PIntensionConstraint) intConstraint.getRef();
					int[] tuple = new int[] {val};
					
					//System.out.println(var.getName());
					//System.out.println(val);
					if(pIntConstraint.computeCostOf(tuple) == 0) {
						//System.out.println("UNARY CONSISTENT");
						return true;
					}
				}
				currCon.incrementConflicts();
				//unary constraint does not support value
				return false;
			}
		}
		//no unary constraint
		return true;
	}
	
	public boolean SUPPORTED(Variable VarA, int ValA, Variable VarB) {
		List<Integer> domain = VarB.getCurrDomain();
		for(int i = 0; i < domain.size(); i++) {
			if(CHECK(VarA, ValA, VarB, domain.get(i))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean REVISE(Variable VarA, Variable VarB) {
		boolean revised = false;
		
		List<Integer> domain = VarA.getCurrDomain();
		for(int i = 0; i < domain.size(); i++) {
			if((SUPPORTED(VarA, domain.get(i), VarB))==false) {
				domain.remove(i);
				fval++;
				revised = true;
			}
		}
		return revised;
	}

	public void NODE_CONSISTENCY(List <Variable> vars) {
		//Check each variable for unary constraint		
		for(int p = 0; p < vars.size(); p++) {
			Variable currVar = vars.get(p);
			for(int b = 0; b < currVar.getCurrDomain().size(); b++) {
				boolean consistent = CHECK_UNARY(currVar,currVar.getCurrDomain().get(b));
				if(!consistent) {
					currVar.getCurrDomain().remove(b);
				}
			}
		}
	}
	
	public Instance AC1() {
		
		//Problem constraints
		List<Constraint> constraints = problem.getConstraints();
		
		//Generate Queue of arcs
		for(int i = 0; i < constraints.size(); i ++) {
			List <Variable> vars = constraints.get(i).getVars();
			
			for(int j = 0; j < vars.size(); j ++) {
				for(int k = 0; k < vars.size(); k++) {
					if(vars.get(j)!=vars.get(k)) {
						Arc newArc = new Arc(vars.get(j), vars.get(k));
						arcs.add(newArc);
					}
				}
			}
		}
		
		//Run REVISE until no change
		boolean change = true;
		while(change) {
			change = false;
			for(int q = 0; q < arcs.size(); q ++) {
				Arc head = arcs.remove(0);
				Variable varA = head.getVarA();
				Variable varB = head.getVarB();	
				
				if(REVISE(varA,varB)) {
					change = true;
				}
			}
		}
		
		
		return problem;
	}
	
	public Instance AC3(List<Arc> arcs) {
			//Run REVISE until no arcs are revised
			while(arcs.size() > 0) {
				Arc head = arcs.remove(0);
				Variable varA = head.getVarA();
				Variable varB = head.getVarB();	
				
				//if domain changed add all arcs connected back onto list
				if(REVISE(varA,varB)) {
					arcs.addAll(varA.getArcs());
				}
			}			
			return problem;
		}

	public void AC2001(List<Arc> arcs) {
		//Run REVISE until no arcs are revised
		while(arcs.size() > 0) {
			Arc head = arcs.remove(0);
			Variable varA = head.getVarA();
			
			//if domain changed add all arcs connected back onto list
			if(AC2001REVISE(head)) {
				arcs.addAll(varA.getArcs());
			}
		}
	}
	
	public boolean AC2001REVISE(Arc head) {
		boolean revised = false;
		Variable varA = head.getVarA();
		Variable varB = head.getVarB();	
		
		List<Integer> domain = varA.getCurrDomain();
		for(int i = 0; i < domain.size(); i++) {
			int last = head.supported(domain.get(i));
			if(!(varB.getCurrDomain().contains(last))) {
				if((AC2001SUPPORTED(head, varA, domain.get(i), varB))==false) {
					domain.remove(i);
					fval++;
					revised = true;
				}
			}			
		}
		return revised;
	}
	
	public boolean AC2001SUPPORTED(Arc head, Variable VarA, int ValA, Variable VarB) {
		List<Integer> domain = VarB.getCurrDomain();
		for(int i = 0; i < domain.size(); i++) {
			if(CHECK(VarA, ValA, VarB, domain.get(i))) {
				List<Integer> support = new ArrayList<Integer>();
				support.add(0,ValA);
				support.add(1, domain.get(i));
				head.addSupport(support);
				return true;
			}
		}
		return false;
	}
	
	public void generateArcs() {
		//Problem constraints
		List<Constraint> constraints = problem.getConstraints();
		
		//Generate Queue of arcs
		for(int i = 0; i < constraints.size(); i ++) {
			List <Variable> vars = constraints.get(i).getVars();
			
			for(int j = 0; j < vars.size(); j ++) {
				for(int k = 0; k < vars.size(); k++) {
					if(vars.get(j)!=vars.get(k)) {
						Arc newArc = new Arc(vars.get(j), vars.get(k));
						newArc.getVarA().addArc(newArc);
						newArc.getVarB().addArc(newArc);
						newArc.setConstraint(constraints.get(i));
						arcs.add(newArc);
					}
				}
			}
		}
	}
	
	public List<Variable> getUnnasignedVars(){
		List <Variable> unnassignedVars = new ArrayList<Variable>();
		for(int i = 0; i < current_path.size(); i++) {
			if(current_path.get(i).instantiated == false) {
				unnassignedVars.add(current_path.get(i));
			}
		}
		return unnassignedVars;
	}
	
	public List<Variable> getAssignedVars(){
		List <Variable> assignedVars = new ArrayList<Variable>();
		for(int i = 0; i < current_path.size(); i++) {
			if(current_path.get(i).instantiated) {
				assignedVars.add(current_path.get(i));
			}
		}
		return assignedVars;
	}
	
	public List<Variable> id_var_st(List<Variable> vars){
		Collections.sort(vars, Variable.VariableComparator);
		return vars;
	}
	
	public List<Variable> ld_var_st(List<Variable> vars){
		Collections.sort(vars, Variable.DomainComparator);
		return vars;
	}
	
	public List<Variable> deg_var_st(List<Variable> vars){
		
		List<Variable> oldVars =  new ArrayList<Variable>(vars);
		
		List<Variable> newVars =  new ArrayList<Variable>();
		
		while(oldVars.size() > 0) {
			int maxDegree = 0;
			int maxIndex = 0;
			for(int j = 0; j < oldVars.size(); j++) {
				if (oldVars.get(j).getDegree() > maxDegree) {
					maxDegree = oldVars.get(j).getDegree();
					maxIndex = j;
				}
			}
			Variable topVar = oldVars.get(maxIndex);
			newVars.add(topVar);
			oldVars.remove(maxIndex);
			
			for(int k = 0; k < oldVars.size(); k++) {
				for(int p = 0; p < oldVars.get(k).getNeighbors().size(); p++) {
					Variable neighbor = oldVars.get(k).getNeighbors().get(p);
					if(neighbor.getName().equalsIgnoreCase(topVar.getName())) {
						oldVars.get(k).decrementDegree();
					}
				}
			}			
			
		}
		return newVars;
	}
	
	public List<Variable> ddr_var_st(List<Variable> vars){
		
		List<Variable> oldVars =  new ArrayList<Variable>(vars);
		
		List<Variable> newVars =  new ArrayList<Variable>();
		while(oldVars.size()>0) {
			double minDD = 999.99;
			int minIndex = 0;
			for(int k = 0; k < oldVars.size(); k++) {
				int deg = oldVars.get(k).getDegree();
				int dom = oldVars.get(k).getCurrDomain().size();
				double ratio = ((double) dom)/ ((double) deg);
				if(ratio < minDD) {
					minIndex = k;
					minDD = ratio;
				}
			}
			newVars.add(oldVars.get(minIndex));
			oldVars.remove(minIndex);
		}
		
		return newVars;
	}

	public int getCC() {
		return cc;
	}
	
	public int getfval() {
		return fval;
	}
	
	public List<Arc> getArcs() {
		return arcs;
	}
	
	public int getSolutionCount() {
		return solutions;
	}

	public void setArcs(LinkedList<Arc> arcs) {
		this.arcs = arcs;
	}
	

	public void setCurrentPath(List<Variable> vars) {
		if(this.variable_ordering_heuristic.equalsIgnoreCase("LX")) {
			current_path = id_var_st(vars);
		}
		else if( this.variable_ordering_heuristic.equalsIgnoreCase("LD")) {
			current_path = ld_var_st(vars);
		}
		else if(this.variable_ordering_heuristic.equalsIgnoreCase("DEG")) {
			current_path = deg_var_st(vars);
		}
		else if( this.variable_ordering_heuristic.equalsIgnoreCase("DD")) {
			current_path = ddr_var_st(vars);
		}
		else {
			current_path = vars;
		}
	}
	
	public void print_heuristics(){
		System.out.println("variable-order-heuristic: " + this.getVariable_ordering_heuristic());
		System.out.println("var-static-dynamic: " + this.getVar_static_dynamic());
		System.out.println("value-ordering-heuristic: " + this.getValue_ordering_heuristic());
		System.out.println("val-static-dynamic: " + this.getValue_static_dynamic());
		
		first_cpu_time = (System.currentTimeMillis() - this.setup_time);
		first_cc = this.getCC();
		first_nv = this.getNV();
		first_bt = this.getBT();
		
		System.out.println("cc: " + this.getCC());
		System.out.println("nv: " + this.getNV());
		System.out.println("bt: " + this.getBT());
		System.out.println("cpu: " + this.first_cpu_time);
		
		System.out.print("First solution: ");

		for(int p = 0; p < this.getAssignedVars().size(); p++) {
			System.out.print(this.getAssignedVars().get(p).getInstantiatedVal() + " ");
		}
	}
	
	public void print_no_solution(){
		System.out.println("variable-order-heuristic: " + this.getVariable_ordering_heuristic());
		System.out.println("var-static-dynamic: " + this.getVar_static_dynamic());
		System.out.println("value-ordering-heuristic: " + this.getValue_ordering_heuristic());
		System.out.println("val-static-dynamic: " + this.getValue_static_dynamic());
		first_cpu_time = (System.currentTimeMillis() - this.setup_time);
		first_cc = this.getCC();
		first_nv = this.getNV();
		first_bt = this.getBT();
		
		System.out.println("cc: " + this.getCC());
		System.out.println("nv: " + this.getNV());
		System.out.println("bt: " + this.getBT());
		System.out.println("cpu: " + this.first_cpu_time);
		
		System.out.print("First solution: " + "No Solution");
	}
	
	public void print_details(Instance instance, String orderHeuristic) {
		System.out.println();
		System.out.println("Instance name: " + instance.getName());
		this.setValue_ordering_heuristic("LX");
		this.setVar_static_dynamic(this.getVar_static_dynamic());
		this.setValue_static_dynamic("static");
	}
	
	public void print_all_solutions() {
		this.cpu_time = (System.currentTimeMillis() - this.setup_time);
		System.out.println();
		System.out.println("all-sol cc: " + this.getCC());
		System.out.println("all-sol nv: " + this.getNV());
		System.out.println("all-sol bt: " + this.getBT());
		System.out.println("all-sol cpu: " + this.cpu_time);
		System.out.println("Number of solutions: " + this.getSolutionCount());
	}
	
	public String getValue_ordering_heuristic() {
		return value_ordering_heuristic;
	}

	public void setValue_ordering_heuristic(String value_ordering_heuristic) {
		this.value_ordering_heuristic = value_ordering_heuristic;
	}

	public String getValue_static_dynamic() {
		return value_static_dynamic;
	}

	public void setValue_static_dynamic(String value_static_dynamic) {
		this.value_static_dynamic = value_static_dynamic;
	}

	public String getVariable_ordering_heuristic() {
		return variable_ordering_heuristic;
	}

	public void setVariable_ordering_heuristic(String variable_ordering_heuristic) {
		this.variable_ordering_heuristic = variable_ordering_heuristic;
	}

	public String getVar_static_dynamic() {
		return var_static_dynamic;
	}

	public void setVar_static_dynamic(String var_static_dynamic) {
		this.var_static_dynamic = var_static_dynamic;
	}

	public int getNV() {
		return nv;
	}

	public int getBT() {
		return bt;
	}
	
	public double getCpuTime() {
		return cpu_time;
	}

	public int getFirst_nv() {
		return first_nv;
	}

	public int getFirst_bt() {
		return first_bt;
	}

	public int getFirst_cc() {
		return first_cc;
	}
	
	public double getFirstCpuTime() {
		return first_cpu_time;
	}
	
	public String getProblemName(){
		String name = this.problem.getName();
		return name;
	}

	public double getSetup_time() {
		return setup_time;
	}
	
	public List<Variable> getCurrentPath(){
		return this.current_path;
	}
}


