package br.com.hadryan.api.field.response;

import br.com.hadryan.api.field.enums.MeasureUnit;

public record FieldResponse(
        Long id,
        String name,
        String location,
        Integer size,
        MeasureUnit measureUnit
) {
}
