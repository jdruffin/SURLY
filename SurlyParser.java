import java.util.*;
import java.util.regex.*;
import java.lang.*;
import java.io.*;

public class SurlyParser{

  private Database database = null;

  public SurlyParser(Database db){
    database = db;
  }

  public void parseInput(){
    Scanner input = new Scanner(System.in);
    System.out.print("SURLY:> ");
    String line = input.nextLine();
    while (!line.toUpperCase().equals("EXIT")) {
      //System.out.println();
      String[] parts = formatCommand(line);

      if (parts[0].toUpperCase().equals("SAVEAS")){ //not implemented yet
        saveToFile(parts[1]);
      } else if (parts[0].toUpperCase().equals("LOAD")){
        database = loadFromFile(parts[1]);
      } else if (parts[0].toUpperCase().equals("INPUT")){
        parseFile(parts[1]);
      } else {
        executeCommand(parts);
      }
      System.out.print("SURLY:> ");
      line = input.nextLine();
    }
		System.out.println("Goodbye.");
		System.exit(1);
    input.close();
  }

  public boolean parseFile(String filename){
    try{
      Scanner scanner = new Scanner(new File(filename));
      while (scanner.hasNextLine()){
				String line = scanner.nextLine();
        String[] parts = formatCommand(line);
        executeCommand(parts);
      }
      scanner.close();
      return true;

    } catch(FileNotFoundException e){
      System.out.print("Could not find file '"+filename+"'");
      return false;
    }
  }

  private void executeCommand(String[] parts){
    if (parts[0].toUpperCase().equals("RELATION")){
      String rName = parts[1];
      String[] schema = Arrays.copyOfRange(parts, 2, parts.length);
      database.addRelation(rName, schema);
    }

    if (parts[0].toUpperCase().equals("INSERT")){
      String rName = parts[1];
      String[] values = Arrays.copyOfRange(parts, 2, parts.length);
      database.insertTuple(rName, values);
    }

    if (parts[0].toUpperCase().equals("PRINT")){
      String[] rNames = Arrays.copyOfRange(parts, 1, parts.length);
      database.print(rNames);
    }
  }

  private String[] formatCommand(String line){
    StringBuffer sb = new StringBuffer();

    Matcher matcher = Pattern.compile("'([^']+)'").matcher(line);
    while (matcher.find()){
      matcher.appendReplacement(sb, matcher.group().replaceAll("\\s+", "|+"));
    }
    matcher.appendTail(sb);
    line = sb.toString();
    String[] parts = line.split(" ");

    for (int i = 1; i < parts.length;i++){
      parts[i] = parts[i].replaceAll("\\(", "");
      parts[i] = parts[i].replaceAll("\\,", "");
      parts[i] = parts[i].replaceAll("\\)", "");
      parts[i] = parts[i].replaceAll("\\;", "");
      parts[i] = parts[i].replaceAll("\\'", "");
      parts[i] = parts[i].replaceAll("\\|\\+", " ");
    }
    return parts;
  }

  private void saveToFile(String fileName){
    try{
      FileOutputStream fileOut = new FileOutputStream(fileName+".sur");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(database);
      out.close();
      fileOut.close();
      System.out.println("Saved database to "+fileName+".sur");
    } catch (IOException e){
      System.out.println("Couldn't save to file.");
    }
  }

  private Database loadFromFile(String fileName){
    Database db = new Database();
    try{
      if (fileName.contains(".sur")){
        FileInputStream fileIn = new FileInputStream(fileName);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        db = (Database) in.readObject();
        in.close();
        fileIn.close();
      } else {
        System.out.println("Requires '<filename>.sur'");
      }
    } catch (IOException e) {
      System.out.println("Could not read from file "+fileName);
    } catch (ClassNotFoundException c) {
      System.out.println("Could not find Database.class");
    }
    return db;
  }

  public Database getDatabase(){
    return database;
  }
}
