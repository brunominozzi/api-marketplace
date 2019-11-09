package com.leroy.apimarketplace;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.leroy.apimarketplace.domain.Product;
import com.leroy.apimarketplace.repository.ProductRepository;
import com.leroy.apimarketplace.services.ProductService;

/**
 * Classe de Testes do ProductService com Mockito e JUnit
 * 
 * @author bruno.minozzi
 * @since 09/11/2019
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceMockTest {

	@InjectMocks
	ProductService service;
	
	@Mock
	ProductRepository repo;

	@BeforeEach
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void findAllTest() {
		List<Product> list = new ArrayList<Product>();
		Product productOne = new Product(1001, "Furadeira X", 0, "Furadeira eficiente X", "100.00",123123);
		Product productTwo = new Product(1002, "Furadeira Y", 1, "Furadeira eficiente Y", "140.00",123123);
		Product productThree = new Product(1003, "Chave de Fenda X", 0, "Chave de fenda simples","20.00", 123123);

		list.add(productOne);
		list.add(productTwo);
		list.add(productThree);

		Mockito.when(repo.findAll()).thenReturn(list);

		// test
		List<Product> productList = service.findAll();

		assertEquals(3, productList.size());
		Mockito.verify(repo, times(1)).findAll();
	}

	@Test
	public void findByIdTest() {
		Product productFour = new Product(1008, "Serra de Marmore", 1, "Serra com 1400W modelo 4100", "399.00", 123123);
		Mockito.when(repo.findById(1008)).thenReturn(Optional.of(productFour));

		Product product = service.findById(1008);

		assertEquals("Serra de Marmore", product.getName());
		assertEquals(new Integer(123123), product.getCategory());
		assertEquals(new Integer(1), product.getFreeShipping());
	}

	@Test
	public void insertTest() {
		Product productFive = new Product(1009, "Broca Z", 0, "Broca simples", "3.90", 123123);

		service.insert(productFive);

		Mockito.verify(repo, times(1)).insert(productFive);
	}
}
