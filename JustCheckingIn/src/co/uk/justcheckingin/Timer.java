package co.uk.justcheckingin;

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
}
