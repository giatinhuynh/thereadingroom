package com.thereadingroom.service.payment;

import com.thereadingroom.utils.auth.PaymentValidator;

import java.util.Optional;

/**
 * PaymentService is responsible for managing payment-related operations.
 * It validates payment details and handles the payment process.
 */
public class PaymentService implements IPaymentService {

    // Singleton instance
    private static PaymentService instance;

    // Private constructor for Singleton pattern
    public PaymentService() {
    }

    /**
     * Singleton - Get the single instance of PaymentService.
     *
     * @return The instance of PaymentService.
     */
    public static synchronized PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }

    /**
     * Validates a payment based on the provided card details.
     *
     * @param cardNumber     The credit card number (should be 16 digits).
     * @param cardHolderName The name on the credit card.
     * @param expiryDate     The expiry date in the MM/YY format.
     * @param cvv            The CVV code (should be 3 digits).
     * @return true if the payment details are valid; false otherwise.
     */
    public boolean validatePayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        return PaymentValidator.validateCardNumber(cardNumber) == null &&
                PaymentValidator.validateExpiryDate(expiryDate) == null &&
                PaymentValidator.validateCVV(cvv) == null;
    }

    /**
     * Processes the payment for the order.
     * If the payment is valid, a unique order reference is generated.
     *
     * @param cardNumber     The credit card number (should be 16 digits).
     * @param cardHolderName The name on the credit card.
     * @param expiryDate     The expiry date in MM/YY format.
     * @param cvv            The CVV code (should be 3 digits).
     * @return An Optional containing a unique order reference if payment succeeds; otherwise, an empty Optional.
     */
    public Optional<String> processPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        if (validatePayment(cardNumber, cardHolderName, expiryDate, cvv)) {
            // Simulate successful payment by generating a unique order reference
            String orderReference = "ORDER-" + System.currentTimeMillis();
            System.out.println("Payment processed successfully. Order Reference: " + orderReference);
            return Optional.of(orderReference);  // Return the generated order reference
        } else {
            System.out.println("Payment validation failed.");
            return Optional.empty();  // Return empty if validation fails
        }
    }
}
