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

    for(int i = 0; i < schema.length; i = i+3){
      Attribute attribute = new Attribute(schema[i], schema[i+1], Integer.parseInt(schema[i+2]), null);
      attributeList.add(attribute);
    }

    Tuple tuple = new Tuple(attributeList);
    tupleList.add(tuple);
    if (findRelation(rName) == null){
      Relation relation = new Relation(rName, tupleList);
      database.add(relation);
    } else {
      System.out.println("Unable to add relation '"+rName+"': Already exists"); // will be changed in v.2 to DESTROY old relation
    }
  }

  public boolean insertTuple(String rName, String[] values){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();

    for (int i = 0; i < database.size(); i++) {
			Relation relation = database.get(i);
      if (relation.getName().equals(rName)){
				LinkedList<Attribute> refTuple = relation.getRelation().getFirst().getTuple();

        for (int j = 0; j < refTuple.size(); j++){
					Attribute refAttribute = refTuple.get(j);
					String name = refAttribute.getName();
					String type = refAttribute.getType();
					int length = refAttribute.getLength();

          Attribute newEntry = new Attribute(name, type, length, values[j]);
          if (newEntry.fitToConstraints()){
          	attributeList.add(newEntry);
					} else {
						System.out.println("Unable to add tuple: Entry '"+values[j]+"' in '"+relation.getName()+"' is invalid for field '"+name+"'.");
						return false;
					}
        }
        Tuple newTuple = new Tuple(attributeList);
        relation.getRelation().add(newTuple);
        return true;
      }
    }
    System.out.println("Unable to find relation '"+rName+"'");
    return false;
  }

  public void print(String[] rNames){
    if (rNames.length == 0){
      for (Relation r : database){
        System.out.println(" "+r.getName());
      }
    } else {
      boolean exists = false;
      for (int i = 0; i < rNames.length; i++){
        for (int j = 0; j < database.size(); j++){
          Relation relation = database.get(j);
          if (relation.getName().equals(rNames[i])){
            exists = true;
            relation.print();
          }
        }
        if (!exists){
          System.out.println("Couldn't find relation '"+rNames[i]+"'");
        }
      }
    }
  }

  private Relation findRelation(String rName){
    for (int i=0; i < database.size(); i++){
      Relation match = database.get(i);
      if (match.getName().equals(rName)){
        return match;
      }
    }
    return null;
  }

}
