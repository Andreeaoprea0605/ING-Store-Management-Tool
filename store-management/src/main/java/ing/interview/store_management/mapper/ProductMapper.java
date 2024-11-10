package ing.interview.store_management.mapper;

import ing.interview.store_management.dto.ProductDto;
import ing.interview.store_management.model.Product;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper() {
        this.modelMapper = new ModelMapper();
    }

    // Convert Product entity to ProductDTO
    public ProductDto toDto(Product product) {
        return modelMapper.map(product, ProductDto.class);
    }

    // Convert ProductDTO to Product entity
    public Product toEntity(ProductDto productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }
}
