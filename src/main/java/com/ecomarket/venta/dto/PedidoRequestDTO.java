package com.ecomarket.venta.dto;

import lombok.Data;

@Data
public class PedidoRequestDTO {
    private Long idVenta;
    private String emailCliente;
    private String direccionDespacho;
}