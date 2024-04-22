package br.com.vs.currency.converter.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "tb_user_conversion")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Conversion {

    @Id
    @Column(name = "id_conversion", nullable = false, updatable = false, length = 36)
    private String id;

    @Column(name = "id_user", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "cd_source_currency", nullable = false, updatable = false, length = 3)
    private String sourceCurrency;

    @Column(name = "vl_source_amount", nullable = false, updatable = false, precision = 2, scale = 18)
    private BigDecimal sourceAmount;

    @Column(name = "cd_target_currency", nullable = false, updatable = false, length = 3)
    private String targetCurrency;

    @Column(name = "vl_target_amount", nullable = false, updatable = false, precision = 2, scale = 18)
    private BigDecimal targetAmount;

    @Column(name = "vl_conversion_fee", nullable = false, updatable = false, precision = 7, scale = 18)
    private BigDecimal conversionFee;

    @CreationTimestamp
    @Column(name = "dt_register", updatable = false, nullable = false)
    private LocalDateTime registerTime;
}
