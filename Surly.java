import java.util.*;
import java.lang.*;
import java.io.*;

public class Surly{

  public static LinkedList<Relation> relationList = new LinkedList<Relation>();

  public static void main(String args[]){
    try{
      Scanner scanner = new Scanner(new File("test.txt"));
      while (scanner.hasNextLine()){

          String[] parts = scanner.nextLine().split(" ");
          for(int i = 1; i < parts.length;i++){
            parts[i] = parts[i].replaceAll("\\(", "");
            parts[i] = parts[i].replaceAll("\\,", "");
            parts[i] = parts[i].replaceAll("\\)", "");
            parts[i] = parts[i].replaceAll("\\;", "");
          }

          if(parts[0].equals("RELATION")){

            LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
            LinkedList<Tuple> tupleList         = new LinkedList<Tuple>();

            for(int i = 2; i < parts.length; i = i+3){
              Attribute attribute = new Attribute(parts[i]/*,parts[i+1],parts[i+2], NULL*/);
              attributeList.add(attribute);
            }

            Tuple tuple = new Tuple(attributeList);
            tupleList.add(tuple);

            Relation relation = new Relation(parts[1], tupleList);
            relationList.add(relation);

          }

          if(parts[0].equals("INSERT")){

            LinkedList<Attribute> attributeList = new LinkedList<Attribute>();
            LinkedList<Tuple> tupleList         = new LinkedList<Tuple>();

            for (int i = 0; i < relationList.size(); i++) {
              if(relationList.get(i).name.equals(parts[1])){
                for(int j = 2; j < parts.length; j++){
                  Attribute attributeInsert = new Attribute(parts[j]/*,parts[j+1],parts[j+2], NULL*/);
                  //Attribute.checkValid();
                  attributeList.add(attributeInsert);
                }
                //ISSUE WITH '' IN TEST.TXT
                Tuple tupleInsert = new Tuple(attributeList);
                relationList.get(i).relation.add(tupleInsert);

              }

		        }

          }

          if(parts[0].equals("PRINT")){
            for (int i = 0; i < relationList.size(); i++){
              for(int j = 1; j < parts.length; j++){
                if(relationList.get(i).name.equals(parts[j])){
                  System.out.print(relationList.get(i).name.toString()+"\n");
                }

              }

           }

          }

      }

        scanner.close();
    } catch(FileNotFoundException e){
      System.out.print("No file found");
    }

    /*take commands from cmd prompt
    while(true){
      system.in
    }*/

 }

}
