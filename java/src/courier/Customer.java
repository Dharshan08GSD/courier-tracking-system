package courier;

/** POJO representing a customer (sender or receiver). */
public class Customer {
    private int    customerId;
    private String name;
    private String contactNumber;
    private String address;

    public Customer() {}

    public Customer(int customerId, String name, String contactNumber, String address) {
        this.customerId    = customerId;
        this.name          = name;
        this.contactNumber = contactNumber;
        this.address       = address;
    }

    // Getters
    public int    getCustomerId()    { return customerId; }
    public String getName()          { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress()       { return address; }

    // Setters
    public void setCustomerId(int customerId)       { this.customerId    = customerId; }
    public void setName(String name)                { this.name          = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setAddress(String address)          { this.address       = address; }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-20s | %-13s | %-35s |",
            customerId, name, contactNumber, address
        );
    }
}
