package com.facturacion.sri.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "secuenciales_comprobantes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"emisor_id", "cod_establecimiento", "pto_emision", "cod_doc"}
        )
)
public class SecuencialComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emisor_id", nullable = false)
    private Emisor emisor;

    @Column(name = "cod_establecimiento", length = 3, nullable = false)
    private String codEstablecimiento;

    @Column(name = "pto_emision", length = 3, nullable = false)
    private String ptoEmision;

    @Column(name = "cod_doc", length = 2, nullable = false)
    private String codDoc;

    @Column(name = "ultimo_secuencial", nullable = false)
    private Long ultimoSecuencial;

    public SecuencialComprobante() {
    }

    public SecuencialComprobante(Emisor emisor, String codEstablecimiento, String ptoEmision, String codDoc, Long ultimoSecuencial) {
        this.emisor = emisor;
        this.codEstablecimiento = codEstablecimiento;
        this.ptoEmision = ptoEmision;
        this.codDoc = codDoc;
        this.ultimoSecuencial = ultimoSecuencial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emisor getEmisor() {
        return emisor;
    }

    public void setEmisor(Emisor emisor) {
        this.emisor = emisor;
    }

    public String getCodEstablecimiento() {
        return codEstablecimiento;
    }

    public void setCodEstablecimiento(String codEstablecimiento) {
        this.codEstablecimiento = codEstablecimiento;
    }

    public String getPtoEmision() {
        return ptoEmision;
    }

    public void setPtoEmision(String ptoEmision) {
        this.ptoEmision = ptoEmision;
    }

    public String getCodDoc() {
        return codDoc;
    }

    public void setCodDoc(String codDoc) {
        this.codDoc = codDoc;
    }

    public Long getUltimoSecuencial() {
        return ultimoSecuencial;
    }

    public void setUltimoSecuencial(Long ultimoSecuencial) {
        this.ultimoSecuencial = ultimoSecuencial;
    }
}
