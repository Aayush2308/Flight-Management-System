package Models;

// Models/Admin.java
public class Admin {
    private String name, email, contactNumber, password;

    public Admin(String name, String email, String contactNumber, String password) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.password = password;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContactNumber() { return contactNumber; }
    public String getPassword() { return password; }
}
