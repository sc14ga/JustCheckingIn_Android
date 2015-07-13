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
        return this.name;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public List<Contact> getList(){
    	return this.list;
    }
}
