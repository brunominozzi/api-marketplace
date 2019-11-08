package com.leroy.apimarketplace.resources.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.leroy.apimarketplace.domain.Product;

/**
 * Classe Utilitária para abstrair manipulações e tratamento de arquivos.
 * 
 * @author bruno.minozzi
 * @since 08/11/2019
 */
public class FileUtil {
	
	/**
	 * Converte tipo MultipartFile em File
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File convertMultipartFileToFile(MultipartFile file) throws IOException {
	    File convFile = new File(file.getOriginalFilename());
	    convFile.createNewFile();
	    FileOutputStream fos = new FileOutputStream(convFile);
	    fos.write(file.getBytes());
	    fos.close();
	    return convFile;
	}
	
	/**
	 * Método responsável por ler arquivo xlsx e processar linhas da planilha.
	 * Retorna Lista processada de produtos.
	 * 
	 * @param productXlsx
	 * @return List<Product>
	 * @throws IOException
	 */
	public static List<Product> readXLSXFile(File productXlsx) throws IOException {
		List<Product> products = null;
		try{
			// Abrindo o arquivo e recuperando a planilha
			FileInputStream file = new FileInputStream(productXlsx);
			Workbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);
	
			products = new ArrayList<Product>();
	
			Iterator<Row> rowIterator = sheet.rowIterator();
			Integer categoryFile = 0;
			while (rowIterator.hasNext()) {
				Product product = new Product();
				Row row = (Row) rowIterator.next();
	
				// Obtendo as informações de Category
				if(row.getRowNum() == 0){
					
					Iterator<Cell> cellTopIterator = row.cellIterator();
					while (cellTopIterator.hasNext()) {
						Cell cellTop = (Cell) cellTopIterator.next();
						switch (cellTop.getColumnIndex()) {
						case 0:
							break;
						case 1:
							categoryFile = (int) cellTop.getNumericCellValue();
							break;
						}
					}
					continue;
				}
				
				// Descantando a primeira linha com o header	
				if(row.getRowNum() == 2){
					continue;
				}
		
				Iterator<Cell> cellIterator = row.cellIterator();
				
				while (cellIterator.hasNext()) {
					Cell cell = (Cell) cellIterator.next();
					switch (cell.getColumnIndex()) {
					case 0:
						product.setId((int) cell.getNumericCellValue());
						break;
					case 1:
						product.setName(cell.getStringCellValue());
						break;
					case 2:
						product.setFreeShipping((int) cell.getNumericCellValue());
						break;
					case 3:
						product.setDescription(cell.getStringCellValue());
						break;
					case 4:
						product.setPrice(cell.getStringCellValue());
						break;
					}
					product.setCategory(categoryFile);
				}
				//insere na lista de produtos
				products.add(product);
			}
			file.close();
			workbook.close();
		}catch(Exception e){
			throw new FileNotFoundException("Falha no processamento do arquivo: "+productXlsx.getName());
		}
		return products;
	}

}
