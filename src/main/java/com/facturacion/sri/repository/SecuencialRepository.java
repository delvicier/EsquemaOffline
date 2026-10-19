package com.facturacion.sri.repository;

import com.facturacion.sri.entity.SecuencialComprobante;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SecuencialRepository extends JpaRepository<SecuencialComprobante, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SecuencialComprobante s WHERE s.emisor.id = :emisorId "
            + "AND s.codEstablecimiento = :estab AND s.ptoEmision = :ptoEmi AND s.codDoc = :codDoc")
    Optional<SecuencialComprobante> obtenerParaIncrementar(
            @Param("emisorId") Long emisorId,
            @Param("estab") String estab,
            @Param("ptoEmi") String ptoEmi,
            @Param("codDoc") String codDoc
    );

    List<SecuencialComprobante> findByEmisorId(Long emisorId);
}
