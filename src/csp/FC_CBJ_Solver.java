/****************************************************
Name: Dana Hoppe
Class: CSCE 421
Date: 5/1/2020
Description: FC-CBJ CSP Solver
*****************************************************/

package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FC_CBJ_Solver extends Solver{
	
	//current variable index
	protected int i = 0;
	
	//future variable index
	protected int j = 0;
	
	public FC_CBJ_Solver(Instance problem) {
		super(problem);
	}
	
	public void FC_CBJ() {
		//initialize current variable index i and future variable index j
		i = 0;
		j = 1;
		
		//dynamic ordering
		if(this.getVariable_ordering_heuristic().equalsIgnoreCase("dLD")) {
			List <Variable> neighbors = new ArrayList<Variable>();
			for(int k = i + 1; k < current_path.size(); k++) {
				Variable next = current_path.remove(k);
				neighbors.add(next);
			}
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
			neighbors = ld_var_st(neighbors);
			current_path.addAll(neighbors);
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
		}
		else if(this.getVariable_ordering_heuristic().equalsIgnoreCase("dDD")) {
			List <Variable> neighbors = new ArrayList<Variable>();
			for(int k = i + 1; k < current_path.size(); k++) {
				Variable next = current_path.remove(k);
				neighbors.add(next);
			}
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
			neighbors = ddr_var_st(neighbors);
			current_path.addAll(neighbors);
		}
		else if(this.getVariable_ordering_heuristic().equalsIgnoreCase("dDEG")) {
			List <Variable> neighbors = new ArrayList<Variable>();
			for(int k = i + 1; k < current_path.size(); k++) {
				Variable next = current_path.remove(k);
				neighbors.add(next);
			}
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
			neighbors = deg_var_st(neighbors);
			current_path.addAll(neighbors);
		}
		
		while(i < current_path.size() && i >= 0) {
			//System.out.println("INDEX: " + i);
			//System.out.println(current_path.get(i).getCurrDomain());
			//System.out.println(this.getAssignedVars().stream().map(x -> String.valueOf(x.instantiated_value)).collect(Collectors.joining(",")));
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
			
			//System.out.println("PAST: " + current_path.get(i).getPast().stream().map(Variable::getName).collect(Collectors.joining(",")));
			boolean assigned = fc_cbj_label();
			//Conflict found
			if(!assigned) {
				if(i>0) {
					fc_cbj_unlabel();
				}
				else {
					break;
				}
			}
			else {
				i++;				
			}
			
			//solution found
			if(i >= current_path.size()) {
				//add solution to solution set
				solutions = solutions + 1;
				if(solutions < 2) {
					Timer time = new Timer();
					print_heuristics();
				}
				
				i = i - 1;
				
				Variable N = current_path.get(i);
				//force N to conflict with every other variable behind it
				for(int m = 0; m < current_path.size(); m ++) {
					N.getConfSet().add(current_path.get(m));
				}
				fc_cbj_unlabel();
			}			
		}
		if(solutions<1) {
			Timer time = new Timer();
			print_no_solution();
		}
				
		return;
	}
	
	public boolean fc_cbj_label() {
		boolean consistent = false;
		
		Variable curr_var = current_path.get(i);
		
		//copy current domain
		List<Integer> domain_copy = new ArrayList<Integer>();
		domain_copy.addAll(curr_var.getCurrDomain());
		int val = 0;
		
		//for each element of the current domain forward check
		while(domain_copy.size() > 0 && !consistent) {
			j = i + 1;
			val = domain_copy.remove(0);
			nv++;
			consistent = true;
			//for each future variable
			while(j < current_path.size()) {
				consistent = check_forward(val);				
				if(!consistent) {
					//remove value from domain
					curr_var.getCurrDomain().removeAll(Arrays.asList(val));
					bt++;
					//add curr_var to reductions
					add_var_past(val, i);
					
					//undo reductions at i for all future variables
					undo_reductions(i);
					
					//conf_set[i] = union(conf_set[i], past_fc[j-1])
					//remove duplicates					
					curr_var.getConfSet().removeAll(current_path.get(j-1).getPast());
					//add past_fc list to conflict set
					curr_var.getConfSet().addAll(current_path.get(j-1).getPast());
					break;
				}
				j++;
			}
			j--;
		}
		if(consistent) {
			//instantiate
			curr_var.instantiateValue(val);
			return true;
		}
		return false;
	}
	
	public boolean fc_cbj_unlabel() {
		bt++;
		int h;
		Variable curr_var = current_path.get(i);;
		
		List<Integer> confs = new ArrayList<Integer>();
		for(int b = 0; b < i; b++) {
			if(curr_var.getConfSet().contains(current_path.get(b))) {
				confs.addAll(Arrays.asList(b));
			}
		}
		int maxConf = 0;
		if(curr_var.getConfSet().size()>0) {
			curr_var.getConfSet().remove(curr_var);
			maxConf = Collections.max(confs);
		}
		
		List<Integer> pasts = new ArrayList<Integer>();
		for(int b = 0; b < i; b++) {
			if(curr_var.getPast().contains(current_path.get(b))) {
				pasts.addAll(Arrays.asList(b));
			}
		}
		int maxPast = Collections.max(pasts);
		h = Math.max(maxConf, maxPast);
		
		Variable h_var = current_path.get(h);
		
		//conf-set[h] t remove(h,union(conf-set[h],union(conf-set[i],past-fc[i]))); 
		List<Variable> conflicts = new ArrayList<Variable>();
		conflicts.addAll(h_var.getConfSet());
		conflicts.removeAll(curr_var.getConfSet());
		conflicts.addAll(curr_var.getConfSet());
		conflicts.removeAll(curr_var.getPast());
		conflicts.addAll(curr_var.getPast());
		
		h_var.getConfSet().clear();
		h_var.getConfSet().addAll(conflicts);
		
		for(int j = i; j > h+1; j--) {
			current_path.get(j).getConfSet().clear(); 
			undo_reductions(j);
			update_domain(j);
		}
		undo_reductions(h);
		int val = h_var.getInstantiatedVal();
		h_var.getCurrDomain().removeAll(Arrays.asList(val));
		h_var.uninstantiate();
		
		add_var_past(val,h);
		
		i = h;
		
		if(h_var.getCurrDomain().size() > 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean check_forward(int val) {
		List<Integer> reduction = new ArrayList<Integer>();
		Variable curr_var = current_path.get(i);
		Variable next_var = current_path.get(j);
		
		List<Integer> domain_copy = new ArrayList<Integer>();
		domain_copy.addAll(next_var.getCurrDomain());
		//System.out.println("Domain at " + j + " : " + domain_copy);
		
		//Check each domain value against the instantiated value
		while(domain_copy.size() > 0) {
			int next_val = domain_copy.remove(0);
			//System.out.println("Value:" + val + " vs " + next_val);
			boolean consistent = CHECK(curr_var, val, next_var, next_val) && CHECK_UNARY(curr_var,val);
			if(!consistent) {
				//System.out.println("REMOVE^");
				//If inconsistent, add to reduction list
				reduction.add(next_val);
			}
		}		

		//if(reduction.size()>0) {		
		//remove conflicting values from domain
		next_var.getCurrDomain().removeAll(reduction);
		
		//System.out.println("Add Reductions: " + reduction + " to " + reductions.get(j) + " at " + j);
		//add reduction list to global list of lists
		next_var.getReductions().add(reduction);
		//System.out.println("Reductions at j : " + reductions.get(j));
		curr_var.getFuture().remove(next_var);
		curr_var.getFuture().add(next_var);
		
		//System.out.println("Past at future index: " + j + " = " + past_fc.get(j));
		//add i to past_fc[j]
		next_var.getPast().remove(curr_var);
		next_var.getPast().add(curr_var);
		
		//if domain wipe out occurs, then return false
		if(next_var.getCurrDomain().size() == 0) {
			return false;
		}
		//consistent values remain, return true
		else {			
			return true;
		}	
	}
	
	public void undo_reductions(int i) {
		//System.out.println("FC" + future_fc);
		//System.out.println(future_fc.get(i).size());
		for(int k = 0; k < current_path.get(i).getFuture().size(); k ++) {
			Variable next_var = current_path.get(i).getFuture().get(k);
			
			
			List<Integer> reduction = new ArrayList<Integer>();
			//System.out.println(reductions.get(fc_index));
			//grab the most recent set of reductions at fc_index
			if(next_var.getReductions().size()>0) {
				//System.out.println("Undo these reductions " + reductions.get(fc_index) + " at " + fc_index);
				reduction = next_var.getReductions().remove(next_var.getReductions().size()-1);
				//check for duplicates
				//System.out.println("DOM of : " + fc_index + " : " + next_var.getCurrDomain());
				next_var.getCurrDomain().removeAll(reduction);
				//add reductions back to the domain of variable[fc_index]
				next_var.getCurrDomain().addAll(reduction);
				//System.out.println(next_var.getCurrDomain());
				
				//System.out.println("PAST at " + fc_index + " : "  + past_fc.get(fc_index) + "removing: " + i);
				//pop past_fc [fc_index]				
				next_var.getPast().remove(next_var);
				
				//System.out.println("PAST at " + fc_index + " : "  + past_fc.get(fc_index));
			}
		}
		current_path.get(i).getFuture().clear();
	}
	
	public void update_domain(int i) {
		Variable curr_var = current_path.get(i);
		curr_var.resetDomain();
		for(int k = 0; k < curr_var.getReductions().size(); k++) {
			List<Integer> reduction = new ArrayList<Integer>();
			reduction = curr_var.getReductions().get(k);
			curr_var.getCurrDomain().removeAll(reduction);
		}
		
	}
	
	public void add_var_past(int val, int p) {
		//add curr_var to reductions
		if(p > 0) {
			//get most recent reductions set at i
			int ri = current_path.get(p).getReductions().size()-1;
			
			current_path.get(p).getReductions().get(ri).addAll(Arrays.asList(val));
		}
	}
	
}
