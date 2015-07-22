package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.util.Log;

public class Event {
	private String name;
	private List<Timer> list = new ArrayList<Timer>();
	
	public Event(){
        super();
    }
    
    public Event(String name, List<Timer> list) {
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
    	for(Timer t : this.list){
    		output += "<Timer>"+t.toString();
    	}
        return output;
    }
    
    public Event fromString(String input){
    	Event event = new Event();
    	
    	String[] tokens = input.split("<Timer>");
    	String token = tokens[0];
    	
    	event.name = token;
    	
    	for(int i=1; i<tokens.length; i++){
    	    Timer timer = new Timer();
    	    event.list.add(timer.fromString(tokens[i]));
    	}
    	
    	return event;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public List<Timer> getList(){
    	return this.list;
    }
}
