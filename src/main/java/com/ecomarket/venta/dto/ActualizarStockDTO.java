package com.ecomarket.venta.dto;

import lombok.Data;

@Data
public class ActualizarStockDTO {

    private Long idProducto;
    private int cantidadVendida;
}