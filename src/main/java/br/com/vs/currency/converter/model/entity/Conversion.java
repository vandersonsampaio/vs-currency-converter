package br.com.vs.currency.converter.model.entity;

import br.com.vs.currency.converter.model.enums.Currency;
import br.com.vs.currency.converter.model.exception.ServerErrorException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static br.com.vs.currency.converter.utils.Messages.CONVERSION_VALUE_ERROR_MESSAGE;

@Table(name = "tb_user_conversion")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Conversion {

    @Id
    @Column(name = "id_conversion", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "id_user", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "cd_source_currency", nullable = false, updatable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Currency sourceCurrency;

    @Column(name = "vl_source_amount", nullable = false, updatable = false, precision = 2, scale = 18)
    private BigDecimal sourceAmount;

    @Column(name = "cd_target_currency", nullable = false, updatable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private Currency targetCurrency;

    @Column(name = "vl_target_amount", nullable = false, updatable = false, precision = 2, scale = 18)
    private BigDecimal targetAmount;

    @Column(name = "vl_rate_source", nullable = false, updatable = false, precision = 7, scale = 18)
    private BigDecimal rateSource;

    @Column(name = "vl_rate_target", nullable = false, updatable = false, precision = 7, scale = 18)
    private BigDecimal rateTarget;

    @Column(name = "vl_rate_compose", nullable = false, updatable = false, precision = 7, scale = 18)
    private BigDecimal rateCompose;

    @CreationTimestamp
    @Column(name = "dt_register", updatable = false, nullable = false)
    private LocalDateTime registerTime;

    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }

    public void calculateTarget(BigDecimal rateSource, BigDecimal rateTarget) {
        if (rateSource.equals(BigDecimal.ZERO) || sourceAmount.equals(BigDecimal.ZERO)) {
            throw new ServerErrorException(CONVERSION_VALUE_ERROR_MESSAGE);
        }

        this.rateSource = rateSource;
        this.rateTarget = rateTarget;

        BigDecimal parcialAmount = sourceAmount.divide(rateSource, 8, RoundingMode.HALF_UP);
        targetAmount = parcialAmount.multiply(rateTarget).setScale(2, RoundingMode.HALF_UP);

        rateCompose = targetAmount.divide(sourceAmount, 8, RoundingMode.HALF_UP);
    }
}
