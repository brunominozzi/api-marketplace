package com.leroy.apimarketplace.dto;

import java.io.Serializable;

import com.leroy.apimarketplace.domain.Product;

/**
 * Objeto DTO que carrega os dados da entidade 'product'.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
public class ProductDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String name;
	private Integer freeShipping;
	private String description;
	private String price;
	private Integer category;
	
	public ProductDTO() {
		
	}

	public ProductDTO(Product obj) {
		super();
		this.id = obj.getId();
		this.name = obj.getName();
		this.freeShipping = obj.getFreeShipping();
		this.description = obj.getDescription();
		this.price = obj.getPrice();
		this.category = obj.getCategory();
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the freeShipping
	 */
	public Integer getFreeShipping() {
		return freeShipping;
	}

	/**
	 * @param freeShipping the freeShipping to set
	 */
	public void setFreeShipping(Integer freeShipping) {
		this.freeShipping = freeShipping;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	/**
	 * @return the category
	 */
	public Integer getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(Integer category) {
		this.category = category;
	}


}
