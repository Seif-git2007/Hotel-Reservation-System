import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataBaseManager {

    private static final String URL  = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USER = "root";
    private static final String PASS = "Seif@2007_roysn";

    private static final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "DB-Writer");
        t.setDaemon(true);
        return t;
    });

    public static void runAsync(Runnable dbWork) {
        dbExecutor.submit(dbWork);
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void loadAll() {
        loadRoomTypes();
        loadAmenities();
        loadRooms();
        loadUsers();
        loadReservations();
    }

    private static void loadRoomTypes() {
        try (Connection c = connect();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery("SELECT * FROM room_types")) {
            while (r.next()) {
                HotelDataBase.roomTypes.add(new RoomType(
                        r.getString("size"),
                        r.getDouble("base_price"),
                        r.getInt("capacity")));
            }
        } catch (SQLException e) {  }
    }

    private static void loadAmenities() {
        try (Connection c = connect();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery("SELECT * FROM amenities")) {
            while (r.next()) {
                HotelDataBase.amenities.add(
                        new Amenity(r.getString("name"), r.getDouble("price")));
            }
        } catch (SQLException e) {  }
    }

    private static void loadRooms() {
        try (Connection c = connect();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery("SELECT * FROM rooms")) {
            while (r.next()) {
                int       num   = r.getInt("room_number");
                int       floor = r.getInt("floor");
                Room.view view  = Room.view.valueOf(r.getString("view"));
                RoomType  type  = HotelDataBase.findRoomType(r.getString("room_type"));
                ArrayList<Amenity> amenities = loadRoomAmenities(c, num);
                HotelDataBase.rooms.add(new Room(type, amenities, num, floor, view));
            }
        } catch (SQLException e) {  }
    }

    private static ArrayList<Amenity> loadRoomAmenities(
            Connection c, int roomNumber) throws SQLException {
        ArrayList<Amenity> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT amenity_name FROM room_amenities WHERE room_number = ?")) {
            ps.setInt(1, roomNumber);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                Amenity a = HotelDataBase.findAmenity(r.getString("amenity_name"));
                if (a != null) list.add(a);
            }
        }
        return list;
    }

    private static void loadUsers() {
        try (Connection c = connect();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery("SELECT * FROM users")) {
            while (r.next()) {
                String      type   = r.getString("type");
                User.Gender gender = User.Gender.valueOf(r.getString("gender"));
                String      uname  = r.getString("username");
                String      pass   = r.getString("password");
                String      email  = r.getString("email");
                java.time.LocalDate dob = r.getDate("date_of_birth").toLocalDate();

                User user = switch (type) {
                    case "GUEST" -> new Guest(
                            uname, pass, dob,
                            r.getDouble("balance"),
                            new roomPreferences(
                                    r.getInt("pref_floor"),
                                    Room.view.valueOf(r.getString("pref_view"))),
                            r.getString("address"),
                            gender,
                            r.getString("display_name"),
                            email);
                    case "RECEPTIONIST" -> new Receptionist(
                            uname, pass, dob,
                            r.getInt("working_hours"),
                            gender, email);
                    case "ADMIN" -> new Admin(
                            uname, pass, dob,
                            r.getInt("working_hours"),
                            gender, email);
                    default -> null;
                };
                if (user != null) HotelDataBase.users.add(user);
            }
        } catch (SQLException e) {  }
    }

    private static void loadReservations() {
        try (Connection c = connect();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery("SELECT * FROM reservations")) {
            while (r.next()) {
                Guest guest = (Guest) HotelDataBase.searchUserByName(
                        r.getString("guest_username"));
                Room  room  = HotelDataBase.findRoom(r.getInt("room_number"));
                if (guest == null || room == null) continue;

                Reservation res = new Reservation(
                        guest, room,
                        r.getDate("check_in").toLocalDate(),
                        r.getDate("check_out").toLocalDate());
                res.setStatus(Reservation.Status.valueOf(r.getString("status")));
                res.setSpecialRequests(r.getString("special_requests"));
                HotelDataBase.reservations.add(res);
            }
        } catch (SQLException e) {  }
    }



    public static void saveRoomType(RoomType rt) {
        String sql = """
            INSERT INTO room_types (size, base_price, capacity) VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE base_price=VALUES(base_price), capacity=VALUES(capacity)
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rt.getSize());
            ps.setDouble(2, rt.getBasePrice());
            ps.setInt   (3, rt.getCapacity());
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.ROOMTYPE_CHANGED);
    }

    public static void deleteRoomType(RoomType rt) {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM room_types WHERE size = ?")) {
            ps.setString(1, rt.getSize());
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.ROOMTYPE_CHANGED);
    }


    public static void saveAmenity(Amenity a) {
        String sql = """
            INSERT INTO amenities (name, price) VALUES (?, ?)
            ON DUPLICATE KEY UPDATE price=VALUES(price)
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getName());
            ps.setDouble(2, a.getPrice());
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.AMENITY_CHANGED);
    }

    public static void deleteAmenity(Amenity a) {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(
                "DELETE FROM amenities WHERE name = ?")) {
            ps.setString(1, a.getName());
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.AMENITY_CHANGED);
    }


    public static void saveRoom(Room room) {
        String sql = """
            INSERT INTO rooms (room_number, floor, view, room_type) VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE floor=VALUES(floor), view=VALUES(view),
                                    room_type=VALUES(room_type)
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt   (1, room.getRoomNumber());
            ps.setInt   (2, room.getFloor());
            ps.setString(3, room.getView().name());
            ps.setString(4, room.getType().getSize());
            ps.executeUpdate();
            saveRoomAmenities(c, room);
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.ROOM_CHANGED);
    }

    private static void saveRoomAmenities(Connection c, Room room) throws SQLException {
        // Clear existing links then re-insert fresh
        try (PreparedStatement del = c.prepareStatement(
                "DELETE FROM room_amenities WHERE room_number = ?")) {
            del.setInt(1, room.getRoomNumber());
            del.executeUpdate();
        }
        try (PreparedStatement ins = c.prepareStatement(
                "INSERT INTO room_amenities (room_number, amenity_name) VALUES (?, ?)")) {
            for (Amenity a : room.getAmenities()) {
                ins.setInt   (1, room.getRoomNumber());
                ins.setString(2, a.getName());
                ins.addBatch();
            }
            ins.executeBatch();
        }
        EventBus.fire(EventBus.Event.ROOM_CHANGED);
    }

    public static void deleteRoom(Room room) {
        try (Connection c = connect()) {
            // Delete amenity links first (FK constraint)
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM room_amenities WHERE room_number = ?")) {
                ps.setInt(1, room.getRoomNumber());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM rooms WHERE room_number = ?")) {
                ps.setInt(1, room.getRoomNumber());
                ps.executeUpdate();
            }
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.ROOM_CHANGED);
    }

    // ─── User ─────────────────────────────────────────────────────────────────

    public static void saveUser(User user) {
        String sql = """
            INSERT INTO users
                (username, password, email, gender, date_of_birth,
                 type, balance, address, display_name, pref_floor, pref_view, working_hours)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            ON DUPLICATE KEY UPDATE
                password=VALUES(password), balance=VALUES(balance),
                address=VALUES(address), display_name=VALUES(display_name)
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getGender().name());
            ps.setDate  (5, Date.valueOf(user.getDateOfBirth()));
            if (user instanceof Guest g) {
                ps.setString(6, "GUEST");
                ps.setDouble(7, g.getBalance());
                ps.setString(8, g.getAddress());
                ps.setString(9, g.getDisplayname());
                ps.setInt   (10, g.getPrefered().getFloor());
                ps.setString(11, g.getPrefered().getView().name());
                ps.setNull  (12, Types.INTEGER);
            } else if (user instanceof Receptionist rec) {
                ps.setString(6, "RECEPTIONIST");
                ps.setNull  (7, Types.DOUBLE);
                ps.setNull  (8, Types.VARCHAR);
                ps.setNull  (9, Types.VARCHAR);
                ps.setNull  (10, Types.INTEGER);
                ps.setNull  (11, Types.VARCHAR);
                ps.setInt   (12, rec.getWorkingHours());
            } else if (user instanceof Admin adm) {
                ps.setString(6, "ADMIN");
                ps.setNull  (7, Types.DOUBLE);
                ps.setNull  (8, Types.VARCHAR);
                ps.setNull  (9, Types.VARCHAR);
                ps.setNull  (10, Types.INTEGER);
                ps.setNull  (11, Types.VARCHAR);
                ps.setInt   (12, adm.getWorkingHours());
            }
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.USER_CHANGED);
    }

    public static void saveReservation(Reservation r) {
        String sql = """
            INSERT INTO reservations
                (guest_username, room_number, check_in, check_out, status, special_requests)
            VALUES (?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE status=VALUES(status),
                                    special_requests=VALUES(special_requests)
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getGuest().getUsername());
            ps.setInt   (2, r.getRoom().getRoomNumber());
            ps.setDate  (3, Date.valueOf(r.getCheckInDate()));
            ps.setDate  (4, Date.valueOf(r.getCheckOutDate()));
            ps.setString(5, r.getStatus().name());
            ps.setString(6, r.getSpecialRequests());
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
    }

    public static void updateReservationStatus(Reservation r) {
        String sql = """
            UPDATE reservations SET status = ?
            WHERE guest_username = ? AND room_number = ? AND check_in = ?
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, r.getStatus().name());
            ps.setString(2, r.getGuest().getUsername());
            ps.setInt   (3, r.getRoom().getRoomNumber());
            ps.setDate  (4, Date.valueOf(r.getCheckInDate()));
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.RESERVATION_CHANGED);
    }

    // ─── Invoice ──────────────────────────────────────────────────────────────
    // db_id is AUTO_INCREMENT and only used internally here to link invoice_reservations
    // Java's Invoice object never stores or sees it

    public static void saveInvoice(Invoice inv) {
        String sql = """
            INSERT INTO invoices
                (guest_username, total, is_paid, payment_method, payment_date,
                 card_holder_name, card_number, card_expiry, card_cvv)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString (1, inv.getGuest().getUsername());
            ps.setDouble (2, inv.getTotal());
            ps.setBoolean(3, inv.isPaid());

            if (inv.getMethod() != null)
                ps.setString(4, inv.getMethod().name());
            else
                ps.setNull(4, Types.VARCHAR);

            if (inv.getPaymentDate() != null)
                ps.setDate(5, Date.valueOf(inv.getPaymentDate()));
            else
                ps.setNull(5, Types.DATE);

            // VisaCard fields — only filled when method is CREDIT
            if (inv.getMethod() == Invoice.paymentMethod.CREDIT && inv.getCardInfo() != null) {
                ps.setString(6, inv.getCardInfo().getCardHolderName());
                ps.setString(7, inv.getCardInfo().getCardNumber());
                ps.setString(8, inv.getCardInfo().getExpiryDate());
                ps.setString(9, inv.getCardInfo().getCvv());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.VARCHAR);
                ps.setNull(9, Types.VARCHAR);
            }

            ps.executeUpdate();

            // Grab the AUTO_INCREMENT db_id to link invoice_reservations
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int dbId = keys.getInt(1);
                saveInvoiceReservations(c, dbId, inv);
            }

        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.INVOICE_CHANGED);
    }

    public static void updateInvoicePayment(Invoice inv) {
        // Finds the invoice by guest + total + unpaid, then updates it
        String sql = """
            UPDATE invoices
            SET is_paid=?, payment_method=?, payment_date=?,
                card_holder_name=?, card_number=?, card_expiry=?, card_cvv=?
            WHERE guest_username=? AND total=? AND is_paid=FALSE
            LIMIT 1
            """;
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, inv.isPaid());
            ps.setString (2, inv.getMethod() != null ? inv.getMethod().name() : null);
            ps.setDate   (3, inv.getPaymentDate() != null ?
                    Date.valueOf(inv.getPaymentDate()) : null);
            if (inv.getMethod() == Invoice.paymentMethod.CREDIT && inv.getCardInfo() != null) {
                ps.setString(4, inv.getCardInfo().getCardHolderName());
                ps.setString(5, inv.getCardInfo().getCardNumber());
                ps.setString(6, inv.getCardInfo().getExpiryDate());
                ps.setString(7, inv.getCardInfo().getCvv());
            } else {
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
            }
            ps.setString(8, inv.getGuest().getUsername());
            ps.setDouble(9, inv.getTotal());
            ps.executeUpdate();
        } catch (SQLException e) {  }
        EventBus.fire(EventBus.Event.INVOICE_CHANGED);
    }

    private static void saveInvoiceReservations(
            Connection c, int invoiceDbId, Invoice inv) throws SQLException {
        String sql = """
            INSERT INTO invoice_reservations
                (invoice_db_id, guest_username, room_number, check_in)
            VALUES (?, ?, ?, ?)
            """;
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Reservation r : inv.getReservation()) {
                ps.setInt   (1, invoiceDbId);
                ps.setString(2, r.getGuest().getUsername());
                ps.setInt   (3, r.getRoom().getRoomNumber());
                ps.setDate  (4, Date.valueOf(r.getCheckInDate()));
                ps.addBatch();
            }
            ps.executeBatch();
        }
        EventBus.fire(EventBus.Event.INVOICE_CHANGED);
    }


    public static void saveChatMessage(String sender, String receiver, String message) {
        String sql = "INSERT INTO chat_messages (sender, receiver, message) VALUES (?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, message);
            ps.executeUpdate();
        } catch (SQLException e) {  }
    }

    public static void seedFromHotelDataBase() {
        for (RoomType  rt : HotelDataBase.getRoomTypes())  saveRoomType(rt);
        for (Amenity   a  : HotelDataBase.getAmenities())  saveAmenity(a);
        for (Room      r  : HotelDataBase.getRooms())      saveRoom(r);
        for (User      u  : HotelDataBase.getUsers())      saveUser(u);
        synchronized (HotelDataBase.reservations) {
            for (Reservation r : HotelDataBase.reservations) saveReservation(r);
        }
        System.out.println("Seed complete.");
    }
}