import java.util.*;
import java.lang.*;
import java.io.*;

public class Attribute implements java.io.Serializable{

	private String name=null;
	private String type=null;
	private int length=0;
	private int columnWidth=0;
	private String value=null;

	//constructor
	public Attribute(String name, String type, int length, String value){
		this.name = name;
		this.type = type;
		this.length = length;
		this.value = value;
		if (name.length() <= length){
			this.columnWidth = length+1;
		} else {
			this.columnWidth = name.length()+1;
		}
	}

	public boolean fitToConstraints(){
    if (type.toUpperCase().equals("NUM")){
      if (!value.matches("[0-9]+")){
        System.out.println("Invalid entry: '" + name + "' field requires only numeric values.");
        return false;
			}
			if (value.length() > length){
				value = value.substring(value.length()-length, value.length());
			}
		} else{
    	if (value.length() > length){
      value = value.substring(0, length);
			}
    }
		return true;
	}

	public void print(){
		System.out.printf("%-"+columnWidth+"s", value);
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

	public int getColumnWidth(){
		return columnWidth;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}
}
