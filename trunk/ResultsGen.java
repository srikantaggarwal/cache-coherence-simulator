import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResultsGen{
	
	//Program usage
	static String usage = 	"java GraphGen [options] file\n" +
							"Options:\n"+
							"\t -p\t Coherence Protocol: 0 = MSI (Default), 1 = MESI"+
							"\t -t\t Test type: 0 = hit rate, 1 = invalidations";
	
	static File traceFile;
	
	static int coherenceProtocol = 0;
	static int testType = 0; // 0 = test hit rate, 1 = test total invalidations
	
	public static void main(String args[]){
		
		processCLI(args);
		
		File resultsFile;
		FileWriter fileWriter;
		
        try {
        	resultsFile = new File("graphing/results");
			fileWriter = new FileWriter(resultsFile);
			fileWriter.write(String.format("#blocks\tblock size\t%s\n", testType == 0 ? "hit rate":"Total invalidations"));
			
			for(int blocks = 2; blocks <= 512; blocks += 24){
				for(int blockSize = 2; blockSize <= 512; blockSize += 24){
					Simulator simulator = new Simulator(blocks, blockSize, coherenceProtocol, traceFile);
					float result = simulator.runTest(testType);
					fileWriter.write(String.format("%d\t%d\t%.2f\n", blocks, blockSize, (float)result));
				}
				fileWriter.write("\n");
			}
			
			fileWriter.close();
			
		} catch (IOException e) {
			System.out.println("Error creating results file");
			e.printStackTrace();
		}
		
	}
	
	public static void processCLI(String[] args){
		
		if(args.length > 0){
			
			// get CLI arguments
			if(args[0].equals("--help")){
				System.out.println(usage);
				System.exit(0);
			}
			
			//Setting up input trace file
			traceFile = new File(args[args.length-1]);
			
			try{
				for(int i = 0; i < args.length-1; i++){
					if(args[i].equals("-p")){
						coherenceProtocol = Integer.parseInt(args[i+1]);
					}
					if(args[i].equals("-t")){
						testType = Integer.parseInt(args[i+1]);
					}
				}
			} catch(NumberFormatException nFE){
				System.out.println("Error, invalid parameters\n\nUsage:"+usage);
			}
		}
	}
	

	
}