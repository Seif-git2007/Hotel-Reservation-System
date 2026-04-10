public class roomPreferences {

    private int floor;
    public enum view{SEA,POOL,CITY};
    view View;
    public roomPreferences(){}
    public roomPreferences(int floor) {
        this.floor = floor;
    }

    public void setView(String s) {
        View=view.valueOf(s);

    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
