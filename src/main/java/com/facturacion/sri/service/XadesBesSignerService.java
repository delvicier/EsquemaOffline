package com.facturacion.sri.service;

import jakarta.annotation.PostConstruct;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class XadesBesSignerService {

    private static final String DS_NS = "http://www.w3.org/2000/09/xmldsig#";
    private static final String ETSI_NS = "http://uri.etsi.org/01903/v1.3.2#";
    private static final String C14N_ALGO = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    private static final String RSA_SHA1_ALGO = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private static final String SHA1_ALGO = "http://www.w3.org/2000/09/xmldsig#sha1";
    private static final String ENVELOPED_TRANSFORM = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";
    private static final String SIGNED_PROPS_TYPE = "http://uri.etsi.org/01903#SignedProperties";

    private static final ZoneId ZONA_ECUADOR = ZoneId.of("America/Guayaquil");
    private static final DateTimeFormatter FORMATO_SIGNING_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @PostConstruct
    public void init() {
        Init.init();
    }

    /**
     * Firma un comprobante XML leyendo el certificado .p12 directamente desde los bytes almacenados en PostgreSQL.
     *
     * @param xmlSinFirmar Cadena XML del comprobante con id="comprobante"
     * @param p12Bytes     Arreglo de bytes del archivo PKCS12 (.p12) guardado en la base de datos
     * @param passwordP12  Contraseña del certificado .p12
     * @return XML firmado electrónicamente en UTF-8
     */
    public String firmarXml(String xmlSinFirmar, byte[] p12Bytes, String passwordP12) {
        if (p12Bytes == null || p12Bytes.length == 0) {
            throw new IllegalArgumentException("El emisor no tiene un archivo de firma electrónica .p12 cargado en la base de datos.");
        }
        try (InputStream is = new ByteArrayInputStream(p12Bytes)) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(is, passwordP12.toCharArray());
            return firmarConKeyStore(xmlSinFirmar, keyStore, passwordP12);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar electrónicamente el comprobante (XAdES-BES): " + e.getMessage(), e);
        }
    }

    /**
     * Firma un comprobante XML
     * @param xmlSinFirmar Cadena XML del comprobante con id="comprobante"
     * @param rutaP12      Ruta al archivo PKCS12 (.p12)
     * @param passwordP12  Contraseña del certificado .p12
     * @return XML firmado electrónicamente en UTF-8
     */
    public String firmarXml(String xmlSinFirmar, String rutaP12, String passwordP12) {
        try {
            KeyStore keyStore = cargarKeyStore(rutaP12, passwordP12);
            return firmarConKeyStore(xmlSinFirmar, keyStore, passwordP12);
        } catch (Exception e) {
            throw new RuntimeException("Error al firmar electrónicamente el comprobante (XAdES-BES): " + e.getMessage(), e);
        }
    }

    public X509Certificate validarYExtraerCertificado(byte[] p12Bytes, String passwordP12) {
        try (InputStream is = new ByteArrayInputStream(p12Bytes)) {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(is, passwordP12.toCharArray());
            String alias = seleccionarAliasFirmante(ks);
            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
            cert.checkValidity();
            return cert;
        } catch (Exception e) {
            throw new IllegalArgumentException("El archivo .p12 o su contraseña son incorrectos, o la firma ha caducado: " + e.getMessage(), e);
        }
    }

    private String firmarConKeyStore(String xmlSinFirmar, KeyStore keyStore, String passwordP12) throws Exception {
        String alias = seleccionarAliasFirmante(keyStore);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, passwordP12.toCharArray());
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

        cert.checkValidity();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xmlSinFirmar)));

        Element rootElement = doc.getDocumentElement();
        rootElement.setIdAttribute("id", true);

        String digestComprobanteB64 = calcularDigestC14N(rootElement);

        String signatureId = "Signature" + randomId();
        String signedInfoId = "Signature-SignedInfo" + randomId();
        String signedPropsId = signatureId + "-SignedProperties" + randomId();
        String signedPropsRefId = "SignedPropertiesID" + randomId();
        String certificateId = "Certificate" + randomId();
        String referenceComprobanteId = "Reference-ID-" + randomId();
        String signatureValueId = "SignatureValue" + randomId();
        String objectId = signatureId + "-Object" + randomId();

        Element signatureElem = doc.createElementNS(DS_NS, "ds:Signature");
        signatureElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:ds", DS_NS);
        signatureElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:etsi", ETSI_NS);
        signatureElem.setAttribute("Id", signatureId);
        signatureElem.setIdAttribute("Id", true);
        rootElement.appendChild(signatureElem);

        Element signedInfoElem = doc.createElementNS(DS_NS, "ds:SignedInfo");
        signedInfoElem.setAttribute("Id", signedInfoId);
        signedInfoElem.setIdAttribute("Id", true);
        signatureElem.appendChild(signedInfoElem);

        Element c14nMethod = doc.createElementNS(DS_NS, "ds:CanonicalizationMethod");
        c14nMethod.setAttribute("Algorithm", C14N_ALGO);
        signedInfoElem.appendChild(c14nMethod);

        Element sigMethod = doc.createElementNS(DS_NS, "ds:SignatureMethod");
        sigMethod.setAttribute("Algorithm", RSA_SHA1_ALGO);
        signedInfoElem.appendChild(sigMethod);

        Element refSignedProps = doc.createElementNS(DS_NS, "ds:Reference");
        refSignedProps.setAttribute("Id", signedPropsRefId);
        refSignedProps.setAttribute("Type", SIGNED_PROPS_TYPE);
        refSignedProps.setAttribute("URI", "#" + signedPropsId);
        Element digestMethod1 = doc.createElementNS(DS_NS, "ds:DigestMethod");
        digestMethod1.setAttribute("Algorithm", SHA1_ALGO);
        Element digestValueSignedProps = doc.createElementNS(DS_NS, "ds:DigestValue");
        refSignedProps.appendChild(digestMethod1);
        refSignedProps.appendChild(digestValueSignedProps);
        signedInfoElem.appendChild(refSignedProps);

        Element refCert = doc.createElementNS(DS_NS, "ds:Reference");
        refCert.setAttribute("URI", "#" + certificateId);
        Element digestMethod2 = doc.createElementNS(DS_NS, "ds:DigestMethod");
        digestMethod2.setAttribute("Algorithm", SHA1_ALGO);
        Element digestValueCert = doc.createElementNS(DS_NS, "ds:DigestValue");
        refCert.appendChild(digestMethod2);
        refCert.appendChild(digestValueCert);
        signedInfoElem.appendChild(refCert);

        Element refComprobante = doc.createElementNS(DS_NS, "ds:Reference");
        refComprobante.setAttribute("Id", referenceComprobanteId);
        refComprobante.setAttribute("URI", "#comprobante");
        Element transforms = doc.createElementNS(DS_NS, "ds:Transforms");
        Element transform = doc.createElementNS(DS_NS, "ds:Transform");
        transform.setAttribute("Algorithm", ENVELOPED_TRANSFORM);
        transforms.appendChild(transform);
        Element digestMethod3 = doc.createElementNS(DS_NS, "ds:DigestMethod");
        digestMethod3.setAttribute("Algorithm", SHA1_ALGO);
        Element digestValueComprobante = doc.createElementNS(DS_NS, "ds:DigestValue");
        digestValueComprobante.setTextContent(digestComprobanteB64);
        refComprobante.appendChild(transforms);
        refComprobante.appendChild(digestMethod3);
        refComprobante.appendChild(digestValueComprobante);
        signedInfoElem.appendChild(refComprobante);

        Element signatureValueElem = doc.createElementNS(DS_NS, "ds:SignatureValue");
        signatureValueElem.setAttribute("Id", signatureValueId);
        signatureElem.appendChild(signatureValueElem);

        Element keyInfoElem = construirKeyInfo(doc, certificateId, cert);
        signatureElem.appendChild(keyInfoElem);

        Element objectElem = doc.createElementNS(DS_NS, "ds:Object");
        objectElem.setAttribute("Id", objectId);
        signatureElem.appendChild(objectElem);

        Element qualifyingProps = doc.createElementNS(ETSI_NS, "etsi:QualifyingProperties");
        qualifyingProps.setAttribute("Target", "#" + signatureId);
        objectElem.appendChild(qualifyingProps);

        Element signedPropsElem = construirSignedProperties(doc, signedPropsId, referenceComprobanteId, cert);
        qualifyingProps.appendChild(signedPropsElem);

        digestValueSignedProps.setTextContent(calcularDigestC14N(signedPropsElem));
        digestValueCert.setTextContent(calcularDigestC14N(keyInfoElem));

        byte[] signedInfoCanonicalBytes = canonicalizarNodo(signedInfoElem);
        Signature rsaSha1 = Signature.getInstance("SHA1withRSA");
        rsaSha1.initSign(privateKey);
        rsaSha1.update(signedInfoCanonicalBytes);
        byte[] firmaBytes = rsaSha1.sign();

        signatureValueElem.setTextContent(Base64.getEncoder().encodeToString(firmaBytes));

        return domToString(doc);
    }

    private Element construirKeyInfo(Document doc, String certificateId, X509Certificate cert) throws Exception {
        Element keyInfo = doc.createElementNS(DS_NS, "ds:KeyInfo");
        keyInfo.setAttribute("Id", certificateId);
        keyInfo.setIdAttribute("Id", true);

        Element x509Data = doc.createElementNS(DS_NS, "ds:X509Data");
        Element x509Certificate = doc.createElementNS(DS_NS, "ds:X509Certificate");
        x509Certificate.setTextContent(Base64.getEncoder().encodeToString(cert.getEncoded()));
        x509Data.appendChild(x509Certificate);
        keyInfo.appendChild(x509Data);

        RSAPublicKey rsaPub = (RSAPublicKey) cert.getPublicKey();
        Element keyValue = doc.createElementNS(DS_NS, "ds:KeyValue");
        Element rsaKeyValue = doc.createElementNS(DS_NS, "ds:RSAKeyValue");

        Element modulus = doc.createElementNS(DS_NS, "ds:Modulus");
        modulus.setTextContent(Base64.getEncoder().encodeToString(eliminarByteCeroInicial(rsaPub.getModulus())));

        Element exponent = doc.createElementNS(DS_NS, "ds:Exponent");
        exponent.setTextContent(Base64.getEncoder().encodeToString(eliminarByteCeroInicial(rsaPub.getPublicExponent())));

        rsaKeyValue.appendChild(modulus);
        rsaKeyValue.appendChild(exponent);
        keyValue.appendChild(rsaKeyValue);
        keyInfo.appendChild(keyValue);

        return keyInfo;
    }

    private Element construirSignedProperties(
            Document doc,
            String signedPropsId,
            String referenceComprobanteId,
            X509Certificate cert) throws Exception {

        Element signedProps = doc.createElementNS(ETSI_NS, "etsi:SignedProperties");
        signedProps.setAttribute("Id", signedPropsId);
        signedProps.setIdAttribute("Id", true);

        Element signedSigProps = doc.createElementNS(ETSI_NS, "etsi:SignedSignatureProperties");

        Element signingTime = doc.createElementNS(ETSI_NS, "etsi:SigningTime");
        signingTime.setTextContent(ZonedDateTime.now(ZONA_ECUADOR).format(FORMATO_SIGNING_TIME));
        signedSigProps.appendChild(signingTime);

        Element signingCert = doc.createElementNS(ETSI_NS, "etsi:SigningCertificate");
        Element certNode = doc.createElementNS(ETSI_NS, "etsi:Cert");

        Element certDigest = doc.createElementNS(ETSI_NS, "etsi:CertDigest");
        Element digestMethod = doc.createElementNS(DS_NS, "ds:DigestMethod");
        digestMethod.setAttribute("Algorithm", SHA1_ALGO);
        Element digestValue = doc.createElementNS(DS_NS, "ds:DigestValue");
        byte[] certHash = MessageDigest.getInstance("SHA-1").digest(cert.getEncoded());
        digestValue.setTextContent(Base64.getEncoder().encodeToString(certHash));
        certDigest.appendChild(digestMethod);
        certDigest.appendChild(digestValue);

        Element issuerSerial = doc.createElementNS(ETSI_NS, "etsi:IssuerSerial");
        Element x509IssuerName = doc.createElementNS(DS_NS, "ds:X509IssuerName");
        x509IssuerName.setTextContent(cert.getIssuerX500Principal().getName());
        Element x509SerialNumber = doc.createElementNS(DS_NS, "ds:X509SerialNumber");
        x509SerialNumber.setTextContent(cert.getSerialNumber().toString());
        issuerSerial.appendChild(x509IssuerName);
        issuerSerial.appendChild(x509SerialNumber);

        certNode.appendChild(certDigest);
        certNode.appendChild(issuerSerial);
        signingCert.appendChild(certNode);
        signedSigProps.appendChild(signingCert);

        Element signedDataObjProps = doc.createElementNS(ETSI_NS, "etsi:SignedDataObjectProperties");
        Element dataObjFormat = doc.createElementNS(ETSI_NS, "etsi:DataObjectFormat");
        dataObjFormat.setAttribute("ObjectReference", "#" + referenceComprobanteId);

        Element description = doc.createElementNS(ETSI_NS, "etsi:Description");
        description.setTextContent("contenido comprobante");
        Element mimeType = doc.createElementNS(ETSI_NS, "etsi:MimeType");
        mimeType.setTextContent("text/xml");

        dataObjFormat.appendChild(description);
        dataObjFormat.appendChild(mimeType);
        signedDataObjProps.appendChild(dataObjFormat);

        signedProps.appendChild(signedSigProps);
        signedProps.appendChild(signedDataObjProps);

        return signedProps;
    }

    private String calcularDigestC14N(Element elemento) throws Exception {
        byte[] canonicalBytes = canonicalizarNodo(elemento);
        byte[] sha1Bytes = MessageDigest.getInstance("SHA-1").digest(canonicalBytes);
        return Base64.getEncoder().encodeToString(sha1Bytes);
    }

    private byte[] canonicalizarNodo(Element elemento) throws Exception {
        Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            canonicalizer.canonicalizeSubtree(elemento, baos);
            return baos.toByteArray();
        }
    }

    private KeyStore cargarKeyStore(String rutaP12, String password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        Path path = Path.of(rutaP12);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("No se encontró el archivo de firma electrónica .p12 en la ruta: " + path.toAbsolutePath());
        }
        try (InputStream is = new FileInputStream(path.toFile())) {
            ks.load(is, password.toCharArray());
        }
        return ks;
    }

    private String seleccionarAliasFirmante(KeyStore ks) throws Exception {
        Enumeration<String> aliases = ks.aliases();
        String fallbackAlias = null;
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (ks.isKeyEntry(alias)) {
                fallbackAlias = alias;
                X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
                if (cert != null && cert.getKeyUsage() != null && cert.getKeyUsage()[0]) {
                    return alias;
                }
            }
        }
        if (fallbackAlias != null) {
            return fallbackAlias;
        }
        throw new IllegalStateException("El archivo .p12 no contiene ninguna clave privada válida para firmar.");
    }

    private byte[] eliminarByteCeroInicial(BigInteger bigInt) {
        byte[] bytes = bigInt.toByteArray();
        if (bytes.length > 1 && bytes[0] == 0) {
            byte[] sinSigno = new byte[bytes.length - 1];
            System.arraycopy(bytes, 1, sinSigno, 0, sinSigno.length);
            return sinSigno;
        }
        return bytes;
    }

    private String domToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            transformer.transform(new DOMSource(doc), new StreamResult(baos));
            return baos.toByteArray() != null ? baos.toString(StandardCharsets.UTF_8) : "";
        }
    }

    private int randomId() {
        return ThreadLocalRandom.current().nextInt(100000, 999999);
    }
}