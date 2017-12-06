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
        if(a.getName() == conditions[i]){
          if(conditions[i+1].equals("=")){
            if(a.getValue() == conditions[i+2]){
              bools.add(TRUE);
            }else{
              bools.add(FALSE);
            }
          }
          if(conditions[i+1].equals("!=")){
            if(a.getValue() != conditions[i+2]){
              bools.add(TRUE);
            }else{
              bools.add(FALSE);
            }
          }
          if(conditions[i+1].equals(">")){
            if(Double.parseDouble(a.getValue()) > Double.parseDouble(conditions[i+2])){
              bools.add(TRUE);
            }else{
              bools.add(FALSE);
            }
          }
          if(conditions[i+1].equals("<")){
            if(Double.parseDouble(a.getValue()) < Double.parseDouble(conditions[i+2])){
              bools.add(TRUE);
            }else{
              bools.add(FALSE);
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
    //for(String s : operators){
	     //System.out.print(s);
    //}
    for(int i = 0; i < operators.size(); i++){
      if(operators.get(i).equals("and")){
        boolean result = bools.get(i) && bools.get(i+1);
        bools.remove(i+1);
        bools.set(i, result);
        operators.remove(i);
        return helper(operators, bools);
      }
    }
    for(int i = 0; i < bools.size(); i++){
      if(bools.get(i) == true){
        return true;
      }
    }

  return false;
  }

  public static boolean evalate(Tuple tuple, String[] conditions){
    ArrayList<String> operators = createOperators(conditions);
    ArrayList<Boolean> bools    = createBools(tuple, conditions);
	  System.out.print("asfasdfasd");
    boolean result = helper(operators, bools);

    return result;

  }

//                     F



//                    or
//           T                       F



//                     or                    and
//           T                      T                 F





//                       or                 and           and
//            T                   T               T              F

  //WHERE CNBR = CSCI241 or CNBR = CSCI145 and PNBR != CSCI141;


}
