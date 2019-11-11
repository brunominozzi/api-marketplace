package com.leroy.apimarketplace.resources.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public static String parseObjectToJson(Object javaObject) {
		final ObjectMapper mapper = new ObjectMapper();
		String json = "";
		try {
			json = mapper.writeValueAsString(javaObject);
		} catch (final JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}
	
}
