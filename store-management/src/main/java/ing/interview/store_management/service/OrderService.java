package ing.interview.store_management.service;

import ing.interview.store_management.dto.OrderDto;
import ing.interview.store_management.dto.OrderProductDto;
import ing.interview.store_management.exception.InsufficientProductQuantityException;
import ing.interview.store_management.exception.NoValidProductInOrderException;
import ing.interview.store_management.mapper.OrderMapper;
import ing.interview.store_management.model.Order;
import ing.interview.store_management.model.OrderProduct;
import ing.interview.store_management.model.Product;
import ing.interview.store_management.repository.OrderProductRepository;
import ing.interview.store_management.repository.OrderRepository;
import ing.interview.store_management.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for handling business logic related to orders.
 * This includes creating, retrieving, and deleting orders.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * Creates an order based on the provided product details and quantities.
     *
     * @param orderProductDTOs A set of {@link OrderProductDto} objects containing the product details and quantities
     * @return The created order as a {@link OrderDto}
     * @throws NoValidProductInOrderException If no valid products are found to add to the order
     * @throws InsufficientProductQuantityException If the requested quantity is more than the available stock
     */
    @Transactional
    public OrderDto createOrder(Set<OrderProductDto> orderProductDTOs) throws NoValidProductInOrderException, InsufficientProductQuantityException {
        int validProductCount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Loop through the products in the order and validate their quantities
        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElse(null);  // Get product, or null if not found

            // Validate product and calculate total price
            if (product != null && validateProductQuantity(product, orderProductDto)) {
                totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(orderProductDto.getQuantity())));
                validProductCount++;  // Increment valid product count
            }
        }

        // If no valid products were added, throw a custom exception
        if (validProductCount == 0) {
            throw new NoValidProductInOrderException("No valid products found to add to the order.");
        }

        // Create the order entity
        Order order = createOrderEntity(totalPrice);

        // Add valid products to the order
        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElse(null);  // Get product or null if not found

            if (product != null && orderProductDto.getQuantity() > 0) {
                addProductToOrder(product, order, orderProductDto);
            }
        }
        return orderMapper.toDto(order);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve.
     * @return the order as a DTO.
     * @throws RuntimeException if the order with the specified ID is not found.
     */
    public OrderDto getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toDto(order);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order to delete.
     */
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    /**
     * Lists all orders in the system.
     *
     * @return a list of all orders as DTOs.
     */
    public List<OrderDto> listAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }


    /**
     * Validates the requested quantity for the product.
     *
     * @param product The product to check
     * @param orderProductDto The DTO containing the requested quantity
     * @return True if the quantity is valid, false otherwise
     * @throws InsufficientProductQuantityException If the requested quantity exceeds available stock
     */
    private boolean validateProductQuantity(Product product, OrderProductDto orderProductDto) throws InsufficientProductQuantityException {
        int availableQuantity = product.getStock();
        int requestedQuantity = orderProductDto.getQuantity();

        // Ensure the requested quantity is valid
        if (requestedQuantity > availableQuantity) {
            throw new InsufficientProductQuantityException(
                    "Product " + product.getName() + " does not have enough stock. Available: " + availableQuantity + ", Requested: " + requestedQuantity);
        }

        // If requested quantity is 0 or negative, skip the product
        return requestedQuantity > 0;
    }

    /**
     * Calculates the total price of the order based on the products and their quantities.
     *
     * @param orderProductDTOs A set of order product DTOs containing the product details and quantities
     * @return The total price of the order
     */
    private BigDecimal calculateTotalPrice(Set<OrderProductDto> orderProductDTOs) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId()).orElseThrow();
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(orderProductDto.getQuantity())));
        }
        return totalPrice;
    }

    /**
     * Creates an order entity with the provided total price.
     *
     * @param totalPrice The total price of the order
     * @return The created order entity
     */
    private Order createOrderEntity(BigDecimal totalPrice) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    /**
     * Adds a product to the order.
     *
     * @param product The product to add
     * @param order The order to add the product to
     * @param orderProductDto The DTO containing the product's quantity
     */
    private void addProductToOrder(Product product, Order order, OrderProductDto orderProductDto) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setOrder(order);
        orderProduct.setQuantity(orderProductDto.getQuantity());
        orderProductRepository.save(orderProduct);
    }
}