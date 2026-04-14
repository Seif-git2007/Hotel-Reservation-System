public class roomPreferences {

    private int floor;

    Room.view View;
    public roomPreferences(){}
    public roomPreferences(int floor,Room.view View) {
        this.floor = floor;
        this.View=View;
    }

    public void setView(String s) {
        View=Room.view.valueOf(s);

    }

    public Room.view getView() {
        return View;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "roomPreferences[" +
                "floor: " + floor +
                " View: " + View +"]"
                ;
    }
}
