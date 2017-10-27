import java.util.*;
import java.util.regex.*;
import java.lang.*;
import java.io.*;

public class SurlyParser{

  Database database = null;

  public SurlyParser(Database db){
    database = db;
  }

  public void parse(String filename){
    try{
      Scanner scanner = new Scanner(new File(filename));
      while (scanner.hasNextLine()){
				
				String line = scanner.nextLine();
				StringBuffer sb = new StringBuffer();

				Matcher matcher = Pattern.compile("'([^']+)'").matcher(line);
				while (matcher.find()){
					matcher.appendReplacement(sb, matcher.group().replaceAll("\\s+", "|+"));
				}
				matcher.appendTail(sb);
				line = sb.toString();
				String[] parts = line.split(" ");

        for(int i = 1; i < parts.length;i++){
          parts[i] = parts[i].replaceAll("\\(", "");
          parts[i] = parts[i].replaceAll("\\,", "");
          parts[i] = parts[i].replaceAll("\\)", "");
          parts[i] = parts[i].replaceAll("\\;", "");
					parts[i] = parts[i].replaceAll("\\'", "");
					parts[i] = parts[i].replaceAll("\\|\\+", " ");
        }

        if(parts[0].equals("RELATION")){
          String rName = parts[1];
          String[] schema = Arrays.copyOfRange(parts, 2, parts.length);
          database.addRelation(rName, schema);

        }

        if(parts[0].equals("INSERT")){
          String rName = parts[1];
          String[] values = Arrays.copyOfRange(parts, 2, parts.length);
          database.insertTuple(rName, values);
        }

        if(parts[0].equals("PRINT")){
          String[] rNames = Arrays.copyOfRange(parts, 1, parts.length);
          database.print(rNames);
        }

      }

        scanner.close();
    } catch(FileNotFoundException e){
      System.out.print("No file found");
    }
  }

}
