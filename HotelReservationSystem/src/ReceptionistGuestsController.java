import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.stream.Collectors;

public class ReceptionistGuestsController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;
    @FXML private TextField                     searchField;
    @FXML private VBox                          guestList;
    @FXML private Label                         lblTotalGuests;
    @FXML private Label                         lblTotalBalance;

    private AppSession session;

    @Override
    public void initSession(AppSession session) {
        this.session = session;
        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.setActive(sidebarController.btnGuests);
        }
        searchField.textProperty().addListener((obs, o, n) -> renderList());
        updateStats();
        renderList();
    }

    private void updateStats() {
        List<Guest> guests = HotelDataBase.filterGuest();
        lblTotalGuests.setText("Total Guests: " + guests.size());
        double totalBalance = guests.stream().mapToDouble(Guest::getBalance).sum();
        lblTotalBalance.setText(String.format("Combined Balance: $%.0f", totalBalance));
    }

    private void renderList() {
        guestList.getChildren().clear();
        String search = searchField.getText().trim().toLowerCase();
        List<Guest> filtered = HotelDataBase.filterGuest().stream()
                .filter(g -> search.isEmpty()
                        || g.getDisplayname().toLowerCase().contains(search)
                        || g.getUsername().toLowerCase().contains(search))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            Label empty = new Label("No guests found.");
            empty.setStyle("-fx-text-fill: #6B6B6B; -fx-font-size: 14px; -fx-padding: 40;");
            empty.setMaxWidth(Double.MAX_VALUE);
            empty.setAlignment(Pos.CENTER);
            guestList.getChildren().add(empty);
            return;
        }
        for (Guest g : filtered) guestList.getChildren().add(buildCard(g));
    }

    private HBox buildCard(Guest guest) {
        HBox card = new HBox(0);
        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #E0DAD0;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 0.5;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(15,33,96,0.06), 8, 0, 0, 2);"
        );

        VBox avatarPanel = new VBox(0);
        avatarPanel.setAlignment(Pos.CENTER);
        avatarPanel.setMinWidth(80);
        avatarPanel.setPrefWidth(80);
        avatarPanel.setStyle(
                "-fx-background-color: #0F2160;" +
                        "-fx-background-radius: 10 0 0 10;"
        );

        Pane topAccent = new Pane();
        topAccent.setPrefHeight(4);
        topAccent.setStyle("-fx-background-color: #C9A84C; -fx-background-radius: 10 0 0 0;");

        Label avatarLabel = new Label(guest.getDisplayname().substring(0, 1).toUpperCase());
        avatarLabel.setStyle(
                "-fx-text-fill: #C9A84C;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 26px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-style: italic;" +
                        "-fx-background-color: rgba(201,168,76,0.15);" +
                        "-fx-background-radius: 50;" +
                        "-fx-min-width: 50;" +
                        "-fx-min-height: 50;" +
                        "-fx-alignment: center;"
        );
        avatarLabel.setMinSize(50, 50);
        avatarLabel.setAlignment(Pos.CENTER);

        Label genderLabel = new Label(guest.getGender() == User.Gender.MALE ? "♂" : "♀");
        genderLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.40);" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 4 0 0 0;"
        );

        VBox avatarContent = new VBox(4, avatarLabel, genderLabel);
        avatarContent.setAlignment(Pos.CENTER);
        VBox.setVgrow(avatarContent, Priority.ALWAYS);
        avatarPanel.getChildren().addAll(topAccent, avatarContent);

        VBox content = new VBox(0);
        HBox.setHgrow(content, Priority.ALWAYS);

        HBox contentHeader = new HBox(0);
        contentHeader.setAlignment(Pos.CENTER_LEFT);
        contentHeader.setStyle("-fx-padding: 12 16 8 16;");

        VBox nameBlock = new VBox(2);
        HBox.setHgrow(nameBlock, Priority.ALWAYS);

        Label displayName = new Label(guest.getDisplayname());
        displayName.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-font-family: 'Georgia', serif;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );
        Label userName = new Label("@" + guest.getUsername());
        userName.setStyle("-fx-text-fill: #9B9589; -fx-font-size: 11px;");
        nameBlock.getChildren().addAll(displayName, userName);

        Label balanceChip = new Label(String.format("$%.0f", guest.getBalance()));
        balanceChip.setStyle(
                "-fx-text-fill: #0F2160;" +
                        "-fx-background-color: #C9A84C;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 12;" +
                        "-fx-background-radius: 20;"
        );

        contentHeader.getChildren().addAll(nameBlock, balanceChip);

        Pane divider = new Pane();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: #F0EDE4;");

        HBox details = new HBox(0);
        details.setStyle(
                "-fx-padding: 10 16 12 16;" +
                        "-fx-background-color: #FDFAF4;" +
                        "-fx-background-radius: 0 0 10 0;"
        );
        details.getChildren().addAll(
                detailCell("📅", "DOB", guest.getDateOfBirth().toString(), true),
                detailCell("📍", "Address", guest.getAddress(), true),
                detailCell("🛏", "Preference",
                        "Floor " + guest.getPrefered().getFloor() +
                                "  •  " + guest.getPrefered().getView() + " view", false)
        );

        long totalRes = HotelDataBase.reservations.stream()
                .filter(r -> r.getGuest() == guest).count();
        long activeRes = HotelDataBase.reservations.stream()
                .filter(r -> r.getGuest() == guest &&
                        (r.getStatus() == Reservation.Status.PENDING ||
                                r.getStatus() == Reservation.Status.CONFIRMED)).count();

        Label resChip = new Label(
                totalRes + (totalRes == 1 ? " reservation" : " reservations") +
                        (activeRes > 0 ? "  •  " + activeRes + " active" : "")
        );
        resChip.setStyle(
                "-fx-text-fill: " + (activeRes > 0 ? "#C9A84C" : "#9B9589") + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.04em;" +
                        "-fx-padding: 0 16 10 16;" +
                        "-fx-background-color: #FDFAF4;" +
                        "-fx-background-radius: 0 0 10 0;"
        );

        content.getChildren().addAll(contentHeader, divider, details, resChip);
        card.getChildren().addAll(avatarPanel, content);

        card.setOnMouseEntered(e -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #E0DAD0;", "-fx-border-color: #C9A84C;")
                .replace("rgba(15,33,96,0.06), 8", "rgba(15,33,96,0.12), 12")));
        card.setOnMouseExited(e -> card.setStyle(card.getStyle()
                .replace("-fx-border-color: #C9A84C;", "-fx-border-color: #E0DAD0;")
                .replace("rgba(15,33,96,0.12), 12", "rgba(15,33,96,0.06), 8")));

        return card;
    }

    private VBox detailCell(String icon, String label, String value, boolean rightBorder) {
        VBox cell = new VBox(3);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.setStyle(
                "-fx-padding: 0 16 0 0;" +
                        (rightBorder
                                ? "-fx-border-color: transparent #E0DAD0 transparent transparent;" +
                                "-fx-border-width: 0 1 0 0; -fx-padding: 0 16 0 0;"
                                : "")
        );
        HBox.setHgrow(cell, Priority.ALWAYS);
        HBox.setMargin(cell, new javafx.geometry.Insets(0, rightBorder ? 16 : 0, 0, 0));

        Label lbl = new Label(icon + "  " + label.toUpperCase());
        lbl.setStyle(
                "-fx-text-fill: #9B9589;" +
                        "-fx-font-size: 9px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-letter-spacing: 0.12em;"
        );
        Label val = new Label(value);
        val.setStyle(
                "-fx-text-fill: #2C2C2C;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;"
        );
        val.setWrapText(true);
        cell.getChildren().addAll(lbl, val);
        return cell;
    }
}