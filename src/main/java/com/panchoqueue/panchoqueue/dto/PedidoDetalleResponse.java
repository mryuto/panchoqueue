package com.panchoqueue.panchoqueue.dto;

import com.panchoqueue.panchoqueue.model.PedidoDetalle;

import java.math.BigDecimal;

public record PedidoDetalleResponse(
		Long id,
		Long menuId,
		String nombreProducto,
		Integer cantidad,
		BigDecimal precio) {

	public static PedidoDetalleResponse desde(PedidoDetalle detalle) {
		return new PedidoDetalleResponse(
				detalle.getId(),
				detalle.getMenu().getId(),
				detalle.getMenu().getNombreProducto(),
				detalle.getCantidad(),
				detalle.getMenu().getPrecio());
	}
}
