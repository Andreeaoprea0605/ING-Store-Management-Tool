package ing.interview.store_management.service;

import ing.interview.store_management.dto.ProductDto;
import ing.interview.store_management.mapper.ProductMapper;
import ing.interview.store_management.model.Product;
import ing.interview.store_management.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for handling business logic related to products.
 * This includes creating, updating, retrieving, and deleting products.
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Creates a new product in the system.
     *
     * @param productDto the DTO containing product details.
     * @return the created product as a DTO.
     */
    public ProductDto createProduct(ProductDto productDto) {
        Product product = productMapper.toEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    /**
     * Updates an existing product based on the provided product DTO.
     *
     * @param id the ID of the product to be updated.
     * @param updatedProductDTO the DTO containing updated product details.
     * @return the updated product as a DTO.
     * @throws RuntimeException if the product with the specified ID is not found.
     */
    public ProductDto updateProduct(Long id, ProductDto updatedProductDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProductDTO.getName());
        product.setDescription(updatedProductDTO.getDescription());
        product.setPrice(updatedProductDTO.getPrice());
        product.setStock(updatedProductDTO.getStock());

        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve.
     * @return the product as a DTO.
     * @throws RuntimeException if the product with the specified ID is not found.
     */
    public ProductDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toDto(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to be deleted.
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Lists all products in the system.
     *
     * @return a list of all products as DTOs.
     */
    public List<ProductDto> listAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }
}