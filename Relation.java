import java.util.*;
import java.lang.*;
import java.io.*;

public class Relation implements java.io.Serializable{

	private String name;
	private int temp;
	private LinkedList<Tuple> relation;
	
	public Relation(){
		name = "";
		temp = 0;
		relation = new LinkedList<Tuple>();
	}
	
	public Relation(String name, LinkedList<Tuple> relation){
		this.name = name;
		this.temp = 0;
		this.relation = relation;
	}

	public Relation(String name, LinkedList<Tuple> relation, int temp) {
		this.name = name;
		this.temp = temp;
		this.relation = relation;
	}

	public void print(){
		for (int i=0; i<name.length()+1; i++){
			System.out.print("_");
		}
		System.out.println();

		System.out.println(name+":");

		for (Attribute a : relation.getFirst().getTuple()){
			System.out.printf("%-"+a.getColumnWidth()+"s", a.getName());
		}
		System.out.println();

		if (relation.size() > 1){
			for (int i=1; i < relation.size(); i++){
				relation.get(i).print();
			}
		}
		System.out.println();
	}

	//gets/sets
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public LinkedList<Tuple> getRelation(){
		return relation;
	}

	public int getTemp(){
		return temp;
	}

	public void setTemp(int t){
		temp = t;
	}

	public void setRelation(LinkedList<Tuple> r){
		this.relation = r;
	}
}
