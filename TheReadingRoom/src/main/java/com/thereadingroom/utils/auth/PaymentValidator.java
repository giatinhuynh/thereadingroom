package com.thereadingroom.utils.auth;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PaymentValidator {

    // Formatter for expiry date string consistently in "MM/yy" format.
    private static final DateTimeFormatter EXPIRY_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    /**
     * Validate the card number.
     * Checks if the card number is non-empty, exactly 16 digits, and numeric.
     *
     * @param cardNumber The card number as a string.
     * @return Error message if invalid, or null if valid.
     */
    public static String validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "Card number cannot be empty.";
        }
        if (!cardNumber.matches("\\d{16}")) {  // Ensures card number has exactly 16 digits
            return "Card number must be 16 digits.";
        }
        return null;  // No error, card number is valid
    }

    /**
     * Validate the expiry date.
     * Ensures the expiry date is in "MM/yy" format and is a future date.
     *
     * @param expiryDate The expiry date as a string in "MM/yy" format.
     * @return Error message if invalid, or null if valid.
     */
    public static String validateExpiryDate(String expiryDate) {
        if (expiryDate == null || expiryDate.isEmpty()) {
            return "Expiry date cannot be empty.";
        }
        try {
            YearMonth expiry = YearMonth.parse(expiryDate, EXPIRY_DATE_FORMAT); // Parse expiry date
            if (expiry.isBefore(YearMonth.now())) {  // Ensure expiry date is in the future
                return "Expiry date must be in the future.";
            }
        } catch (DateTimeParseException e) {
            return "Expiry date format must be MM/yy.";  // Handle incorrect format
        }
        return null;  // No error, expiry date is valid
    }

    /**
     * Validate the CVV.
     * Checks if the CVV is non-empty, exactly 3 digits, and numeric.
     *
     * @param cvv The CVV as a string.
     * @return Error message if invalid, or null if valid.
     */
    public static String validateCVV(String cvv) {
        if (cvv == null || cvv.isEmpty()) {
            return "CVV cannot be empty.";
        }
        if (!cvv.matches("\\d{3}")) {  // Ensures CVV has exactly 3 digits
            return "CVV must be 3 digits.";
        }
        return null;  // No error, CVV is valid
    }
}
