package com.panchoqueue.panchoqueue.controller;

import com.panchoqueue.panchoqueue.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MenuControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MenuRepository menuRepository;

	@Test
	void creaYListaProductosDelMenu() throws Exception {
		mockMvc.perform(post("/api/menus")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombreProducto": "Pancho clásico",
								  "precio": 2500.50
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.nombreProducto").value("Pancho clásico"))
				.andExpect(jsonPath("$.precio").value(2500.50));

		mockMvc.perform(get("/api/menus"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombreProducto").value("Pancho clásico"))
				.andExpect(jsonPath("$[0].precio").value(2500.50));

		Long id = menuRepository.findAllByOrderByIdAsc().get(0).getId();
		mockMvc.perform(get("/api/menus/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.nombreProducto").value("Pancho clásico"))
				.andExpect(jsonPath("$.precio").value(2500.50));
	}

	@Test
	void devuelve404SiNoExisteElProducto() throws Exception {
		mockMvc.perform(get("/api/menus/{id}", Long.MAX_VALUE))
				.andExpect(status().isNotFound());
	}

	@Test
	void rechazaNombreVacioOPrecioNoPositivo() throws Exception {
		mockMvc.perform(post("/api/menus")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombreProducto": " ",
								  "precio": 0
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.nombreProducto").exists())
				.andExpect(jsonPath("$.errores.precio").exists());
	}
}
