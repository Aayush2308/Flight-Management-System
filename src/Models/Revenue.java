package Models;

public class Revenue {
    private int transactionId;
    private double amount;
    private String paymentType;
    private String ticketNumber;

    // Constructor, getters, and setters
    public Revenue(int transactionId, double amount, String paymentType, String ticketNumber) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentType = paymentType;
        this.ticketNumber = ticketNumber;
    }

    // Getters and setters for all fields
    public int getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }
    public String getPaymentType() { return paymentType; }
    public String getTicketNumber() { return ticketNumber; }
}
