package marketit.backend.project.product.repository;

import lombok.RequiredArgsConstructor;
import marketit.backend.project.product.domain.Product;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final EntityManager em;

    public Product findProduct(Long productId) {
        return em.find(Product.class, productId);
    }
}
