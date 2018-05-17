package qa.edu.qu.cmps312.safedrivingapplication.models;


public class Contact {
    private String contactLname;
    private String contactFname;
    private String contactId;

    public Contact(String contactLname, String contactFname, String contactId) {
        this.contactLname = contactLname;
        this.contactFname = contactFname;
        this.contactId = contactId;
    }

    public Contact() {
    }

    public String getContactLname() {
        return contactLname;
    }

    public void setContactLname(String contactLname) {
        this.contactLname = contactLname;
    }

    public String getContactFname() {
        return contactFname;
    }

    public void setContactFname(String contactFname) {
        this.contactFname = contactFname;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
