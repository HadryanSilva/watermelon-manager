package br.com.hadryan.api.purchase;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "purchase",  ignore = true)
    Item dtoToItem(ItemDTO itemDTO);

    @Mapping(target = "productId", source = "product.id")
    List<Item> itemsToDto(List<ItemDTO> items);

    @Mapping(target = "productId", source = "product.id")
    ItemDTO itemToDto(Item item);
}
