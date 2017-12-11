import java.util.*;
import java.util.regex.*;
import java.lang.*;
import java.io.*;

public class SurlyParser{

  private Database database = null;

	public SurlyParser(){
		database = new Database();
	}

  public SurlyParser(Database db){
    database = db;
  }

  public void parseInput(){
    Scanner input = new Scanner(System.in);
    boolean parsing = true;
    while (parsing){
			System.out.print("SURLY:> ");
			String line = input.nextLine();
      String[] parts = formatCommand(line);

      switch (parts[0].toUpperCase()){
        case "SAVEAS":
          if (parts.length == 2){
            saveToFile(parts[1]);
          } else {
            formatErr("SAVEAS");
          }
          break;
        case "LOAD":
        if (parts.length == 2){
          loadFromFile(parts[1]);
        } else {
          formatErr("LOAD");
        }
          break;
        case "INPUT":
          if (parts.length == 2){
            parseFile(parts[1]);
          } else {
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
		String command = parts[0].toUpperCase();
		if (parts.length > 1 || command.equals("PRINT")){		
			if (command.equals("PRINT") || !parts[1].equals("=")){
				switch (command){
					case "RELATION":
						if (parts.length >= 3){
							String relationName = parts[1];
							String[] schema = Arrays.copyOfRange(parts, 2, parts.length);
							database.addRelation(relationName, schema);
						} else {
							formatErr("RELATION");
						}
						return;
					case "INSERT":
						if (parts.length >= 4){
							String insertName = parts[1];
							String[] values = Arrays.copyOfRange(parts, 2, parts.length);
							database.insertTuple(insertName, values);
						} else {
							formatErr("INSERT");
						}
						return;
					case "PRINT":
						database.print(parts);
						return;
					case "DESTROY":
						if (parts.length == 2){
							String destroyName = parts[1];
							database.destroy(destroyName, false);
						} else {
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
						} else {
							formatErr("DELETE");
						}
						return;
					default:
						System.out.println(parts[0]+": command not found");
						return;
				}
			} else if (parts.length >= 3){
				String tempName = parts[0];
				command = parts[2].toUpperCase();
				switch (command){
					case "SELECT":
						if (parts.length >= 4){
							String selectName = parts[3];
							String[] conditions = {};
							if (parts.length > 4){
								conditions = Arrays.copyOfRange(parts, 4, parts.length);
							}
							database.selectWhere(selectName, conditions, tempName);
						} else {
							formatErr("SELECT");
						}
						return;
					case "PROJECT":
						if (parts.length >= 6){
							String projectName = parts[parts.length-1];
							String[] attributes = Arrays.copyOfRange(parts, 3, parts.length-2);
							database.project(projectName, attributes, tempName);
						} else {
							formatErr("PROJECT");
						}
						return;
					case "JOIN":
						if (parts.length >= 5){
							String joinName1 = parts[3];
							String joinName2 = parts[4];
							String[] joinCondition;
							if (parts.length == 9){
								joinCondition = Arrays.copyOfRange(parts, 6, parts.length);
								database.join(joinName1, joinName2, joinCondition, tempName);
							} else if (parts.length == 5){
								joinCondition = null;
								database.join(joinName1, joinName2, joinCondition, tempName);
							}
						} else {
							formatErr("JOIN");
						}
						return;
					default:
						System.out.println(parts[2]+": command not found");
						return;
				}
			}
		}
		if (!command.equals("")){
			System.out.println(parts[0]+": command not found");
		}
		return;
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

  private void loadFromFile(String fileName){
    Database db = database;
		if (fileName.length() >= 4){
			String extension = fileName.substring(fileName.length()-4);
			try{
				if (extension.equals(".sur")){
					System.out.println("Successfully loaded database");
					FileInputStream fileIn = new FileInputStream(fileName);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					db = (Database) in.readObject();
					in.close();
					fileIn.close();
				} else {
					System.out.println("Requires 'filename.sur'");
				}
			} catch (IOException e) {
				System.out.println("Could not read from file "+fileName);
			} catch (ClassNotFoundException c) {
				System.out.println("Could not find Database.class");
			}
		}
		database = db;
    return;
  }

  private void help(String[] commands){
    HashMap<String, String> helpMsgs = new HashMap<String, String>(12);

     helpMsgs.put("DELETE", "DELETE:  Deletes tuples from relation 'name' that satisfy all conditions, or all tuples if no condition.\n"+
                              "  usage: DELETE name [WHERE condition ...]");
     helpMsgs.put("DESTROY", "DESTROY:  Removes relation 'name' from the database.\n"+
                              "  usage: DESTROY name");
     helpMsgs.put("INSERT", "INSERT:  Inserts a tuple of values 'value' into relation 'name' that match the relation's schema.\n"+
                              "  usage: INSERT name value ...");
     helpMsgs.put("INPUT", "INPUT:  Runs commands from 'file'.\n"+
                              "  usage: INPUT file.txt");
     helpMsgs.put("JOIN", "JOIN:  Joins tuples from 'relation1' with tuples from 'relation2' if they satisfy 'condition', and stores them in temporary relation.\n"+
                              "  usage: name = JOIN relation1, relation2 ON condition");
     helpMsgs.put("LOAD", "LOAD:  Replaces the current database state with the state saved in fileName.sur.\n"+
                              "  usage: LOAD file.sur");
     helpMsgs.put("PRINT", "PRINT:  Prints the database schema, or any specified relations 'name'.\n"+
                              "  usage: PRINT [name ...]");
     helpMsgs.put("PROJECT", "PROJECT:  Stores all attributes listed from 'relation' in temporary relation 'name'.\n"+
                              "  usage: temp = PROJECT attribute ... FROM relation");
     helpMsgs.put("RELATION", "RELATION:  Creates a new relation called 'name' with attributes 'title' of type 'type' and max length 'length'.\n"+
                              "  usage: RELATION name (title, type, length ...)");
     helpMsgs.put("SAVEAS", "SAVEAS:  Saves the database state to file 'filepath'.\n"+
                              "  usage: SAVEAS filepath");
     helpMsgs.put("SELECT", "SELECT:  Stores all tuples from 'relation' that meet the conditions in temporary relation 'name'.\n"+
                              "  usage: name = SELECT relation [WHERE condition ...]");
     helpMsgs.put("EXIT", "EXIT:  Exits the program.\n"+
                              "  usage: EXIT");
    if (commands.length == 1){
      System.out.println("--SURLY HELP--\n"+
                          "User input options:\n"+
													"Type 'help name' to find out more about the function 'name'.");
      for (Map.Entry<String, String> entry : helpMsgs.entrySet()){
        String msg = entry.getValue();
        System.out.println(msg);
      }
			System.out.println("------END-----");
    } else {
      String cmd;
      for (int i=1; i < commands.length; i++){
        cmd = commands[i];
        System.out.println(helpMsgs.get(cmd.toUpperCase()));
      }
    }
  }

  private void formatErr(String cmd){
    System.out.println("FORMAT_ERR: Bad format for command "+cmd+".");
    String[] helpType = {"HELP", cmd};
    help(helpType);
  }

  public Database getDatabase(){
    return database;
  }
	public void setDatabase(Database db){
		database = db;
	}
}
