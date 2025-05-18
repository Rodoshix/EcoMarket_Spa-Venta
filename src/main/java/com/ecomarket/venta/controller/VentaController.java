package com.ecomarket.venta.controller;

import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.services.VentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // Registrar una venta (requiere validación de token)
    @PostMapping
    public ResponseEntity<Venta> registrarVenta(
            @RequestBody Venta venta,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        return ResponseEntity.ok(ventaService.registrarVenta(venta, jwt));
    }

    // Obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    // Obtener ventas por email de usuario (requiere token)
    @GetMapping("/{email}")
    public ResponseEntity<List<Venta>> obtenerVentasPorUsuario(
            @PathVariable String email,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        return ResponseEntity.ok(ventaService.obtenerVentasPorUsuario(email, jwt));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVenta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        ventaService.cancelarVenta(id, jwt);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/boleta")
    public ResponseEntity<byte[]> generarBoleta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        byte[] pdf = ventaService.generarBoleta(id, jwt);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=boleta_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    
}
