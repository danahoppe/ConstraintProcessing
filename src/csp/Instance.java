package csp;

import java.util.List;

public class Instance {
	
	private List<Constraint> constraints;
	private String name;
	private List<Variable> variables;

	public Instance(String name, List<Constraint> constraints, List<Variable> variables) {
		this.name = name;
		this.constraints = constraints;
		this.variables = variables;
	}
	
	public List<Constraint> getConstraints(){
		return constraints;
	}
	
	public List<Variable> getVariables(){
		return variables;
	}
	

	public String getName(){
		return name;
	}
}
