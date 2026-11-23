package com.panchoqueue.panchoqueue.model;

public enum EstadoPedido {
	PENDIENTE,
	PREPARANDO,
	LISTO,
	ENTREGADO;

	public boolean puedeAvanzarA(EstadoPedido siguiente) {
		return switch (this) {
			case PENDIENTE -> siguiente == PREPARANDO;
			case PREPARANDO -> siguiente == LISTO;
			case LISTO -> siguiente == ENTREGADO;
			case ENTREGADO -> false;
		};
	}
}
