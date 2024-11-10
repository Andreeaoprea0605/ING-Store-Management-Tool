package ing.interview.store_management.service;

import ing.interview.store_management.dto.OrderDto;
import ing.interview.store_management.dto.OrderProductDto;
import ing.interview.store_management.exception.InsufficientStockException;
import ing.interview.store_management.exception.NoValidProductInOrderException;
import ing.interview.store_management.exception.OrderNotFoundException;
import ing.interview.store_management.exception.ProductNotFoundException;
import ing.interview.store_management.mapper.OrderMapper;
import ing.interview.store_management.model.Order;
import ing.interview.store_management.model.OrderProduct;
import ing.interview.store_management.model.OrderStatus;
import ing.interview.store_management.model.Product;
import ing.interview.store_management.repository.OrderProductRepository;
import ing.interview.store_management.repository.OrderRepository;
import ing.interview.store_management.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
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

    // A cache or map to hold orders with pending status for processing later (for status change)
    private Map<Long, Order> pendingOrders = new HashMap<>();

    /**
     * Creates an order based on the provided product details and quantities.
     *
     * @param orderProductDTOs A set of {@link OrderProductDto} objects containing the product details and quantities
     * @return The created order as a {@link OrderDto}
     * @throws NoValidProductInOrderException If no valid products are found to add to the order
     * @throws InsufficientStockException If the requested quantity is more than the available stock
     */
    @Transactional
    public OrderDto createOrder(Set<OrderProductDto> orderProductDTOs) throws NoValidProductInOrderException, InsufficientStockException {
        // Validate and create the order, and calculate total price
        BigDecimal totalPrice = validateAndCreateOrder(orderProductDTOs);

        // Create the order entity
        Order order = createOrderEntity(totalPrice);

        // Save the order and process order products
        saveOrderAndProducts(orderProductDTOs, order);

        // Change the order status to "PLACED"
        changeOrderStatusToPlaced(order);

        // Add to pending orders for future status change
        pendingOrders.put(order.getId(), order);

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
     * Validates products in the order and calculates the total price.
     *
     * @param orderProductDTOs the list of products in the order
     * @return the total price of the order
     * @throws NoValidProductInOrderException if no valid products were found for the order
     * @throws InsufficientStockException if the quantity requested exceeds available stock
     */
    private BigDecimal validateAndCreateOrder(Set<OrderProductDto> orderProductDTOs) throws NoValidProductInOrderException, InsufficientStockException {
        int validProductCount = 0;
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Loop through the products in the order and validate their quantities
        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElse(null);

            if (product != null) {
                // Check if there's enough stock
                if (product.getStock() >= orderProductDto.getQuantity()) {
                    totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(orderProductDto.getQuantity())));
                    validProductCount++;
                } else {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }
            }
        }

        if (validProductCount == 0) {
            throw new NoValidProductInOrderException("No valid products to add to the order.");
        }

        return totalPrice;
    }

    /**
     * Creates a new order entity and sets its initial properties.
     *
     * @param totalPrice the total price calculated for the order
     * @return the created order entity
     */
    private Order createOrderEntity(BigDecimal totalPrice) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED.getStatus());
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    /**
     * Saves the order and the associated products.
     *
     * @param orderProductDTOs the list of products in the order
     * @param order the order entity to save
     */
    private void saveOrderAndProducts(Set<OrderProductDto> orderProductDTOs, Order order) {
        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElse(null);

            if (product != null && orderProductDto.getQuantity() > 0) {
                // Deduct stock for the product
                updateProductStock(product, orderProductDto.getQuantity());

                // Create and save OrderProduct entry
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProduct(product);
                orderProduct.setOrder(order);
                orderProduct.setQuantity(orderProductDto.getQuantity());
                orderProductRepository.save(orderProduct);
            }
        }
    }

    /**
     * Updates the stock for a product by deducting the quantity ordered.
     *
     * @param product the product to update
     * @param quantity the quantity to deduct from the stock
     */
    private void updateProductStock(Product product, int quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    /**
     * Changes the order status to "PLACED" after the order has been created and processed.
     *
     * @param order the order to update
     */
    private void changeOrderStatusToPlaced(Order order) {
        order.setStatus(OrderStatus.PLACED.getStatus());
        orderRepository.save(order);
    }

    /**
     * This method schedules the order status change to "COMPLETED" after 2 minutes.
     * A Spring @Scheduled task will be used to trigger the status update.
     */
    @Scheduled(fixedRate = 120000)  // Run every 2 minutes
    public void updateOrderStatusToCompleted() {
        // Iterate over the pending orders and update their status after 2 minutes
        Iterator<Map.Entry<Long, Order>> iterator = pendingOrders.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Order> entry = iterator.next();
            Order order = entry.getValue();

            if (Objects.equals(order.getStatus(), OrderStatus.PLACED.getStatus())) {
                // Update the status to completed
                order.setStatus(OrderStatus.COMPLETED.getStatus());
                orderRepository.save(order);

                // Remove the order from pending orders after completion
                iterator.remove();
            }
        }
    }

    /**
     * Method to handle order update, adjusting the stock as necessary.
     *
     * @param orderId the order ID to update
     * @param orderProductDTOs the new list of products and quantities
     * @return the updated order DTO
     * @throws InsufficientStockException if the requested quantity exceeds the stock
     */
    @Transactional
    public OrderDto updateOrder(Long orderId, Set<OrderProductDto> orderProductDTOs) throws InsufficientStockException, OrderNotFoundException, ProductNotFoundException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        // Track old quantities for stock adjustment
        Map<Long, Integer> oldProductQuantities = getOldProductQuantities(order);

        // Validate the new quantities and adjust stock
        BigDecimal totalPrice = validateAndUpdateOrder(orderProductDTOs, oldProductQuantities);

        // Update the order with new products
        updateOrderProducts(orderProductDTOs, order, totalPrice);

        return orderMapper.toDto(order);
    }

    /**
     * Retrieves the old quantities of products in the order to track stock changes.
     *
     * @param order the order to fetch old product quantities for
     * @return a map of product IDs to their old quantities
     */
    private Map<Long, Integer> getOldProductQuantities(Order order) {
        Map<Long, Integer> oldProductQuantities = new HashMap<>();
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            oldProductQuantities.put(orderProduct.getProduct().getId(), orderProduct.getQuantity());
        }
        return oldProductQuantities;
    }

    /**
     * Validates and updates the order, adjusting product stock accordingly.
     *
     * @param orderProductDTOs the updated list of products in the order
     * @param oldProductQuantities the old quantities of products in the order
     * @return the new total price for the updated order
     * @throws InsufficientStockException if the requested quantity exceeds the available stock
     */
    private BigDecimal validateAndUpdateOrder(Set<OrderProductDto> orderProductDTOs, Map<Long, Integer> oldProductQuantities) throws InsufficientStockException {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElse(null);

            if (product != null) {
                int quantityChange = orderProductDto.getQuantity() - oldProductQuantities.getOrDefault(orderProductDto.getProductId(), 0);
                if (product.getStock() + quantityChange < 0) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }

                // Adjust stock based on quantity change
                updateProductStock(product, quantityChange);

                totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(orderProductDto.getQuantity())));
            }
        }

        return totalPrice;
    }

    /**
     * Updates the order products with the new quantities.
     *
     * @param orderProductDTOs the updated list of products in the order
     * @param order the order entity to update
     * @param totalPrice the new total price for the updated order
     */
    private void updateOrderProducts(Set<OrderProductDto> orderProductDTOs, Order order, BigDecimal totalPrice) {
        Set<OrderProduct> updatedOrderProducts = new HashSet<>();
        for (OrderProductDto orderProductDto : orderProductDTOs) {
            Product product = productRepository.findById(orderProductDto.getProductId())
                    .orElse(null);

            if (product != null && orderProductDto.getQuantity() > 0) {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProduct(product);
                orderProduct.setOrder(order);
                orderProduct.setQuantity(orderProductDto.getQuantity());
                updatedOrderProducts.add(orderProduct);
            }
        }
        order.setTotalPrice(totalPrice);
        order.setOrderProducts(updatedOrderProducts);
        orderRepository.save(order);
    }
}