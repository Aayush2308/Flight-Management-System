// Service/PassengerService.java
package Service;

import Models.Passenger;
import DBConnection.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class PassengerService {
    public List<Passenger> getAllPassengers() throws SQLException {
        List<Passenger> passengers = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name, ticketNumber, passportNumber, contactNumber FROM Passenger")) {
            
            while (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setName(rs.getString("name"));
                passenger.setTicketNumber(rs.getString("ticketNumber"));
                passenger.setPassportNumber(rs.getString("passportNumber"));
                passenger.setContactNumber(rs.getString("contactNumber"));
                passengers.add(passenger);
            }
        }
        return passengers;
    }
    
    public void addPassenger(String passport, String name, String contact) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL InsertNewPassenger(?, ?, ?)}")) {
            cs.setString(1, passport);
            cs.setString(2, name);
            cs.setString(3, contact);
            cs.execute();
        }
    }
    
    public void deletePassenger(String ticketNumber) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM Passenger WHERE ticketNumber = ?")) {
            ps.setString(1, ticketNumber);
            int res = ps.executeUpdate();
            if(res>0){
                JOptionPane.showMessageDialog(null, "Passenger deleted successfully.");
            }
        }
    }
    
    public Passenger getPassengerDetails(String ticketNumber) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Passenger WHERE ticketNumber = ?")) {
            ps.setString(1, ticketNumber);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Passenger passenger = new Passenger();
                passenger.setName(rs.getString("name"));
                passenger.setTicketNumber(rs.getString("ticketNumber"));
                passenger.setPassportNumber(rs.getString("passportNumber"));
                passenger.setContactNumber(rs.getString("contactNumber"));
                return passenger;
            }
        }
        return null;
    }
    
    public float getLuggageWeight(String ticketNumber) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT weightOfItems FROM Luggage WHERE ticketNumber = ?")) {
            ps.setString(1, ticketNumber);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getFloat("weightOfItems");
            }
        }
        return -1; // Indicates no luggage found
    }
}