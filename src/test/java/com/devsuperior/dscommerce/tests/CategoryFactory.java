package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;

public class CategoryFactory {
	
	//static para poder chamar o método direto CategorFactory.blablabla
	public static Category createCategory() {
		return new Category(1L, "Categoria Teste");
	}
	
	public static Category createCategory(Long id, String name) {
		return new Category(id, name);
		
	}

}
