import java.util.*;
import java.util.regex.*;
import java.lang.*;

public class conditionParser{

  public static ArrayList<Boolean> createBools(Tuple tuple, String[] conditions){
    ArrayList<Boolean> bools = new ArrayList<Boolean>();
    boolean FALSE = new Boolean(false);
    boolean TRUE = new Boolean(true);

    for(int i = 1; i < conditions.length; i = i + 4){
      for(Attribute a : tuple.getTuple()){
        if (a.getValue() != null){
          if(a.getName().equals(conditions[i])){
            if(conditions[i+1].equals("=")){
              if(a.getValue().equals(conditions[i+2])){
                bools.add(TRUE);
              }else{
                bools.add(FALSE);
              }
            }
            else if(conditions[i+1].equals("!=")){
              if(!a.getValue().equals(conditions[i+2])){
                bools.add(TRUE);
              }else{
                bools.add(FALSE);
              }
            }
            else if(conditions[i+1].equals(">")){
              if(Double.parseDouble(a.getValue()) > Double.parseDouble(conditions[i+2])){
                bools.add(TRUE);
              }else{
                bools.add(FALSE);
              }
            }
            else if(conditions[i+1].equals("<")){
              if(Double.parseDouble(a.getValue()) < Double.parseDouble(conditions[i+2])){
                bools.add(TRUE);
              }else{
                bools.add(FALSE);
              }
            }
            else if(conditions[i+1].equals(">=")){
              if(Double.parseDouble(a.getValue()) >= Double.parseDouble(conditions[i+2])){
                bools.add(TRUE);
              }else{
                bools.add(FALSE);
              }
            }
            else if(conditions[i+1].equals("<=")){
              if(Double.parseDouble(a.getValue()) <= Double.parseDouble(conditions[i+2])){
                bools.add(TRUE);
              }else{
                bools.add(FALSE);
              }
            }
            else{
              System.out.println("incorrect operator usage.");
            }
          }
        }
      }
      }
      return bools;
    }


  public static ArrayList<String> createOperators(String[] conditions){
    ArrayList<String> operators = new ArrayList<String>();
    for(int i = 4; i < conditions.length; i=i+4){
      operators.add(conditions[i]);
    }
    return operators;
  }

  public static boolean helper(ArrayList<String> operators, ArrayList<Boolean> bools){
    if(bools.size() > 0){
      int enumerate = 0;
      for(String op : operators){
        if(op.equals("and")){
          boolean result = bools.get(enumerate) && bools.get(enumerate+1);
          bools.remove(enumerate+1);
          bools.set(enumerate, result);
          operators.remove(enumerate);
          return helper(operators, bools);
        }
        enumerate ++;
      }
      for(Boolean b : bools){
        if (b){
          return true;
        }
      }
    }
    return false;
  }

  public static boolean evaluate(Tuple tuple, String[] conditions){
      ArrayList<String> operators = createOperators(conditions);
      ArrayList<Boolean> bools    = createBools(tuple, conditions);
      boolean result = helper(operators, bools);
      return result;
  }
}
