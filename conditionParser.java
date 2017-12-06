import java.util.*;
import java.util.regex.*;
import java.lang.*;

public class conditionParser{

  public static ArrayList<Boolean> createBools(Tuple tuple, String[] conditions){
    ArrayList<Boolean> bools = new ArrayList<Boolean>();
    boolean FALSE = new Boolean(false);
    boolean TRUE = new Boolean(true);
    //System.out.println("in create bools");
    // for(int i = 0; i < conditions.length; i ++){
     //  System.out.println(conditions[i]);
    // }
    for(int i = 1; i < conditions.length; i = i + 4){
      for(Attribute a : tuple.getTuple()){
      //    System.out.println(a.getValue() + i);
        if (a.getValue() != null){
        //  System.out.println(a.getName());
        //  System.out.println(conditions[i]);

          if(a.getName().equals(conditions[i])){
            if(conditions[i+1].equals("=")){
           //   System.out.println(a.getValue());
            //  System.out.println(conditions[i+2]);

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
    //System.out.println("in create operators");
    // for(int i = 0; i < conditions.length; i ++){
    //   System.out.println(conditions[i]);
    // }
    for(int i = 4; i < conditions.length; i=i+4){
      operators.add(conditions[i]);
      //System.out.println(conditions[i]);
    }
    return operators;
  }

  public static boolean helper(ArrayList<String> operators, ArrayList<Boolean> bools){
    //System.out.println(operators.size());
    //System.out.println(bools.size());
    if(bools.size() == 0){
      return false;
    }
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

  public static boolean evaluate(Tuple tuple, String[] conditions){
    ArrayList<String> operators = createOperators(conditions);
    ArrayList<Boolean> bools    = createBools(tuple, conditions);

	   for(String s: operators){
       System.out.println(s);
     }

     for(boolean s: bools){
       System.out.println(s);
     }
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
