package csp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import abscon.instance.components.PConstraint;
import abscon.instance.components.PVariable;

public abstract class Constraint {

	protected List<Variable> vars = new ArrayList<Variable>();
	protected String name;
	protected PConstraint conRef;
	private int order;
	
	protected int weight;
	
	protected int conflicts;

	public Constraint(PConstraint constraint) {
		conRef = constraint;
		name = constraint.getName();
		PVariable[] variables = constraint.getScope();
		
		weight = 1;
		
		order = Integer.parseInt(name.replace("C", ""));
		
		for(int i = 0; i < variables.length; i++) {
			Variable newVar = new Variable(variables[i]);
			vars.add(newVar);
		}
	}

	public String getName() {
		return name;
	}
	
	public List<Variable> getVars() {
		return vars;
	}
	
    public int getOrder() {
		return order;
	}
    
    public PConstraint getRef() {
    	return conRef;
    }
    
    public void setVariabls(List<Variable> variables) {
    	vars.clear();
		for(int i = 0; i < variables.size(); i++) {
			List<Constraint> currCons = variables.get(i).getConstraints();
			for(int j = 0; j < currCons.size();j++) {
				String currName = currCons.get(j).getName();
				if(currName == this.name){
					vars.add(variables.get(i));
				}
			}
		}
	}
    
	public List<Variable> getUnnasignedVars(){
		List <Variable> unnassignedVars = new ArrayList<Variable>();
		for(int i = 0; i < vars.size(); i++) {
			if(vars.get(i).instantiated == false) {
				unnassignedVars.add(vars.get(i));
			}
		}
		return unnassignedVars;
	}
	
	public List<Variable> getAssignedVars(){
		List <Variable> assignedVars = new ArrayList<Variable>();
		for(int i = 0; i < vars.size(); i++) {
			if(vars.get(i).instantiated) {
				assignedVars.add(vars.get(i));
			}
		}
		return assignedVars;
	}
	
	public int getConflicts() {
		return conflicts;
	}
	
	public void incrementConflicts() {
		conflicts++;
	}
	
	public void resetConflicts() {
		conflicts = 0;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void incrWeight() {
		weight++;
	}

	public static Comparator<Constraint> ConstraintComparator = new Comparator<Constraint>() {

		public int compare(Constraint c1, Constraint c2) {
		   int CName1 = c1.getOrder();
		   int CName2 = c2.getOrder();
	
		   //ascending order
		   return Integer.compare(CName1,CName2);
		}
    };
}
