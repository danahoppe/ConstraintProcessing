/****************************************************
Name: Dana Hoppe
Class: CSCE 421 
Description: Controller Class for CSP Solvers
*****************************************************/

package csp;

import java.util.ArrayList;
import java.util.List;

public class CSP {
	protected static List<Solver> solutions = new ArrayList<Solver>();
	protected static String static_dynamic = null;
	
	protected static  String hw_name;
	
	protected static Instance instance;
	
	protected static String searchType;
	protected static String orderHeuristic;
	
	
	
	public static void main(String[] args)  {
		hw_name = " ";
		
		if(args.length <= 1) {
			System.out.println("No args - exiting...");
			return;
		}
		
		searchType = args[4];
		
		for(int b = 8; b < args.length; b++) {
			MyParser parser = new MyParser(args[b]);
			instance = parser.getInstance();			
			
			//runAllOrderings();
			
			runInputOrdering(args[6]);			
		}
		FileWriter write = new FileWriter();
		write.WriteFiles(solutions, hw_name);
		
	}

	private static void BT_SOLVE(Instance instance, String orderHeuristic) {
		instance.getVariables().forEach((n) -> n.resetDomain());
		//instance.getVariables().forEach((n) -> n.uninstantiate());
		
		BT_Solver solver = new BT_Solver(instance);
		solver.setVariable_ordering_heuristic(orderHeuristic);
		solver.setVar_static_dynamic(static_dynamic);
		
		solver.print_details(instance, orderHeuristic);
		
		//set variables for solver
		solver.setCurrentPath(instance.getVariables());
		
		solver.BT_SEARCH();
		
		solver.print_all_solutions();
		
		solutions.add(solver);		
	}
	
	private static void CBJ_SOLVE(Instance instance, String orderHeuristic) {
		instance.getVariables().forEach((n) -> n.resetDomain());
		//instance.getVariables().forEach((n) -> n.uninstantiate());
		
		CBJ_Solver solver = new CBJ_Solver(instance);
		solver.setVariable_ordering_heuristic(orderHeuristic);
		solver.setVar_static_dynamic(static_dynamic);
		solver.print_details(instance, orderHeuristic);
		
		solver.setCurrentPath(instance.getVariables());
		
		
		solver.CBJ();
		solver.print_all_solutions();
		
		solutions.add(solver);
	}
	
	private static void FC_SOLVE(Instance instance, String orderHeuristic){
		instance.getVariables().forEach((n) -> n.resetDomain());
		//instance.getVariables().forEach((n) -> n.uninstantiate());
		
		FC_Solver solver = new FC_Solver(instance);
		solver.setVariable_ordering_heuristic(orderHeuristic);
		solver.setVar_static_dynamic(static_dynamic);
		solver.print_details(instance, orderHeuristic);
		
		solver.setCurrentPath(instance.getVariables());
		
		solver.FC();
		solver.print_all_solutions();
		
		solutions.add(solver);
	}
	
	private static void MAC_SOLVE(Instance instance, String orderHeuristic){
		instance.getVariables().forEach((n) -> n.resetDomain());
		//instance.getVariables().forEach((n) -> n.uninstantiate());
		
		MAC_DOM_WDEG solver = new MAC_DOM_WDEG(instance);
		solver.setVariable_ordering_heuristic(orderHeuristic);
		solver.setVar_static_dynamic(static_dynamic);
		solver.print_details(instance, orderHeuristic);
		
		solver.setCurrentPath(instance.getVariables());
		solver.MAC();		
		
		solver.print_all_solutions();
		
		solutions.add(solver);
	}
	
	private static void FC_CBJ_SOLVE(Instance instance, String orderHeuristic){
		FC_CBJ_Solver solver = new FC_CBJ_Solver(instance);
		solver.setVariable_ordering_heuristic(orderHeuristic);
		solver.setVar_static_dynamic(static_dynamic);
		solver.print_details(instance, orderHeuristic);
		
		instance.getVariables().forEach((n) -> n.resetDomain());
		//instance.getVariables().forEach((n) -> n.uninstantiate());
		solver.setCurrentPath(instance.getVariables());
		
		solver.FC_CBJ();		
		
		solver.print_all_solutions();
		
		solutions.add(solver);
	}
	
	private static void AC1() {
		/* AC1 + AC3
		String acStr = args[4];
		
		Timer time = new Timer();
		
		if(acStr.equals("ac1")) {
			Solver solver = new Solver(instance);
			
			solver.AC1();
			
			System.out.println("cc: " + solver.getCC());
			System.out.println("cpu: " + time.getCpuTime());
			System.out.println("fval: " + solver.getfval() + " (before discovering domain wipeout)");
		}
		else if(acStr.contentEquals("ac3")) {
			Solver solver = new Solver(instance);
			
			solver.AC1();
			
			System.out.print("cc: " + solver.getCC());
		}
		*/
	}
	private static void AC3() {
		return;
	}
	
	private static void runInputOrdering(String string) {
		Timer time = new Timer();
		
		if(string.equalsIgnoreCase("LX")) {
			orderHeuristic = "LX";
			static_dynamic = "static";
		}
		else if(string.equalsIgnoreCase("LD")) {
			orderHeuristic = "LD";
			static_dynamic = "static";
		}
		else if(string.equalsIgnoreCase("DD")) {
			orderHeuristic = "DD";
			static_dynamic = "static";
		}
		else if(string.equalsIgnoreCase("DEG")) {
			orderHeuristic = "DEG";
			static_dynamic = "static";
		}
		else if(string.equalsIgnoreCase("dLD")) {
			orderHeuristic = "dLD";
			static_dynamic = "dynamic";
		}
		else if(string.equalsIgnoreCase("dDEG")) {
			orderHeuristic = "dDEG";
			static_dynamic = "dynamic";
		}
		else if(string.equalsIgnoreCase("dDD")) {
			orderHeuristic = "dDD";
			static_dynamic = "dynamic";
		}
		else if(string.equalsIgnoreCase("dom/wdeg")) {
			orderHeuristic = "dom/wdeg";
			static_dynamic = "dynamic";
		}
		
		if(searchType.contentEquals("BT")) {
			BT_SOLVE(instance, orderHeuristic);
			hw_name = "BT";
		}
		else if(searchType.contentEquals("CBJ")) {
			CBJ_SOLVE(instance, orderHeuristic);
			hw_name = "CBJ";
		}
		else if(searchType.contentEquals("FC")) {
			FC_SOLVE(instance, orderHeuristic);	
			hw_name = "FC";
		}
		else if(searchType.contentEquals("MAC")) {
			MAC_SOLVE(instance, orderHeuristic);
			hw_name = "MAC";
		}
		else if(searchType.contentEquals("FCCBJ")) {
			FC_CBJ_SOLVE(instance, orderHeuristic);		
			hw_name = "FC-CBJ";
		}		
	}
	
	private static void runAllOrderings() {
		for(int i = 0; i < 5; i++) {
			Timer time = new Timer();
			if(i == 0) {
				orderHeuristic = "LX";
				static_dynamic = "static";
			}
			else if(i == 1) {
				orderHeuristic = "LD";
				static_dynamic = "static";
			}
			else if(i == 2) {
				orderHeuristic = "DD";
				static_dynamic = "static";
			}
			else if(i == 3) {
				orderHeuristic = "DEG";
				static_dynamic = "static";
			}
			else if(i == 4) {
				orderHeuristic = "dom/wdeg";
				static_dynamic = "static";
			}
			else if(i == 5) {
				orderHeuristic = "dLD";
				static_dynamic = "dynamic";
			}
			else if(i == 6) {
				orderHeuristic = "dDEG";
				static_dynamic = "dynamic";
			}
			else if(i == 7) {
				orderHeuristic = "dDD";
				static_dynamic = "dynamic";
			}
			
			if(searchType.contentEquals("CBJ")) {
				BT_SOLVE(instance, orderHeuristic);
				hw_name = "BT";
			}
			else if(searchType.contentEquals("CBJ")) {
				CBJ_SOLVE(instance, orderHeuristic);
				hw_name = "CBJ";
			}
			else if(searchType.contentEquals("FC")) {
				FC_SOLVE(instance, orderHeuristic);	
				hw_name = "FC";
			}
			else if(searchType.contentEquals("MAC")) {
				MAC_SOLVE(instance, orderHeuristic);	
				hw_name = "MAC";
			}
			else if(searchType.contentEquals("FCCBJ")) {
				FC_CBJ_SOLVE(instance, orderHeuristic);		
				hw_name = "FC-CBJ";
			}
		
		}
		
	}


}
