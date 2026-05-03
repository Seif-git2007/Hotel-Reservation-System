import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class ReceptionistRoomsController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private FlowPane                      roomGrid;
    @FXML private Label                         lblTotalRooms;
    @FXML private Label                         lblAvailableRooms;
    @FXML private Label                         lblOccupiedRooms;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnRooms);
        }
        updateStats();
        renderRooms();
    }

    private void updateStats() {
        long total    = HotelDataBase.getRooms().size();
        long occupied = HotelDataBase.getRooms().stream().filter(r ->
                HotelDataBase.reservations.stream().anyMatch(res ->
                        res.getRoom() != null &&
                                res.getRoom().getRoomNumber() == r.getRoomNumber() &&
                                (res.getStatus() == Reservation.Status.CONFIRMED ||
                                        res.getStatus() == Reservation.Status.PENDING))
        ).count();
        long available = total - occupied;

        lblTotalRooms.setText("Total Rooms: " + total);
        lblAvailableRooms.setText("✔ Available: " + available);
        lblOccupiedRooms.setText("⏳ Occupied: " + occupied);
    }

    private void renderRooms() {
        roomGrid.getChildren().clear();

        if (HotelDataBase.getRooms().isEmpty()) {
            Label empty = new Label("No rooms on record.");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            roomGrid.getChildren().add(empty);
            return;
        }

        for (Room room : HotelDataBase.getRooms()) {
            roomGrid.getChildren().add(buildCard(room));
        }
    }

    private HBox buildCard(Room room) {
        boolean occupied = HotelDataBase.reservations.stream().anyMatch(r ->
                r.getRoom() != null &&
                        r.getRoom().getRoomNumber() == room.getRoomNumber() &&
                        (r.getStatus() == Reservation.Status.CONFIRMED ||
                                r.getStatus() == Reservation.Status.PENDING));

        HBox card = new HBox(0);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #E0DAD0;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15,33,96,0.06), 8, 0, 0, 2);"
        );

        // ── Left navy panel ───────────────────────────────────────────────
        VBox leftPanel = new VBox(0);
        leftPanel.setMinWidth(160);
        leftPanel.setPrefWidth(160);
        leftPanel.setAlignment(Pos.TOP_LEFT);
        leftPanel.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-background-radius: 12 0 0 12;" +
                        "-fx-padding: 0 0 16 0;"
        );

        Pane accentBar = new Pane();
        accentBar.setPrefHeight(4);
        accentBar.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 12 0 0 0;");

        Label roomNo = new Label("#" + room.getRoomNumber());
        roomNo.setStyle(
                "-fx-text-fill: #C9A84C;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-style: italic;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 16 0 16;"
        );

        Label titleLabel = new Label(room.getType().getSize() + " Room");
        titleLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 2 16 0 16;"
        );

        Label viewChip = new Label(room.getView() + " VIEW");
        viewChip.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-background-color: #C9A84C;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.10em;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 10;"
        );
        HBox chipRow = new HBox(viewChip);
        chipRow.setStyle("-fx-padding: 6 16 0 16;");

        leftPanel.getChildren().addAll(accentBar, roomNo, titleLabel, chipRow);

        // ── Right content panel ───────────────────────────────────────────
        VBox rightPanel = new VBox(10);
        rightPanel.setStyle("-fx-padding: 14 16 14 16;");
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        // Meta row
        HBox metaRow = new HBox(0);
        metaRow.setStyle(
                "-fx-background-color: #F8F5EF;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: #EDE7D8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: 0.5;"
        );
        metaRow.getChildren().addAll(
                metaCell("CAPACITY", room.getType().getCapacity() + " Guests", true),
                metaCell("FLOOR",    ordinal(room.getFloor()), true),
                metaCell("RATE",     String.format("$%.0f / night", room.getType().getBasePrice()), false)
        );

        // Amenity pills
        FlowPane pills = new FlowPane(5, 5);
        if (room.getAmenities().isEmpty()) {
            Label none = new Label("No amenities");
            none.setStyle("-fx-text-fill: #AAAAAA; -fx-font-size: 11px; -fx-font-style: italic;");
            pills.getChildren().add(none);
        } else {
            for (Amenity a : room.getAmenities()) {
                Label pill = new Label(a.getName());
                pill.setStyle(
                        "-fx-background-color: #FFFFFF;" +
                                "-fx-text-fill: #0F2160;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-padding: 4 12;" +
                                "-fx-background-radius: 20;" +
                                "-fx-border-color: #C9A84C;" +
                                "-fx-border-radius: 20;" +
                                "-fx-border-width: 1.2;"
                );
                pills.getChildren().add(pill);
            }
        }

        // Status badge only — no edit/delete buttons
        HBox footer = new HBox(8);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setStyle(
                "-fx-border-color: #E8E3DA transparent transparent transparent;" +
                        "-fx-border-width: 0.5 0 0 0;" +
                        "-fx-padding: 10 0 0 0;"
        );

        Label badge = new Label(occupied ? "OCCUPIED" : "AVAILABLE");
        badge.setStyle(occupied
                ? "-fx-background-color: rgba(176,0,32,0.07); -fx-text-fill: #7F0010;" +
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.12em;" +
                "-fx-padding: 4 10; -fx-background-radius: 20;" +
                "-fx-border-color: rgba(176,0,32,0.20); -fx-border-radius: 20; -fx-border-width: 0.5;"
                : "-fx-background-color: rgba(46,125,50,0.08); -fx-text-fill: #1B5E20;" +
                "-fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.12em;" +
                "-fx-padding: 4 10; -fx-background-radius: 20;" +
                "-fx-border-color: rgba(46,125,50,0.25); -fx-border-radius: 20; -fx-border-width: 0.5;"
        );
        footer.getChildren().add(badge);

        rightPanel.getChildren().addAll(metaRow, pills, footer);
        card.getChildren().addAll(leftPanel, rightPanel);

        card.setOnMouseEntered(e -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #E0DAD0;", "-fx-border-color: #C9A84C;")));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #C9A84C;", "-fx-border-color: #E0DAD0;")));

        return card;
    }

    private HBox metaCell(String label, String value, boolean rightBorder) {
        VBox cell = new VBox(3);
        cell.setAlignment(Pos.CENTER);
        cell.setPadding(new Insets(8, 10, 8, 10));
        cell.setStyle(rightBorder
                ? "-fx-border-color: transparent #E8E3DA transparent transparent; -fx-border-width: 0 0.5 0 0;"
                : "");
        HBox.setHgrow(cell, Priority.ALWAYS);

        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #9B9589; -fx-font-size: 10px; -fx-font-weight: bold; -fx-letter-spacing: 0.12em;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: #0F2160; -fx-font-size: 14px; -fx-font-weight: bold;");
        cell.getChildren().addAll(lbl, val);

        HBox wrapper = new HBox(cell);
        HBox.setHgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    private String ordinal(int n) {
        if (n == 1) return "1st";
        if (n == 2) return "2nd";
        if (n == 3) return "3rd";
        return n + "th";
    }
}