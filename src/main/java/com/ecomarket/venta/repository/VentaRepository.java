package com.ecomarket.venta.repository;

import com.ecomarket.venta.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByEmailUsuario(String emailUsuario);
}