package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {
	
	public static Product createproduct() {
		Category category = CategoryFactory.createCategory();		
		Product product = new Product(1L, "Console PlayStation 5",  "Descrição qualquer", 3999.0,"https://desban.org.br/teste");
		product.getCategories().add(category);
		return product;		
	}
	
	public static Product createProduct(String name) {
		Product product = createproduct();
		product.setName(name);
		return product;
	}

}
