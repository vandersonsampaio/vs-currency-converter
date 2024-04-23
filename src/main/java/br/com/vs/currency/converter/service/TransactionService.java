package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.model.entity.Conversion;

import java.util.List;

public interface TransactionService {

    Conversion converter(Conversion conversion);
    List<Conversion> findTransactions(Long userId);
    Conversion getTransaction(String id);
}
