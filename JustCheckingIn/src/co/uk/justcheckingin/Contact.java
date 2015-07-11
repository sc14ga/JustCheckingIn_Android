package co.uk.justcheckingin;

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
        return this.name + ":" + this.number;
    }
}
