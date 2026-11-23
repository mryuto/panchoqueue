package com.panchoqueue.panchoqueue.controller;

import com.panchoqueue.panchoqueue.model.EstadoPedido;
import com.panchoqueue.panchoqueue.model.Menu;
import com.panchoqueue.panchoqueue.model.Pedido;
import com.panchoqueue.panchoqueue.repository.MenuRepository;
import com.panchoqueue.panchoqueue.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

	@Autowired
	private MenuRepository menuRepository;

	@Test
	void crearPedidoYListarlo() throws Exception {
		Menu menu = guardarMenu("Pancho", "2500.50");
		Menu acompanamiento = guardarMenu("Papas", "1200.00");
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "Ana",
								  "detalles": [
								    {"menuId": %d, "cantidad": 2},
								    {"menuId": %d, "cantidad": 1}
								  ]
								}
								""".formatted(menu.getId(), acompanamiento.getId())))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.numeroTurno").doesNotExist())
				.andExpect(jsonPath("$.estado").value("PENDIENTE"))
				.andExpect(jsonPath("$.fechaCreacion").isNotEmpty())
				.andExpect(jsonPath("$.fechaActualizacion").isNotEmpty())
				.andExpect(jsonPath("$.detalles.length()").value(2))
				.andExpect(jsonPath("$.detalles[0].menuId").value(menu.getId()))
				.andExpect(jsonPath("$.detalles[0].nombreProducto").value("Pancho"))
				.andExpect(jsonPath("$.detalles[0].cantidad").value(2))
				.andExpect(jsonPath("$.detalles[0].precio").value(2500.50))
				.andExpect(jsonPath("$.detalles[1].menuId").value(acompanamiento.getId()))
				.andExpect(jsonPath("$.detalles[1].nombreProducto").value("Papas"))
				.andExpect(jsonPath("$.detalles[1].cantidad").value(1))
				.andExpect(jsonPath("$.detalles[1].precio").value(1200.00));

		mockMvc.perform(get("/api/pedidos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cliente").value("Ana"))
				.andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
				.andExpect(jsonPath("$[0].detalles.length()").value(2));
	}

	@Test
	void rechazaDatosInvalidos() throws Exception {
		Menu menu = guardarMenu("Pancho", "2500");
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": " ",
								  "detalles": [{"menuId": %d, "cantidad": 1}]
								}
								""".formatted(menu.getId())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.cliente").exists());
	}

	@Test
	void rechazaCantidadInvalidaOMenuInexistente() throws Exception {
		Menu menu = guardarMenu("Pancho", "2500");
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "Ana",
								  "detalles": [{"menuId": %d, "cantidad": 0}]
								}
								""".formatted(menu.getId())))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores['detalles[0].cantidad']").exists());

		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "Ana",
								  "detalles": [{"menuId": 999999, "cantidad": 1}]
								}
								"""))
				.andExpect(status().isNotFound());
	}

	@Test
	void requiereAlMenosUnDetalle() throws Exception {
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "Ana",
								  "detalles": []
								}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.detalles").exists());
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
	void listaPedidosPorCadaEstado() throws Exception {
		guardarPedido("Pendiente", EstadoPedido.PENDIENTE);
		guardarPedido("Preparando", EstadoPedido.PREPARANDO);
		guardarPedido("Listo", EstadoPedido.LISTO);
		guardarPedido("Entregado", EstadoPedido.ENTREGADO);

		mockMvc.perform(get("/api/pedidos/pendientes"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cliente").value("Pendiente"));

		mockMvc.perform(get("/api/pedidos/preparando"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cliente").value("Preparando"));

		mockMvc.perform(get("/api/pedidos/listos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cliente").value("Listo"));

		mockMvc.perform(get("/api/pedidos/entregados"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].cliente").value("Entregado"));
	}

	@Test
	void validaLaSecuenciaDeEstados() throws Exception {
		crearPedido("Ana");
		Long id = pedidoRepository.findAll().get(0).getId();

		mockMvc.perform(post("/api/pedidos/{id}/entregado", id))
				.andExpect(status().isConflict());

		mockMvc.perform(post("/api/pedidos/siguiente"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("PREPARANDO"));

		mockMvc.perform(post("/api/pedidos/{id}/entregado", id))
				.andExpect(status().isConflict());

		mockMvc.perform(post("/api/pedidos/{id}/listo", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("LISTO"));

		mockMvc.perform(post("/api/pedidos/{id}/entregado", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("ENTREGADO"));

		mockMvc.perform(post("/api/pedidos/{id}/listo", id))
				.andExpect(status().isConflict());
	}

	private void crearPedido(String cliente) throws Exception {
		Menu menu = guardarMenu("Pancho", "2500");
		mockMvc.perform(post("/api/pedidos")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "cliente": "%s",
								  "detalles": [{"menuId": %d, "cantidad": 1}]
								}
								""".formatted(cliente, menu.getId())))
				.andExpect(status().isCreated());
	}

	private Menu guardarMenu(String nombre, String precio) {
		Menu menu = new Menu();
		menu.setNombreProducto(nombre);
		menu.setPrecio(new BigDecimal(precio));
		return menuRepository.saveAndFlush(menu);
	}

	private void guardarPedido(String cliente, EstadoPedido estado) {
		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setEstado(estado);
		pedidoRepository.saveAndFlush(pedido);
	}
}
