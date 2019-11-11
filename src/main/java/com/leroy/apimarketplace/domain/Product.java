package com.leroy.apimarketplace.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Classe que representa bean equivalente ao modelo de entidade 'product'.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
@Document(collection="product")
public class Product implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	private Integer id;
	private String name;
	private Integer freeShipping;
	private String description;
	private String price;
	private Integer category;
	
	public Product() {
		
	}

	public Product(Integer id, String name, Integer freeShipping, String description, String price, Integer category) {
		super();
		this.id = id;
		this.name = name;
		this.freeShipping = freeShipping;
		this.description = description;
		this.price = price;
		this.category = category;
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
	 * @param d the price to set
	 */
	public void setPrice(String d) {
		this.price = d;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((freeShipping == null) ? 0 : freeShipping.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (category == null) {
			if (other.category != null)
				return false;
		} else if (!category.equals(other.category))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (freeShipping == null) {
			if (other.freeShipping != null)
				return false;
		} else if (!freeShipping.equals(other.freeShipping))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", freeShipping=" + freeShipping + ", description="
				+ description + ", price=" + price + ", category=" + category + "]";
	}


}
