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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private Set<OrderProductDto> orderProductDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample product for testing
        product = new Product();
        product.setId(1L);
        product.setName("Product1");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);

        // Create sample OrderProductDto
        OrderProductDto orderProductDto = new OrderProductDto();
        orderProductDto.setProductId(1L);
        orderProductDto.setQuantity(5);

        orderProductDtos = new HashSet<>();
        orderProductDtos.add(orderProductDto);

        // Mock the orderRepository.save() method to return an order
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);  // Set a mock ID for the saved order
            return order;
        });
    }

    @Test
    void createOrder_shouldCreateOrderSuccessfully() throws NoValidProductInOrderException, InsufficientStockException {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        // Act
        OrderDto orderDto = orderService.createOrder(orderProductDtos);

        // Assert
        assertNotNull(orderDto);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderProductRepository, times(1)).save(any(OrderProduct.class));
    }

    @Test
    void createOrder_shouldThrowInsufficientStockException_whenStockIsNotEnough() {
        // Arrange
        product.setStock(3); // Insufficient stock for the order
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(orderProductDtos);
        });
    }

    @Test
    void createOrder_shouldThrowNoValidProductInOrderException_whenNoProductFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoValidProductInOrderException.class, () -> {
            orderService.createOrder(orderProductDtos);
        });
    }

    @Test
    void updateOrder_shouldUpdateOrderSuccessfully() throws InsufficientStockException, OrderNotFoundException, ProductNotFoundException {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PLACED.getStatus());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        // Act
        OrderDto updatedOrderDto = orderService.updateOrder(1L, orderProductDtos);

        // Assert
        assertNotNull(updatedOrderDto);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder_shouldThrowOrderNotFoundException_whenOrderDoesNotExist() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.updateOrder(1L, orderProductDtos);
        });
    }

    @Test
    void updateOrder_shouldThrowInsufficientStockException_whenStockIsNotEnoughForUpdatedQuantity() {
        product.setStock(2); // Stock is insufficient
        Order order = new Order();
        order.setId(1L);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED.getStatus());
        order.setTotalPrice(BigDecimal.valueOf(100));

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(1L);
        orderProduct.setProduct(product);
        orderProduct.setOrder(order);
        orderProduct.setQuantity(34);  // Initial quantity is 1, total stock is 2

        // Add the order product to the order
        Set<OrderProduct> orderProducts = new HashSet<>();
        orderProducts.add(orderProduct);
        order.setOrderProducts(orderProducts);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> {
            orderService.updateOrder(1L, orderProductDtos);
        });
    }

    @Test
    void deleteOrder_shouldDeleteOrder() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void getOrder_shouldReturnOrderDto() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        // Act
        OrderDto orderDto = orderService.getOrder(1L);

        // Assert
        assertNotNull(orderDto);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void createOrder_shouldHandleMultipleProducts() throws NoValidProductInOrderException, InsufficientStockException {
        // Arrange
        OrderProductDto orderProductDto1 = new OrderProductDto();
        orderProductDto1.setProductId(1L);
        orderProductDto1.setQuantity(3);

        OrderProductDto orderProductDto2 = new OrderProductDto();
        orderProductDto2.setProductId(2L);
        orderProductDto2.setQuantity(5);

        Set<OrderProductDto> orderProductDtos = new HashSet<>();
        orderProductDtos.add(orderProductDto1);
        orderProductDtos.add(orderProductDto2);

        // Create another product for testing
        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product2");
        product2.setPrice(BigDecimal.valueOf(150));
        product2.setStock(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderDto());

        // Act
        OrderDto orderDto = orderService.createOrder(orderProductDtos);

        // Assert
        assertNotNull(orderDto);
        verify(orderRepository, times(2)).save(any(Order.class));
    }
}