package br.com.vs.currency.converter.service;

import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.model.exception.NotFoundException;
import br.com.vs.currency.converter.model.repository.ConversionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static br.com.vs.currency.converter.utils.Messages.TRANSACTION_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final ExchangeService exchangeService;
    private final ConversionRepository repository;

    @Override
    public Conversion converter(Conversion conversion) {
        BigDecimal rateSource = BigDecimal.ONE;
        BigDecimal rateTarget = BigDecimal.ONE;

        if (!conversion.getTargetCurrency().equals(conversion.getSourceCurrency())) {
            var rates = exchangeService.rates();

            rateSource = getRate(conversion.getSourceCurrency(), rates);
            rateTarget = getRate(conversion.getTargetCurrency(), rates);
        } else {
            log.debug("m=converter, msg=\"Same currency to conversion\"");
        }

        conversion.calculateTarget(rateSource, rateTarget);
        conversion.generateId();

        return repository.save(conversion);
    }

    private BigDecimal getRate(Currency currency, Map<Currency, BigDecimal> rates) {
        return currency.isBase() ? BigDecimal.ONE : rates.get(currency);
    }

    @Override
    public List<Conversion> findTransactions(Long userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public Conversion getTransaction(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(TRANSACTION_NOT_FOUND_MESSAGE, id)));
    }
}
