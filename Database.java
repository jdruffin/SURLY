import java.util.*;
import java.lang.*;
import java.io.*;

public class Database{

  private LinkedList<Relation> database = null;

  public Database(){
    database = new LinkedList<Relation>();
  }

	public Database(LinkedList<Relation> db){
		database = db;
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

    Relation relation = new Relation(rName, tupleList);
    database.add(relation);
  }

  public void insertTuple(String rName, String[] values){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();
		
		boolean goodTuple = true;
    for (int i = 0; i < database.size(); i++) {
			Relation relation = database.get(i);
      if(relation.getName().equals(rName)){
				LinkedList<Attribute> refTuple = relation.getRelation().getFirst().getTuple();
        for(int j = 0; j < refTuple.size(); j++){
					Attribute refAttribute = refTuple.get(j);
					String name = refAttribute.getName();
					String type = refAttribute.getType();
					int length = refAttribute.getLength();

          Attribute newEntry = new Attribute(name, type, length, values[j]);
          if(newEntry.fitToConstraints()){
          	attributeList.add(newEntry);
					} else {
						System.out.println("Unable to add tuple: Entry '"+values[j]+"' in '"+relation.getName()+"' is invalid.");
						goodTuple = false;
						break;
					}
        }
        //ISSUE WITH '' IN TEST.TXT
				if (goodTuple){
        	Tuple newTuple = new Tuple(attributeList);
        	relation.getRelation().add(newTuple);
				}
      }
    }
  }
  
  public void print(String[] rNames){
    for (int i = 0; i < database.size(); i++){
			System.out.println();
      for(int j = 0; j < rNames.length; j++){
        Relation relation = database.get(i);
        if(relation.getName().equals(rNames[j])){
          relation.print();
        }
      }
    }
  }

}
