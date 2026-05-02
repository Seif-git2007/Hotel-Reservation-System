import java.util.Objects;

public class Amenity {
    private String name;
    private double price;

    public Amenity(String name, double price) { this.name = name; this.price = price; }

    public String getName()       { return name; }
    public void setName(String n) { name = n; }
    public double getPrice()      { return price; }
    public void setPrice(double p){ price = p; }

    @Override
    public String toString() { return "Amenity: name: '" + name + "', price: " + price; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Amenity other)) return false;
        return name.equals(other.name) ;
    }

    public static void create(Amenity a) throws InvalidInputException {

            for (Amenity am : HotelDataBase.amenities)
                if (am.equals(a))
                    throw new RoomInUseException("Amenity '" + a.getName() + "' already exists.");
            HotelDataBase.amenities.add(a);

        System.out.println("Amenity " + a.getName() + " added successfully.");
    }

    public static void read() {
        int cnt = 1;

        for (Amenity a : HotelDataBase.amenities)
            System.out.println(cnt++ + ". Amenity: " + a.getName() + " | Price: $" + a.getPrice());

    }

    public void update(Amenity modifiedAmenity) throws InvalidInputException {

        for (Reservation r : HotelDataBase.reservations)
            if (r.getRoom().getAmenities().contains(this) && (r.getStatus() == Reservation.Status.PENDING || r.getStatus() == Reservation.Status.CONFIRMED)){
                throw new RoomInUseException("Cannot update amenity '" + name + "' as it is in use");
    }


        for (Amenity a : HotelDataBase.amenities){
            if (a.equals(modifiedAmenity)){
                throw new RoomInUseException("No Modifications Are Performed");}}

        this.name = modifiedAmenity.getName();
        this.price = modifiedAmenity.getPrice();
        System.out.println("Amenity updated to: " + name + ", $" + price);
    }

    public void delete(int index) throws InvalidInputException {
        Amenity a = HotelDataBase.amenities.get(index);
        for (Reservation r : HotelDataBase.reservations)
            if (r.getRoom().getAmenities().contains(a) && (r.getStatus() == Reservation.Status.PENDING || r.getStatus() == Reservation.Status.CONFIRMED)){
                throw new RoomInUseException("Cannot delete amenity '" + a.getName() + "' as it is in an active reservation.");
            }
        for (Room room : HotelDataBase.rooms){
            room.getAmenities().remove(a);
        }
        HotelDataBase.amenities.remove(index);
        System.out.println("Amenity " + a.getName() + " deleted");

    }
}
