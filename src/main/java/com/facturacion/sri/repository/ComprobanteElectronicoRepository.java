package com.facturacion.sri.repository;

import com.facturacion.sri.entity.ComprobanteElectronicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobanteElectronicoRepository extends JpaRepository<ComprobanteElectronicoEntity, Long> {

    Optional<ComprobanteElectronicoEntity> findByClaveAcceso(String claveAcceso);
}
