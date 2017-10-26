import java.util.*;
import java.lang.*;
import java.io.*;

public class Tuple{

	private LinkedList<Attribute> tuple = new LinkedList<Attribute>();

	//constructor for player object
	public Tuple(LinkedList<Attribute> tuple){
		this.tuple = tuple;
	}

	public void print(){
		for (Attribute a : tuple){
			a.print();
			System.out.print(' ');
		}
		System.out.println();
	}

	public LinkedList<Attribute> getTuple(){
		return tuple;
	}

}
