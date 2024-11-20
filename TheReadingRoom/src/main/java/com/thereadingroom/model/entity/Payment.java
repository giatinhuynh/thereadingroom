package com.thereadingroom.model.entity;

import com.thereadingroom.utils.auth.PaymentValidator;

/**
 * Represents a payment, including card details like card number,
 * cardholder name, expiry date, and CVV.
 * This class also performs validation for card details using the PaymentValidator.
 */
public class Payment {

    private String cardNumber;       // The card number of the payment method
    private String cardHolderName;   // The name of the cardholder
    private String expiryDate;       // The expiry date of the card (MM/YY format)
    private String cvv;              // The CVV (Card Verification Value) code

    /**
     * Constructor for creating a Payment instance with validation.
     *
     * @param cardNumber     The card number for the payment
     * @param cardHolderName The name of the cardholder
     * @param expiryDate     The card's expiry date in MM/YY format
     * @param cvv            The CVV code (usually 3-4 digits)
     * @throws IllegalArgumentException if any payment validation fails
     */
    public Payment(String cardNumber, String cardHolderName, String expiryDate, String cvv) {
        validatePayment(cardNumber, expiryDate, cvv);  // Validate card details before setting
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    /**
     * Validates the payment details using PaymentValidator.
     *
     * @param cardNumber The card number to validate
     * @param expiryDate The expiry date to validate
     * @param cvv        The CVV to validate
     * @throws IllegalArgumentException if any validation fails
     */
    private void validatePayment(String cardNumber, String expiryDate, String cvv) {
        // Validate the card number
        String cardNumberError = PaymentValidator.validateCardNumber(cardNumber);
        if (cardNumberError != null) {
            throw new IllegalArgumentException(cardNumberError);  // Throw exception if validation fails
        }

        // Validate the expiry date
        String expiryDateError = PaymentValidator.validateExpiryDate(expiryDate);
        if (expiryDateError != null) {
            throw new IllegalArgumentException(expiryDateError);  // Throw exception if validation fails
        }

        // Validate the CVV
        String cvvError = PaymentValidator.validateCVV(cvv);
        if (cvvError != null) {
            throw new IllegalArgumentException(cvvError);  // Throw exception if validation fails
        }
    }
}
