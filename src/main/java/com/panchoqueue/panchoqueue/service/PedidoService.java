package com.panchoqueue.panchoqueue.service;

import com.panchoqueue.panchoqueue.dto.CrearPedidoRequest;
import com.panchoqueue.panchoqueue.dto.PedidoResponse;
import com.panchoqueue.panchoqueue.model.EstadoPedido;
import com.panchoqueue.panchoqueue.model.Pedido;
import com.panchoqueue.panchoqueue.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	public PedidoService(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@Transactional(readOnly = true)
	public List<PedidoResponse> listarPedidos() {
		return pedidoRepository.findAllByOrderByFechaCreacionAscIdAsc()
				.stream()
				.map(PedidoResponse::desde)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<PedidoResponse> listarCola() {
		return listarPorEstado(EstadoPedido.PENDIENTE);
	}

	@Transactional(readOnly = true)
	public List<PedidoResponse> listarPorEstado(EstadoPedido estado) {
		return pedidoRepository.findAllByEstadoOrderByFechaCreacionAscIdAsc(estado)
				.stream()
				.map(PedidoResponse::desde)
				.toList();
	}

	@Transactional
	public PedidoResponse crearPedido(CrearPedidoRequest request) {
		Pedido pedido = new Pedido();
		pedido.setCliente(request.cliente().trim());
		pedido.setEstado(EstadoPedido.PENDIENTE);
		return PedidoResponse.desde(pedidoRepository.save(pedido));
	}

	@Transactional
	public Optional<PedidoResponse> tomarSiguientePedido() {
		return pedidoRepository.findFirstByEstadoOrderByFechaCreacionAscIdAsc(EstadoPedido.PENDIENTE)
				.map(pedido -> {
					pedido.setEstado(EstadoPedido.PREPARANDO);
					return PedidoResponse.desde(pedidoRepository.saveAndFlush(pedido));
				});
	}

	@Transactional
	public PedidoResponse marcarListo(Long id) {
		return cambiarEstado(id, EstadoPedido.PREPARANDO, EstadoPedido.LISTO);
	}

	@Transactional
	public PedidoResponse marcarEntregado(Long id) {
		return cambiarEstado(id, EstadoPedido.LISTO, EstadoPedido.ENTREGADO);
	}

	private PedidoResponse cambiarEstado(Long id, EstadoPedido estadoEsperado, EstadoPedido nuevoEstado) {
		Pedido pedido = pedidoRepository.findByIdForUpdate(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));

		if (pedido.getEstado() != estadoEsperado || !estadoEsperado.puedeAvanzarA(nuevoEstado)) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Para cambiar el pedido a " + nuevoEstado + ", debe estar en estado " + estadoEsperado);
		}

		pedido.setEstado(nuevoEstado);
		return PedidoResponse.desde(pedidoRepository.saveAndFlush(pedido));
	}
}
