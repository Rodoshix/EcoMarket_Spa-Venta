package com.ecomarket.venta.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.hateoas.server.core.Relation;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @NotBlank(message = "El correo del usuario es obligatorio")
    @Email(message = "Debe ingresar un correo válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Schema(description = "Correo del usuario que realizó la compra", example = "cliente@email.com", maxLength = 100)
    private String emailUsuario;

    @NotNull(message = "La fecha y hora de la venta es obligatoria")
    @Schema(description = "Fecha y hora de la venta", example = "2025-06-15T14:30:00")
    private LocalDateTime fechaHora;

    @Min(value = 0, message = "El total de la venta no puede ser negativo")
    @Schema(description = "Total a pagar de la venta", example = "50000.0", minimum = "0")
    private double total;

    @NotBlank(message = "El método de pago es obligatorio")
    @Size(max = 50, message = "El método de pago no debe superar los 50 caracteres")
    @Schema(description = "Método de pago usado", example = "Transferencia", maxLength = 50)
    private String metodoPago;

    @NotBlank(message = "La dirección de despacho es obligatoria")
    @Size(max = 200, message = "La dirección de despacho no debe superar los 200 caracteres")
    @Schema(description = "Dirección de despacho", example = "Calle Falsa 123", maxLength = 200)
    private String direccionDespacho;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
    @Schema(description = "Lista de productos vendidos")
    private List<DetalleVenta> detalles;
}