package co.uk.justcheckingin;

import java.util.StringTokenizer;

import android.util.Log;

public class Contact {
	private String name;
	private String number;
	
	public Contact(){
        super();
    }
    
    public Contact(String name, String number) {
        super();
        this.name = name;
        this.number = number;
    }

    @Override
    public String toString() {
        return this.name + "<:>" + this.number;
    }
    
    public Contact fromString(String input){
    	Contact contact = new Contact();
    	
    	String[] tokens = input.split("<:>");
    	//StringTokenizer tokens = new StringTokenizer(input, "<:>");
    	String token = tokens[0];
    	//Log.d("DEBUG_CONTACT_NAME_BEFORE", token);
    	/*while(token.isEmpty()){
    		token = tokens.nextToken();
    	}*/
    	contact.name = token;
    	Log.d("DEBUG_CONTACT_NAME", token);
    	contact.number = tokens[1];
    	Log.d("DEBUG_CONTACT_NUMBER", contact.number);
    	
    	return contact;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getNumber() {
        return this.number;
    }
}
