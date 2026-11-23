package com.panchoqueue.panchoqueue.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearPedidoDetalleRequest(
		@NotNull(message = "El menuId es obligatorio")
		@Positive(message = "El menuId debe ser positivo")
		Long menuId,
		@NotNull(message = "La cantidad es obligatoria")
		@Positive(message = "La cantidad debe ser mayor que cero")
		Integer cantidad) {
}
