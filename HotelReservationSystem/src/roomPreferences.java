public class roomPreferences {

    private int floor;
    public enum view{SEA,POOL,CITY};
    view view;
    public roomPreferences(){}
    public roomPreferences(int floor) {
        this.floor = floor;
    }

    public void setView(int v) {
        if (v ==1){
            view= view.SEA;
        }
        else if (v ==2){
            view=view.POOL;
        } else if (v== 3) {
            view= view.CITY;
        }

    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
