package br.com.vs.currency.converter.resource.dto.mapper;

import br.com.vs.currency.converter.model.entity.Conversion;
import br.com.vs.currency.converter.resource.dto.request.ConversionRequest;
import br.com.vs.currency.converter.resource.dto.response.ConversionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversionMapper {

    @Mappings({
            @Mapping(target = "amount", source = "sourceAmount"),
            @Mapping(target = "rate", source = "rateCompose"),
            @Mapping(target = "timestamp", source = "registerTime")
    })
    ConversionResponse from(Conversion entity);
    List<ConversionResponse> from(List<Conversion> entities);

    @Mapping(target = "sourceAmount", source = "amount")
    Conversion to(ConversionRequest request);
}
