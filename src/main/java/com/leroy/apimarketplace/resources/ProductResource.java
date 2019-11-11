package com.leroy.apimarketplace.resources;

import java.io.File;
import java.net.HttpURLConnection;
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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Classe Controller com características RESTful para manipulação 
 * das requisições relacionadas a 'Produtos'.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
@RestController
@RequestMapping(value="/products")
@Api(value = "/products", description = "API Marketplace para cadastro de produtos")
public class ProductResource {
	
	public static final Logger LOGGER = LogManager.getLogger(ProductResource.class);
	
	@Autowired
	private ProductService service;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	private final static String EXCHANGE_NAME = "marketplace";
	
	private final static String QUEUE_NAME = "product";
	

	@RequestMapping(method=RequestMethod.GET)
	@ApiOperation(value = "Efetua a pesquisa de todos os produtos cadastrados no BD")
	@ApiResponses({ @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Pesquisa realizada com sucesso") })
	public ResponseEntity<List<ProductDTO>> findAll(){
		List<Product> listProduct =  service.findAll();
		List<ProductDTO> listProductDto = listProduct.stream().map(x -> new ProductDTO(x)).collect(Collectors.toList());
		
		return ResponseEntity.ok().body(listProductDto);
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	@ApiOperation(value = "Efetua a pesquisa de produto por Id")
	@ApiResponses({ @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Pesquisa realizada com sucesso") })
	public ResponseEntity<ProductDTO> findById(@PathVariable Integer id){
		Product obj = service.findById(id);
		
		return ResponseEntity.ok().body(new ProductDTO(obj));
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	@ApiOperation(value = "Deleta o registro da Base através do Id")
	@ApiResponses({ @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Produto apagado com sucesso") })
	public ResponseEntity<Void> delete(@PathVariable Integer id){
		service.delete(id);
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value="/{id}",method=RequestMethod.PUT)
	@ApiOperation(value = "Efetua a atualização do produto através do Id")
	@ApiResponses({ @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Alteração realizada com sucesso") })
	public ResponseEntity<Void> update(@RequestBody ProductDTO objDTO, @PathVariable Integer id){
		Product obj = service.fromDTO(objDTO);
		obj.setId(id);
		obj = service.update(obj);
		
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value="/status", method=RequestMethod.GET)
	@ApiOperation(value = "Verifica o processamento da planilha, 'TRUE' = sucesso e 'FALSE' = falha.")
	@ApiResponses({ @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Pesquisa de processamento realizada com sucesso") })
	public ResponseEntity<Boolean> statusProcessamento(){
		Boolean statusProcessamento = Boolean.TRUE;
		List<Product> listProduct =  service.findAll();
		statusProcessamento = !listProduct.isEmpty();
		return new ResponseEntity<Boolean>(statusProcessamento, HttpStatus.OK);
	}
	
	/**
	 * Método responsável pela requisição Post para envio e processamento de planilha de produtos.
	 * 
	 * @param productFile
	 * @return
	 * @throws Exception
	 */
	@PostMapping
	@ApiOperation(value = "Efetua o upload e processamento da Planilha de Produtos")
	@ApiResponses({ @ApiResponse(code = HttpURLConnection.HTTP_OK, message = "Processamento realizado com sucesso") })
	public ResponseEntity<String> uploadData(@RequestParam("productFile") MultipartFile productFile) throws Exception {
		LOGGER.info("ResponseEntity.uploadData() - INÍCIO Leitura productFile '" + productFile.getOriginalFilename()+ "'");
	
		//Passo 1 de 3: Leitura de planilha e transformação em Lista de Objetos 'Product'
		File productXlsx = FileUtil.convertMultipartFileToFile(productFile);
		List<Product> listProducts = new ArrayList<Product>(); 
		listProducts = FileUtil.readXLSXFile(productXlsx);

		//Passo 2 de 3: ForEach produz uma mensagem por produto cadastrado na planilha.
		listProducts.stream().forEach(product -> this.produceMessage(EXCHANGE_NAME, QUEUE_NAME, ParserUtil.parseObjectToJson(product)));
		
		//Passo 3 de 3: ForEach garante via threads que cada mensagem produzida no passo anterior seja devidamente recebida e processada.
		listProducts.stream().forEach(product -> {
			new Thread(() -> {
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					LOGGER.info("ResponseEntity.uploadData() - FALHA Thread de Consumo de fila Interrompida!'");
					return;
				}
				/**
				 * Chamada ao Método responsável por consumir fila efetuar insert na coleção de
				 * produtos em BD.
				 */
				this.consumeMessage(QUEUE_NAME);
			}).start();
		});
		
		LOGGER.info(" ResponseEntity.uploadData() - FINAL -> Disparadas Threads de processamento de file: '" + productFile.getOriginalFilename()+ "'");
		return new ResponseEntity<String>(productFile.getOriginalFilename(), HttpStatus.OK);
	}
	
	/**
	 * Método privado responsável por inserir mensagem no RabbitMQ
	 * 
	 * @param exchangeName
	 * @param queueName
	 * @param jsonObject
	 */
	private void produceMessage(final String exchangeName, final String queueName, String jsonObject){
		
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
            LOGGER.info("ResponseEntity.produceMessage() - SEND TO QUEUE: '" + message + "'");
        }catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Método privado responsável por consumir fila de produtos no RabbitMQ
	 *  e efetuar insert na coleção de produtos no MongoDB.
	 * 
	 * @param queueName
	 * @throws Exception
	 */
	private void consumeMessage(final String queueName) {

		Map<String,Object> mapArgumentos = new HashedMap<String, Object>();
	    mapArgumentos.put("x-queue-type", "classic");
	    
	    ConnectionFactory factory = new ConnectionFactory();
	    //factory.setHost("localhost");
	    try (Connection connection = factory.newConnection();
	             Channel channel = connection.createChannel()) {
	
		    channel.queueDeclare(queueName, true, false, false, mapArgumentos);
		    LOGGER.info("ResponseEntity.consumeMessage() - 'Waiting for messages!");
	
		    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
		        String jsonSource = new String(delivery.getBody(), "UTF-8");
		        Product product = jacksonObjectMapper.readValue(jsonSource, Product.class);
		        service.insert(product);
		        LOGGER.info("ResponseEntity.consumeMessage() - RECEIVED '" + product + "'");
		    };
		    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
	    } catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
