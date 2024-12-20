package ing.interview.store_management.controller;

import ing.interview.store_management.dto.OrderDto;
import ing.interview.store_management.dto.OrderProductDto;
import ing.interview.store_management.exception.InsufficientStockException;
import ing.interview.store_management.exception.NoValidProductInOrderException;
import ing.interview.store_management.exception.OrderNotFoundException;
import ing.interview.store_management.exception.ProductNotFoundException;
import ing.interview.store_management.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


/**
 * REST Controller for managing orders.
 * Provides endpoints for creating, updating, retrieving, and deleting orders.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Creates a new order in the system.
     *
     * @param orderProductDTOs the list of order product DTOs containing product details and quantities.
     * @return the created order as a DTO.
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Set<OrderProductDto> orderProductDTOs) {
        try {
            OrderDto orderDto = orderService.createOrder(orderProductDTOs);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderDto);
        } catch (NoValidProductInOrderException | InsufficientStockException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the ID of the order to retrieve.
     * @return the order as a DTO.
     * @throws RuntimeException if the order with the specified ID is not found.
     */
    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

    /**
     * Lists all orders in the system.
     *
     * @return a list of all orders as DTOs.
     */
    @GetMapping
    public List<OrderDto> listAllOrders() {
        return orderService.listAllOrders();
    }

    /**
     * Update orders in the system.
     *
     * @return the updated order dto.
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId,
                                         @RequestBody Set<OrderProductDto> updatedOrderProductDtos) {
        try {
            OrderDto updatedOrder = orderService.updateOrder(orderId, updatedOrderProductDtos);
            return ResponseEntity.ok(updatedOrder);
        } catch (ProductNotFoundException | InsufficientStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}