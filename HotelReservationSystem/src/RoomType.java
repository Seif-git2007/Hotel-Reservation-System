public class RoomType implements Manageable{
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

    public void create() {

    }
    @Override
    public void read(){

    }

    public void update() {

    }

    @Override
    public void delete(int index) {

    }
}
