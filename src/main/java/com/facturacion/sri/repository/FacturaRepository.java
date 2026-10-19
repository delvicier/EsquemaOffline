package com.facturacion.sri.repository;

import com.facturacion.sri.entity.FacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacturaRepository extends JpaRepository<FacturaEntity, Long> {
    Optional<FacturaEntity> findByClaveAcceso(String claveAcceso);
}