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

  public void insertTuple(String rName, String[] values){
    LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
    LinkedList<Tuple>     tupleList     = new LinkedList<Tuple>();

    Relation insertRel = findRelation(rName);
    if (insertRel != null){
      LinkedList<Attribute> refTuple = insertRel.getRelation().getFirst().getTuple();
			if (values.length != refTuple.size()){
				System.out.println("INSERT_ERR: Mismatched number of attributes ("+insertRel.getName()+").");
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
          System.out.println("INSERT_ERR: Entry '"+values[enumerate]+"' in '"+insertRel.getName()+"' has invalid format for field '"+name+"'.");
					return;
				}
        enumerate++;
      }
      Tuple newTuple = new Tuple(attributeList);
      insertRel.getRelation().add(newTuple);
      return;
    } else {
      System.out.println("INSERT_ERR: Unable to find relation ("+rName+").");
      return;
    }
  }

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
        for (Relation r : database){
          if (r.getName().equalsIgnoreCase(rNames[i])){
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

	// needs error handling
  public void deleteWhere(String rName, String[] condList){
		Relation deleteRel = findRelation(rName);
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

	// needs error handling
  public void selectWhere(String rName, String[] condList, String tName){
		Relation selectRel = findRelation(rName);
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
		Relation tmpRel = new Relation(tName, newRel);
		destroy(tName);
		database.add(tmpRel);
  }

	// needs to be checked for duplicates, error handling
  public void project(String rName, String[] attList, String tName){
		LinkedList<Tuple> projectRel = findRelation(rName).getRelation();
		if (projectRel != null){
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

			Relation tmpRel = new Relation(tName, tmpTup);
			destroy(tName);
			database.add(tmpRel);
		}
  }

	// only takes one equivalence type condition:
	// expand to use conditionParser and to allow
	// for unconditional joins (cartesian product).
  public void join(String rName1, String rName2, String[] cond, String tName){
		LinkedList<Tuple> rel1 = null;
		LinkedList<Tuple> rel2 = null;
		String op1 = cond[0];
		String op2 = cond[2];
		for (Relation r : database){
			if (r.getName().equalsIgnoreCase(rName1)){
				rel1 = r.getRelation();
			}
			if (r.getName().equalsIgnoreCase(rName2)){
				rel2 = r.getRelation();
			}
		}
		LinkedList<Attribute> tup1 = rel1.getFirst().getTuple();
		LinkedList<Attribute> tup2 = rel2.getFirst().getTuple();
		int[] indices = new int[2];
		int enumerate = 0;
		for (Attribute a : tup1){
			if (a.getName().equalsIgnoreCase(op1)){
				indices[0] = enumerate;
				break;
			}
			enumerate++;
		}
		enumerate = 0;
		for (Attribute a : tup2){
			if (a.getName().equalsIgnoreCase(op2)){
				indices[1] = enumerate;
				break;
			}
			enumerate++;
		}
		LinkedList<Attribute> tmpAtt = new LinkedList<Attribute>();
		LinkedList<Tuple> newRel = new LinkedList<Tuple>();
		tmpAtt.addAll(rel1.getFirst().getTuple());
		tmpAtt.addAll(rel2.getFirst().getTuple());
		Tuple base = new Tuple(tmpAtt);
		newRel.add(base);
		for (Tuple t : rel1){
			for (Tuple r : rel2){
				String data1 = t.getTuple().get(indices[0]).getValue();
				String data2 = r.getTuple().get(indices[1]).getValue();
				if (data1 != null && data2 != null && data1.equalsIgnoreCase(data2)){
					tmpAtt = new LinkedList<Attribute>();
					tmpAtt.addAll(t.getTuple());
					tmpAtt.addAll(r.getTuple());
					Tuple tmpTup = new Tuple(tmpAtt);
					newRel.add(tmpTup);
				}
			}
		}
		Relation tmpRel = new Relation(tName, newRel);
		destroy(tName);
		database.add(tmpRel);
  }

	// helper function
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
