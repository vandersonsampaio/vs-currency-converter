package br.com.vs.currency.converter.model.enums;

import lombok.Getter;

@Getter
public enum Currency {

    EUR("EUR", true, "Euro"),
    USD("USD", false, "American Dollar"),
    BRL("BRL", false, "Brazilian Real"),
    JPY("JPY", false, "Japanese Yen");

    Currency(String value, boolean base, String name) {
        this.value = value;
        this.base = base;
        this.name = name;
    }

    private final String value;
    private final boolean base;
    private final String name;
}
