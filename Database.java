import java.util.*;
import java.lang.*;
import java.io.*;

public class Database implements java.io.Serializable{

  private LinkedList<Relation> database = null;

  // constructor
  public Database(){
    database = new LinkedList<Relation>();
  }

  // adds a realtion to the database
  public void addRelation(String rName, String[] schema){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();
		String name, type, length;

		if ((schema.length % 3) == 0){
    	for (int i = 0; i < schema.length; i = i+3){
				name = schema[i];
				type = schema[i+1];
				length = schema[i+2];

				if ((type.equalsIgnoreCase("CHAR") || type.equalsIgnoreCase("NUM")) && length.matches("\\d+")){
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

  // inserts a tuple into an existing relation
  public void insertTuple(String rName, String[] values){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();

    Relation insertRel = findRelation(rName);
    if (insertRel == null){
      System.out.println("INSERT_ERR: Unable to find relation ("+rName+").");
    } else if (insertRel.getTemp() == 1){
      System.out.println("INSERT_ERR: Unable to insert into a temporary relation.");
    } else{
      LinkedList<Attribute> refTuple = insertRel.getRelation().getFirst().getTuple();
			if (values.length != refTuple.size()){
				System.out.println("INSERT_ERR: Mismatched number of attributes ("+insertRel.getName()+").");
			} else{
        int enumerate = 0;
        for (Attribute a : refTuple){
          String name = a.getName();
  				String type = a.getType();
  				int length = a.getLength();
          Attribute newEntry = new Attribute(name, type, length, values[enumerate]);
          if (newEntry.fitToConstraints()){
          	attributeList.add(newEntry);
  				} else{
            System.out.println("INSERT_ERR: Entry '"+values[enumerate]+"' in '"+insertRel.getName()+"' has invalid format for field '"+name+"'.");
  					return;
  				}
          enumerate++;
        }
        Tuple newTuple = new Tuple(attributeList);
        insertRel.getRelation().add(newTuple);
      }
    }
  }

  // prints all relations specified in rNames,
  // or prints database schema if none are specified.
  public void print(String[] rNames){
    if (rNames.length == 1){
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
      for (int i = 1; i < rNames.length; i++){
        exists = false;
        Relation r = findRelation(rNames[i]);
        if (r == null){
          System.out.println("PRINT_ERR: Unable to find relation ("+rNames[i]+").");
        } else{
          exists = true;
          r.print();
        }
      }
    }
  }

  // removes a relation from the database
  public void destroy(String rName){
    Relation oldRelation = findRelation(rName);
    if (oldRelation != null){
      database.remove(oldRelation);
      System.out.println("Destroyed relation "+rName+".");
    }
  }

	// needs error handling
  // deletes all tuples in the specified relation that meet the conditions,
  // or deletes all tuples if no conditions are specified
  public void deleteWhere(String rName, String[] condList){
		Relation deleteRel = findRelation(rName);
    if (deleteRel == null){
      System.out.println("DELETE_ERR: Unable to find relation "+rName);
    } else if (deleteRel.getTemp() == 1){
      System.out.println("DELETE_ERR: Unable to delete from a temporary relation.");
    } else{
  		LinkedList<Tuple> newRel = new LinkedList<Tuple>();
  		LinkedList<Tuple> oldRel = deleteRel.getRelation();
  		if (condList.length == 0){
  			Tuple base = oldRel.remove();
  			newRel.addFirst(base);
  		} else{
  			for (Tuple t : oldRel){
          for (Attribute a : t.getTuple()){
            System.out.println(a.getValue());
          }
    		  if (!conditionParser.evaluate(t, condList)){
    					newRel.add(t);
    			}
  			}
  		}
  		deleteRel.setRelation(newRel);
    }
  }

	// needs error handling
  // creates a temporary relation called tName with tuples from
  // the specified relation that meet the conditions,
  // or all tuples if no conditions are specified.
  public void selectWhere(String rName, String[] condList, String tName){
		Relation selectRel = findRelation(rName);
    if (deleteRel == null){
      System.out.println("DELETE_ERR: Unable to find relation "+rName);
    } else{
  		LinkedList<Tuple> newRel = new LinkedList<Tuple>();
  		LinkedList<Tuple> oldRel = selectRel.getRelation();
  		if (condList.length == 0){
  			newRel = oldRel;
  		} else{
  			for (Tuple t : oldRel){
  				if (conditionParser.evaluate(t, condList)){
  					newRel.add(t);
  				}
  			}
  			Tuple base = oldRel.getFirst();
  			newRel.addFirst(base);
  		}
  		Relation tmpRel = new Relation(tName, newRel, 1);
  		destroy(tName);
  		database.add(tmpRel);
    }
  }

	// needs to be checked for duplicates, error handling.
  // creates a temporary relation called tName with only the
  // specified attributes from the specified relation.
  public void project(String rName, String[] attList, String tName){
		LinkedList<Tuple> projectRel = findRelation(rName).getRelation();
		if (projectRel == null){
      System.out.println("PROJECT_ERR: Unable to find relation "+rName);
    } else{
			Tuple baseTuple = projectRel.getFirst();
			LinkedList<Attribute> tmpAtt = new LinkedList<Attribute>();
			LinkedList<Integer> indices = new LinkedList<Integer>();

			for (int i=0; i < attList.length; i++){
				int enumerate = 0;
				for (Attribute a : baseTuple.getTuple()){
					if (a.getName().equalsIgnoreCase(attList[i])){
						tmpAtt.add(a);
						indices.add(enumerate);
						break;
					}
					enumerate++;
				}
			}
			LinkedList<Tuple> tmpTup = new LinkedList<Tuple>();
			tmpTup.add(new Tuple(tmpAtt));

			for (Tuple t : projectRel){
				if (!t.equals(projectRel.getFirst())){
					tmpAtt = new LinkedList<Attribute>();
					LinkedList<Attribute> att = t.getTuple();
					for (int i : indices){
						tmpAtt.add(att.get(i));
					}
					Tuple projectTup = new Tuple(tmpAtt);
					tmpTup.add(projectTup);
				}
			}

			Relation tmpRel = new Relation(tName, tmpTup, 1);
			destroy(tName);
			database.add(tmpRel);
		}
  }

	// only takes one equivalence type condition:
	// expand to use conditionParser and to allow
	// for unconditional joins (cartesian product).
  // creates a new table with tuples that are joined from
  // relation1 and relation2 if they meet the join condition.
  public void join(String rName1, String rName2, String[] cond, String tName){
    Relation relation1 = findRelation();
    Relation relation2 = findRelation();
    if (rel1 == null || rel2 == null){
      System.out.println("JOIN_ERR: Unable to find relation (JOIN "+rName1+", "+rName2+")");
    } else{
  		LinkedList<Tuple> rel1 = relation1.getFirst();
  		LinkedList<Tuple> rel2 = relation2.getFirst();
  		String op1 = cond[0];
  		String op2 = cond[2];
      int index1 = getAttributeIndex(op1, rel1);
      int index2 = getAttributeIndex(op2, rel2);
      if (index1 == -1 || index2 == -1){
        System.out.println("JOIN_ERR: Unable to find attribute ("+op1+" in "+rName1+", "+op2+" in "+rName2+")");
      } else{
    		LinkedList<Attribute> tmpAtt = new LinkedList<Attribute>();
    		LinkedList<Tuple> newRel = new LinkedList<Tuple>();
    		tmpAtt.addAll(rel1.getFirst().getTuple());
    		tmpAtt.addAll(rel2.getFirst().getTuple());
    		Tuple base = new Tuple(tmpAtt);
    		newRel.add(base);
    		for (Tuple t : rel1){
    			for (Tuple r : rel2){
    				String data1 = t.getTuple().get(index1).getValue();
    				String data2 = r.getTuple().get(index2).getValue();
    				if (data1 != null && data2 != null && data1.equalsIgnoreCase(data2)){
    					tmpAtt = new LinkedList<Attribute>();
    					tmpAtt.addAll(t.getTuple());
    					tmpAtt.addAll(r.getTuple());
    					Tuple tmpTup = new Tuple(tmpAtt);
    					newRel.add(tmpTup);
    				}
    			}
    		}
    		Relation tmpRel = new Relation(tName, newRel, 1);
    		destroy(tName);
    		database.add(tmpRel);
      }
    }
  }

  private int getAttributeIndex(String attName, LinkedList<Tuple> rel){
    LinkedList<Attribute> tuple = rel.getFirst().getTuple();
    int index = -1;
    int enumerate = 0;
    for (Attribute a : tuple){
      if (a.getName().equalsIgnoreCase(attName)){
        index = enumerate;
        break;
      }
      enumerate++;
    }
    return index;
  }
	// returns the relation whose name matches rName
  private Relation findRelation(String rName){
    for (Relation r : database){
      if (r.getName().equalsIgnoreCase(rName)){
        return r;
      }
    }
    //else
    return null;
  }

}
