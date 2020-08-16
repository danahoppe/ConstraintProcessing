----------------------
Project
----------------------
Dana Hoppe
CSCE 421
Homework 7
5/7/2020

----------------------
Description
----------------------

This program implements A mulititude of consistency algorithms for enforcing CSP constraints. Specifically added
for this assignment were tools for MAC3, MAC2001, and dom/wdeg.

Files:

CSP.java - main class for calling Parser and Solver methods.

Solver.java - class with AC1 and AC3 functions in addition to supporting functions such as CHECK,
			  REVISE, and SUPPORTED.
BT_Solver.java - Backtrack Search

CBJ_Solver.java - conflict directed backjumping

FC_Solver - Forward Checking

FC_CBJ_Solver - Constraint backjumping with forward check

MAC_DOM_WDEG - implements MAC and dom/wdeg ordering

FileWriter - writes data to excel file
			  
Arc.java - data structure for storing variable pairs for constraint network

abscon(folder)- library including all files neccesary for abscon parsing. 

MyParser.java -	using abscon-parsor to parse csp data structures from .xml
		files and wrap them in .java data structures.

Variable.java -	class with data structure containing variable name, related constraints, domain, and neighbors.

ExtensionConstraint.java  
	      - class with data structure including constraint name, related variables, allowable/forbidden 
		tuples, and defintion of relation.

IntensionConstraint.java
	      - class with data structure including constrain name, related variables, predicate functions,
		and function type.

csp.jar	      - .jar file for running the program.

makeFile.mf   - file for creating csp.jar.

runProgram.sh - instructions for webgrader on how to run the program

----------------------
Command-Line
----------------------

-make			: generates csp.jar when executed in directory containing makeFile.mf

-java -jar csp.jar	: executes csp.jar

-java -Xmx256m -jar csp.jar -s <SEARCH TYPE> -u <ORDERINGHEURISTIC> -f <FILENAME>.xml
			: executes program for given .xml file

----------------------
References
----------------------

Utilized ArrayList sorting methods using Comparator found at:
https://beginnersbook.com/2013/12/java-arraylist-of-object-sort-example-comparable-and-comparator/