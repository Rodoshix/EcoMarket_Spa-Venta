package com.ecomarket.venta.services;

import com.ecomarket.venta.dto.ActualizarStockDTO;
import com.ecomarket.venta.dto.PedidoRequestDTO;
import com.ecomarket.venta.model.DetalleVenta;
import com.ecomarket.venta.model.Venta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class VentaServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private VentaService ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        venta = new Venta();
        venta.setId(1L);
        venta.setEmailUsuario("correo@correo.cl");
        venta.setMetodoPago("Transferencia");
        venta.setDireccionDespacho("Calle Falsa 123");
        venta.setTotal(10000.0);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setIdProducto(1L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(5000.0);
        detalle.setNombreProducto("Detergente");
        detalle.setVenta(venta);

        venta.setDetalles(List.of(detalle));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testActualizarStock() {
        when(restTemplate.exchange(
            eq("http://localhost:8082/api/productos/stock"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(ResponseEntity.ok("OK"));

        ventaService.actualizarStock(1L, 2);

        ArgumentCaptor<HttpEntity<ActualizarStockDTO>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
            eq("http://localhost:8082/api/productos/stock"),
            eq(HttpMethod.POST),
            captor.capture(),
            eq(String.class)
        );

        ActualizarStockDTO dto = captor.getValue().getBody();
        assertNotNull(dto);
        assertEquals(1L, dto.getIdProducto());
        assertEquals(2, dto.getCantidadVendida());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testCrearPedido() {
        when(restTemplate.exchange(
            eq("http://localhost:8085/api/pedidos"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(ResponseEntity.ok("Pedido creado"));

        ventaService.crearPedido(1L, "correo@correo.cl", "Calle Falsa 123");

        ArgumentCaptor<HttpEntity<PedidoRequestDTO>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(
            eq("http://localhost:8085/api/pedidos"),
            eq(HttpMethod.POST),
            captor.capture(),
            eq(String.class)
        );

        PedidoRequestDTO dto = captor.getValue().getBody();
        assertNotNull(dto);
        assertEquals(1L, dto.getIdVenta());
        assertEquals("correo@correo.cl", dto.getEmailCliente());
        assertEquals("Calle Falsa 123", dto.getDireccionDespacho());
    }
}
