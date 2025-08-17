package com.next.app.api.product.service;

import com.next.app.api.product.entity.Product;
import com.next.app.api.product.repository.ProductRepository;
import com.next.app.api.product.controller.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 상품 관련 비즈니스 로직 담당 서비스
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 전체 상품 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ID로 상품 조회
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // 상품 등록
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    // 상품 수정
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setImageUrl(productDetails.getImageUrl());
        product.setCategory(productDetails.getCategory());
        return productRepository.save(product);
    }

    // 상품 삭제
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // 조건별 페이징 상품 리스트(DTO 반환)
    public Page<ProductDto> list(String q, Long categoryId, Pageable pageable) {
        boolean hasQ = q != null && !q.isBlank();
        boolean hasCat = categoryId != null;

        Page<Product> page = (hasCat && hasQ)
                ? productRepository.findByCategory_IdAndNameContainingIgnoreCase(categoryId, q, pageable)
                : (hasCat ? productRepository.findByCategory_Id(categoryId, pageable)
                : (hasQ ? productRepository.findByNameContainingIgnoreCase(q, pageable)
                : productRepository.findAll(pageable)));

        return page.map(p -> new ProductDto(
                p.getId(), p.getName(), p.getPrice(), p.getImageUrl(),
                p.getCategory() != null ? p.getCategory().getId() : null
        ));
    }

    // 단건 상품 DTO 반환
    public ProductDto get(Long id) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
        return new ProductDto(
                p.getId(), p.getName(), p.getPrice(), p.getImageUrl(),
                p.getCategory() != null ? p.getCategory().getId() : null
        );
    }
}
