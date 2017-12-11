import java.util.*;
import java.lang.*;
import java.io.*;

public class Attribute implements java.io.Serializable{

	private String name;
	private String type;
	private int length;
	private int columnWidth;
	private String value;

	public Attribute(){	
		this.name = null;
		this.type = null;
		this.length = 0;
		this.value = null;
		this.columnWidth = 0;
	}

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
		} else {
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
	
	public void setName(String n){
		name = n;
	}

	public String getType(){
		return type;
	}
	
	public void setType(String t){
		type = t;
	}

	public int getLength(){
		return length;
	}
	
	public void setLength(int l){
		length = l;
	}

	public int getColumnWidth(){
		return columnWidth;
	}

	public void setColumntWidth(int c){
		columnWidth = c;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		value = value;
	}
}
