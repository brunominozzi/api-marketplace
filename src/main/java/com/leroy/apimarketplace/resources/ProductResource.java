package com.leroy.apimarketplace.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leroy.apimarketplace.domain.Product;
import com.leroy.apimarketplace.dto.ProductDTO;
import com.leroy.apimarketplace.resources.util.FileUtil;
import com.leroy.apimarketplace.resources.util.ParserUtil;
import com.leroy.apimarketplace.services.ProductService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

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
	
	public static final Logger LOGGER = LogManager.getLogger(ProductResource.class);
	
	@Autowired
	private ProductService service;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	private final static String EXCHANGE_NAME = "marketplace";
	
	private final static String QUEUE_NAME = "product";
	
	@PostMapping
	public ResponseEntity<String> uploadData(@RequestParam("productFile") MultipartFile productFile) throws Exception {

		File productXlsx = FileUtil.convertMultipartFileToFile(productFile);
		
		List<Product> listProducts = new ArrayList<Product>(); 
		listProducts = FileUtil.readXLSXFile(productXlsx);
		
		listProducts.stream().forEach(product -> {
			try {
				this.produceMessage(EXCHANGE_NAME, QUEUE_NAME, ParserUtil.parseObjectToJson(product));
			} catch (Exception e) {
				System.out.println(" [x] Erro no processamento do Produto '" + product.getId() + "'");
				return;
			}
		});
		
		listProducts.stream().forEach(product -> {
			try {
				this.consumeMessage(QUEUE_NAME);
			} catch (Exception e) {
				System.out.println(" [x] Erro no consumo do fila Product '");
				return;
			}
		});
        
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
	
	/**
	 * Método responsável por inserir mensagem no RabbitMQ
	 * 
	 * @param exchangeName
	 * @param queueName
	 * @param jsonObject
	 */
	public void produceMessage(final String exchangeName, final String queueName, String jsonObject){
		
        ConnectionFactory factory = new ConnectionFactory();
       // factory.setHost("localhost");
        
        Map<String,Object> mapArgumentos = new HashedMap<String, Object>();
        mapArgumentos.put("x-queue-type", "classic");
        try {
        	Connection connection = factory.newConnection(); 
        	Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, true, false, false, mapArgumentos);
            String message = jsonObject;
            channel.basicPublish(exchangeName, "", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Método responsável por consumir fila de produtos no RabbitMQ
	 *  e efetuar insert na coleção de produtos em BD.
	 * 
	 * @param queueName
	 * @throws Exception
	 */
	public void consumeMessage(final String queueName) throws Exception{

		Map<String,Object> mapArgumentos = new HashedMap<String, Object>();
	    mapArgumentos.put("x-queue-type", "classic");
	    
	    ConnectionFactory factory = new ConnectionFactory();
	    //factory.setHost("localhost");
	    try (Connection connection = factory.newConnection();
	             Channel channel = connection.createChannel()) {
	
		    channel.queueDeclare(queueName, true, false, false, mapArgumentos);
		    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	
		    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		        String jsonSource = new String(delivery.getBody(), "UTF-8");
		        Product product = jacksonObjectMapper.readValue(jsonSource, Product.class);
		        service.insert(product);
		        System.out.println(" [x] Received '" + product + "'");
		    };
		    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	    }
	}
	
}
