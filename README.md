# API MARKETPLACE

REST API para Cadastro de Produtos de um Marketplace.

## Regras de funcionamento

Desenvolver	 uma API RESTful que:
* Receberá uma planilha de produtos (segue em anexo) que deve ser processada em background (queue).
* Ter um endpoint que informe se a planilha for processada com sucesso ou não.
* Seja possível visualizar, atualizar e apagar os produtos (só é possível criar novos produtos via planilha).

### Pré-Requisitos

É necessário ter instalado em seu ambiente:

* [MongoDB](https://www.mongodb.com/) - Banco de Dados utilizado.
* [RabbitMQ](https://www.rabbitmq.com/) - Servidor de Mensageria.
* [Maven](https://maven.apache.org/) - Controle de Dependências do projeto.
* [Java 8](https://www.java.com/pt_BR/download/) - Java versão 8 ou superior.

### Configurações do Ambiente

Criar database MongoDB com as infos: 
* [New Database MongoDB]
	- Database Name: marketplace
	- Collection Name: product

Criar nova Exchange e Queue RabbitMQ com as infos:
* [New Exchange and Queue RabbitMQ]
	- Exchange Name: marketplace
	- Queue Name: product

### Apoio Técnico

* [Start/Create RabbitMQ] -> (http://coderjony.com/blogs/how-to-enable-rabbitmq-management-plugin-in-windows/)

* [Start/Create MongoDB] -> https://docs.mongodb.com/compass/master/databases/	


### Instalar e executar o projeto

Fazer o donwload deste repositório, para rodar a aplicaçao:

<h5>Passo 1:</h5> Na raiz do projeto, executar o comando para instalar projeto: <br>

```
mvn clean install
```

<h5>Passo 2:</h5> Executar o comando para iniciar a aplicação:

```
mvn spring-boot:run
```

A aplicação api-marketplace http://localhost:8080/products está rodando e pronta para utilização.


### Documentação API REST

O projeto está configurado com swagger para documentação e detalhamento dos métodos disponíveis via Rest. 

Acesse swagger-ui:  http://localhost:8080/swagger-ui.html#/product-resource/


### Processamento via Postman

Configurar Body(form-data) da chamada <b>POST</b> com Key 'productFile' selecionando a planilha conforme abaixo:<br><br>
![process_postman](https://user-images.githubusercontent.com/56572201/68627452-8b8f2b80-04bc-11ea-9967-03c984afd5aa.png)


### TODO

#### Melhorar tratamentos de Exceptions em geral
 - Aprimorar a aplicação para remover os trechos de código 'e.printStackTrace()' e trata-los corretamente.
 - Ajustar o ponto de insert na base para transformar UncheckedException do Mongo em Exception tratável sem interromper o processo em caso de erro de gravação.

#### Otimização do controle da mensageria
 - Melhorar o tratamento de Threads para otimizar a Concorrência do consumo da Queue.

#### Instalação do Projeto com Docker
 - Realizar os steps de configuração do projeto com Docker, automatizando o processo de instalação da aplicação e recursos necessários.

### Autor
* [Bruno Minozzi](https://github.com/brunominozzi)