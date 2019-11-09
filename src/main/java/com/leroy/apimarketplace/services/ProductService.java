package com.leroy.apimarketplace.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leroy.apimarketplace.domain.Product;
import com.leroy.apimarketplace.dto.ProductDTO;
import com.leroy.apimarketplace.repository.ProductRepository;
import com.leroy.apimarketplace.services.exception.ObjectNotFoundException;

/**
 * Classe de serviço responsável por expor operações para 'Product' 
 * Apta a ser injetável em outras classes.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repo;
	
	public List<Product> findAll(){
		return repo.findAll();
	}
	
	public Product findById(Integer id) {
		Optional<Product> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado!")); 
	}
	
	public Product insert(Product obj) {
		return repo.insert(obj);
	}
	
	public void delete(Integer id) {
		findById(id);
		repo.deleteById(id);
	}
	
	public Product update(Product obj) {
		Product newObj = findById(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
		}
	
	private void updateData(Product newObj, Product obj) {
		newObj.setName(obj.getName());
		newObj.setDescription(obj.getDescription());
		newObj.setFreeShipping(obj.getFreeShipping());
		newObj.setCategory(obj.getCategory());
	}

	public Product fromDTO(ProductDTO objDTO) {
		return new Product(objDTO.getId(), objDTO.getName(), objDTO.getFreeShipping(), objDTO.getDescription(), objDTO.getPrice(), objDTO.getCategory());
	}

}
