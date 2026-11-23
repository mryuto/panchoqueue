package com.panchoqueue.panchoqueue.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CrearMenuRequest(
		@NotBlank(message = "El nombre del producto es obligatorio")
		@Size(max = 255, message = "El nombre del producto no puede superar 255 caracteres")
		String nombreProducto,
		@NotNull(message = "El precio es obligatorio")
		@DecimalMin(value = "0.01", message = "El precio debe ser mayor que cero")
		@Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
		BigDecimal precio) {
}
