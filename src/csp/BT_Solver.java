package csp;

public class BT_Solver extends Solver{

	public BT_Solver(Instance problem) {
		super(problem);
	}

	public void BT_SEARCH() {
		
		//Run Node Consistency
		NODE_CONSISTENCY(current_path);
		
		int i = 0;
		String status = "unknown";
		boolean consistent = true;
		while(status.contentEquals("unknown")){
			
			//System.out.println(cc);
			if(consistent) {
				int label = bt_label(i);
				if(label == i) {
					consistent = false;
				}
				i = label;
			}
			else{
				int label = bt_unlabel(i);
				if(label > -1) {
					consistent = (current_path.get(label).getCurrDomain().size() > 0);
				}
				i = label;
			}
			
			if(i>=current_path.size()) {
				//System.out.println(this.getAssignedVars().stream().map(x -> String.valueOf(x.instantiated_value)).collect(Collectors.joining(",")));
				//System.out.println(current_path.stream().map(Variable::getName).collect(Collectors.joining(",")));
				solutions = solutions + 1;
				

				if(solutions < 2) {
					Timer time = new Timer();
					
					System.out.println("variable-order-heuristic: " + this.getVariable_ordering_heuristic());
					System.out.println("var-static-dynamic: " + this.getVar_static_dynamic());
					System.out.println("value-ordering-heuristic: " + this.getValue_ordering_heuristic());
					System.out.println("val-static-dynamic: " + this.getValue_static_dynamic());
					
					first_cpu_time = time.getCpuTime()/100000000.00;
					first_cc = this.getCC();
					first_nv = this.getNV();
					first_bt = this.getBT();
					
					System.out.println("cc: " + this.getCC());
					System.out.println("nv: " + this.getNV());
					System.out.println("bt: " + this.getBT());
					System.out.println("cpu: " + this.getCpuTime());
					
					System.out.print("First solution: ");

					for(int p = 0; p < this.getAssignedVars().size(); p++) {
						System.out.print(this.getAssignedVars().get(p).getInstantiatedVal() + " ");
					}
				}
				
				//To-Do remove the last instantiated variable
				Variable finalVar = current_path.get(current_path.size()-1);
				for(int b = 0; b < finalVar.getCurrDomain().size(); b++) {
					if(finalVar.getCurrDomain().get(b) == finalVar.getInstantiatedVal()) {
						finalVar.getCurrDomain().remove(b);
					}
				}
				finalVar.uninstantiate();
				
				i = i - 1;
				
			}
			else if(i<0) {
				
				if(solutions<1) {
					Timer time = new Timer();
					
					System.out.println("variable-order-heuristic: " + this.getVariable_ordering_heuristic());
					System.out.println("var-static-dynamic: " + this.getVar_static_dynamic());
					System.out.println("value-ordering-heuristic: " + this.getValue_ordering_heuristic());
					System.out.println("val-static-dynamic: " + this.getValue_static_dynamic());
					
					first_cpu_time = time.getCpuTime()/100000000.00;
					first_cc = this.getCC();
					first_nv = this.getNV();
					first_bt = this.getBT();
					
					System.out.println("cc: " + this.getCC());
					System.out.println("nv: " + this.getNV());
					System.out.println("bt: " + this.getBT());
					System.out.println("cpu: " + this.first_cpu_time);
					
					System.out.print("First solution: " + "No Solution");
				}
				
				status = "exit";
			}
		}
	
	}
	
	public int bt_label(int i) {
		boolean consistent = false;
		Variable var = current_path.get(i);
		int j = 0;
		
		//Check against previously instantiated variables
		while(consistent == false && var.getCurrDomain().size()>0) {
			nv = getNV() + 1;
			consistent = true;
			//check unary consistency
			boolean unary_consistent;
			unary_consistent = CHECK_UNARY(var,var.getCurrDomain().get(j));
			//check against previous instantiations
			for(int h = 0; h < i; h ++) {
				Variable hVar = current_path.get(h);
				consistent = CHECK(var, var.getCurrDomain().get(j), hVar, hVar.getInstantiatedVal());
				if(consistent == false) {
					var.getCurrDomain().remove(j);
					break;
				}
			}
			if(consistent && unary_consistent) {
				var.instantiateValue(var.getCurrDomain().get(j));
				break;
			}
			else if(consistent && !unary_consistent) {
				var.getCurrDomain().remove(j);
				break;
			}
		}
		if(consistent == true) {
			return i + 1;
		}
		else {
			return i;
		}
	}
	
	public int bt_unlabel(int i){
		int h = i - 1;
		current_path.get(i).resetDomain();
		if(h > -1) {
			for(int j = 0; j < current_path.get(h).getCurrDomain().size();j++) {
				if(current_path.get(h).getCurrDomain().get(j) == current_path.get(h).getInstantiatedVal()) {
					current_path.get(h).getCurrDomain().remove(j);
				}
			}
			current_path.get(h).uninstantiate();
		}
		bt = getBT() + 1;
		return h;
	}
	
}
