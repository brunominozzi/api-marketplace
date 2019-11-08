package com.leroy.apimarketplace.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.leroy.apimarketplace.domain.Product;

/**
 * Objeto que torna capaz execução de operações básicas com 'produtos' no MongoDB
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
@Repository
public interface ProductRepository extends MongoRepository<Product, Integer>{

}
