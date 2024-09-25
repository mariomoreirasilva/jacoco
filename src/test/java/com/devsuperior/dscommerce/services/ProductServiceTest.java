package com.devsuperior.dscommerce.services;

import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTest {
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingProductId, nomExistingProductId,dependentProductID;
	private String productName;
	private Product product;
	private PageImpl<Product> page;
	private ProductDTO productDTO;
	//primeiro é configurar o setup antes de cada teste
	@BeforeEach
	private void setup() throws Exception{
		existingProductId = 1L;
		nomExistingProductId = 2L;
		dependentProductID = 3L;
		
		productName = "PlayStation 5";
		
		product = ProductFactory.createProduct(productName);
		productDTO = new ProductDTO(product);
		page = new PageImpl<>(List.of(product));
		
		Mockito.when(repository.findById(existingProductId)).thenReturn(Optional.of(product));
		
		Mockito.when(repository.getReferenceById(existingProductId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(nomExistingProductId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.searchByName(any(), (Pageable)any())).thenReturn(page);
		
		Mockito.when(repository.save(any())).thenReturn(product);
		//mocks do delete
		Mockito.when(repository.existsById(existingProductId)).thenReturn(true);
		Mockito.when(repository.existsById(dependentProductID)).thenReturn(true);
		Mockito.when(repository.existsById(nomExistingProductId)).thenReturn(false);
		Mockito.doNothing().when(repository).deleteById(existingProductId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductID);
		
	}
	// segundo começa o teste unitario
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.findById(existingProductId);
		//terceiro os assertions
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), existingProductId);
		Assertions.assertEquals(result.getName(), product.getName());
	}
	
	@Test
	public void findByIdShuldReturnResourceNotFoundExceptionWhenIddoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nomExistingProductId);
		});
		
	}
	
	@Test
	public void findAllShouldReturnProductMinDTO() {
		
		Pageable pageable = PageRequest.of(0, 12);
		
		Page<ProductMinDTO> result = service.findAll(productName, pageable);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getSize(), 1);
		Assertions.assertEquals(result.iterator().next().getName(), productName);
	}
	
	
	@Test
	public void insertShouldReturnProductDTO() {
		ProductDTO result = service.insert(productDTO);
		
		Assertions.assertNotNull(productDTO);
		Assertions.assertEquals(result.getId(), product.getId());
		
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenexistingProductId() {
		ProductDTO result = service.update(existingProductId,productDTO);
		
		Assertions.assertNotNull(productDTO);
		Assertions.assertEquals(result.getId(),existingProductId);
		Assertions.assertEquals(result.getName(), productDTO.getName());
				
	}
	
	@Test
	public void updateShouldRetunrResourceNotFoundExceptionWhennomExistingProductId() {
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			 service.update(nomExistingProductId, productDTO);
		});
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExistis() {
		Assertions.assertDoesNotThrow(() ->{
			service.delete(existingProductId);
		});
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhennomExistingProductId() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			 service.delete(nomExistingProductId);
		});
	}
	
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhendependentProductID() {
		
		Assertions.assertThrows(DatabaseException.class, () ->{
			 service.delete(dependentProductID);
		});
	}
	

}
