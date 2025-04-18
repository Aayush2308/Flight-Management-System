package Models;

public class Account {

    private int adminId;
    private String email;

    public Account(int adminId, String email) {
        this.adminId = adminId;
        this.email = email;
    }
    
    // Constructor, getters, setters
    public int getAdminId() {
        return adminId;
    }
    public String getEmail() {
        return email;
    }
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
