# EcoMarket SPA - Microservicio Venta-Service

Este microservicio gestiona el registro y control de ventas realizadas en EcoMarket SPA. Incluye funcionalidades de validación de stock, generación de boletas en PDF y cancelación de ventas.

---

## Tecnologías

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Lombok
- MySQL
- RestTemplate (para comunicación entre microservicios)
- OpenPDF (para generación de boletas)

---

## Configuración del entorno

### Base de datos

- Motor: MySQL (MariaDB compatible)
- Nombre: `ventas_db`
- Usuario: `root`
- Contraseña: *(vacía por defecto en XAMPP)*

### Archivo `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ventas_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8084
```

---

## Funcionalidades

- Registrar ventas con múltiples productos.
- Validar token JWT con `usuarios-auth-service`.
- Validar stock en tiempo real consultando `inventario-service`.
- Descontar automáticamente el stock al confirmar una venta.
- Generar boletas PDF.
- Cancelar ventas registradas.

---

## Microservicios integrados

- **usuarios-auth-service** (`localhost:8081`)
- **inventario-service** (`localhost:8082`)

---

## Endpoints disponibles

| Método | Endpoint                                             | Descripción                                                                                 |
|---------|-----------------------------------------------------|---------------------------------------------------------------------------------------------|
| POST    | `/api/ventas`                                       | Registra una venta completa. Valida el stock y descuenta unidades, requiere token.          |
| GET     | `/api/ventas`                                       | Lista todas las ventas registradas.                                                         |
| GET     | `/api/ventas/{email}`                               | Lista las ventas asociadas a un usuario autenticado.                                        |
| DELETE  | `/api/ventas/{id}`                                  | Elimina una venta y sus detalles, requiere token.                                           |
| GET     | `/api/ventas/{id}/boleta`                           | Descarga la boleta en formato PDF de una venta específica.                                  |

---

## Ejemplo de uso en Postman

  Importa la colección incluida:  
  **`EcoMarket-VentaService-Postman.json`**
### Login (desde usuarios-auth)
```http
POST http://localhost:8081/auth/login
{
  "email": "cliente@example.com",
  "password": "cliente123"
}
```

### Registrar venta
```http
POST http://localhost:8084/api/ventas
Headers: Authorization: Bearer {{token}}

{
  "emailUsuario": "cliente@example.com",
  "metodoPago": "Tarjeta",
  "total": 5980,
  "detalles": [
    {
      "idProducto": 1,
      "nombreProducto": "",
      "cantidad": 2,
      "precioUnitario": 0
    }
  ]
}
```

---
