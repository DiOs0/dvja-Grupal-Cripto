package com.appsecco.dvja.services;

import com.appsecco.dvja.models.Product;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Transactional
public class ProductService {

    private static final Logger logger = Logger.getLogger(ProductService.class);

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void save(Product product) {

        logger.debug("Saving product with name: " + product.getName());

        if (product.getId() != null)
            entityManager.merge(product);
        else
            entityManager.persist(product);
    }

    public Product find(int id) {
        return entityManager.find(Product.class, id);
    }

    public List<Product> findAll() {

        Query query = entityManager.createQuery(
                "SELECT p FROM Product p");

        return query.getResultList();
    }

    /*
     * Consulta corregida mediante parámetros
     * para evitar SQL/JPQL Injection.
     */
    public List<Product> findContainingName(String name) {

        Query query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.name LIKE :name"
        );

        query.setParameter("name", "%" + name + "%");

        return query.getResultList();
    }
}