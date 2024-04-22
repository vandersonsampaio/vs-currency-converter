package br.com.vs.currency.converter.model.repository;

import br.com.vs.currency.converter.model.entity.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, String> {
}
