package ing.interview.store_management.mapper;

import ing.interview.store_management.dto.OrderProductDto;
import ing.interview.store_management.model.OrderProduct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderProductMapper {

    private final ModelMapper modelMapper;

    public OrderProductMapper() {
        this.modelMapper = new ModelMapper();
    }

    // Convert OrderProduct entity to OrderProductDto
    public OrderProductDto toDto(OrderProduct orderProduct) {
        return modelMapper.map(orderProduct, OrderProductDto.class);
    }

    // Convert OrderProductDto to OrderProduct entity
    public OrderProduct toEntity(OrderProductDto orderProductDto) {
        return modelMapper.map(orderProductDto, OrderProduct.class);
    }
}