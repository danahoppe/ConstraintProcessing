//Dana Hoppe	CSCE 421	1/31/2020

package csp;

import abscon.instance.components.PConstraint;
import abscon.instance.components.PRelation;
import abscon.instance.components.PVariable;

public class ExtensionConstraint extends Constraint{

	protected String definition;
	protected String varNames;
	protected String tuples;
	protected PRelation pRel; 
	
	protected int[][] tups;
	
	
	public ExtensionConstraint(PConstraint con, PRelation rel) {
		super(con);
		PVariable[] variables = con.getScope();
		definition = rel.getSemantics();
		
		varNames = variables[0].getName();
		
		for(int i = 1; i < variables.length; i++) {
			Variable newVar = new Variable(variables[i]);
			varNames = varNames + "," + newVar.getName();
		}
		
		tuples = rel.getStringListOfTuples();
		tups = rel.getTuples();
	}
	
	public int[][] getTuples(){
		return tups;
	}
	
	public String toString() {
		return "Name: " + name + ", variables: {" + varNames + "}, definition: " + definition + " {(" + tuples + ")}";
	}

	public String getDefinition() {
		return definition;
	}	
}