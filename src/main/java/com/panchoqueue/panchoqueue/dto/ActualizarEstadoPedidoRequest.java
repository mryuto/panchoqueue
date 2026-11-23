package com.panchoqueue.panchoqueue.dto;

import com.panchoqueue.panchoqueue.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoPedidoRequest(
		@NotNull(message = "El estado es obligatorio")
		EstadoPedido estado) {
}
