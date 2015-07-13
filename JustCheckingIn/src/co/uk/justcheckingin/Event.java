package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;

public class Event {
	private String name;
	private List<ContactList> list = new ArrayList<ContactList>();
	private List<Integer> hour = new ArrayList<Integer>();
	private List<Integer> minute = new ArrayList<Integer>();
	
	public Event(){
        super();
    }
    
    public Event(String name, List<ContactList> list) {
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
    
    public List<ContactList> getList(){
    	return this.list;
    }
}
