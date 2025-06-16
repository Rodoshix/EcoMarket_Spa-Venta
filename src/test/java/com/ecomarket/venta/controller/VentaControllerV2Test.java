package com.ecomarket.venta.controller;

import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.services.VentaService;
import com.ecomarket.venta.assemblers.VentaAssembler;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.EntityModel;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(VentaControllerV2.class)
public class VentaControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private VentaAssembler ventaAssembler;

    @Test
    public void testListarVentasConHateoas() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        venta.setEmailUsuario("correo@correo.cl");
        venta.setFechaHora(LocalDateTime.now());
        venta.setMetodoPago("Transferencia");
        venta.setDireccionDespacho("Calle Falsa 123");
        venta.setTotal(20000.0);

        EntityModel<Venta> model = EntityModel.of(venta); // simulación simple

        when(ventaService.listarVentas()).thenReturn(List.of(venta));
        when(ventaAssembler.toModel(venta)).thenReturn(model);

        mockMvc.perform(get("/api/v2/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }
}