package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;

public class ContactList {
	private String name;
	private List<Contact> list = new ArrayList<Contact>();
	
	public ContactList(){
        super();
    }
    
    public ContactList(String name, List<Contact> list) {
        super();
        this.name = name;
        this.list = list;
    }

    @Override
    public String toString() {
    	String output = this.name+",";
    	for (int i = 0; i < list.size()-1; i++) {
            output+=list.get(i).toString()+",";
        }
    	output+=list.get(list.size()-1).toString();
        return output;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public List<Contact> getList(){
    	return this.list;
    }
}
