<div align="center">
  <img src="https://srienlinea.sri.gob.ec/recursos-sri/imagenes/logo/sri-en-linea.svg" alt="SRI en Línea" width="300"/>
  <h1>🧾 Facturador SRI Ecuador</h1>
  <p><em>Sistema Empresarial de Facturación Electrónica (Esquema Offline)</em></p>
</div>

---

## 🚀 Sobre el Proyecto

Sistema API REST desarrollado en **Java 25+ con Spring Boot** y **PostgreSQL**. Diseñado bajo una arquitectura centralizada que cumple estrictamente con la **Ficha Técnica de Comprobantes Electrónicos** y el **Reglamento de Comprobantes de Venta** del Servicio de Rentas Internas (SRI) del Ecuador.

### ✨ Características Principales

* 🔐 **Almacenamiento Criptográfico Seguro:** Certificados y credenciales de correo se guardan directo en base de datos.  
* 🛡️ **Control Transaccional:** Prevención de colisiones en números secuenciales.  
* ✍️ **Firma Digital Nativa XAdES-BES:** Firma de XML sin dependencias externas.  
* 📧 **Envío de Correo:** Notificación automática vía email `.xml` y `.pdf`.

---

## ⚙️ 1\. Instalación y Configuración Inicial

Para levantar el servidor, debes preparar la base de datos y las variables de entorno.

### Crear Base de Datos (PostgreSQL en Docker)

docker exec \-it server\_postgres psql \-U root \-d postgres \-c "CREATE DATABASE dbfacturacion OWNER root;"

### Configuración del Entorno (`.env`)

El sistema lee estas variables de entorno en su arranque para conectarse a la base de datos y establecer el administrador inicial:

```
\# POSTGRESQL CONFIG

DB\_HOST=localhost

DB\_PORT=5432

DB\_USER=root

DB\_PASS=xxx

DB\_NAME=dbfacturacion

\# CREDENCIALES INICIALES DEL ADMINISTRADOR

APP\_ADMIN\_USER=admin

APP\_ADMIN\_PASS=Admin123\*

### Iniciar el Servidor

./mvnw spring-boot:run
```

---

## 📡 2\. Documentación de Rutas (Endpoints) y Payloads

Todas las rutas operan bajo el prefijo `/api/v1/` y requieren autenticación **Basic Auth**. A continuación, se detalla la estructura JSON exigida por la API para cada proceso.

### 🔐 2.1. Seguridad y Autenticación

* **`GET /api/v1/auth/perfil`**  
  * **Para qué sirve:** Consulta el estado del usuario autenticado y su rol.  
  * **Respuesta Esperada:**

```json
{

  "username": "admin",

  "rol": "ROLE\_ADMIN",

  "requiereCambioClave": true,

  "fechaActualizacion": null

}
```

* **`PUT /api/v1/auth/cambiar-credenciales`**  
  * **Para qué sirve:** Modifica el nombre de usuario y la contraseña del administrador del sistema. Exige validaciones de longitud (usuario de 4-60 caracteres y contraseña mínima de 8 caracteres).  
  * **Cuerpo de la Petición:**

```json
{

  "passwordActual": "Admin123\*",

  "nuevoUsername": "admin\_nuevo",

  "nuevoPassword": "NuevaPasswordSegura2026\*"

}
```

---

### 🏢 2.2. Configuración del Emisor

Antes de facturar, el sistema exige la carga de la firma, los secuenciales iniciales y la configuración del correo de salida.

* **`POST /api/v1/emisores/configurar`**  
    
  * **Para qué sirve:** Carga inicial del emisor y guardado del certificado `.p12`.  
  * **Cuerpo de la Petición (multipart/form-data):**  
    * `archivoFirma`: (File) Archivo `.p12`  
    * `claveFirma`: (Text) Contraseña del certificado.  
    * `ruc`: (Text) Ej. 1790000000001  
    * `razonSocial`: (Text)  
    * `dirMatriz`: (Text)  
    * `dirEstablecimiento`: (Text)  
    * `codEstablecimiento`: (Text) Ej. 001  
    * `ptoEmision`: (Text) Ej. 001  
    * `ultimoSecuencialFactura`: (Text) Ej. 150 (Coloque 0 si nunca ha emitido).  
    * *\[Otros campos opcionales según normativa\]*


* **`PUT /api/v1/emisores/{ruc}/secuenciales`**  
    
  * **Para qué sirve:** Define o actualiza el último número secuencial emitido para cada tipo de comprobante.  
  * **Cuerpo de la Petición:**

```json
{

  "ultimoSecuencialFactura": 150,

  "ultimoSecuencialLiquidacionCompra": 0,

  "ultimoSecuencialNotaCredito": 12,

  "ultimoSecuencialNotaDebito": 0,

  "ultimoSecuencialGuiaRemision": 5,

  "ultimoSecuencialRetencion": 45

}
```

* **`GET /api/v1/emisores/{ruc}/secuenciales`**  
    
  * **Para qué sirve:** Consulta en qué número va la facturación actual.


* **`PUT /api/v1/emisores/{ruc}/smtp`**  
    
  * **Para qué sirve:** Configura las credenciales del servidor de correo para envíos automáticos.  
  * **Cuerpo de la Petición:**

```json
{

  "mailHost": "ejemplo.servidor.correo",

  "mailPort": 587,

  "mailUser": "tuempresa@correo.com",

  "mailPass": "clave\_de\_aplicacion\_generada",

  "usarStartTls": true,

  "probarConexion": true

}
```

* **`GET /api/v1/emisores/{ruc}`**  
  * **Para qué sirve:** Retorna toda la configuración actual del emisor (sin claves).

---

### 🛒 2.3. Emisión de Comprobantes (Transacciones)

#### Facturas (01)

* **`POST /api/v1/facturas/emitir`**  
  * **Para qué sirve:** Genera, firma, autoriza y envía una factura de venta.  
  * **Cuerpo de la Petición:**

```json
{

  "rucEmisor": "1790000000001",

  "tipoIdentificacionComprador": "05",

  "identificacionComprador": "1712345678",

  "razonSocialComprador": "Juan Perez",

  "direccionComprador": "Av. Amazonas y Patria",

  "emailComprador": "cliente@correo.com",

  "telefonoComprador": "0999999999",

  "formaPago": "20",

  "plazoPago": 15,

  "unidadTiempoPago": "dias",

  "detalles": 

    {

      "codigoPrincipal": "PROD-01",

      "codigoAuxiliar": "P-01",

      "descripcion": "Laptop Dell Inspiron",

      "cantidad": 1.00,

      "precioUnitario": 750.00,

      "descuento": 0.00,

      "codigoPorcentajeIva": "4",

      "tarifaIva": 15.00

    } 

  "infoAdicional": {

    "Vendedor": "Caja 1"

  }

}
```

* **Operaciones sobre Facturas Emitidas:**  
  * **`GET /api/v1/facturas`**: Lista todas las facturas guardadas.  
  * **`GET /api/v1/facturas/{claveAcceso}/estado-validez`**: Consulta en vivo si la factura sigue autorizada o fue anulada en el SRI.  
  * **`GET /api/v1/facturas/{claveAcceso}/autorizacion`**: Consulta los datos de autorización almacenados.  
  * **`GET /api/v1/facturas/{claveAcceso}/xml`**: Descarga el XML firmado y autorizado.  
  * **`GET /api/v1/facturas/{claveAcceso}/ride-pdf`**: Descarga la representación impresa PDF (RIDE) con código de barras.  
  * **`POST /api/v1/facturas/{claveAcceso}/enviar-email?email=cliente@correo.com`**: Reenvío manual del XML y PDF.

#### Anulación (Nota de Crédito \- 04\)

* **`POST /api/v1/facturas/{claveAcceso}/anular-con-nota-credito?motivo=Devolucion`**  
    
  * **Para qué sirve:** Endpoint rápido para anular una factura previamente emitida generando su nota de crédito automáticamente.


* **`POST /api/v1/comprobantes/nota-credito/emitir`**  
    
  * **Para qué sirve:** Emisión manual detallada de una Nota de Crédito.  
  * **Cuerpo de la Petición:**

```json
{

  "rucEmisor": "1790000000001",

  "tipoIdentificacionComprador": "04",

  "identificacionComprador": "1791234567001",

  "razonSocialComprador": "Empresa S.A.",

  "emailComprador": "empresa@correo.com",

  "codDocModificado": "01",

  "numDocModificado": "001-001-000000150",

  "fechaEmisionDocSustento": "15/10/2026",

  "motivo": "Devolución parcial de mercadería",

  "detalles": 

    {

      "codigoPrincipal": "PROD-01",

      "descripcion": "Laptop Dell Inspiron",

      "cantidad": 1.00,

      "precioUnitario": 750.00,

      "descuento": 0.00,

      "codigoPorcentajeIva": "4",

      "tarifaIva": 15.00

    } 

}
```

#### Notas de Débito (05)

* **`POST /api/v1/comprobantes/nota-debito/emitir`**  
  * **Para qué sirve:** Incrementa el valor a cobrar sobre una factura existente, usualmente por recargos o intereses por mora.  
  * **Cuerpo de la Petición:**

```json
{

  "rucEmisor": "1790000000001",

  "tipoIdentificacionComprador": "04",

  "identificacionComprador": "1791234567001",

  "razonSocialComprador": "Empresa S.A.",

  "emailComprador": "empresa@correo.com",

  "codDocModificado": "01",

  "numDocModificado": "001-001-000000150",

  "fechaEmisionDocSustento": "15/10/2026",

  "codigoPorcentajeIva": "4",

  "tarifaIva": 15.00,

  "formaPago": "20",

  "motivos": 

    {

      "razon": "Interés por mora a 30 días",

      "valor": 12.50

    } 

}
```


#### Comprobantes de Retención (07)

* **`POST /api/v1/comprobantes/retencion/emitir`**  
  * **Para qué sirve:** Documenta la retención de impuestos (Renta, IVA o ISD) realizada a un proveedor.  
  * **Cuerpo de la Petición:**

```json
{

  "rucEmisor": "1790000000001",

  "tipoIdentificacionSujetoRetenido": "04",

  "parteRel": "NO",

  "identificacionSujetoRetenido": "0991234567001",

  "razonSocialSujetoRetenido": "Proveedor Global",

  "emailSujetoRetenido": "pagos@proveedor.com",

  "periodoFiscal": "10/2026",

  "codSustento": "01",

  "codDocSustento": "01",

  "numDocSustento": "001-001-000009999",

  "fechaEmisionDocSustento": "18/10/2026",

  "codigoPorcentajeIvaSustento": "4",

  "tarifaIvaSustento": 15.00,

  "baseImponibleSustento": 1000.00,

  "formaPago": "20",

  "retenciones": 

    {

      "codigo": "1",

      "codigoRetencion": "312",

      "baseImponible": 1000.00,

      "porcentajeRetener": 1.75

    },

    {

      "codigo": "2",

      "codigoRetencion": "1",

      "baseImponible": 150.00,

      "porcentajeRetener": 30.00

    } 

}
```

#### Liquidación de Compra (03)

* **`POST /api/v1/comprobantes/liquidacion-compra/emitir`**  
  * **Para qué sirve:** Emite un comprobante de adquisición para justificar compras a personas naturales sin RUC.  
  * **Cuerpo de la Petición:**

```json
{

  "rucEmisor": "1790000000001",

  "tipoIdentificacionProveedor": "05",

  "identificacionProveedor": "1712345678",

  "razonSocialProveedor": "Artesano Local",

  "direccionProveedor": "Mercado Central",

  "emailProveedor": "artesano@correo.com",

  "formaPago": "01",

  "detalles": 

    {

      "codigoPrincipal": "SERV-01",

      "descripcion": "Servicios de albañilería",

      "cantidad": 1.00,

      "precioUnitario": 120.00,

      "descuento": 0.00,

      "codigoPorcentajeIva": "4",

      "tarifaIva": 15.00

    } 

}
```

#### Guías de Remisión (06)

* **`POST /api/v1/comprobantes/guia-remision/emitir`**  
  * **Para qué sirve:** Sustenta el transporte físico de mercaderías.  
  * **Cuerpo de la Petición:**

```json
{

  "rucEmisor": "1790000000001",

  "dirPartida": "Bodega Principal UIO",

  "razonSocialTransportista": "Transportes Rápidos S.A.",

  "tipoIdentificacionTransportista": "04",

  "rucTransportista": "1799887766001",

  "fechaIniTransporte": "18/10/2026",

  "fechaFinTransporte": "20/10/2026",

  "placa": "PBA-1234",

  "identificacionDestinatario": "0991234567001",

  "razonSocialDestinatario": "Cliente Mayorista",

  "dirDestinatario": "Sucursal GYE",

  "motivoTraslado": "Venta de mercadería",

  "emailDestinatario": "logistica@cliente.com",

  "detalles": 

    {

      "codigoInterno": "PROD-01",

      "descripcion": "Laptop Dell Inspiron",

      "cantidad": 50.00

    } 

}
```

---

## 🛠️ 3\. Catálogo de Errores Comunes

La API está configurada con un `GlobalExceptionHandler` que estandariza las respuestas de error en un formato JSON claro y en español.

### Estructura de un Error:

```json
{

  "timestamp": "2026-10-18T21:27:00.000",

  "status": 400,

  "error": "Solicitud Incorrecta \- Error de Validación",

  "mensaje": "Uno o más campos enviados en la petición no cumplen con el formato requerido por el SRI.",

  "ruta": "/api/v1/facturas/emitir",

  "detalles": {

    "rucEmisor": "El RUC del emisor debe tener 13 dígitos y terminar en 001"

  }

}
```


### Códigos de Estado y su Significado:

* **`400 BAD REQUEST`**: Datos faltantes, JSON mal formado o incumplimiento de reglas de negocio (ej. Facturar a Consumidor Final por más de \$50 USD).  
* **`401 UNAUTHORIZED` / `403 FORBIDDEN`**: Problemas con las credenciales de Autenticación Básica.  
* **`404 NOT FOUND`**: La ruta solicitada no existe o el recurso (factura, emisor) no se encuentra en la base de datos.  
* **`405 METHOD NOT ALLOWED`**: Uso incorrecto del verbo HTTP (ej. intentar hacer un GET en una ruta POST).  
* **`409 CONFLICT`**: Problemas de integridad en la BD (ej. intentar registrar un secuencial duplicado) o intento de emitir un comprobante sin haber configurado el secuencial primero.  
* **`413 PAYLOAD TOO LARGE` / `415 UNSUPPORTED MEDIA TYPE`**: Archivo de firma electrónica demasiado pesado o uso de un formato incorrecto (ej. enviar JSON en lugar de form-data).  
* **`500 INTERNAL SERVER ERROR`**: Errores no controlados o excepciones de código interno.  
* **`502 BAD GATEWAY` / `504 GATEWAY TIMEOUT`**: Problemas de conexión con los servidores Web Service del SRI.  
* **`503 SERVICE UNAVAILABLE`**: El servidor PostgreSQL de la base de datos no está disponible.