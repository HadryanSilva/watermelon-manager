package br.com.hadryan.api.field.request;

import br.com.hadryan.api.field.enums.MeasureUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FieldPostRequest(
         @NotBlank
         String name,

         @NotBlank
         String location,

         @NotNull
         Integer size,

         @NotNull
         MeasureUnit measureUnit
) {
}
