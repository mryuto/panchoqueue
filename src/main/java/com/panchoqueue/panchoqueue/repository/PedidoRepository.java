package com.panchoqueue.panchoqueue.repository;

import com.panchoqueue.panchoqueue.model.Pedido;
import com.panchoqueue.panchoqueue.model.EstadoPedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findAllByOrderByFechaCreacionAscIdAsc();

	List<Pedido> findAllByEstadoOrderByFechaCreacionAscIdAsc(EstadoPedido estado);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<Pedido> findFirstByEstadoOrderByFechaCreacionAscIdAsc(EstadoPedido estado);
}
