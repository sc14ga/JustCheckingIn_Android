
package co.uk.justcheckingin;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves all the information of an Event.
 * 
 * @author Georgios Aikaterinakis
 * @see Timer
 * @see ContactList
 * @see Contact
 */
public class Event {
    private String name;
    private List<Timer> list = new ArrayList<Timer>();

    /**
     * The status of the OFF/ON switch.
     */
    private boolean enabled;

    /**
     * The status of the Repeat CheckBox.
     */
    private boolean repeat;

    /**
     * A boolean for each day of the week holds the status of the respective ToggleButton.
     */
    private boolean sun, mon, tue, wed, thu, fri, sat;

    /**
     * An identifier given at the creation of the Event instance and used to give unique id's to the
     * alarms programmed by this Event. e.g. Sunday:id+0, Monday:id+1, and so on...
     */
    int id;

    public Event() {
        super();
    }

    public Event(String name, List<Timer> list) {
        super();
        this.name = name;
        this.list = list;
        this.enabled = false;
        this.repeat = false;
        this.sun = false;
        this.mon = false;
        this.tue = false;
        this.wed = false;
        this.thu = false;
        this.fri = false;
        this.sat = false;
        this.id = EventsActivity.counter + 10;
        EventsActivity.counter = EventsActivity.counter + 10;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Serialization
     */
    public String toXML() {
        String output = this.name + "<boolean>" + String.valueOf(this.enabled);
        output += "<boolean>" + String.valueOf(this.repeat);
        output += "<boolean>" + String.valueOf(this.sun);
        output += "<boolean>" + String.valueOf(this.mon);
        output += "<boolean>" + String.valueOf(this.tue);
        output += "<boolean>" + String.valueOf(this.wed);
        output += "<boolean>" + String.valueOf(this.thu);
        output += "<boolean>" + String.valueOf(this.fri);
        output += "<boolean>" + String.valueOf(this.sat);
        output += "<boolean>" + String.valueOf(this.id);
        for (Timer t : this.list) {
            output += "<Timer>" + t.toString();
        }
        return output;
    }

    /**
     * De-serialization
     */
    public Event fromString(String input) {
        Event event = new Event();

        String[] tokens = input.split("<Timer>");
        String token = tokens[0];

        String[] vars = token.split("<boolean>");
        event.name = vars[0];
        event.enabled = Boolean.parseBoolean(vars[1]);
        event.repeat = Boolean.parseBoolean(vars[2]);
        event.sun = Boolean.parseBoolean(vars[3]);
        event.mon = Boolean.parseBoolean(vars[4]);
        event.tue = Boolean.parseBoolean(vars[5]);
        event.wed = Boolean.parseBoolean(vars[6]);
        event.thu = Boolean.parseBoolean(vars[7]);
        event.fri = Boolean.parseBoolean(vars[8]);
        event.sat = Boolean.parseBoolean(vars[9]);
        event.id = Integer.parseInt(vars[10]);

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

    public boolean getStatus() {
        return this.enabled;
    }

    public boolean getRepeat() {
        return this.repeat;
    }

    public boolean getSun() {
        return this.sun;
    }

    public boolean getMon() {
        return this.mon;
    }

    public boolean getTue() {
        return this.tue;
    }

    public boolean getWed() {
        return this.wed;
    }

    public boolean getThu() {
        return this.thu;
    }

    public boolean getFri() {
        return this.fri;
    }

    public boolean getSat() {
        return this.sat;
    }

    public void setStatus(boolean s) {
        this.enabled = s;
    }

    public void setRepeat(boolean s) {
        this.repeat = s;
    }

    public void setSun(boolean s) {
        this.sun = s;
    }

    public void setMon(boolean s) {
        this.mon = s;
    }

    public void setTue(boolean s) {
        this.tue = s;
    }

    public void setWed(boolean s) {
        this.wed = s;
    }

    public void setThu(boolean s) {
        this.thu = s;
    }

    public void setFri(boolean s) {
        this.fri = s;
    }

    public void setSat(boolean s) {
        this.sat = s;
    }
}
