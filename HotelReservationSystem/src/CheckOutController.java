import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.temporal.ChronoUnit;

public class CheckOutController implements SessionController {

    @FXML private GuestSidebarController sidebarController;
    private final Runnable refreshListener = this::refresh;

    @FXML private VBox errorPane, mainContent, visaForm, invoiceItems;
    @FXML private VBox balancePane, cashPane;

    @FXML private Label errorLabel, lblGuestName, lblTotal;
    @FXML private Label lblInvoiceNumber, lblDate, lblBalanceAmount;

    @FXML private RadioButton rdoCredit, rdoBalance, rdoCash;
    private final ToggleGroup paymentGroup = new ToggleGroup();

    @FXML private TextField     txtHolderName, txtCardNumber, txtExpiry;
    @FXML private PasswordField txtCVV;

    private AppSession session;
    private Invoice    currentInvoice;
    private VisaCard   validatedCard = null;

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        if (sidebarController != null) {
            sidebarController.initSession(session);
            sidebarController.btnCheckOut.getStyleClass().add("sidebar-nav-btn-active");
        }

        rdoCredit.setToggleGroup(paymentGroup);
        rdoBalance.setToggleGroup(paymentGroup);
        rdoCash.setToggleGroup(paymentGroup);

        paymentGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            hideAllPaymentPanes();
            validatedCard = null;
            if      (newVal == rdoCredit)  {
                visaForm.setVisible(true);    visaForm.setManaged(true);
            } else if (newVal == rdoBalance) {
                balancePane.setVisible(true); balancePane.setManaged(true);
                lblBalanceAmount.setText(String.format("$%.2f", session.getCurrentGuest().getBalance()));
            } else if (newVal == rdoCash) {
                cashPane.setVisible(true);    cashPane.setManaged(true);
            }
        });

        refresh();
        EventBus.subscribe(EventBus.Event.TIME_JUMPED, refreshListener);

        mainContent.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                EventBus.unsubscribe(EventBus.Event.TIME_JUMPED, refreshListener);

            }
        });
    }
    public void refresh() {
        errorPane.setVisible(false);
        errorPane.setManaged(false);
        try {
            if(session.getDueInvoice()!=null){
                currentInvoice=session.getDueInvoice();
            }else {
                currentInvoice = session.getCurrentGuest().checkOut();
            }
            showInvoice();
        } catch (InvalidInputException e) {
            showError(e.getMessage());
        }
    }
    private void showInvoice() {
        mainContent.setVisible(true);
        mainContent.setManaged(true);

        Guest guest = session.getCurrentGuest();
        lblGuestName.setText(guest.getDisplayname());
        lblDate.setText(JumpInTime.now.toString());
        lblInvoiceNumber.setText("INV-" + String.valueOf(System.currentTimeMillis()).substring(8));
        lblTotal.setText(String.format("$%.2f", currentInvoice.getTotal()));

        invoiceItems.getChildren().clear();
        for (Reservation r : currentInvoice.getReservation()) {
            invoiceItems.getChildren().add(buildReservationBlock(r));
        }
    }

    private VBox buildReservationBlock(Reservation r) {
        long nights = ChronoUnit.DAYS.between(r.getCheckInDate(), r.getCheckOutDate());
        if (nights == 0) nights = 1;

        double basePrice    = r.getRoom().getType().getBasePrice();
        double amenityTotal = 0;
        for (Amenity a : r.getRoom().getAmenities()) {
            amenityTotal += a.getPrice();
        }

        double normalCharge = nights * basePrice;

        long daysLate = ChronoUnit.DAYS.between(r.getCheckOutDate(), JumpInTime.now);
        boolean isLate = daysLate > 0;
        double lateFee = isLate ? (normalCharge + amenityTotal) * 0.20 : 0;
        double lineTotal = normalCharge + amenityTotal + lateFee;

        VBox block = new VBox(8);
        block.getStyleClass().add("invoice-reservation-block");

        Label header = new Label(String.format("Room %d  ·  %s",
                r.getRoom().getRoomNumber(), r.getRoom().getType().getSize()));
        header.getStyleClass().add("invoice-reservation-header");

        Label dates = new Label(String.format("Booked: %s  →  %s",
                r.getCheckInDate(), r.getCheckOutDate()));
        dates.getStyleClass().add("invoice-reservation-dates");

        block.getChildren().addAll(header, dates);

        block.getChildren().add(buildLineRow(
                String.format("Room rate (%d night%s × $%.2f)",
                        nights, nights == 1 ? "" : "s", basePrice),
                String.format("$%.2f", normalCharge), false, false));

        if (amenityTotal > 0) {
            block.getChildren().add(buildLineRow(
                    "Amenities", String.format("$%.2f", amenityTotal), false, false));
        }

        if (isLate) {
            Label lateNotice = new Label(String.format(
                    "LATE PAYMENT: Due on %s — %d day%s overdue",
                    r.getCheckOutDate(),
                    daysLate, daysLate == 1 ? "" : "s"));
            lateNotice.getStyleClass().add("invoice-late-notice");
            lateNotice.setWrapText(true);
            block.getChildren().add(lateNotice);

            block.getChildren().add(buildLineRow(
                    "Late payment fee (20% of total)",
                    String.format("$%.2f", lateFee), true, false));
        }

        Pane subDivider = new Pane();
        subDivider.getStyleClass().add("invoice-sub-divider");
        subDivider.setPrefHeight(1);
        block.getChildren().add(subDivider);

        block.getChildren().add(buildLineRow(
                "Subtotal", String.format("$%.2f", lineTotal), false, true));

        return block;
    }

    private HBox buildLineRow(String label, String amount, boolean late, boolean bold) {
        Label lblName = new Label(label);
        lblName.getStyleClass().add(late ? "invoice-line-label-late" : "invoice-line-label");
        if (bold) {
            lblName.getStyleClass().add("invoice-line-bold");
        }

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label lblAmount = new Label(amount);
        lblAmount.getStyleClass().add(late ? "invoice-line-amount-late" : "invoice-line-amount");
        if (bold) {
            lblAmount.getStyleClass().add("invoice-line-bold");
        }

        HBox row = new HBox(lblName, spacer, lblAmount);
        row.getStyleClass().add("invoice-line-row");
        return row;
    }

    private void showError(String message) {
        errorPane.setVisible(true);    errorPane.setManaged(true);
        errorLabel.setText(message);
        mainContent.setVisible(false); mainContent.setManaged(false);
    }

    private void hideAllPaymentPanes() {
        visaForm.setVisible(false);    visaForm.setManaged(false);
        balancePane.setVisible(false); balancePane.setManaged(false);
        cashPane.setVisible(false);    cashPane.setManaged(false);
    }

    @FXML
    private void handleConfirmVisa() {
        try {
            Authenticator.validateVisaCard(
                    txtHolderName.getText(), txtCardNumber.getText(),
                    txtExpiry.getText(), txtCVV.getText());

            validatedCard = new VisaCard();
            validatedCard.setCardHolderName(txtHolderName.getText());
            validatedCard.setCardNumber(txtCardNumber.getText());
            validatedCard.setExpiryDate(txtExpiry.getText());
            validatedCard.setCvv(txtCVV.getText());

            new Alert(Alert.AlertType.INFORMATION,
                    "Card verified. You may now complete check-out.").show();
        } catch (InvalidInputException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            validatedCard = null;
        }
    }

    @FXML
    private void handleFinalPayment(ActionEvent event) {
        try {
            if (paymentGroup.getSelectedToggle() == null) {
                throw new InvalidInputException("Please select a payment method.");
            }

            Invoice.paymentMethod method;
            if (rdoCredit.isSelected()) {
                if (validatedCard == null) {
                    throw new InvalidInputException("Please verify your card details first.");
                }
                method = Invoice.paymentMethod.CREDIT;
            } else if (rdoBalance.isSelected()) {
                method = Invoice.paymentMethod.ONLINE;
            } else {
                method = Invoice.paymentMethod.CASH;
            }

            session.getCurrentGuest().pay(currentInvoice, method, validatedCard);
            session.getCurrentGuest().setOverDue(false);
            session.setDueInvoice(null);
            new Alert(Alert.AlertType.INFORMATION,
                    "Check-out complete. Thank you for staying with us.").showAndWait();
            MainController.navigate(event, "Guest_Dashboard.fxml");

        } catch (InvalidInputException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}