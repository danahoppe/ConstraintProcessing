package csp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MAC_DOM_WDEG extends Solver{
	
	//current variable
	protected int i;
	
	//future variable
	protected int j;
		
	public MAC_DOM_WDEG(Instance problem) {
		super(problem);
	}
	
	public void MAC() {
		i = 0;
		j = 1;
		
		generateArcs();
		while(i < current_path.size() && i >= 0) {
			//System.out.println("INDEX: " + i);
			//System.out.println(current_path.get(i).getCurrDomain());
			//System.out.println(this.getAssignedVars().stream().map(x -> String.valueOf(x.instantiated_value)).collect(Collectors.joining(",")));
			//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
			
			//attempts to assign value to current variable by forward checking
			boolean assigned = fc_label();
			//Conflict found
			if(!assigned) {
				if(i>0) {
					fc_unlabel();
				}
				else {
					break;
				}
			}
			else {
				AC2001(current_path.get(i).getArcs());
				//AC3(current_path.get(i).getArcs());
				if(i < current_path.size() - 1) {
					if(this.getVariable_ordering_heuristic().equalsIgnoreCase("dom/wdeg")) {
						int maxWeight = -1;
						int maxIndex = -1;
						for(int k = i + 1; k < current_path.size(); k++) {
							Variable next = current_path.get(k);
							next.setWeight();
							if(next.getWeight() > maxWeight) {
								maxWeight = next.getWeight();
								maxIndex = k;
							}
						}
						//System.out.println(maxWeight);
						//System.out.println("SWAP " + maxIndex + " AND " + (i+1));
						Variable max = current_path.remove(maxIndex);
						current_path.add(i+1, max);	
						
						//domino effect
						for(int d = i+1; d < current_path.size();d++) {
							if(current_path.get(d).getCurrDomain().size() == 1){
								Variable domino = current_path.remove(d);
								current_path.add(i+1, domino);
							}
						}
					}
				}
				i++;				
			}
		
			//solution found
			if(i >= current_path.size()) {
				solutions = solutions + 1;
				if(solutions < 2) {
					print_heuristics();
				}				
				i = i - 1;				
				fc_unlabel();
			}
		}
		if(solutions<1) {
			print_no_solution();
		}
	}
	public boolean fc_label() {
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
			//instantiate
			curr_var.instantiateValue(val);
			nv++;
			
			consistent = true;
			//for each future variable
			while(j < current_path.size()) {
				consistent = check_forward(val);				
				if(!consistent) {
					//remove value from domain
					curr_var.uninstantiate();
					bt++;
					//add curr_var to reductions
					add_var_past(val, i);
					
					//undo reductions at i for all future variables
					undo_reductions(i);
					break;
				}
				j++;
			}
			j--;
		}
		if(consistent) {
			return true;
		}
		return false;
	}
	
	public void fc_unlabel() {
		int h = i - 1;
		undo_reductions(h);
		update_domain(i);
		bt++;
		//un-instantiate and remove value of previous variable[h]
		if(h > -1) {
			Variable curr_var = current_path.get(h);
			int val = curr_var.getInstantiatedVal();
			curr_var.uninstantiate();
			//add curr_var to reductions
			add_var_past(val, h);
		}	
		
		i = h;		
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
		
		curr_var.getFuture().add(next_var);
		
		//System.out.println("Past at future index: " + j + " = " + past_fc.get(j));
		//add i to past_fc[j]
		next_var.getPast().add(curr_var);
		
		//if domain wipe out occurs, then return false
		if(next_var.getCurrDomain().size() == 0) {
			//Determine whether constraint exists between the variables
			for(int i = 0; i < curr_var.getConstraints().size(); i++) {
				Constraint constraintA = curr_var.getConstraints().get(i);
				
				for(int j = 0; j < next_var.getConstraints().size(); j++) {
					Constraint constraintB = next_var.getConstraints().get(j);
					if(constraintA.equals(constraintB)) {
						if(constraintA.getConflicts() == reduction.size()) {
							constraintA.incrWeight();
						}						
					}
				}
			}
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
	
	public void dynamic() {
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
	}
}
