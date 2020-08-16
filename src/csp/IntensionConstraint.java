//Dana Hoppe	CSCE 421	1/31/2020

package csp;

import abscon.instance.components.PFunction;
import abscon.instance.components.PIntensionConstraint;
import abscon.instance.components.PVariable;

public class IntensionConstraint extends Constraint{
	protected String definition;
	protected String varNames;
	protected String tuples;
	protected String params;

	
	public IntensionConstraint(PIntensionConstraint con, PFunction func) {
		super(con);
		PVariable[] variables = con.getScope();
		definition = "intension function";
		
		varNames = variables[0].getName();
		
		for(int i = 1; i < variables.length; i++) {
			Variable newVar = new Variable(variables[i]);
			varNames = varNames + "," + newVar.getName();
		}
		
		tuples = func.getFunctionalExpression();
		
		params = con.getEffectiveParametersExpression();
		params = params.replaceAll("\\s+",",");
		
	}
	
	
	public String toString() {
		return "Name: " + name + ", variables: {" + varNames + "}, definition: " + definition + ": " + tuples + ", params: {" + params + "}";
	}	
}