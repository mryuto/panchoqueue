package com.panchoqueue.panchoqueue.controller;

import com.panchoqueue.panchoqueue.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PedidoControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Test
	void crearPedidoYListarlo() throws Exception {
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "Ana"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.numeroTurno").doesNotExist())
				.andExpect(jsonPath("$.estado").value("PENDIENTE"))
				.andExpect(jsonPath("$.fechaCreacion").isNotEmpty())
				.andExpect(jsonPath("$.fechaActualizacion").isNotEmpty());

		mockMvc.perform(get("/api/pedidos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cliente").value("Ana"))
				.andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
	}

	@Test
	void rechazaDatosInvalidos() throws Exception {
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": " "
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.cliente").exists());
	}

	@Test
	void tomaPedidosPendientesEnOrdenFifo() throws Exception {
		crearPedido("Primero");
		crearPedido("Segundo");

		mockMvc.perform(post("/api/pedidos/siguiente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cliente").value("Primero"))
				.andExpect(jsonPath("$.estado").value("PREPARANDO"));

		mockMvc.perform(post("/api/pedidos/siguiente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.cliente").value("Segundo"))
				.andExpect(jsonPath("$.estado").value("PREPARANDO"));

		mockMvc.perform(post("/api/pedidos/siguiente"))
				.andExpect(status().isNoContent());
	}

	@Test
	void validaLaSecuenciaDeEstados() throws Exception {
		crearPedido("Ana");
		Long id = pedidoRepository.findAll().get(0).getId();

		mockMvc.perform(patch("/api/pedidos/{id}/estado", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"estado":"LISTO"}
								"""))
				.andExpect(status().isConflict());

		mockMvc.perform(post("/api/pedidos/siguiente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("PREPARANDO"));

		mockMvc.perform(patch("/api/pedidos/{id}/estado", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"estado":"LISTO"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("LISTO"));

		mockMvc.perform(patch("/api/pedidos/{id}/estado", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"estado":"ENTREGADO"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("ENTREGADO"));

		mockMvc.perform(patch("/api/pedidos/{id}/estado", id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"estado":"PREPARANDO"}
								"""))
				.andExpect(status().isConflict());
	}

	private void crearPedido(String cliente) throws Exception {
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "%s"
								}
								""".formatted(cliente)))
				.andExpect(status().isCreated());
	}
}
