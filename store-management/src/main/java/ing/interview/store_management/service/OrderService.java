package ing.interview.store_management.service;
import ing.interview.store_management.model.Order;
import ing.interview.store_management.model.OrderProduct;
import ing.interview.store_management.repository.OrderProductRepository;
import ing.interview.store_management.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    public Order createOrder(Set<OrderProduct> orderProducts) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Pending");

        BigDecimal totalPrice = orderProducts.stream()
                .map(op -> op.getProduct().getPrice().multiply(new BigDecimal(op.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
        order.setOrderProducts(orderProducts);

        for (OrderProduct orderProduct : orderProducts) {
            orderProduct.setOrder(order);
        }

        orderRepository.save(order);
        return order;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }
}
