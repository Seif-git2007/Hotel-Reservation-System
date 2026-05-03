import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdminReservationsController implements SessionController {

    @FXML private AdminSidebarController sidebarController;
    @FXML private ComboBox<String>       filterCombo;
    @FXML private VBox                   reservationList;
    @FXML private Label                  lblTotal;
    @FXML private Label                  lblPending;
    @FXML private Label                  lblConfirmed;
    @FXML private Label                  lblCompleted;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnReservations);
        }

        filterCombo.getItems().addAll(
                "All", "Pending", "Confirmed", "Awaiting Confirmation", "Completed", "Cancelled"
        );
        filterCombo.setValue("All");
        filterCombo.setOnAction(e -> renderList());

        updateStats();
        renderList();
    }

    private void updateStats() {
        List<Reservation> all = new ArrayList<>(HotelDataBase.reservations);
        lblTotal.setText("Total: " + all.size());
        lblPending.setText("⏳ Pending: " + all.stream()
                .filter(r -> r.getStatus() == Reservation.Status.PENDING).count());
        lblConfirmed.setText("✔ Confirmed: " + all.stream()
                .filter(r -> r.getStatus() == Reservation.Status.CONFIRMED).count());
        lblCompleted.setText("Completed: " + all.stream()
                .filter(r -> r.getStatus() == Reservation.Status.COMPLETED).count());
    }

    private void renderList() {
        reservationList.getChildren().clear();

        String filter = filterCombo.getValue();
        List<Reservation> filtered = new ArrayList<>(HotelDataBase.reservations);

        if (filter != null && !filter.equals("All")) {
            filtered = filtered.stream()
                    .filter(r -> r.getStatus().toString()
                            .equalsIgnoreCase(filter.replace(" ", "_")))
                    .collect(Collectors.toList());
        }

        if (filtered.isEmpty()) {
            Label empty = new Label("No reservations found.");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            reservationList.getChildren().add(empty);
            return;
        }

        for (Reservation res : filtered) {
            reservationList.getChildren().add(buildCard(res));
        }
    }

    private VBox buildCard(Reservation res) {
        VBox card = new VBox(0);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E0DAD0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15,33,96,0.06), 8, 0, 0, 2);"
        );

        // ── Navy header ───────────────────────────────────────────────────
        HBox header = new HBox(0);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-background-radius: 10 10 0 0;" +
                        "-fx-padding: 10 16 10 16;"
        );

        // Gold top accent
        Pane accent = new Pane();
        accent.setPrefWidth(4);
        accent.setPrefHeight(32);
        accent.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 2;");
        HBox.setMargin(accent, new javafx.geometry.Insets(0, 12, 0, 0));

        // Guest name + room
        VBox headerLeft = new VBox(2);
        Label guestLabel = new Label(res.getGuest().getDisplayname());
        guestLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );
        Label roomLabel = new Label(
                res.getRoom().getType().getSize() + "  •  Room " + res.getRoom().getRoomNumber()
        );
        roomLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.60);" +
                        "-fx-font-size: 11px;"
        );
        headerLeft.getChildren().addAll(guestLabel, roomLabel);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status badge
        Label statusBadge = new Label(res.getStatus().toString().toUpperCase());
        statusBadge.getStyleClass().addAll(
                "status-badge",
                "status-" + res.getStatus().toString().toLowerCase()
        );

        header.getChildren().addAll(accent, headerLeft, spacer, statusBadge);

        // ── Body ──────────────────────────────────────────────────────────
        HBox body = new HBox(0);
        body.setStyle("-fx-padding: 14 16 14 16;");

        // Dates block
        long nights = ChronoUnit.DAYS.between(res.getCheckInDate(), res.getCheckOutDate());
        if (nights == 0) nights = 1;

        VBox datesBlock = new VBox(6);
        datesBlock.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(datesBlock, Priority.ALWAYS);

        Label dateLabel = new Label(
                "📅  " + res.getCheckInDate() + "  →  " + res.getCheckOutDate() +
                        "  (" + nights + (nights == 1 ? " night" : " nights") + ")"
        );
        dateLabel.setStyle("-fx-text-fill: #2C2C2C; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label detailsLabel = new Label(
                "Floor " + res.getRoom().getFloor() +
                        "  •  Capacity: " + res.getRoom().getType().getCapacity() + " guests" +
                        "  •  " + res.getRoom().getView() + " view"
        );
        detailsLabel.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 12px;");

        String amenities = ViewRoomsController.formatAmenities(res.getRoom().getAmenities());
        Label amenLabel = new Label("Includes: " + amenities);
        amenLabel.setStyle(
                "-fx-text-fill: rgba(107,107,107,0.55);" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-style: italic;"
        );

        datesBlock.getChildren().addAll(dateLabel, detailsLabel, amenLabel);

        // Price block
        double total = nights * res.getRoom().getType().getBasePrice();
        VBox priceBlock = new VBox(2);
        priceBlock.setAlignment(Pos.CENTER_RIGHT);
        priceBlock.setMinWidth(110);

        Label totalLabel = new Label(String.format("$%.0f", total));
        totalLabel.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );
        Label rateLabel = new Label(String.format("$%.0f / night", res.getRoom().getType().getBasePrice()));
        rateLabel.setStyle("-fx-text-fill: #9B9589; -fx-font-size: 10px; -fx-font-style: italic;");

        priceBlock.getChildren().addAll(totalLabel, rateLabel);

        body.getChildren().addAll(datesBlock, priceBlock);

        // ── Special requests (only if present) ───────────────────────────
        if (res.getSpecialRequests() != null && !res.getSpecialRequests().isBlank()) {
            Pane divider = new Pane();
            divider.setPrefHeight(1);
            divider.setStyle("-fx-background-color: #F0EDE4;");

            HBox reqRow = new HBox(6);
            reqRow.setAlignment(Pos.CENTER_LEFT);
            reqRow.setStyle(
                    "-fx-background-color: #FDFAF4;" +
                            "-fx-padding: 8 16 8 16;" +
                            "-fx-background-radius: 0 0 10 10;"
            );
            Label reqIcon = new Label("💬");
            reqIcon.setStyle("-fx-font-size: 11px;");
            Label reqLabel = new Label(res.getSpecialRequests());
            reqLabel.setStyle(
                    "-fx-text-fill: #6B6B6B;" +
                            "-fx-font-size: 11px;" +
                            "-fx-font-style: italic;"
            );
            reqRow.getChildren().addAll(reqIcon, reqLabel);
            card.getChildren().addAll(header, body, divider, reqRow);
        } else {
            card.getChildren().addAll(header, body);
        }

        return card;
    }
}