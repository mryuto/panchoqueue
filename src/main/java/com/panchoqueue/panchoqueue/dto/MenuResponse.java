package com.panchoqueue.panchoqueue.dto;

import com.panchoqueue.panchoqueue.model.Menu;

import java.math.BigDecimal;

public record MenuResponse(Long id, String nombreProducto, BigDecimal precio) {

	public static MenuResponse desde(Menu menu) {
		return new MenuResponse(menu.getId(), menu.getNombreProducto(), menu.getPrecio());
	}
}
