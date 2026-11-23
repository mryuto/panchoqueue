-- Ejecutar una sola vez en PostgreSQL, con la aplicacion detenida y una copia de seguridad.
BEGIN;

ALTER TABLE pedido_detalles ADD COLUMN IF NOT EXISTS menu_id BIGINT;

INSERT INTO menus (nombre_producto, precio)
SELECT DISTINCT detalle.producto, detalle.precio_unitario
FROM pedido_detalles detalle
WHERE detalle.producto IS NOT NULL
  AND detalle.precio_unitario IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM menus menu
      WHERE menu.nombre_producto = detalle.producto
        AND menu.precio = detalle.precio_unitario
  );

UPDATE pedido_detalles detalle
SET menu_id = menu.id
FROM menus menu
WHERE menu.nombre_producto = detalle.producto
  AND menu.precio = detalle.precio_unitario
  AND detalle.menu_id IS NULL;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pedido_detalles WHERE menu_id IS NULL) THEN
        RAISE EXCEPTION 'Hay detalles sin menu asociado; revisa producto y precio_unitario antes de continuar';
    END IF;
END $$;

ALTER TABLE pedido_detalles ALTER COLUMN menu_id SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_pedido_detalles_menu'
          AND conrelid = 'pedido_detalles'::regclass
    ) THEN
        ALTER TABLE pedido_detalles
            ADD CONSTRAINT fk_pedido_detalles_menu
            FOREIGN KEY (menu_id) REFERENCES menus (id);
    END IF;
END $$;

ALTER TABLE pedido_detalles DROP COLUMN producto;
ALTER TABLE pedido_detalles DROP COLUMN precio_unitario;

COMMIT;
