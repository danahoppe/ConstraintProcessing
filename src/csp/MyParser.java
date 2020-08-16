//Dana Hoppe	CSCE 421	1/31/2020

package csp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import abscon.instance.components.PConstraint;
import abscon.instance.components.PIntensionConstraint;
import abscon.instance.components.PPredicate;
import abscon.instance.components.PRelation;
import abscon.instance.tools.InstanceParser;

public class MyParser {
	private List<Variable> variables;
	private List<Constraint> constraints;
	private Instance instance;
	
	public MyParser(String filename) {
		InstanceParser parser = new InstanceParser();
		parser.loadInstance(filename);
		parser.parse(false);
		
		variables = new ArrayList<Variable>();
		constraints = new ArrayList<Constraint>();
		String name = parser.getName();		
		
		//System.out.println("Instance name: " + name);

		for(int i = 0; i < parser.getVariables().length; i++) {
			Variable newVar = new Variable(parser.getVariables()[i]);
			variables.add(newVar);
		}
		
		for(String key : parser.getMapOfConstraints().keySet()) {
			PConstraint currCon = parser.getMapOfConstraints().get(key);
			if(parser.getMapOfRelations().containsKey(currCon.getReference())) {
				PRelation relation = parser.getMapOfRelations().get(parser.getMapOfConstraints().get(key).getReference());
				ExtensionConstraint newCon = new ExtensionConstraint(parser.getMapOfConstraints().get(key), relation);
				constraints.add(newCon);
			}
			else if(parser.getMapOfPredicates().containsKey(currCon.getReference())) {
				PPredicate predicate = parser.getMapOfPredicates().get(parser.getMapOfConstraints().get(key).getReference());
				PIntensionConstraint intCon = (PIntensionConstraint) parser.getMapOfConstraints().get(key);
				IntensionConstraint newCon = new IntensionConstraint(intCon, predicate);
				constraints.add(newCon);
			}
			
		}
		
		//System.out.println("Variables:");
		for (int j = 0; j < variables.size(); j++) {
			variables.get(j).setConstraints(constraints);
			variables.get(j).setNeighbors(variables);
			//System.out.println(variables.get(j).toString());
		}
		
		Collections.sort(constraints, Constraint.ConstraintComparator);
		
		//System.out.println("Constraints:");	
		for (int b = 0; b < constraints.size(); b++) {
			constraints.get(b).setVariabls(variables);
			//System.out.println(constraints.get(b).toString());
		}
		
		instance = new Instance(name,constraints,variables);
		
	}
	
	public Instance getInstance() {
		return instance;
	}
	
	/*
	public static void main(String[] args) {
		MyParser parser = new MyParser(args[2]);
		Instance instance = parser.getInstance();
		
		System.out.print(instance.getName());
	}
	*/
}	
