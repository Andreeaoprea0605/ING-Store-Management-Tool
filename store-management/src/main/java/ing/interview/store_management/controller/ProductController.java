package ing.interview.store_management.controller;

import ing.interview.store_management.dto.ProductDto;
import ing.interview.store_management.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing products.
 * Provides endpoints for creating, updating, deleting, and retrieving products.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Creates a new product in the system.
     *
     * @param productDTO the DTO containing product details.
     * @return the created product as a DTO.
     */
    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto productDTO) {
        return productService.createProduct(productDTO);
    }

    /**
     * Updates an existing product based on the provided product DTO.
     *
     * @param id         the ID of the product to update.
     * @param productDTO the DTO containing updated product details.
     * @return the updated product as a DTO.
     */
    @PutMapping("/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve.
     * @return the product as a DTO.
     */
    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    /**
     * Retrieves a list of all products in the system.
     *
     * @return a list of all products as DTOs.
     */
    @GetMapping
    public List<ProductDto> listAllProducts() {
        return productService.listAllProducts();
    }
}