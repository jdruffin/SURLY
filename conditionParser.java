import java.util.*;
import java.lang.*;

public class conditionParser{

  public static ArrayList<Boolean> createOperands(Tuple tuple, String[] conditions){
    ArrayList<Boolean> operands = new ArrayList<Boolean>();
    boolean FALSE = new Boolean(false);
    boolean TRUE = new Boolean(true);
		if (conditions.length >= 4 && conditions.length % 2 == 0){
			for (int i = 1; i < conditions.length; i = i + 4){
				for (Attribute a : tuple.getTuple()){
					String operand1 = conditions[i];
					String operator = conditions[i+1];
					String operand2 = conditions[i+2];
					String value = a.getValue();
					if (a.getName().equals(operand1) && value != null){
						switch (operator){
							case "=":
								if (value.equals(operand2)){
									operands.add(TRUE);
								} else {
									operands.add(FALSE);
								} break;
							case "!=":
								if (!value.equals(operand2)){
									operands.add(TRUE);
								} else {
									operands.add(FALSE);
								} break;
							case ">":
								if (value.compareTo(operand2) > 0){
									operands.add(TRUE);
								} else {
									operands.add(FALSE);
								} break;
							case "<":
								if (value.compareTo(operand2) < 0){
									operands.add(TRUE);
								} else {
									operands.add(FALSE);
								} break;
							case ">=":
								if (value.compareTo(operand2) >= 0){
									operands.add(TRUE);
								} else {
									operands.add(FALSE);
								} break;
							case "<=":
								if (value.compareTo(operand2) <= 0){
									operands.add(TRUE);
								} else {
									operands.add(FALSE);
								} break;
							default:
								System.out.println("CONDITION_ERR: Invalid operator usage.");
								break;
						}
					}
				}
			}
		} else {
			System.out.println("CONDITION_ERR: Invalid condition in command.");
			operands = null;
		}
    return operands;
  }

  public static ArrayList<String> createOperators(String[] conditions){
    ArrayList<String> operators = new ArrayList<String>();
    for(int i = 4; i < conditions.length; i=i+4){
			String operator = conditions[i];
			if (operator.equalsIgnoreCase("AND") || operator.equalsIgnoreCase("OR")){
      	operators.add(conditions[i]);
			} else {
				operators = null;
				break;
			}
    }
    return operators;
  }

  public static boolean helper(ArrayList<String> operators, ArrayList<Boolean> operands){
    if (operands.size() > 0){
      int index = 0;
      for(String op : operators){
        if(op.equalsIgnoreCase("AND")){
          boolean result = operands.get(index) && operands.get(index+1);
          operands.remove(index+1);
          operands.set(index, result);
          operators.remove(index);
          return helper(operators, operands);
        }
        index++;
      }
      for(Boolean b : operands){
        if (b){
          return true;
        }
      }
    }
    return false;
  }

  public static Boolean evaluate(Tuple tuple, String[] conditions){
    ArrayList<String> operators = createOperators(conditions);
    ArrayList<Boolean> operands = createOperands(tuple, conditions);
		Boolean result;
		if (operators != null && operands != null){
      result = helper(operators, operands);
		} else {
			result = null;
		}
    return result;
  }
}
