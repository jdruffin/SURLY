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
    boolean parsing = true;
    while (parsing){
      String[] parts = formatCommand(line);

      switch (parts[0].toUpperCase()){
        case "SAVEAS":
          if (parts.length == 2){
            saveToFile(parts[1]);
          } else{
            formatErr("SAVEAS");
          }
          break;
        case "LOAD":
        if (parts.length == 2){
          database = loadFromFile(parts[1]);
        } else{
          formatErr("LOAD");
        }
          break;
        case "INPUT":
          if (parts.length == 2){
            parseFile(parts[1]);
          } else{
            formatErr("INPUT");
          }
          break;
			  case "HELP":
          help(parts);
          break;
        case "EXIT":
          parsing = false;
          break;
        default:
          executeCommand(parts);
          break;
      }
      if (parsing){
        System.out.print("SURLY:> ");
        line = input.nextLine();
      }
    }

		System.out.println("Goodbye.");
		System.exit(0);
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
    if ((parts.length > 1 && !parts[1].equals("=")) || parts[0].equalsIgnoreCase("PRINT")){
      switch (parts[0].toUpperCase()){
  			case "RELATION":
          if (parts.length >= 3){
    				String createName = parts[1];
          	String[] schema = Arrays.copyOfRange(parts, 2, parts.length);
          	database.addRelation(createName, schema);
          } else{
            formatErr("RELATION");
          }
  				return;
      	case "INSERT":
          if (parts.length >= 4){
          	String insertName = parts[1];
          	String[] values = Arrays.copyOfRange(parts, 2, parts.length);
          	database.insertTuple(insertName, values);
          } else{
            formatErr("INSERT");
          }
  				return;
      	case "PRINT":
        	database.print(parts);
  				return;
        case "DESTROY":
          if (parts.length == 2){
            String destroyName = parts[1];
            database.destroy(destroyName);
          } else{
            formatErr("DESTROY");
          }
          return;
        case "DELETE":
          if (parts.length >= 2){
            String deleteName = parts[1];
            String[] conditions = {};
            if (parts.length > 2){
              conditions = Arrays.copyOfRange(parts, 2, parts.length);
            }
            database.deleteWhere(deleteName, conditions);
          } else{
            formatErr("DELETE");
          }
          return;
  			default:
          System.out.println(parts[0]+" is not a command.");
  				return;
      }
    } else{
      String tempName = parts[0];
      switch (parts[2].toUpperCase()){
        case "SELECT":
          if (parts.length >= 4){
            String selectName = parts[3];
            String[] conditions = {};
            if (parts.length > 4){
              conditions = Arrays.copyOfRange(parts, 4, parts.length);
            }
            database.selectWhere(selectName, conditions, tempName);
          } else{
            formatErr("SELECT");
          }
          return;
        case "PROJECT":
          if (parts.length >= 6){
            String projectName = parts[parts.length-1];
            String[] attributes = Arrays.copyOfRange(parts, 3, parts.length-2);
            database.project(projectName, attributes, tempName);
          } else{
            formatErr("PROJECT");
          }
          return;
        case "JOIN":
          if (parts.length == 9){
            String joinName1 = parts[3];
            String joinName2 = parts[4];
            String[] joinCondition = Arrays.copyOfRange(parts, 6, parts.length);
            database.join(joinName1, joinName2, joinCondition, tempName);
          } else{
            formatErr("JOIN");
          }
          return;
        default:
          System.out.println(parts[2]+" is not a command.");
          return;
      }
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

    for (int i = 0; i < parts.length;i++){
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

  private void help(String[] commands){
    HashMap<String, String> helpMsgs = new HashMap<String, String>(12);

     helpMsgs.put("DELETE", "'DELETE':  Deletes tuples from a relation that meet the conditions, or all tuples if no conditions are entered.\n"+
                              "  usage: DELETE <relationName> [WHERE <conditions>];\n");
     helpMsgs.put("DESTROY", "'DESTROY':  Removes the relation from the database.\n"+
                              "  usage: DESTROY <relationName>;\n");
     helpMsgs.put("INSERT", "'INSERT':  Inserts a tuple of values into the relation.\n"+
                              "  usage: INSERT <relationName> <valueList>;\n");
     helpMsgs.put("INPUT", "'INPUT':  Runs Surly commands from the input file.\n"+
                              "  usage: INPUT <fileName.txt>;\n");
     helpMsgs.put("JOIN", "'JOIN':  Joins tuples from relation1 with tuples from relation2 if they satisfy the join condition, and stores them in a new temporary relation.\n"+
                              "  usage: <tempName> = JOIN <relation1>, <relation2> ON <joinCondition>;\n");
     helpMsgs.put("LOAD", "'LOAD':  Replaces the current database state with the state saved in fileName.sur.\n"+
                              "  usage: LOAD <fileName.sur>;\n");
     helpMsgs.put("PRINT", "'PRINT':  Prints the state of the specified relations, or the database schema if no relations are entered.\n"+
                              "  usage: PRINT [<relationList>];\n");
     helpMsgs.put("PROJECT", "'PROJECT':  Stores a temporary copy of the specified relation with only the specified attributes.\n"+
                              "  usage: <tempName> = PROJECT <attributeList> FROM <relationName>;\n");
     helpMsgs.put("RELATION", "'RELATION':  Creates new relation with the specified name and schema.\n"+
                              "  usage: RELATION <relationName> (<schema>);\n");
     helpMsgs.put("SAVEAS", "'SAVEAS':  Saves the database state as fileName.sur.\n"+
                              "  usage: SAVEAS <fileName>;\n");
     helpMsgs.put("SELECT", "'SELECT':  Stores a copy of the specified relation with only tuples that meet the conditions.\n"+
                              "  usage: <tempName> = SELECT <relationName> [WHERE <conditions>];\n");
     helpMsgs.put("EXIT", "'EXIT':  Exits the program.\n"+
                              "  usage: EXIT\n");

    if (commands.length == 1){
      System.out.println("--SURLY Help--\n"+
                          "User input options:\n");
      for (Map.Entry<String, String> entry : helpMsgs.entrySet()){
        String msg = entry.getValue();
        System.out.println(msg);
      }
    } else{
      String cmd;
      for (int i=1; i < commands.length; i++){
        cmd = commands[i];
        System.out.println(helpMsgs.get(cmd));
      }
    }
  }

  private void formatErr(String cmd){
    System.out.println("FORMAT_ERR: Bad format for command "+cmd+".");
    String[] helpType = {cmd};
    help(helpType);
  }

  public Database getDatabase(){
    return database;
  }
}
