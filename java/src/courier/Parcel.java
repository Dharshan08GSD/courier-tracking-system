package courier;

/** POJO representing a parcel/shipment. */
public class Parcel {
    private int    parcelId;
    private String trackingNumber;
    private int    senderId;
    private int    receiverId;
    private String parcelType;
    private double weight;
    private String bookingDate;
    private String destination;
    private String distanceCategory;
    private String serviceType;
    private double deliveryCharge;
    private String status;

    public Parcel() {}

    public Parcel(int parcelId, String trackingNumber, int senderId, int receiverId,
                  String parcelType, double weight, String bookingDate, String destination,
                  String distanceCategory, String serviceType, double deliveryCharge, String status) {
        this.parcelId         = parcelId;
        this.trackingNumber   = trackingNumber;
        this.senderId         = senderId;
        this.receiverId       = receiverId;
        this.parcelType       = parcelType;
        this.weight           = weight;
        this.bookingDate      = bookingDate;
        this.destination      = destination;
        this.distanceCategory = distanceCategory;
        this.serviceType      = serviceType;
        this.deliveryCharge   = deliveryCharge;
        this.status           = status;
    }

    // Getters
    public int    getParcelId()         { return parcelId; }
    public String getTrackingNumber()   { return trackingNumber; }
    public int    getSenderId()         { return senderId; }
    public int    getReceiverId()       { return receiverId; }
    public String getParcelType()       { return parcelType; }
    public double getWeight()           { return weight; }
    public String getBookingDate()      { return bookingDate; }
    public String getDestination()      { return destination; }
    public String getDistanceCategory() { return distanceCategory; }
    public String getServiceType()      { return serviceType; }
    public double getDeliveryCharge()   { return deliveryCharge; }
    public String getStatus()           { return status; }

    // Setters
    public void setParcelId(int parcelId)                 { this.parcelId         = parcelId; }
    public void setTrackingNumber(String trackingNumber)  { this.trackingNumber   = trackingNumber; }
    public void setSenderId(int senderId)                 { this.senderId         = senderId; }
    public void setReceiverId(int receiverId)             { this.receiverId       = receiverId; }
    public void setParcelType(String parcelType)          { this.parcelType       = parcelType; }
    public void setWeight(double weight)                  { this.weight           = weight; }
    public void setBookingDate(String bookingDate)        { this.bookingDate      = bookingDate; }
    public void setDestination(String destination)        { this.destination      = destination; }
    public void setDistanceCategory(String dc)            { this.distanceCategory = dc; }
    public void setServiceType(String serviceType)        { this.serviceType      = serviceType; }
    public void setDeliveryCharge(double deliveryCharge)  { this.deliveryCharge   = deliveryCharge; }
    public void setStatus(String status)                  { this.status           = status; }

    @Override
    public String toString() {
        return String.format(
            "+--------------------------------------------------+%n" +
            "| Parcel ID     : %-32d|%n" +
            "| Tracking No.  : %-32s|%n" +
            "| Type          : %-32s|%n" +
            "| Weight        : %-30.2f kg|%n" +
            "| Destination   : %-32s|%n" +
            "| Service       : %-32s|%n" +
            "| Distance Cat. : %-32s|%n" +
            "| Charge        : Rs. %-29.2f|%n" +
            "| Booking Date  : %-32s|%n" +
            "| Status        : %-32s|%n" +
            "+--------------------------------------------------+",
            parcelId, trackingNumber, parcelType, weight, destination,
            serviceType, distanceCategory, deliveryCharge, bookingDate, status
        );
    }
}
