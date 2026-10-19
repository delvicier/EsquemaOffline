package com.facturacion.sri.repository;

import com.facturacion.sri.entity.Emisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmisorRepository extends JpaRepository<Emisor, Long> {
    Optional<Emisor> findByRucAndActivoTrue(String ruc);
}