package co.uk.justcheckingin;

import java.util.StringTokenizer;

import android.util.Log;

public class Timer {
	ContactList list;
	int hour, minute;
	
	public Timer(){
        super();
    }
    
    public Timer(ContactList cList, int hour, int minute) {
        super();
        this.list = cList;
        this.hour = hour;
        this.minute = minute;
    }
    
    @Override
    public String toString() {
    	String s = this.list.toXML();
    	return s+"<int>"+this.hour+"<int>"+this.minute;
    }
    
    public Timer fromString(String input){
    	Timer timer = new Timer();
    	
    	String[] tokens = input.split("<int>");
    	//StringTokenizer tokens = new StringTokenizer(input, "<int>");
    	String token = tokens[0];
    	//Log.d("DEBUG_TIMER_AFTER_int", token);
    	/*while(token.isEmpty()){
    		token = tokens.nextToken();
    	}*/
    	
    	ContactList cl = new ContactList();
    	Log.d("DEBUG_CONTACTLIST", token);
    	this.list = cl.fromString(token);
    	this.hour = Integer.parseInt(tokens[1]);
    	this.minute = Integer.parseInt(tokens[2]);
    	
    	Log.d("DEBUG_TIMER_INTS", this.hour+" "+this.minute);
    	
    	return timer;
    }
}
