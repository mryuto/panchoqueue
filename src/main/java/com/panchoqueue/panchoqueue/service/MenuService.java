package com.panchoqueue.panchoqueue.service;

import com.panchoqueue.panchoqueue.dto.CrearMenuRequest;
import com.panchoqueue.panchoqueue.dto.MenuResponse;
import com.panchoqueue.panchoqueue.model.Menu;
import com.panchoqueue.panchoqueue.repository.MenuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MenuService {

	private final MenuRepository menuRepository;

	public MenuService(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	@Transactional(readOnly = true)
	public List<MenuResponse> listarProductos() {
		return menuRepository.findAllByOrderByIdAsc()
				.stream()
				.map(MenuResponse::desde)
				.toList();
	}

	@Transactional(readOnly = true)
	public MenuResponse obtenerProducto(Long id) {
		return menuRepository.findById(id)
				.map(MenuResponse::desde)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
	}

	@Transactional
	public MenuResponse crearProducto(CrearMenuRequest request) {
		Menu menu = new Menu();
		menu.setNombreProducto(request.nombreProducto().trim());
		menu.setPrecio(request.precio());
		return MenuResponse.desde(menuRepository.save(menu));
	}
}
