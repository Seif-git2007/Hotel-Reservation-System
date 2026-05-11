import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class SyncService {

    private static Timeline pollTimer;
    private static String lastReservationSnapshot = "";
    private static String lastUserSnapshot        = "";
    private static String lastRoomSnapshot        = "";
    private static String lastDateSnapshot        = "";

    public static void start() {
        pollTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> poll()));
        pollTimer.setCycleCount(Timeline.INDEFINITE);
        pollTimer.play();
    }

    public static void stop() {
        if (pollTimer != null) pollTimer.stop();
    }

    private static void poll() {
        new Thread(() -> {
            try {
                checkReservations();
                checkUsers();
                checkRooms();
                checkDate();
            } catch (Exception ex) {
                System.out.println("Sync poll error: " + ex.getMessage());
            }
        }, "Sync-Poller").start();
    }

    private static void checkReservations() throws SQLException {
        String snapshot = getSnapshot("SELECT guest_username, room_number, check_in, status FROM reservations ORDER BY check_in");
        if (!snapshot.equals(lastReservationSnapshot)) {
            lastReservationSnapshot = snapshot;
            synchronized (HotelDataBase.reservations) {
                HotelDataBase.reservations.clear();
            }
            DataBaseManager.reloadReservations();
            Platform.runLater(() -> EventBus.fire(EventBus.Event.RESERVATION_CHANGED));
        }
    }

    private static void checkUsers() throws SQLException {
        String snapshot = getSnapshot("SELECT username, balance, over_due, logged_in FROM users ORDER BY username");
        if (!snapshot.equals(lastUserSnapshot)) {
            lastUserSnapshot = snapshot;
            try (Connection c = DataBaseManager.connect();
                 Statement  s = c.createStatement();
                 ResultSet  r = s.executeQuery("SELECT * FROM users")) {
                while (r.next()) {
                    String username = r.getString("username");
                    User existing = HotelDataBase.searchUserByName(username);
                    if (existing != null) {
                        // Update existing object in place — same reference, no stale pointer
                        existing.setLoggedIn(r.getBoolean("logged_in"));
                        if (existing instanceof Guest g) {
                            g.setBalance(r.getDouble("balance"));
                            g.setOverDue(r.getBoolean("over_due"));
                        }
                    } else {
                        // New user added — create and add
                        User.Gender gender = User.Gender.valueOf(r.getString("gender"));
                        String      uname  = r.getString("username");
                        String      pass   = r.getString("password");
                        String      email  = r.getString("email");
                        java.time.LocalDate dob = r.getDate("date_of_birth").toLocalDate();
                        User newUser = switch (r.getString("type")) {
                            case "GUEST" -> {
                                Guest g = new Guest(uname, pass, dob,
                                        r.getDouble("balance"),
                                        new roomPreferences(r.getInt("pref_floor"),
                                                Room.view.valueOf(r.getString("pref_view"))),
                                        r.getString("address"), gender,
                                        r.getString("display_name"), email);
                                g.setOverDue(r.getBoolean("over_due"));
                                g.setLoggedIn(r.getBoolean("logged_in"));
                                yield g;
                            }
                            case "RECEPTIONIST" -> {
                                Receptionist rec = new Receptionist(uname, pass, dob,
                                        r.getInt("working_hours"), gender, email);
                                rec.setLoggedIn(r.getBoolean("logged_in"));
                                yield rec;
                            }
                            case "ADMIN" -> {
                                Admin adm = new Admin(uname, pass, dob,
                                        r.getInt("working_hours"), gender, email);
                                adm.setLoggedIn(r.getBoolean("logged_in"));
                                yield adm;
                            }
                            default -> null;
                        };
                        if (newUser != null) HotelDataBase.users.add(newUser);
                    }
                }
            }
            Platform.runLater(() -> EventBus.fire(EventBus.Event.USER_CHANGED));
        }
    }

    private static void checkRooms() throws SQLException {
        String snapshot = getSnapshot("SELECT room_number, floor, view, room_type FROM rooms ORDER BY room_number");
        if (!snapshot.equals(lastRoomSnapshot)) {
            lastRoomSnapshot = snapshot;
            synchronized (HotelDataBase.rooms)     { HotelDataBase.rooms.clear(); }
            synchronized (HotelDataBase.roomTypes) { HotelDataBase.roomTypes.clear(); }
            synchronized (HotelDataBase.amenities) { HotelDataBase.amenities.clear(); }
            DataBaseManager.reloadRooms();
            Platform.runLater(() -> {
                EventBus.fire(EventBus.Event.ROOM_CHANGED);
                EventBus.fire(EventBus.Event.ROOMTYPE_CHANGED);
                EventBus.fire(EventBus.Event.AMENITY_CHANGED);
            });
        }
    }

    private static void checkDate() throws SQLException {
        String snapshot = getSnapshot("SELECT value FROM settings WHERE key_name = 'current_date'");
        if (!snapshot.equals(lastDateSnapshot)) {
            lastDateSnapshot = snapshot;
            try (Connection c = DataBaseManager.connect();
                 PreparedStatement ps = c.prepareStatement(
                     "SELECT value FROM settings WHERE key_name = 'current_date'")) {
                ResultSet r = ps.executeQuery();
                if (r.next()) {
                    LocalDate newDate = LocalDate.parse(r.getString("value"));
                    if (!newDate.equals(JumpInTime.now)) {
                        JumpInTime.now = newDate;
                        Platform.runLater(() -> {
                            EventBus.fire(EventBus.Event.TIME_JUMPED);
                            EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
                        });
                    }
                }
            }
        }
    }

    private static String getSnapshot(String sql) throws SQLException {
        StringBuilder sb = new StringBuilder();
        try (Connection c = DataBaseManager.connect();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery(sql)) {
            ResultSetMetaData meta = r.getMetaData();
            int cols = meta.getColumnCount();
            while (r.next()) {
                for (int i = 1; i <= cols; i++) {
                    sb.append(r.getString(i)).append("|");
                }
                sb.append(";");
            }
        }
        return sb.toString();
    }
}
