package ing.interview.store_management.mapper;

import ing.interview.store_management.dto.OrderDto;
import ing.interview.store_management.dto.OrderProductDto;
import ing.interview.store_management.model.Order;
import ing.interview.store_management.model.OrderProduct;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final ModelMapper modelMapper;

    public OrderMapper() {
        this.modelMapper = new ModelMapper();
    }

    // Convert Order entity to OrderDto
    public OrderDto toDto(Order order) {
        Set<OrderProductDto> orderProductsDTO = order.getOrderProducts().stream()
                .map(orderProduct -> modelMapper.map(orderProduct, OrderProductDto.class))
                .collect(Collectors.toSet());

        OrderDto OrderDto = modelMapper.map(order, OrderDto.class);
        OrderDto.setOrderProducts(orderProductsDTO);
        return OrderDto;
    }

    // Convert OrderDto to Order entity
    public Order toEntity(OrderDto OrderDto) {
        Set<OrderProduct> orderProducts = OrderDto.getOrderProducts().stream()
                .map(OrderProductDto -> modelMapper.map(OrderProductDto, OrderProduct.class))
                .collect(Collectors.toSet());

        Order order = modelMapper.map(OrderDto, Order.class);
        order.setOrderProducts(orderProducts);
        return order;
    }
}

