package com.leroy.apimarketplace.resources;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.leroy.apimarketplace.domain.Product;
import com.leroy.apimarketplace.dto.ProductDTO;
import com.leroy.apimarketplace.resources.util.FileUtil;
import com.leroy.apimarketplace.services.ProductService;

/**
 * Classe Controller com características RESTful para manipulação 
 * das requisições relacionadas a 'Produtos'.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
@RestController
@RequestMapping(value="/products")
public class ProductResource {
	
	@Autowired
	private ProductService service;
	
	@PostMapping
	public ResponseEntity<String> uploadData(@RequestParam("productFile") MultipartFile productFile) throws Exception {
		File productXlsx = FileUtil.convertMultipartFileToFile(productFile);
		
		List<Product> listProducts = new ArrayList<Product>(); 
		listProducts = FileUtil.readXLSXFile(productXlsx);
		listProducts.stream().forEach(product -> service.insert(product));
		
		return new ResponseEntity<String>(productFile.getOriginalFilename(), HttpStatus.OK);
	}

	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<ProductDTO>> findAll(){
		List<Product> listProduct =  service.findAll();
		List<ProductDTO> listProductDto = listProduct.stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
		
		return ResponseEntity.ok().body(listProductDto);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<ProductDTO> findById(@PathVariable Integer id){
		Product obj = service.findById(id);
		
		return ResponseEntity.ok().body(new ProductDTO(obj));
	}
	
	@RequestMapping(value="/newproduct", method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody ProductDTO objDTO){
		Product obj = service.fromDTO(objDTO);
		obj = service.insert(obj);
		
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build();
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		service.delete(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	public ResponseEntity<Void> update(@RequestBody ProductDTO objDTO, @PathVariable Integer id){
		Product obj = service.fromDTO(objDTO);
		obj.setId(id);
		obj = service.update(obj);
		
		return ResponseEntity.noContent().build();
	}
	
}
