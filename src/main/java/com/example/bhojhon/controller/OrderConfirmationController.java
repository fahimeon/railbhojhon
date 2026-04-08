package com.example.bhojhon.controller;

import com.example.bhojhon.model.CartItem;
import com.example.bhojhon.model.Order;
import com.example.bhojhon.util.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.stage.FileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Controller for the Order Confirmation screen.
 * Displays order summary after successful order.
 */
public class OrderConfirmationController extends BaseController {

    @FXML
    private Label orderIdLabel;

    @FXML
    private Label trainInfoLabel;

    @FXML
    private Label stationInfoLabel;

    @FXML
    private Label passengerInfoLabel;

    @FXML
    private Label deliveryNoteLabel;

    @FXML
    private TextArea orderDetailsArea;

    @FXML
    private Label totalLabel;

    @FXML
    private Label timerLabel;
    @FXML
    private Region step2Dot, step3Dot, step4Dot;
    @FXML
    private Region line1, line2, line3;
    @FXML
    private Label label2, label3, label4;

    private int secondsRemaining = 120; // 2 minutes
    private Timeline timeline;

    private Order order;

    @Override
    public void initialize() {
        // Generate order ID
        String orderId = generateOrderId();

        // Get order details from cart
        com.example.bhojhon.data.CartManager cartManager = com.example.bhojhon.data.CartManager.getInstance();

        order = new Order(
                orderId,
                cartManager.getCartItems(),
                cartManager.getSelectedTrainNumber(),
                cartManager.getSelectedStationName(),
                cartManager.getPassengerName(),
                cartManager.getPhoneNumber(),
                cartManager.getSeatNumber(),
                cartManager.getDeliveryNote());

        // Display order information
        orderIdLabel.setText("Order #" + orderId);
        trainInfoLabel.setText("Train: " + order.getTrainNumber());
        stationInfoLabel.setText("Delivery Station: " + order.getStationName());
        passengerInfoLabel.setText(String.format("Passenger: %s (Seat: %s)",
                order.getPassengerName(), order.getSeatNumber()));

        if (order.getDeliveryNote() != null && !order.getDeliveryNote().isEmpty()) {
            deliveryNoteLabel.setText("Note: " + order.getDeliveryNote());
            deliveryNoteLabel.setVisible(true);
        } else {
            deliveryNoteLabel.setVisible(false);
        }

        totalLabel.setText("Total: " + order.getFormattedTotal());

        // Build order details text
        StringBuilder details = new StringBuilder();
        details.append("Order Date: ").append(order.getFormattedOrderTime()).append("\n\n");
        details.append("Items Ordered:\n");
        details.append("─────────────────────────────────────\n");

        for (CartItem item : order.getItems()) {
            details.append(String.format("• %s\n", item.getFoodItem().getName()));
            details.append(String.format("  Quantity: %d × ৳%.0f = ৳%.0f\n",
                    item.getQuantity(),
                    item.getFoodItem().getPrice(),
                    item.getSubtotal()));
            details.append("\n");
        }

        details.append("─────────────────────────────────────\n");
        details.append(String.format("TOTAL: %s", order.getFormattedTotal()));

        orderDetailsArea.setText(details.toString());
        orderDetailsArea.setEditable(false);

        // Send confirmation email in a background thread to avoid blocking UI
        String userEmail = cartManager.getUserEmail();
        String currentOrderId = orderId;
        String currentOrderDetails = details.toString();

        // Save complete order to database
        saveOrderToDatabase(orderId, order, details.toString());

        // Clear cart after order is confirmed
        cartManager.clearCart();

        // Start order tracking
        startTracking();

        new Thread(() -> {
            try {
                com.example.bhojhon.service.EmailService emailService = new com.example.bhojhon.service.EmailService();
                emailService.sendOrderConfirmation(userEmail, currentOrderId, currentOrderDetails);
            } catch (Exception e) {
                System.err.println("Background email sending error: " + e.getMessage());
            }
        }).start();
    }

    private void startTracking() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondsRemaining--;
            if (secondsRemaining <= 0) {
                secondsRemaining = 0;
                timeline.stop();
            }
            updateTrackingUI();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTrackingUI() {
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        // Update progress stages based on time
        if (secondsRemaining <= 10) { // < 10s: Arrived
            activateStep(step4Dot, label4, line3);
        } else if (secondsRemaining <= 50) { // < 50s: On the way
            activateStep(step3Dot, label3, line2);
        } else if (secondsRemaining <= 90) { // < 90s: Preparing
            activateStep(step2Dot, label2, line1);
        }
    }

    private void activateStep(Region dot, Label label, Region line) {
        dot.getStyleClass().removeAll("step-inactive");
        dot.getStyleClass().add("step-active");
        label.getStyleClass().removeAll("label-inactive");
        label.getStyleClass().add("label-active");
        if (line != null) {
            line.getStyleClass().removeAll("line-inactive");
            line.getStyleClass().add("line-active");
        }
    }

    private void saveOrderToDatabase(String orderId, Order order, String orderDetails) {
        com.example.bhojhon.data.DatabaseHelper db = new com.example.bhojhon.data.DatabaseHelper();
        com.example.bhojhon.data.CartManager cart = com.example.bhojhon.data.CartManager.getInstance();

        // Format order items for storage
        StringBuilder itemsSummary = new StringBuilder();
        for (CartItem item : order.getItems()) {
            itemsSummary.append(item.getFoodItem().getName())
                    .append(" x").append(item.getQuantity())
                    .append(", ");
        }
        if (itemsSummary.length() > 0) {
            itemsSummary.setLength(itemsSummary.length() - 2); // Remove last comma
        }

        db.saveOrder(
                orderId,
                order.getPassengerName(),
                order.getPhoneNumber(),
                cart.getPnr() != null ? cart.getPnr() : "N/A",
                order.getTrainNumber(),
                order.getSeatNumber(),
                cart.getJourneyDate() != null ? cart.getJourneyDate() : "N/A",
                order.getDeliveryNote(),
                order.getStationName(),
                itemsSummary.toString(),
                order.getTotal(),
                order.getFormattedOrderTime());
    }

    /**
     * Handle Print Bill button - Downloads PDF
     */
    @FXML
    private void handlePrintBill() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Bill as PDF");

        // Set default save location to user's Downloads folder
        String userHome = System.getProperty("user.home");
        File downloadsDir = new File(userHome, "Downloads");
        if (downloadsDir.exists()) {
            fileChooser.setInitialDirectory(downloadsDir);
        }

        fileChooser.setInitialFileName(
                "RailKhabar_Bill_" + orderIdLabel.getText().replace("#", "").replace(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(orderIdLabel.getScene().getWindow());

        if (file != null) {
            try {
                generatePDF(file);

                // Show success with option to open file
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Bill Downloaded Successfully!");
                alert.setContentText("Your bill has been saved to:\n" + file.getAbsolutePath() +
                        "\n\nWould you like to open it now?");

                javafx.scene.control.ButtonType openButton = new javafx.scene.control.ButtonType("Open PDF");
                javafx.scene.control.ButtonType closeButton = new javafx.scene.control.ButtonType("Close",
                        javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(openButton, closeButton);

                alert.showAndWait().ifPresent(response -> {
                    if (response == openButton) {
                        try {
                            java.awt.Desktop.getDesktop().open(file);
                        } catch (Exception e) {
                            showAlert("Error", "Could not open PDF: " + e.getMessage());
                        }
                    }
                });

            } catch (IOException e) {
                showAlert("Error", "Failed to save PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate PDF bill
     */
    private void generatePDF(File file) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        float margin = 60;
        float pageWidth = page.getMediaBox().getWidth();
        float yPosition = page.getMediaBox().getHeight() - margin;
        float leading = 18f;

        // ========== HEADER ==========
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
        String headerText = "RAILKHABAR";
        float headerWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(headerText) / 1000 * 22;
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - headerWidth) / 2, yPosition);
        contentStream.showText(headerText);
        contentStream.endText();
        yPosition -= leading * 1.2f;

        contentStream.setFont(PDType1Font.HELVETICA, 12);
        String subHeader = "Order Receipt";
        float subHeaderWidth = PDType1Font.HELVETICA.getStringWidth(subHeader) / 1000 * 12;
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - subHeaderWidth) / 2, yPosition);
        contentStream.showText(subHeader);
        contentStream.endText();
        yPosition -= leading * 1.5f;

        // Order ID
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        String orderIdText = orderIdLabel.getText();
        float orderIdWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(orderIdText) / 1000 * 14;
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - orderIdWidth) / 2, yPosition);
        contentStream.showText(orderIdText);
        contentStream.endText();
        yPosition -= leading * 1.8f;

        // Double line separator
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(pageWidth - margin, yPosition);
        contentStream.stroke();
        yPosition -= 3;
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(pageWidth - margin, yPosition);
        contentStream.stroke();
        yPosition -= leading * 1.5f;

        // ========== PASSENGER DETAILS ==========
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 13);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("PASSENGER DETAILS");
        contentStream.endText();
        yPosition -= leading * 1.3f;

        contentStream.setFont(PDType1Font.HELVETICA, 11);
        String[][] passengerDetails = {
                { "Name:", order.getPassengerName() },
                { "Phone:", order.getPhoneNumber() },
                { "Seat Number:", order.getSeatNumber() }
        };

        for (String[] detail : passengerDetails) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
            contentStream.showText(detail[0]);
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.showText("  " + detail[1]);
            contentStream.endText();
            yPosition -= leading * 0.9f;
        }
        yPosition -= leading * 0.5f;

        // ========== JOURNEY DETAILS ==========
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 13);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("JOURNEY DETAILS");
        contentStream.endText();
        yPosition -= leading * 1.3f;

        contentStream.setFont(PDType1Font.HELVETICA, 11);
        String[][] journeyDetails = {
                { "Train Number:", order.getTrainNumber() },
                { "Delivery Station:", order.getStationName() }
        };

        for (String[] detail : journeyDetails) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
            contentStream.showText(detail[0]);
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.showText("  " + detail[1]);
            contentStream.endText();
            yPosition -= leading * 0.9f;
        }

        if (order.getDeliveryNote() != null && !order.getDeliveryNote().isEmpty()) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
            contentStream.showText("Delivery Note:");
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 11);
            contentStream.showText("  " + order.getDeliveryNote());
            contentStream.endText();
            yPosition -= leading * 0.9f;
        }
        yPosition -= leading * 0.8f;

        // Separator
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(pageWidth - margin, yPosition);
        contentStream.stroke();
        yPosition -= leading * 1.5f;

        // ========== ORDER ITEMS ==========
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 13);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("ORDER ITEMS");
        contentStream.endText();
        yPosition -= leading * 1.5f;

        // Table header
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin + 10, yPosition);
        contentStream.showText("Item");
        contentStream.newLineAtOffset(280, 0);
        contentStream.showText("Qty");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText("Price");
        contentStream.endText();
        yPosition -= leading * 0.8f;

        // Light separator under header
        contentStream.setLineWidth(0.3f);
        contentStream.moveTo(margin + 10, yPosition);
        contentStream.lineTo(pageWidth - margin - 10, yPosition);
        contentStream.stroke();
        yPosition -= leading * 0.8f;

        // Items
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        for (CartItem item : order.getItems()) {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin + 10, yPosition);

            // Item name (truncate if too long)
            String itemName = item.getFoodItem().getName();
            if (itemName.length() > 35) {
                itemName = itemName.substring(0, 32) + "...";
            }
            contentStream.showText(itemName);

            // Quantity
            contentStream.newLineAtOffset(280, 0);
            contentStream.showText("x" + item.getQuantity());

            // Price
            contentStream.newLineAtOffset(60, 0);
            contentStream.showText(String.format("Tk %.0f", item.getSubtotal()));
            contentStream.endText();
            yPosition -= leading * 0.9f;
        }
        yPosition -= leading * 0.5f;

        // Separator before total
        contentStream.setLineWidth(1f);
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(pageWidth - margin, yPosition);
        contentStream.stroke();
        yPosition -= leading * 1.2f;

        // ========== TOTAL ==========
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        // Replace Bengali Taka symbol with Tk for PDF compatibility
        String totalText = "TOTAL: " + order.getFormattedTotal().replace("৳", "Tk ");
        float totalWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(totalText) / 1000 * 16;
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - totalWidth) / 2, yPosition);
        contentStream.showText(totalText);
        contentStream.endText();
        yPosition -= leading * 2f;

        // Separator
        contentStream.setLineWidth(0.5f);
        contentStream.moveTo(margin, yPosition);
        contentStream.lineTo(pageWidth - margin, yPosition);
        contentStream.stroke();
        yPosition -= leading * 1.5f;

        // ========== FOOTER ==========
        contentStream.setFont(PDType1Font.HELVETICA, 9);
        contentStream.beginText();
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Order Date: " + order.getFormattedOrderTime());
        contentStream.endText();
        yPosition -= leading * 1.5f;

        // Thank you message
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 11);
        String thankYouText = "Thank you for ordering with RailKhabar!";
        float thankYouWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(thankYouText) / 1000 * 11;
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - thankYouWidth) / 2, yPosition);
        contentStream.showText(thankYouText);
        contentStream.endText();
        yPosition -= leading * 1.2f;

        // Contact info
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        String contactText = "For support, visit www.railkhabar.com";
        float contactWidth = PDType1Font.HELVETICA.getStringWidth(contactText) / 1000 * 8;
        contentStream.beginText();
        contentStream.newLineAtOffset((pageWidth - contactWidth) / 2, yPosition);
        contentStream.showText(contactText);
        contentStream.endText();

        contentStream.close();
        document.save(file);
        document.close();
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Generate a random order ID
     */
    private String generateOrderId() {
        Random random = new Random();
        int randomNum = 10000 + random.nextInt(90000);
        return "BD" + randomNum;
    }

    /**
     * Handle Return to Home button
     */
    @FXML
    private void handleReturnHome() {
        // Clear navigation data
        navigationManager.clearData();
        navigationManager.clearHistory();
        navigateTo("/com/example/bhojhon/main-menu-view.fxml");
    }
}
