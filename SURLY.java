import java.util.*;
import java.lang.*;
import java.io.*;

public class SURLY{

	public static void main(String args[]){
		Database database = new Database();
    SurlyParser parser = new SurlyParser(database);
		if (args.length > 0){
			boolean parseable = parser.parseFile(args[0]);
    	if (!parseable){
				System.out.println("Usage: SURLY.java [filename]");
			}
		}
		System.out.println("Enter 'help' for help, 'exit' to close;");
		parser.parseInput();
  }

}
