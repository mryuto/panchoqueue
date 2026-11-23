package com.panchoqueue.panchoqueue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearPedidoRequest(
		@NotBlank(message = "El cliente es obligatorio")
		@Size(max = 255, message = "El nombre del cliente no puede superar 255 caracteres")
		String cliente) {
}
