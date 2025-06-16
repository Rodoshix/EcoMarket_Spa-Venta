package com.ecomarket.venta.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de un producto en una venta")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del detalle", example = "1")
    private Long id;

    @Schema(description = "ID del producto vendido", example = "1001")
    private Long idProducto;

    @Schema(description = "Nombre del producto vendido", example = "Detergente ecológico")
    private String nombreProducto;

    @Schema(description = "Cantidad de unidades vendidas", example = "2")
    private int cantidad;

    @Schema(description = "Precio por unidad del producto", example = "25000.0")
    private double precioUnitario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;
    
}