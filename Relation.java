import java.util.*;
import java.lang.*;
import java.io.*;

public class Relation{

	private String name;
	private LinkedList<Tuple> relation = new LinkedList<Tuple>();

	//constructor
	public Relation(String name, LinkedList<Tuple> Relation) {
		this.name = name;
		this.relation = Relation;
	}

	public void print(){
		System.out.println(name);
		for (Attribute a : relation.getFirst().getTuple()){
			System.out.print(a.getName()+' ');
		}
		System.out.println();
		for (Tuple t : relation){
			t.print();
		}
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
}
