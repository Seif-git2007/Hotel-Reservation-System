import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CheckOutController implements SessionController {

    @FXML private GuestSidebarController sidebarController;

    @FXML private VBox errorPane,mainContent, visaForm, invoiceItems;
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

        try {
            currentInvoice = session.getCurrentGuest().checkOut();
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
            long nights = java.time.temporal.ChronoUnit.DAYS.between(
                    r.getCheckInDate(), r.getCheckOutDate());
            if (nights == 0) nights = 1;
            double lineTotal = r.getRoom().calcTotal(r.getCheckInDate(), r.getCheckOutDate());

            GridPane row = new GridPane();
            row.getStyleClass().add("invoice-item-row");

            ColumnConstraints c1 = new ColumnConstraints();
            c1.setHgrow(Priority.ALWAYS);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setMinWidth(80);
            ColumnConstraints c3 = new ColumnConstraints();
            c3.setMinWidth(90);
            c3.setHalignment(javafx.geometry.HPos.RIGHT);
            row.getColumnConstraints().addAll(c1, c2, c3);

            Label desc = new Label(String.format("Room %d — %s\n%s  →  %s",
                    r.getRoom().getRoomNumber(), r.getRoom().getType().getSize(),
                    r.getCheckInDate(), r.getCheckOutDate()));
            desc.getStyleClass().add("invoice-value");
            desc.setWrapText(true);

            Label nightsLbl = new Label(nights + (nights == 1 ? " night" : " nights"));
            nightsLbl.getStyleClass().add("invoice-value");

            Label amtLbl = new Label(String.format("$%.2f", lineTotal));
            amtLbl.getStyleClass().add("invoice-value");
            amtLbl.setStyle("-fx-font-weight: bold;");

            row.add(desc,      0, 0);
            row.add(nightsLbl, 1, 0);
            row.add(amtLbl,    2, 0);
            invoiceItems.getChildren().add(row);
        }
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
            if (paymentGroup.getSelectedToggle() == null)
                throw new InvalidInputException("Please select a payment method.");

            Invoice.paymentMethod method;
            if (rdoCredit.isSelected()) {
                if (validatedCard == null)
                    throw new InvalidInputException("Please verify your card details first.");
                method = Invoice.paymentMethod.CREDIT;
            } else if (rdoBalance.isSelected()) {
                method = Invoice.paymentMethod.ONLINE;
            } else {
                method = Invoice.paymentMethod.CASH;
            }

            session.getCurrentGuest().pay(currentInvoice, method, validatedCard);

            new Alert(Alert.AlertType.INFORMATION,
                    "Check-out complete. Thank you for staying with us.").showAndWait();
            MainController.navigate(event, "Guest_Dashboard.fxml");

        } catch (InvalidInputException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }
}