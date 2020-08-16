/****************************************************
Name: Dana Hoppe
Class: CSCE 421 
Date: 5/1/2020
Description: CBJ CSP Solver
*****************************************************/

package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.xmlbeans.impl.store.Path;

public class CBJ_Solver extends Solver {
	
	//current variable
	protected int i;
	
	//instantiate super class
	public CBJ_Solver(Instance problem) {
		super(problem);
	}
	
	public void CBJ() {
		i = 0;
		//NODE_CONSISTENCY(current_path);
		
		while(i < current_path.size() && i >= 0) {
			//System.out.println("INDEX: " + i);
			//System.out.println(current_path.get(i).getCurrDomain());
			//System.out.println(this.getAssignedVars().stream().map(x -> String.valueOf(x.instantiated_value)).collect(Collectors.joining(",")));
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
			
			Variable curr_var = current_path.get(i);
			//instantiate variable at i
			boolean consistent = cbj_label(curr_var);
			
			
			//no value found
			if(!consistent) {
				//make sure we are not on the first variable -> cannot backjump from
				if(i > 0) {					
					cbj_unlabel();
				}
				else {
					break;
				}
			}
			//value found
			else {
				//step forward
				i++;
				//reset mutable domain?			
			}
			
			//solution found
			if(i >= current_path.size()) {				
				//System.out.println(this.getAssignedVars().stream().map(x -> String.valueOf(x.instantiated_value)).collect(Collectors.joining(",")));
				//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
				
				solutions = solutions + 1;
				if(solutions < 2) {
					Timer time = new Timer();
					print_heuristics();
				}
				i--;
				Variable N = current_path.get(i);
				//force N to conflict with every other variable behind it
				for(int m = 0; m < current_path.size(); m ++) {
					N.getConfSet().add(current_path.get(m));
				}
				cbj_unlabel();
			}			
		}
		if(solutions<1) {
			Timer time = new Timer();
			print_no_solution();
		}
		//TO-DO implement all solutions
	}
	

	private void cbj_unlabel() {
		//System.out.println("UNLABEL!");
		Variable curr_var = current_path.get(i);
		
		//System.out.println("conflicts at " + i + " : " + curr_var.getConfSet());
		//jump to deepest variable in conflict set
		int h = 0;
		if(curr_var.getConfSet().size()>0) {
			curr_var.getConfSet().remove(curr_var);
			for(int b = 0; b < i; b ++) {
				for(int m = 0; m < curr_var.getConfSet().size(); m++) {
					if(curr_var.getConfSet().get(m).equals(current_path.get(b))){
						if(b > h) {
							h = b;
						}
					}
				}
			}
		}
		else {
			h = i - 1;
		}
		//merge conflict sets of deepest conflicted variable and current variable
		List<Variable> deepConflicts = new ArrayList<>(current_path.get(h).getConfSet());
        deepConflicts.removeAll(curr_var.getConfSet());
        
        //remove deep variable index from current conflict set
        curr_var.getConfSet().remove(current_path.get(h));
        curr_var.getConfSet().addAll(deepConflicts);
        
        for(int k = h + 1; k <= i; k++) {
        	current_path.get(k).getConfSet().clear();
        	current_path.get(k).resetDomain();
        	current_path.get(k).uninstantiate();
        }
        
        int conflict_value = current_path.get(h).getInstantiatedVal();
        current_path.get(h).getCurrDomain().removeAll(Arrays.asList(conflict_value));
        //un-instantiate variable
        current_path.get(h).uninstantiate();
        
		bt++;
		i = h;
		return;		
	}


	public boolean cbj_label(Variable var){
		boolean consistent = false;
		//until domain is empty, evaluate consistency of each value
			
		//copy current domain
		List<Integer> domain_copy = new ArrayList<Integer>();
		domain_copy.addAll(var.getCurrDomain());
		
		while(domain_copy.size() > 0) {
			//pop head
			int a = domain_copy.remove(0);
			//instantiate value 'a'
			var.instantiateValue(a);
			nv++;
			
			consistent = true;
			int k = 0;
			//Loop over every previously instantiated variable
			while((k < i) && consistent) {
				//Check current domain value against previous variable instantiations 
				//System.out.println("CHECKING VAL: " + a + " at " + k);
				
				consistent = CHECK(var, a, current_path.get(k), current_path.get(k).getInstantiatedVal());
				if(!consistent) {
					//System.out.println("conflict found");
					//System.out.println("conflicts at " + i + " : " + var.getConfSet());
					//remove duplicates
					var.getConfSet().removeAll(Arrays.asList(k));
					//add deepest conflict to conflict set
					var.getConfSet().add(current_path.get(k));
					//System.out.println("conflicts at " + i + " : " + var.getConfSet());
					
					//remove value from current domain
					var.getCurrDomain().removeAll(Arrays.asList(a));
					var.uninstantiate();
					//conflict found
					consistent = false;
					break;
				}
				else {
					k = k + 1;
				}
			}
			//no conflict found
			if(consistent) {
				//System.out.println("instantiated: " + a + " at " + i);
				nv = getNV() + 1;
				return true;
			}
		}
		//No consistent domain value assignment found = dead end
		return false;
	}

}
