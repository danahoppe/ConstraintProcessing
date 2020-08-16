//Dana Hoppe	CSCE 421	1/31/2020

package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import abscon.instance.components.PVariable;

public class Variable {
	private List<Arc> arcs = new ArrayList<Arc>();
	
	//store sets of values remove from current-domain[j] by some variable before v[j]
	protected List<List<Integer>> reductions = new ArrayList<List<Integer>>();
	
	//subset of the future variables that v[i] checks against (redundant)
	protected List<Variable> future_fc = new ArrayList<Variable>();
	
	//past variables that checked against v[i]
	protected List<Variable> past_fc = new ArrayList<Variable>();
		
	/// Keep a reference to the original variable, just in case it is needed later
	protected PVariable varRef;
	/// Best to create a *deep copy* of the data-structures that are needed for the homework
	protected String name;
	protected String dom;
	protected String consts;
	protected String neighs;
	
	protected int weight;
	
	protected int degree;
	protected int[] initDomain;
	
	//conflict set
	protected List <Variable> confSet = new ArrayList<Variable>();
	
	//domains
	protected List <Integer> initialDomain = new ArrayList<Integer>();
	protected List <Integer> currDomain = new ArrayList<Integer>();
	protected List <Integer> tempDomain = new ArrayList<Integer>();
	
	//constraints and neighbors
	protected List <Constraint> constraints = new ArrayList<Constraint>();
	protected List <Variable> neighbors = new ArrayList<Variable>();
	
	protected int instantiated_value = -1;
	protected boolean instantiated = false;

	
	public Variable(PVariable var) {
		weight = 0;
		varRef = var;
		name = var.getName();
		initDomain = var.getDomain().getValues();
		
		dom = "{" + initDomain[0];
		currDomain.add(initDomain[0]);
		initialDomain.add(initDomain[0]);
		
		for(int i = 1; i < initDomain.length; i++) {
			dom = dom + ("," + initDomain[i]);
			currDomain.add(initDomain[i]);
			initialDomain.add(initDomain[i]);
		}
		
	}
	
	public List<Arc> getArcs(){
		return arcs;
	}
	
	public void clearArcs() {
		arcs.clear();
	}
	
	public void addArc(Arc arc) {
		this.arcs.add(arc);
	}
	
	public String getName() {
		return name;
	}
	
	public List <Integer> getCurrDomain() {
		return currDomain;
	}
	
	public List <Integer> getInitDomain() {
		return initialDomain;
	}
	
	public void setConstraints(List<Constraint> cons) {	
		for(int i = 0; i < cons.size(); i++) {
			List<Variable> currVars = cons.get(i).getVars();
			for(int j = 0; j < currVars.size();j++) {
				String currName = currVars.get(j).getName();
				if(currName == this.name){
					constraints.add(cons.get(i));
				}
			}
		}
		Collections.sort(constraints, Constraint.ConstraintComparator);
		if(constraints.size()>0) {
			consts = constraints.get(0).getName();
		}
		for(int k = 1; k < constraints.size(); k++) {
			consts = consts + "," + constraints.get(k).getName();
		}
	}
	
	
	public void setNeighbors(List<Variable> variables) {
		for(int i = 0; i < variables.size();i++) {
			if(variables.get(i).name != name) {
				neighbors.add(variables.get(i));
				degree++;
			}
		}
		neighs = neighbors.get(0).getName();
		for (int j = 1; j< neighbors.size(); j++) {
			neighs += "," + neighbors.get(j).getName();
		}
		
	}
	
	public void instantiateValue(int val) {
		instantiated_value = val;
		instantiated = true;
		currDomain.removeAll(Arrays.asList(val));
		tempDomain.addAll(currDomain);
		currDomain.clear();
		currDomain.add(val);
	}
	
	public int getInstantiatedVal() {
		return instantiated_value;
	}
	
	public void uninstantiate() {
		instantiated_value = -1;
		instantiated = false;
		currDomain.clear();
		currDomain.addAll(tempDomain);
		tempDomain.clear();
	}
	
	public void resetDomain() {
		currDomain.clear();
		for(int t = 0; t<initialDomain.size();t++) {
			currDomain.add(initialDomain.get(t));
		}
		
	}
	
	public List<Variable> getConfSet(){
		return confSet;
	}
	
	public int getDegree() {
		return degree;
	}
	
	public List<Variable> getNeighbors(){
		return neighbors;
	}
	
	public List<List<Integer>> getReductions(){
		return reductions;
	}
	public List<Variable> getFuture(){
		return future_fc;
	}
	public List<Variable> getPast(){
		return past_fc;
	}
	
	public void setWeight() {
		weight = 0;
		for(int i = 0; i < constraints.size(); i++) {
			weight += constraints.get(i).getWeight();
		}
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void decrementDegree() {
		degree = degree - 1;
	}
	
	public List<Constraint> getConstraints() {
		return constraints;
	}
	
	public String toString() {
		return "Name: " + name + ", initial-domain: " + dom + "}, constraints: {" + consts + "}, neighbors: {" + neighs + "}";
	}
	
	public static Comparator<Variable> VariableComparator = new Comparator<Variable>() {

		public int compare(Variable c1, Variable c2) {
		   String CName1 = c1.getName();
		   String CName2 = c2.getName();
	
		   //ascending order
		 	
		   return CName1.compareTo(CName2);
		}
    };
    
	public static Comparator<Variable> DomainComparator = new Comparator<Variable>() {

		public int compare(Variable c1, Variable c2) {
		  int Domain1 = c1.getCurrDomain().size();
		   int Domain2 = c2.getCurrDomain().size();
	
		   //ascending order
		 	
		   return Integer.compare(Domain1, Domain2);
		}
    };
}

