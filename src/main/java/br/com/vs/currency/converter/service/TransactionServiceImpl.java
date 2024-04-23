package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.model.exception.NotFoundException;
import br.com.vs.currency.converter.model.repository.ConversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.vs.currency.converter.utils.Messages.TRANSACTION_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final ExchangeService exchangeService;
    private final ConversionRepository repository;

    @Override
    public Conversion converter(Conversion conversion) {
        var rates = exchangeService.rates();

        return repository.save(conversion);
    }

    @Override
    public List<Conversion> findTransactions(Long userId) {
        return repository.findAllBiUserId(userId);
    }

    @Override
    public Conversion getTransaction(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TRANSACTION_NOT_FOUND_MESSAGE, id)));
    }
}
