package br.com.vs.currency.converter.model.repository;

import br.com.vs.currency.converter.model.entity.Conversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversionRepository extends JpaRepository<Conversion, String> {

    List<Conversion> findAllByUserId(Long userId);
}
