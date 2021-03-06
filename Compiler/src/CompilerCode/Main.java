package CompilerCode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
	public static void main(String[] args) throws IOException{
		boolean printTokens = false;
		boolean printParseTree = false;
		boolean printSymbolTable = false;
		boolean printIR = false;
		boolean printAR = false;
		boolean readIR = false;
		boolean exportIR = false;
		boolean exportAR = false;
		String filename = null;
		String IRfilenamein = null;
		String IRfilenameout = null;
		String ARfilename = null;
		Scan scanner;
		Grammar grammar;
		SymbolTable symTable = null;
		
		//Not using command line arguments
		if(args.length == 0) {
			printTokens = true;
			printParseTree = true;
			printSymbolTable = true;
			printIR = true;
			printAR = true;
			filename = "TestFiles/Testfile.txt";
		}
		
		
		//Check for command line arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			switch(arg) {
			case "-h":
				System.out.println("Usage:");
				System.out.println("./compiler [-t] [-p] [-s] [-ir] [-ar] [-readIR filename] [-exportIR filename] [-exportAR filename] [-f filename]");
				System.out.println("    -f compiles the specified c file");
				System.out.println("    -t prints out scanner tokens");
				System.out.println("    -p prints out parse tree");
				System.out.println("    -s prints out the symbol table");
				System.out.println("    -ir prints out the intermediate representation");
				System.out.println("    -ar prints out the Assembly Representation");
				System.out.println("    -readIR reads the IR from a file");
				System.out.println("    -exportIR exports the IR to a file");
				System.out.println("    -exportAR exports the AR to a file");
				return;
			case "-t":
				printTokens = true;
				break;
			case "-p":
				printParseTree = true;
				break;
			case "-s":
				printSymbolTable = true;
				break;
			case "-ir":
				printIR = true;
				break;
			case "-ar":
				printAR = true;
				break;
			case "-f":
				i++;
				filename = args[i];
				break;
			case "-readIR":
				i++;
				IRfilenamein = args[i];
				readIR = true;
				break;
			case "-exportIR":
				i++;
				IRfilenameout = args[i];
				exportIR = true;
				break;
			case "-exportAR":
				i++;
				ARfilename = args[i];
				exportAR = true;
				break;
			}
			if (arg == args[args.length - 1]) {
				filename = arg;
			}
		}
		
		
		
		
		
		if(!readIR) {
			if(filename == null) {
				filename = "Testfile.txt";
			}
		
			List<String> lines = readFile(filename);
		
			//Scanner
			scanner = new Scan(lines);
		
			if(ErrorHandler.errorsExist()) {
				ErrorHandler.printStrings("Scanner");
				return;
			}
		
			if (printTokens) {
				System.out.println("Scanner:");
				scanner.printTokens();
			}
		
			//Parse Tree
			grammar = new Grammar(scanner.tokens);
			
			if(grammar.valid == false) {
				return;
			}
			if(ErrorHandler.errorsExist()) {
				ErrorHandler.printStrings("Parse Tree Creation");
				return;
			}
		
			if(printParseTree) {
				grammar.printTree();
			}
		
			grammar.root.parseErrorChecker();
			if(ErrorHandler.errorsExist()) {
				ErrorHandler.printStrings("parseErrorChecker");
				return;
			}

			//Symbol Table
			symTable = new SymbolTable();
			symTable.buildDeclarationTable(grammar.root);
			if(printSymbolTable) {
				symTable.printSymbolTable();
				System.out.println();
			}
			if(ErrorHandler.errorsExist()) {
				ErrorHandler.printStrings("Symbol Table Creation");
				return;
			}
					
			//Intermediate Representation
			IRcreation.createIR(grammar.root);
		} else {
			IR.readIRFromFile(IRfilenamein);
		}
		
		if(ErrorHandler.errorsExist()) {
			ErrorHandler.printStrings("IR creation");
			return;
		}
		
		if(exportIR) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(IRfilenameout));
			writer.write(IR.IRtoFile());
			writer.close();
		}
		
		
		if(printIR) {
			IR.printIR();
		}
		
		IRtransformation.IRtransformationFunction(symTable.entries.size() > 0);
		System.out.println("\n");
		
		if(printIR) {
			IR.printIR();
		}
		
		ARcreation.createAR();

		if(ErrorHandler.errorsExist()) {
			ErrorHandler.printStrings("AR Creation");
			return;
		}
		
		if(printAR) {
			System.out.println("\n\nAR output\n");
			AR.printAR();
		}
		
		
		if(exportAR) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(ARfilename));
			writer.write(AR.ARtoFile());
			writer.close();
		}
	}

	
	/**
	 * Read in file and return a list of strings for each line
	 * @param filename The name of the file to open.
	 * @return a list of strings
	 */
	private static List<String> readFile(String filename)
	{
		List<String> lines = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       lines.add(line);
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}
}

