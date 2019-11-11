package com.leroy.apimarketplace.resources.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leroy.apimarketplace.domain.Product;

/**
 * Classe Utilitária que utiliza as bibliotecas Jackson 
 * ObjectMapper para efetuar parse de objetos.
 * 
 * @author bruno.minozzi
 * @since 10/11/2019
 */
public class ParserUtil {
	
	/**
	 * Converter para json.
	 * 
	 * @param objeto the objeto
	 * @return the string
	 * @throws AplicacaoException
	 */
	public static String parseObjectToJson(Object javaObject) throws Exception {
		final ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(javaObject);
		} catch (final JsonProcessingException e) {
			throw new Exception(e);
		}
		return json;
	}


	/**
	 * Converter json para objeto
	 * 
	 * @param jsonSource
	 * @param class1
	 * @return
	 * @throws Exception
	 */
	public static Object parseJsonToObject(String jsonSource, Class<Product> class1) throws Exception {
		final ObjectMapper mapper = new ObjectMapper();
		Object value = "";
		try {
			value = mapper.readValue(jsonSource , class1);
		} catch (final JsonProcessingException e) {
			throw new Exception(e);
		}
		return value;
	}
}
