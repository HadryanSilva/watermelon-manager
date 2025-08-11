package br.com.hadryan.api.field;

import br.com.hadryan.api.field.request.FieldPostRequest;
import br.com.hadryan.api.field.response.FieldResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FieldMapper {

    @Mapping(target = "id",  ignore = true)
    Field postToField(FieldPostRequest request);

    FieldResponse fieldToResponse(Field field);

}
