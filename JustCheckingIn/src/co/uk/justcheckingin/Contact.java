
package co.uk.justcheckingin;

/**
 * Stores the details of a selected contact from the imported phone contacts.
 * 
 * @author Georgios Aikaterinakis
 * @see ContactList
 */
public class Contact {
    private String name;
    private String number;

    public Contact() {
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

    /**
     * De-serialization
     */
    public Contact fromString(String input) {
        Contact contact = new Contact();

        String[] tokens = input.split("<:>");
        String token = tokens[0];

        contact.name = token;
        contact.number = tokens[1];

        return contact;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }
}
