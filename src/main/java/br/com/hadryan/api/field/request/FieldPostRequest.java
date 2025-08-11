package br.com.hadryan.api.field.request;

import br.com.hadryan.api.field.enums.MeasureUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FieldPostRequest(

         @NotNull
         Long accountId,

         @NotBlank
         String name,

         @NotBlank
         String location,

         @NotNull
         Integer size,

         @NotNull
         MeasureUnit measureUnit,

         @NotNull
         LocalDate startDate,

         @NotNull
         LocalDate finishDate
) {
}
