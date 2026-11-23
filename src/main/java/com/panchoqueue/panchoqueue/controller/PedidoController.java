package com.panchoqueue.panchoqueue.controller;

import com.panchoqueue.panchoqueue.dto.ActualizarEstadoPedidoRequest;
import com.panchoqueue.panchoqueue.dto.CrearPedidoRequest;
import com.panchoqueue.panchoqueue.dto.PedidoResponse;
import com.panchoqueue.panchoqueue.model.EstadoPedido;
import com.panchoqueue.panchoqueue.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@GetMapping
	public List<PedidoResponse> listarPedidos() {
		return pedidoService.listarPedidos();
	}

	@GetMapping("/cola")
	public List<PedidoResponse> listarCola() {
		return pedidoService.listarCola();
	}

	@GetMapping("/pendientes")
	public List<PedidoResponse> listarPendientes() {
		return pedidoService.listarPorEstado(EstadoPedido.PENDIENTE);
	}

	@GetMapping("/preparando")
	public List<PedidoResponse> listarPreparando() {
		return pedidoService.listarPorEstado(EstadoPedido.PREPARANDO);
	}

	@GetMapping("/listos")
	public List<PedidoResponse> listarListos() {
		return pedidoService.listarPorEstado(EstadoPedido.LISTO);
	}

	@GetMapping("/entregados")
	public List<PedidoResponse> listarEntregados() {
		return pedidoService.listarPorEstado(EstadoPedido.ENTREGADO);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse crearPedido(@Valid @RequestBody CrearPedidoRequest request) {
		return pedidoService.crearPedido(request);
	}

	@PostMapping("/siguiente")
	public ResponseEntity<PedidoResponse> tomarSiguientePedido() {
		return pedidoService.tomarSiguientePedido()
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}

	@PatchMapping("/{id}/estado")
	public PedidoResponse actualizarEstado(
			@PathVariable Long id,
			@Valid @RequestBody ActualizarEstadoPedidoRequest request) {
		return pedidoService.actualizarEstado(id, request.estado());
	}
}
