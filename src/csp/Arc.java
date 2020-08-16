/****************************************************
Name: Dana Hoppe
Class: CSCE 421 
Date: 5/1/2020
Assignment: Homework 5
Description: Forward Checking
*****************************************************/

package csp;

import java.util.ArrayList;
import java.util.List;

public class Arc {
	
	private Variable varA;
	private Variable varB;
	private Constraint constraint;
	
	private List<List<Integer>> supports = new ArrayList<List<Integer>>();;
	
	public Arc(Variable varA, Variable varB) {
		this.varA = varA;
		this.varB = varB;
	}
	
	public void addSupport(List<Integer> support) {
		supports.add(support);
	}
	
	public void removeSupport(List<Integer> support) {
		supports.remove(support);
	}
	
	public int supported(int value) {
		for(int s = 0; s < supports.size(); s ++) {
			if(supports.get(s).get(0) == value) {
				return supports.get(s).get(1);
			}
		}
		return -1;
	}

	public Variable getVarA() {
		return varA;
	}

	public Variable getVarB() {
		return varB;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public void setConstraint(Constraint constraint2) {
		constraint = constraint2;
		
	}
	
	
}
