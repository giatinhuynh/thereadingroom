package com.thereadingroom.service.payment;

import java.util.Optional;

/**
 * IPaymentService defines the contract for payment processing services.
 * It includes methods for validating payment details and processing payments.
 */
public interface IPaymentService {

    /**
     * Validates the payment details provided by the user.
     *
     * @param cardNumber     The credit card number (should be 16 digits).
     * @param cardHolderName The name on the credit card.
     * @param expiryDate     The expiry date in MM/YY format.
     * @param cvv            The CVV code (should be 3 digits).
     * @return true if the payment details are valid; false otherwise.
     */
    boolean validatePayment(String cardNumber, String cardHolderName, String expiryDate, String cvv);

    /**
     * Processes the payment using the provided card details.
     * If the payment is valid, it returns a unique order reference.
     *
     * @param cardNumber     The credit card number (should be 16 digits).
     * @param cardHolderName The name on the credit card.
     * @param expiryDate     The expiry date in MM/YY format.
     * @param cvv            The CVV code (should be 3 digits).
     * @return An Optional containing the unique order reference if payment succeeds; otherwise, an empty Optional.
     */
    Optional<String> processPayment(String cardNumber, String cardHolderName, String expiryDate, String cvv);
}
