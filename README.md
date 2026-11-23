# 🌭 PanchoQueue

PanchoQueue es una API REST para administrar el menú y los pedidos de un local de comida. El proyecto fue creado como práctica de desarrollo backend con **Java y Spring Boot**, incluyendo diseño de endpoints REST, validación de datos, persistencia con **Spring Data JPA** e integración con **PostgreSQL**.

## 🛠️ Tecnologías

- ☕ Java 21
- 🍃 Spring Boot 4
- 🌐 Spring Web MVC
- 🗃️ Spring Data JPA / Hibernate
- ✅ Jakarta Bean Validation
- 🐘 PostgreSQL
- 📦 Maven Wrapper
- 🧪 H2 para las pruebas

## 📋 Requisitos

- JDK 21
- PostgreSQL

## 🚀 Configuración y ejecución

1. Cloná el repositorio y creá una base de datos PostgreSQL, por ejemplo `panchoqueue`.
2. Configurá la conexión a PostgreSQL con las variables de entorno correspondientes.

3. Iniciá la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

   La API queda disponible en `http://localhost:8080`.

## 🔌 API

Los endpoints están bajo el prefijo `/api`.

### 🍔 Menú

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/menus` | Lista los productos del menú |
| `GET` | `/api/menus/{id}` | Obtiene un producto por ID |
| `POST` | `/api/menus` | Crea un producto |

Ejemplo para crear un producto:

```bash
curl -X POST http://localhost:8080/api/menus \
  -H 'Content-Type: application/json' \
  -d '{"nombreProducto":"Pancho clásico","precio":2500.00}'
```

### 🧾 Pedidos

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/pedidos` | Lista todos los pedidos |
| `GET` | `/api/pedidos/cola` | Lista los pedidos pendientes, en orden de creación |
| `GET` | `/api/pedidos/pendientes` | Lista los pedidos pendientes |
| `GET` | `/api/pedidos/preparando` | Lista los pedidos en preparación |
| `GET` | `/api/pedidos/listos` | Lista los pedidos listos |
| `GET` | `/api/pedidos/entregados` | Lista los pedidos entregados |
| `POST` | `/api/pedidos` | Crea un pedido |
| `POST` | `/api/pedidos/siguiente` | Toma el pedido pendiente más antiguo y lo pasa a preparación |
| `POST` | `/api/pedidos/{id}/listo` | Marca como listo un pedido en preparación |
| `POST` | `/api/pedidos/{id}/entregado` | Marca como entregado un pedido listo |

Para crear un pedido, cada `menuId` debe corresponder a un producto existente y la cantidad debe ser positiva:

```bash
curl -X POST http://localhost:8080/api/pedidos \
  -H 'Content-Type: application/json' \
  -d '{
    "cliente": "Ana",
    "detalles": [
      {"menuId": 1, "cantidad": 2}
    ]
  }'
```

El flujo de estados es `PENDIENTE` → `PREPARANDO` → `LISTO` → `ENTREGADO`. Si no hay pedidos pendientes, `POST /api/pedidos/siguiente` responde `204 No Content`.

## 🧪 Pruebas

Ejecutá la suite con:

```bash
./mvnw test
```

Las pruebas usan H2 en memoria, por lo que no requieren una instancia de PostgreSQL.
