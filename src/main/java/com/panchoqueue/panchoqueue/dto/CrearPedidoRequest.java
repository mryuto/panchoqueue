package com.panchoqueue.panchoqueue.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CrearPedidoRequest(
		@NotBlank(message = "El cliente es obligatorio")
		@Size(max = 255, message = "El nombre del cliente no puede superar 255 caracteres")
		String cliente,
		@NotEmpty(message = "El pedido debe incluir al menos un detalle")
		List<@NotNull @Valid CrearPedidoDetalleRequest> detalles) {
}
