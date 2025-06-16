package com.ecomarket.venta.assemblers;

import com.ecomarket.venta.controller.VentaController;
import com.ecomarket.venta.model.Venta;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class VentaAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentasPorUsuario(venta.getEmailUsuario(), "")).withRel("ventas-del-usuario"),
                linkTo(methodOn(VentaController.class).generarBoleta(venta.getId(), "")).withRel("boleta"),
                linkTo(methodOn(VentaController.class).cancelarVenta(venta.getId(), "")).withRel("cancelar"));
    }
}