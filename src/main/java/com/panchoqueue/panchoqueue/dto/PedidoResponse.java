package com.panchoqueue.panchoqueue.dto;

import com.panchoqueue.panchoqueue.model.EstadoPedido;
import com.panchoqueue.panchoqueue.model.Pedido;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
		Long id,
		String cliente,
		EstadoPedido estado,
		LocalDateTime fechaCreacion,
		LocalDateTime fechaActualizacion,
		List<PedidoDetalleResponse> detalles) {

	public static PedidoResponse desde(Pedido pedido) {
		return new PedidoResponse(
				pedido.getId(),
				pedido.getCliente(),
				pedido.getEstado(),
				pedido.getFechaCreacion(),
				pedido.getFechaActualizacion(),
				pedido.getDetalles().stream()
						.map(PedidoDetalleResponse::desde)
						.toList());
	}
}
