
package co.uk.justcheckingin;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Event {
    private String name;
    private List<Timer> list = new ArrayList<Timer>();
    private boolean enabled;

    public Event() {
        super();
    }

    public Event(String name, List<Timer> list) {
        super();
        this.name = name;
        this.list = list;
        this.enabled = false;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String toXML() {
        String output = this.name+"<boolean>"+String.valueOf(this.enabled);
        for (Timer t : this.list) {
            output += "<Timer>" + t.toString();
        }
        return output;
    }

    public Event fromString(String input) {
        Event event = new Event();

        String[] tokens = input.split("<Timer>");
        String token = tokens[0];

        String[] vars = token.split("<boolean>");
        event.name = vars[0];
        event.enabled = Boolean.parseBoolean(vars[1]);

        for (int i = 1; i < tokens.length; i++) {
            Timer timer = new Timer();
            event.list.add(timer.fromString(tokens[i]));
        }

        return event;
    }

    public String getName() {
        return this.name;
    }

    public List<Timer> getList() {
        return this.list;
    }
    
    public boolean getStatus(){
        return this.enabled;
    }
    
    public void setStatus(boolean s){
        this.enabled = s;
    }
}
