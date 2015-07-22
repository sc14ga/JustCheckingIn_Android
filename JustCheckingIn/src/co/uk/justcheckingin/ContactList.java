package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.util.Log;

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
        return this.name;
    }
    
    public String toXML() {
    	String output = this.name;
    	for(Contact c : list){
    		output += "<Contact>"+c.toString();
    	}
        return output;
    }
    
    public ContactList fromString(String input){
    	ContactList cl = new ContactList();
    	
    	String[] tokens = input.split("<Contact>");
    	String token = tokens[0];

    	cl.name = token;
    	
    	for(int i=1; i<tokens.length; i++){
    		token = tokens[i];
    	    Contact contact = new Contact();
    	    cl.list.add(contact.fromString(tokens[i]));
    	}
    	
    	return cl;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public List<Contact> getList(){
    	return this.list;
    }
}
