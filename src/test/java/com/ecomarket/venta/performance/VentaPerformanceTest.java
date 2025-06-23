package com.ecomarket.venta.performance;

import com.ecomarket.venta.controller.VentaController;
import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.services.VentaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VentaController.class)
@ActiveProfiles("test")
public class VentaPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        venta = new Venta();
        venta.setId(1L);
        venta.setEmailUsuario("correo@correo.cl");
        venta.setFechaHora(LocalDateTime.now());
        venta.setMetodoPago("Transferencia");
        venta.setDireccionDespacho("Av. Rápida 789");
        venta.setTotal(35000.0);
    }

    @Test
    public void testTiempoDeRespuesta_listarVentas() throws Exception {
        when(ventaService.listarVentas()).thenReturn(List.of(venta));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());

        stopWatch.stop();
        long tiempo = stopWatch.getTotalTimeMillis();

        System.out.println("Tiempo de respuesta /api/ventas: " + tiempo + " ms");

        assertThat(tiempo).isLessThan(500); // Umbral de rendimiento aceptable
    }
}