import java.util.*;
import java.lang.*;
import java.io.*;

public class Attribute{

	private String name=null;
	private String type=null;
	private int length=0;
	private String value=null;


	//constructor
	public Attribute(String name, String type, int length, String value){
		this.name = name;
		this.type = type;
		this.length = length;
		this.value = value;
	}

	public boolean fitToConstraints(){
    if (type.equals("NUM")){
      if (!value.matches("[0-9]+")){
        System.out.println("Invalid entry: '" + name + "' field requires only numeric values.");
        return false;
			}
		}
    if (value.length() > length){
      value = value.substring(0, length);
    }
		return true;
	}

	public void print(){
		System.out.print(value);
	}

	//gets/sets-------------------------------------------------------------------

	public String getName(){
		return name;
	}

	public String getType(){
		return type;
	}

	public int getLength(){
		return length;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}
}
