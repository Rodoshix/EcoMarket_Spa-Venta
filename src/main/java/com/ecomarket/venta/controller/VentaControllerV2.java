package com.ecomarket.venta.controller;

import com.ecomarket.venta.assemblers.VentaAssembler;
import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.services.VentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/ventas")
public class VentaControllerV2 {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaAssembler ventaAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> listarVentas() {
        List<EntityModel<Venta>> ventasConLinks = ventaService.listarVentas().stream()
                .map(ventaAssembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(ventasConLinks,
                linkTo(methodOn(VentaControllerV2.class).listarVentas()).withSelfRel()));
    }
}