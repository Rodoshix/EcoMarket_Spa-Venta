package com.ecomarket.venta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto vendido", example = "1001")
    private Long idProducto;

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    @Size(max = 150, message = "El nombre del producto no debe superar los 150 caracteres")
    @Schema(description = "Nombre del producto vendido", example = "Detergente ecológico", maxLength = 150)
    private String nombreProducto;

    @Min(value = 1, message = "La cantidad mínima vendida debe ser al menos 1 unidad")
    @Schema(description = "Cantidad de unidades vendidas", example = "2", minimum = "1")
    private int cantidad;

    @Min(value = 0, message = "El precio unitario no puede ser negativo")
    @Schema(description = "Precio por unidad del producto", example = "25000.0", minimum = "0")
    private double precioUnitario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "venta_id")
    private Venta venta;
}