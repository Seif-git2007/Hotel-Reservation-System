import java.util.Objects;

public class RoomType {
    private String size;
    private double basePrice;
    private int    capacity;

    public RoomType(String size, double basePrice, int capacity) {
        this.size = size; this.basePrice = basePrice; this.capacity = capacity;
    }

    public String getSize()           { return size; }
    public void setSize(String s)     { size = s; }
    public double getBasePrice()      { return basePrice; }
    public void setBasePrice(double p){ basePrice = p; }
    public int getCapacity()          { return capacity; }
    public void setCapacity(int c)    { capacity = c; }

    public static void create(RoomType newRoomType) throws InvalidInputException {
        for (RoomType r : HotelDataBase.roomTypes){
            if (r.getSize().equalsIgnoreCase(newRoomType.getSize())){
                throw new RoomInUseException("RoomType Already Exists");
            }
        }
        HotelDataBase.roomTypes.add(newRoomType);
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveRoomType(newRoomType);
            EventBus.fire(EventBus.Event.ROOMTYPE_CHANGED);
        });
        System.out.println("RoomType Has Been Successfully Added");
    }

    public static void read() {
        int cnt = 1;
        for (RoomType r : HotelDataBase.roomTypes){
            System.out.println(cnt++ + ". " + r);
        }
    }

    public void update(RoomType modifiedRoomType) throws InvalidInputException {
        for (RoomType r : HotelDataBase.roomTypes){
            if (r.equals(modifiedRoomType)){
                throw new RoomInUseException("No Modifications Are Performed");
            }
        }
        for (Reservation res : HotelDataBase.reservations){
            if (res.getRoom().getType().equals(this)
                    && (res.getStatus() == Reservation.Status.PENDING
                        || res.getStatus() == Reservation.Status.CONFIRMED)){
                throw new RoomInUseException("Can't Modify RoomType While It's In Use");
            }
        }
        this.basePrice = modifiedRoomType.getBasePrice();
        this.capacity  = modifiedRoomType.getCapacity();
        this.size      = modifiedRoomType.getSize();
        DataBaseManager.runAsync(() -> {
            DataBaseManager.saveRoomType(this);
            EventBus.fire(EventBus.Event.ROOMTYPE_CHANGED);
        });
        System.out.println("RoomType Has Been Modified Successfully");
    }

    public void delete(int index) throws InvalidInputException {
        for (Reservation res : HotelDataBase.reservations){
            if (res.getRoom().getType().getSize().equalsIgnoreCase(HotelDataBase.roomTypes.get(index).getSize())
                    && (res.getStatus() == Reservation.Status.PENDING
                        || res.getStatus() == Reservation.Status.CONFIRMED)){
                throw new RoomInUseException("Can't Delete RoomType While It's In Use");
            }
        }
        RoomType target = HotelDataBase.roomTypes.get(index);
        HotelDataBase.roomTypes.remove(index);
        DataBaseManager.runAsync(() -> {
            DataBaseManager.deleteRoomType(target);
            EventBus.fire(EventBus.Event.ROOMTYPE_CHANGED);
        });
        System.out.println("RoomType Has Been Deleted Successfully");
    }

    @Override
    public String toString() {
        return "RoomType{size='" + size + "', basePrice=" + basePrice + ", capacity=" + capacity + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomType roomType)) return false;
        return Double.compare(basePrice, roomType.basePrice) == 0 && capacity == roomType.capacity && Objects.equals(size.toLowerCase(), roomType.size.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, basePrice, capacity);
    }
}
