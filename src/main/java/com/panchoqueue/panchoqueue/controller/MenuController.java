package com.panchoqueue.panchoqueue.controller;

import com.panchoqueue.panchoqueue.dto.CrearMenuRequest;
import com.panchoqueue.panchoqueue.dto.MenuResponse;
import com.panchoqueue.panchoqueue.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

	private final MenuService menuService;

	public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}

	@GetMapping
	public List<MenuResponse> listarProductos() {
		return menuService.listarProductos();
	}

	@GetMapping("/{id}")
	public MenuResponse obtenerProducto(@PathVariable Long id) {
		return menuService.obtenerProducto(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MenuResponse crearProducto(@Valid @RequestBody CrearMenuRequest request) {
		return menuService.crearProducto(request);
	}
}
