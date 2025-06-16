package com.ecomarket.venta.controller;

import com.ecomarket.venta.model.DetalleVenta;
import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.services.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WebMvcTest(VentaController.class)
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOKEN = "Bearer fake.jwt.token";

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setId(1L);
        venta.setEmailUsuario("correo@correo.cl");
        venta.setFechaHora(LocalDateTime.now());
        venta.setTotal(50000.0);
        venta.setMetodoPago("Transferencia");
        venta.setDireccionDespacho("Calle Falsa 123");

        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1L);
        detalle.setIdProducto(10L);
        detalle.setNombreProducto("Detergente ecológico");
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(25000.0);
        detalle.setVenta(venta);

        venta.setDetalles(List.of(detalle));
    }

    @Test
    public void testRegistrarVenta() throws Exception {
        Mockito.when(ventaService.registrarVenta(any(Venta.class), eq("fake.jwt.token")))
                .thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(venta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.emailUsuario").value("correo@correo.cl"))
                .andExpect(jsonPath("$.total").value(50000.0))
                .andExpect(jsonPath("$.detalles[0].nombreProducto").value("Detergente ecológico"));
    }

    @Test
    public void testListarVentas() throws Exception {
        Mockito.when(ventaService.listarVentas()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailUsuario").value("correo@correo.cl"));
    }

    @Test
    public void testObtenerVentasPorUsuario() throws Exception {
        Mockito.when(ventaService.obtenerVentasPorUsuario(eq("correo@correo.cl"), eq("fake.jwt.token")))
                .thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas/correo@correo.cl")
                .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].detalles[0].nombreProducto").value("Detergente ecológico"));
    }

    @Test
    public void testCancelarVenta() throws Exception {
        Mockito.doNothing().when(ventaService).cancelarVenta(eq(1L), eq("fake.jwt.token"));

        mockMvc.perform(delete("/api/ventas/1")
                .header("Authorization", TOKEN))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGenerarBoleta() throws Exception {
        byte[] pdfBytes = "Contenido PDF de prueba".getBytes();

        Mockito.when(ventaService.generarBoleta(eq(1L), eq("fake.jwt.token")))
                .thenReturn(pdfBytes);

        mockMvc.perform(get("/api/ventas/1/boleta")
                .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=boleta_1.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfBytes));
    }
}