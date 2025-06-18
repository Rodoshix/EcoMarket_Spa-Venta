package com.ecomarket.venta.integration;

import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class VentaIntegrationTest {

    @Autowired
    private VentaRepository ventaRepository;

    @Test
    public void testGuardarVentaEnBaseDeDatos() {
        Venta venta = new Venta();
        venta.setEmailUsuario("test@correo.cl");
        venta.setFechaHora(LocalDateTime.now());
        venta.setMetodoPago("Transferencia");
        venta.setDireccionDespacho("Av. Test 456");
        venta.setTotal(10000.0);

        Venta guardada = ventaRepository.save(venta);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getEmailUsuario()).isEqualTo("test@correo.cl");
    }
}