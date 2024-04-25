package br.com.vs.currency.converter.utils;

public class Messages {

    Messages() { }

    public static final String BASE_CURRENCY_ERROR_MESSAGE = "Base currency not defined";
    public static final String GET_EXCHANGE_RATES_ERROR_MESSAGE = "Failed to get rates from external API";
    public static final String TRANSACTION_NOT_FOUND_MESSAGE = "Conversion Transaction Not Found for id %s";
    public static final String CONVERSION_VALUE_ERROR_MESSAGE = "Conversion value is invalid";
    public static final String USER_ID_POSITIVE_MESSAGE = "User Id must be greater than zero";
    public static final String CONVERSION_ID_LENGTH_MESSAGE = "Id must be exactly 36 characters";
    public static final String CONVERSION_AMOUNT_POSITIVE_MESSAGE = "Amount must be greater than zero";
    public static final String CURRENCY_NOT_NULL_MESSAGE = "Currency must be not null value";
}
