import java.util.*;
import java.lang.*;
import java.io.*;

public class Database implements java.io.Serializable{

  private LinkedList<Relation> database = null;

  public Database(){
    database = new LinkedList<Relation>();
  }

  public void addRelation(String rName, String[] schema){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();
		String name, type, length;

		if ((schema.length % 3) == 0){
    	for (int i = 0; i < schema.length; i = i+3){
				name = schema[i];
				type = schema[i+1];
				length = schema[i+2];

				if ((type.toUpperCase().equals("CHAR") || type.toUpperCase().equals("NUM")) && length.matches("\\d+")){
					Attribute attribute = new Attribute(name, type, Integer.parseInt(length), null);
      		attributeList.add(attribute);
				} else {
					System.out.println("RELATION_ERR: Invalid attribute domain ("+rName+").");
					return;
				}
    	}
		} else {
			System.out.println("RELATION_ERR: Wrong number of arguments in declaration ("+rName+").");
			return;
		}

    Tuple tuple = new Tuple(attributeList);
    tupleList.add(tuple);

    Relation relation = new Relation(rName, tupleList);
    destroy(rName);
    database.add(relation);
  }

  public void insertTuple(String rName, String[] values){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();

    for (Relation r : database){
      if (r.getName().equals(rName)){
        LinkedList<Attribute> refTuple = r.getRelation().getFirst().getTuple();
				if (values.length != refTuple.size()){
					System.out.println("INSERT_ERR: Mismatched number of attributes ("+r.getName()+").");
					return;
				}
        int enumerate = 0;
        for (Attribute a : refTuple){
          String name = a.getName();
					String type = a.getType();
					int length = a.getLength();
          Attribute newEntry = new Attribute(name, type, length, values[enumerate]);
          if (newEntry.fitToConstraints()){
          	attributeList.add(newEntry);
					} else {
            System.out.println("INSERT_ERR: Entry '"+values[enumerate]+"' in '"+r.getName()+"' has invalid format for field '"+name+"'.");
						return;
					}
          enumerate += 1;
        }
        Tuple newTuple = new Tuple(attributeList);
        r.getRelation().add(newTuple);
        return;
      }
    }
    System.out.println("INSERT_ERR: Unable to find relation ("+rName+").");
    return;
  }

  public void print(String[] rNames){
    if (rNames.length == 0){
			String schema = "";
      for (Relation r : database){
        schema = " "+r.getName()+" (";
				for (Attribute a : r.getRelation().getFirst().getTuple()){
						schema += a.getName()+", ";
				}
				schema = schema.substring(0, schema.length()-2)+");";
				System.out.println(schema);
      }
    } else {
      boolean exists = false;
      for (int i = 0; i < rNames.length; i++){
        exists = false;
        for (Relation r : database){
          if (r.getName().equals(rNames[i])){
            exists = true;
            r.print();
          }
        }
        if (!exists){
          System.out.println("PRINT_ERR: Unable to find relation ("+rNames[i]+").");
        }
      }
    }
  }

  public void destroy(String rName){
    Relation oldRelation = findRelation(rName);
    if (oldRelation != null){
      database.remove(oldRelation);
      System.out.println("Destroyed relation "+rName+".");
    }
  }

  public void deleteWhere(String rName, String[] condList){

  }

  public void selectWhere(String rName, String[] condList){

  }

  public void project(String rName, String[] attList){

  }

  public void join(String r1, String r2, String[] cond){

  }

  private Relation findRelation(String rName){
    for (Relation r : database){
      if (r.getName().equals(rName)){
        return r;
      }
    }
    //else
    return null;
  }

}
