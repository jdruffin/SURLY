import java.util.*;
import java.lang.*;
import java.io.*;

	public class Relation{

	String name;
	LinkedList<Tuple> relation = new LinkedList<Tuple>();

	//constructor for player object
	public Relation(String name, LinkedList<Tuple> Relation) {
		this.name = name;
		this.relation = Relation;
	}

}
