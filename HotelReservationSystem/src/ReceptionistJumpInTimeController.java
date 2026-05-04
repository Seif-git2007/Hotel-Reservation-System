import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class ReceptionistJumpInTimeController implements SessionController {

    @FXML private ReceptionistSidebarController sidebarController;

    @FXML private VBox       stage;
    @FXML private Pane       pulseDot;
    @FXML private Pane       scanline;
    @FXML private Label      lblArrow;

    @FXML private Label      lblCurrentDate;
    @FXML private Label      lblCurrentMeta;
    @FXML private DatePicker datePicker;
    @FXML private Label      lblTargetMeta;
    @FXML private Label      lblDelta;
    @FXML private Label      lblError;
    @FXML private Button     btnExecute;
    @FXML private Label      lblUplink;

    private AppSession session;

    private Timeline pulseAnim;
    private Timeline scanlineAnim;
    private Timeline arrowGlow;

    @Override
    public void initSession(AppSession session) {
        this.session = session;

        if (sidebarController != null) {
            sidebarController.initSession(session);
            if (sidebarController.btnJumpInTime != null) {
                sidebarController.btnJumpInTime.getStyleClass().add("sidebar-nav-btn-active");
            }
        }

        renderCurrent();

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateAndUpdate(newVal);
        });

        startPulse();
        startScanline();
        startArrowGlow();
    }

    private void renderCurrent() {
        LocalDate today = JumpInTime.now;
        lblCurrentDate.setText(formatConsole(today));
        lblCurrentMeta.setText(buildMeta(today));
    }

    private String formatConsole(LocalDate d) {
        return d.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    private String buildMeta(LocalDate d) {
        String day = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toUpperCase();
        int week = d.get(WeekFields.ISO.weekOfWeekBasedYear());
        int quarter = (d.getMonthValue() - 1) / 3 + 1;
        return day + " / WEEK " + week + " / Q" + quarter;
    }

    private void validateAndUpdate(LocalDate target) {
        if (target == null) {
            hideError();
            lblTargetMeta.setText("SELECT TARGET DATE");
            lblDelta.setText("--");
            return;
        }

        LocalDate today = JumpInTime.now;

        if (target.isBefore(today)) {
            showError("ERR_TEMPORAL_CONFLICT // CANNOT JUMP TO PAST");
            lblTargetMeta.setText("INVALID COORDINATES");
            lblDelta.setText("--");
            return;
        }
        if (target.isEqual(today)) {
            showError("ERR_TEMPORAL_CONFLICT // ALREADY AT TARGET DATE");
            lblTargetMeta.setText("INVALID COORDINATES");
            lblDelta.setText("--");
            return;
        }

        hideError();
        long days = ChronoUnit.DAYS.between(today, target);
        lblTargetMeta.setText("+" + days + " DAY" + (days == 1 ? "" : "S") + " FORWARD");
        lblDelta.setText("+" + days + " DAYS / " + (days * 24) + " HOURS");
    }

    private void showError(String msg) {
        boolean wasHidden = !lblError.isVisible();
        lblError.setText("⚠ " + msg);
        lblError.setVisible(true);
        lblError.setManaged(true);
        btnExecute.setDisable(true);
        if (wasHidden) {
            shakeStage();
        }
    }

    private void hideError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
        btnExecute.setDisable(false);
    }

    private void shakeStage() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), stage);
        shake.setFromX(0);
        shake.setByX(8);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(e -> stage.setTranslateX(0));
        shake.play();
    }

    private void startPulse() {
        pulseAnim = new Timeline(
                new KeyFrame(Duration.ZERO,        new KeyValue(pulseDot.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(600), new KeyValue(pulseDot.opacityProperty(), 0.25)),
                new KeyFrame(Duration.millis(1200),new KeyValue(pulseDot.opacityProperty(), 1.0))
        );
        pulseAnim.setCycleCount(Animation.INDEFINITE);
        pulseAnim.play();
    }

    private void startScanline() {
        scanlineAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(scanline.translateYProperty(), 0)),
                new KeyFrame(Duration.seconds(4),
                        new KeyValue(scanline.translateYProperty(), 580))
        );
        scanlineAnim.setCycleCount(Animation.INDEFINITE);
        scanlineAnim.play();
    }

    private void startArrowGlow() {
        arrowGlow = new Timeline(
                new KeyFrame(Duration.ZERO,        new KeyValue(lblArrow.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(800), new KeyValue(lblArrow.opacityProperty(), 0.4)),
                new KeyFrame(Duration.millis(1600),new KeyValue(lblArrow.opacityProperty(), 1.0))
        );
        arrowGlow.setCycleCount(Animation.INDEFINITE);
        arrowGlow.play();
    }

    @FXML
    private void handleJump(ActionEvent event) {
        LocalDate target = datePicker.getValue();
        if (target == null) {
            showError("ERR_NULL_TARGET // NO COORDINATES SET");
            return;
        }
        LocalDate today = JumpInTime.now;
        if (!target.isAfter(today)) {
            showError("ERR_TEMPORAL_CONFLICT // CANNOT JUMP BACKWARDS");
            return;
        }

        playJumpAnimation(target);
    }

    private void playJumpAnimation(LocalDate target) {
        btnExecute.setDisable(true);
        datePicker.setDisable(true);
        lblUplink.setText("UPLINK: JUMPING...");

        FadeTransition flash = new FadeTransition(Duration.millis(160), stage);
        flash.setFromValue(1.0);
        flash.setToValue(0.15);
        flash.setAutoReverse(true);
        flash.setCycleCount(2);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(320), stage);
        zoom.setFromX(1.0); zoom.setFromY(1.0);
        zoom.setToX(1.04);  zoom.setToY(1.04);
        zoom.setAutoReverse(true);
        zoom.setCycleCount(2);

        ParallelTransition combo = new ParallelTransition(flash, zoom);

        combo.setOnFinished(e -> {
            JumpInTime.now = target;
            EventBus.fire(EventBus.Event.RESERVATION_CHANGED);

            renderCurrent();
            datePicker.setValue(null);
            datePicker.setDisable(false);
            btnExecute.setDisable(false);
            lblTargetMeta.setText("JUMP COMPLETE — AWAITING NEXT TARGET");
            lblDelta.setText("--");
            lblUplink.setText("UPLINK: STABLE");
        });

        combo.play();
    }
}
