package courier;

/** POJO representing a delivery staff member. */
public class DeliveryPersonnel {
    private int    personnelId;
    private String name;
    private String contactNumber;
    private String assignedArea;
    private String status;

    public DeliveryPersonnel() {}

    public DeliveryPersonnel(int personnelId, String name, String contactNumber,
                              String assignedArea, String status) {
        this.personnelId   = personnelId;
        this.name          = name;
        this.contactNumber = contactNumber;
        this.assignedArea  = assignedArea;
        this.status        = status;
    }

    // Getters
    public int    getPersonnelId()   { return personnelId; }
    public String getName()          { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getAssignedArea()  { return assignedArea; }
    public String getStatus()        { return status; }

    // Setters
    public void setPersonnelId(int personnelId)         { this.personnelId   = personnelId; }
    public void setName(String name)                    { this.name          = name; }
    public void setContactNumber(String contactNumber)  { this.contactNumber = contactNumber; }
    public void setAssignedArea(String assignedArea)    { this.assignedArea  = assignedArea; }
    public void setStatus(String status)                { this.status        = status; }

    @Override
    public String toString() {
        return String.format(
            "| %-4d | %-20s | %-13s | %-18s | %-12s |",
            personnelId, name, contactNumber, assignedArea, status
        );
    }
}
