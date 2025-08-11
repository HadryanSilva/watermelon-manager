package br.com.hadryan.api.purchase;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    Item dtoToItem(ItemDTO itemDTO);

    @Mapping(target = "productId", ignore = true)
    List<Item> itemsToDto(List<ItemDTO> items);

    @Mapping(target = "productId", ignore = true)
    ItemDTO itemToDto(Item item);
}
