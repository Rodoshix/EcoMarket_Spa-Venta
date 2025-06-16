package com.ecomarket.venta.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "ventas")
@Schema(description = "Entidad que representa una venta con sus productos asociados")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la venta", example = "1")
    private Long id;

    @Schema(description = "Correo del usuario que realizó la compra", example = "cliente@email.com")
    private String emailUsuario;

    @Schema(description = "Fecha y hora de la venta", example = "2025-06-15T14:30:00")
    private LocalDateTime fechaHora;

    @Schema(description = "Total a pagar de la venta", example = "50000.0")
    private double total;

    @Schema(description = "Método de pago usado", example = "Transferencia")
    private String metodoPago;

    @Schema(description = "Dirección de despacho", example = "Calle Falsa 123")
    private String direccionDespacho;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @Schema(description = "Lista de productos vendidos")
    private List<DetalleVenta> detalles;
}
