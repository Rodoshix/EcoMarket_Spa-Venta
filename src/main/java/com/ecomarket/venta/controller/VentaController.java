package com.ecomarket.venta.controller;

import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.services.VentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
    @Operation(summary = "Registrar una nueva venta", description = "Registra una venta validando el stock y usando el token JWT del usuario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta registrada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o error en la solicitud"),
        @ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<Venta> registrarVenta(
            @RequestBody Venta venta,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        return ResponseEntity.ok(ventaService.registrarVenta(venta, jwt));
    }

    // Obtener todas las ventas
    @Operation(summary = "Listar todas las ventas con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Listado con enlaces generado correctamente")
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    // Obtener ventas por email de usuario (requiere token)
    @Operation(summary = "Obtener ventas por usuario", description = "Filtra las ventas por correo del usuario autenticado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ventas encontradas para el usuario"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no autorizado"),
        @ApiResponse(responseCode = "404", description = "No se encontraron ventas para el usuario"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{email}")
    public ResponseEntity<List<Venta>> obtenerVentasPorUsuario(
            @PathVariable String email,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        return ResponseEntity.ok(ventaService.obtenerVentasPorUsuario(email, jwt));
    }

    // Cancelar una venta por ID (requiere token)
    @Operation(summary = "Cancelar una venta", description = "Cancela una venta existente usando su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Venta cancelada correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no autorizado"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVenta(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.replace("Bearer ", "").trim();
        ventaService.cancelarVenta(id, jwt);
        return ResponseEntity.noContent().build();
    }

    // Generar boleta de venta por ID (requiere token)
    @Operation(summary = "Generar boleta PDF", description = "Genera y descarga una boleta en formato PDF para una venta específica.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Boleta generada exitosamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no autorizado"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error al generar el PDF")
    })
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
