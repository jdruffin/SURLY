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

    if (findRelation(rName) == null){
      Relation relation = new Relation(rName, tupleList);
      database.add(relation);
    } else {
      System.out.println("RELATION_ERR: Relation already exists ("+rName+")."); // will be changed in v.2 to DESTROY old relation
			return;
    }
  }

  public void insertTuple(String rName, String[] values){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();
		
    for (int i = 0; i < database.size(); i++) {
			Relation relation = database.get(i);
      if (relation.getName().equals(rName)){
				LinkedList<Attribute> refTuple = relation.getRelation().getFirst().getTuple();
				if (values.length != refTuple.size()){
					System.out.println("INSERT_ERR: Mismatched number of attributes ("+relation.getName()+").");
					return;
				}
        for (int j = 0; j < refTuple.size(); j++){
					Attribute refAttribute = refTuple.get(j);
					String name = refAttribute.getName();
					String type = refAttribute.getType();
					int length = refAttribute.getLength();

          Attribute newEntry = new Attribute(name, type, length, values[j]);
          if (newEntry.fitToConstraints()){
          	attributeList.add(newEntry);
					} else {
						System.out.println("INSERT_ERR: Entry '"+values[j]+"' in '"+relation.getName()+"' has invalid format for field '"+name+"'.");
						return;
					}
        }
        Tuple newTuple = new Tuple(attributeList);
        relation.getRelation().add(newTuple);
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
        for (int j = 0; j < database.size(); j++){
          Relation relation = database.get(j);
          if (relation.getName().equals(rNames[i])){
            exists = true;
            relation.print();
          }
        }
        if (!exists){
          System.out.println("PRINT_ERR: Unable to find relation ("+rNames[i]+").");
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
