package csp;

import java.util.List;
import java.io.File;
import java.io.FileOutputStream;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * This class is responsible for writing solutions for multiple test cases
 * to an excel file for debugging and grading purposes.
 * Based on https://www.geeksforgeeks.org/reading-writing-data-excel-file-using-apache-poi/
 */

public class FileWriter {

	public FileWriter() {
		// TODO Auto-generated constructor stub
	}
	
	public void WriteFiles(List<Solver> solutions, String name) {
		
	
		//Create blank workbook
	    XSSFWorkbook workbook = new XSSFWorkbook();
	      
	    //Create a blank sheet
	    XSSFSheet spreadsheet = workbook.createSheet( " Search Results ");
		
	  //Create a blank sheet
	    XSSFSheet ordersheet = workbook.createSheet( " Orderings ");
	    
		// Iterate over data and write to sheet 
		int rownum = 0; 
		 int rownum2 = 0;
		
		for(int i = 0; i < solutions.size(); i++) {			  

		    //this creates a new row in the sheet 
		    Row row = spreadsheet.createRow(rownum++); 
		    int cellnum = 0; 
		    
	        // this line creates a cell in the next column of that row 
	        Cell cell = row.createCell(cellnum++); 
	        
	        //Problem Name
	        cell.setCellValue(solutions.get(i).getProblemName()); 
	        cell = row.createCell(cellnum++);
	        
	        //Algorithm
	        cell.setCellValue(name);
	        cell = row.createCell(cellnum++); 

	        //Ordering Heuristic
	        cell.setCellValue(solutions.get(i).getVariable_ordering_heuristic());
	        cell = row.createCell(cellnum++);
	        
	        //First Solution CC
	        cell.setCellValue(solutions.get(i).getFirst_cc());
	        cell = row.createCell(cellnum++); 
	        
	        //First Solution NV
	        cell.setCellValue(solutions.get(i).getFirst_nv());
	        cell = row.createCell(cellnum++); 
	        
	        //First Solution BT
	        cell.setCellValue(solutions.get(i).getFirst_bt());
	        cell = row.createCell(cellnum++); 
	        
	        //First Solution CPU
	        cell.setCellValue(solutions.get(i).getFirstCpuTime());
	        cell = row.createCell(cellnum++);
	        
	        //All Solutions CC
	        cell.setCellValue(solutions.get(i).getCC());
	        cell = row.createCell(cellnum++); 
	        
	        //All Solutions NV
	        cell.setCellValue(solutions.get(i).getNV());
	        cell = row.createCell(cellnum++); 
	        
	        //All Solutions BT
	        cell.setCellValue(solutions.get(i).getBT());
	        cell = row.createCell(cellnum++); 
	        
	        //All Solution CPU
	        cell.setCellValue(solutions.get(i).getCpuTime());
	        cell = row.createCell(cellnum++); 
	        
	        //Setup Time
	        cell.setCellValue(solutions.get(i).getSolutionCount());
	        cell = row.createCell(cellnum++);
	        
	        //Add orderings to order sheet
	        
	        //this creates a new row in the sheet 
		    Row row2 = ordersheet.createRow(rownum2++); 
		   
		    int cellnum2 = 0; 
		    
	        // this line creates a cell in the next column of that row 
	        Cell cell2 = row2.createCell(cellnum2++); 
	        
	        //Problem Name
	        cell2.setCellValue(solutions.get(i).getProblemName()); 
	        cell2 = row2.createCell(cellnum2++);
	        
	      //Ordering type Name
	        cell2.setCellValue(solutions.get(i).getVariable_ordering_heuristic()); 
	        cell2 = row2.createCell(cellnum2++);
	        
	        for(int j = 0; j < solutions.get(i).getCurrentPath().size(); j++) {
	        	//Variable at i
		        cell2.setCellValue(solutions.get(i).getCurrentPath().get(j).getName()); 
		        cell2 = row2.createCell(cellnum2++);
	        }
	      
	        
	        
	        
		} 
			try { 
			    //this Writes the workbook gfgcontribute 
			    FileOutputStream out = new FileOutputStream(new File("Dana_Hoppe_Homework_" + name + ".xlsx")); 
			    workbook.write(out); 
			    out.close(); 
			    System.out.println("File written successfully on disk."); 
			} 
			catch (Exception e) { 
			    e.printStackTrace(); 
			} 
			
		}

}
