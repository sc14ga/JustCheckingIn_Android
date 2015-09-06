
package co.uk.justcheckingin;

/**
 * Used as attribute to Events. Specifies the selected time of an alarm and the ContactList that
 * will be notified if the alarm goes off.
 * 
 * @author Georgios Aikaterinakis
 * @see Event
 * @see ContactList
 * @see Contact
 */
public class Timer {
    ContactList list;
    String hour, minute;

    public Timer() {
        super();
    }

    public Timer(ContactList cList, String hour, String minute) {
        super();
        this.list = cList;
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String toString() {
        String s = this.list.toXML();
        return s + "<int>" + this.hour + "<int>" + this.minute;
    }

    /**
     * De-serialization
     */
    public Timer fromString(String input) {
        Timer timer = new Timer();

        String[] tokens = input.split("<int>");
        String token = tokens[0];

        ContactList cl = new ContactList();
        timer.list = new ContactList();
        timer.list = cl.fromString(token);
        timer.hour = tokens[1];
        timer.minute = tokens[2];

        return timer;
    }
}
