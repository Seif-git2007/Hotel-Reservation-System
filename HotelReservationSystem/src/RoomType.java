import java.util.Objects;

public class RoomType {
    private String size;//single double etc..
    private double basePrice;//amenity prices will be added
    private int capacity;

    public RoomType(String size, double basePrice, int capacity) {
        this.size = size;
        this.basePrice = basePrice;
        this.capacity = capacity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public static void create(RoomType newRoomType) throws  InvalidInputException {
        for(RoomType r : HotelDataBase.roomTypes){
            if(r.equals(HotelDataBase.roomTypes)){
                throw new InvalidInputException("RoomTypes Already Exists");
            }
        }
        HotelDataBase.roomTypes.add(newRoomType);
        System.out.println("RoomType Has Been Successfully Added");

    }

    public static void read(){
        System.out.println("ALl RoomType Details");
        for(RoomType r : HotelDataBase.roomTypes){
            System.out.println(r.toString());
        }

    }

    public void update(RoomType modifiedRoomType) throws InvalidInputException {
        for(RoomType r : HotelDataBase.roomTypes){
            if(r.equals(modifiedRoomType)){
                throw new InvalidInputException("No Modifications Are Preformed");
            }
        }
        for(Reservation res : HotelDataBase.reservations){
            if(res.getRoom().getType().equals(this) && (res.getStatus() == Reservation.Status.PENDING || res.getStatus() == Reservation.Status.CONFIRMED )){
                throw new InvalidInputException("Can't Modify RoomType While It's In Use");
            }
        }
        this.basePrice = modifiedRoomType.getBasePrice();
        this.capacity = modifiedRoomType.getCapacity();
        this.size = modifiedRoomType.getSize();
        System.out.println("RoomType Has Been Modified Successfully");
    }

    public void delete(int index) throws InvalidInputException {
        for(Reservation res : HotelDataBase.reservations){
            if(res.getRoom().getType().equals(HotelDataBase.rooms.get(index).getType()) && (res.getStatus() == Reservation.Status.PENDING || res.getStatus() == Reservation.Status.CONFIRMED )){
                throw new InvalidInputException("Can't Delete RoomType While It's In Use");
            }
        }
        HotelDataBase.rooms.remove(index);
        System.out.println("RoomType Has Been Deleted Successfully");
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "size='" + size + '\'' +
                ", basePrice=" + basePrice +
                ", capacity=" + capacity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomType roomType)) return false;
        return Double.compare(basePrice, roomType.basePrice) == 0 && capacity == roomType.capacity && Objects.equals(size, roomType.size);
    }

}
