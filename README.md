# 🍔 Franchises API — Prueba Técnica Accenture

API REST reactiva para gestión de franquicias, sucursales y productos, con persistencia en **MongoDB Atlas** (nube).

## 🛠️ Stack Tecnológico

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 17 | Lenguaje |
| Spring Boot | 4.0.5 | Framework base |
| Spring WebFlux | 4.0.5 | Programación reactiva (Project Reactor) |
| Spring Data MongoDB Reactive | 4.0.5 | Persistencia reactiva |
| **MongoDB Atlas** | **Cloud** | **Base de datos NoSQL en la nube** |
| Docker / Docker Compose | - | Contenedorización |

---

## ✅ Criterios de Aceptación Cubiertos

| # | Criterio | Estado |
|---|---|---|
| 1 | Desarrollado en Spring Boot | ✅ |
| 2 | Endpoint agregar franquicia | ✅ |
| 3 | Endpoint agregar sucursal | ✅ |
| 4 | Endpoint agregar producto | ✅ |
| 5 | Endpoint eliminar producto | ✅ |
| 6 | Endpoint modificar stock | ✅ |
| 7 | Endpoint producto con más stock por sucursal | ✅ |
| 8 | Persistencia en la nube con **MongoDB Atlas** | ✅ |

## ⭐ Puntos Extra Cubiertos

- ✅ Empaquetado con **Docker** (multi-stage build)
- ✅ Programación **funcional y reactiva** (Spring WebFlux + Project Reactor)
- ✅ Endpoint actualizar nombre de franquicia
- ✅ Endpoint actualizar nombre de sucursal
- ✅ Endpoint actualizar nombre de producto

---

## ☁️ MongoDB Atlas — Base de datos en la nube

Este proyecto **no usa una base de datos local**. La persistencia está en **MongoDB Atlas**, el servicio cloud administrado de MongoDB. Esto significa:

- No necesitas instalar MongoDB en tu máquina.
- No hay contenedor de base de datos en Docker Compose.
- La conexión se realiza mediante una URI segura (`mongodb+srv://...`) provista como variable de entorno.
- El cluster fue aprovisionado con **Terraform** (ver carpeta `terraform/`).

> ⚠️ Para correr el proyecto necesitas una URI de conexión válida a MongoDB Atlas. Ver sección de configuración más abajo.

---

## 🏗️ Arquitectura del Proyecto

```
src/main/java/com/jmt/franchises/
├── FranchisesApplication.java
├── controller/
│   └── FranchiseController.java         # Capa HTTP (endpoints REST)
├── service/
│   ├── FranchiseService.java            # Contrato del servicio
│   └── FranchiseServiceImpl.java        # Lógica de negocio
├── repository/
│   └── FranchiseRepository.java         # Acceso a MongoDB Atlas
├── model/
│   ├── Franchise.java                   # Documento raíz MongoDB
│   ├── Branch.java                      # Subdocumento embebido
│   └── Product.java                     # Subdocumento embebido
├── dto/
│   ├── request/                         # DTOs de entrada con validaciones
│   └── response/                        # DTOs de salida
├── mapper/
│   └── FranchiseMapper.java             # Mapeo entidad ↔ DTO (MapStruct)
└── exception/
    ├── GlobalExceptionHandler.java      # Manejo centralizado de errores
    ├── ResourceNotFoundException.java   # HTTP 404
    ├── DuplicateResourceException.java  # HTTP 409
    └── BusinessException.java           # HTTP 400
```

---

## 🚀 Opción 1 — Correr con Docker (Recomendado)

### Pre-requisitos
- [Docker](https://docs.docker.com/get-docker/) >= 24.0
- [Docker Compose](https://docs.docker.com/compose/install/) >= 2.0
- URI de conexión a MongoDB Atlas

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/franchises.git
cd franchises

# 2. Crear el archivo .env en la raíz del proyecto
cp .env.example .env

# 3. Editar el .env con tu URI real de Atlas
#    MONGO_URI=mongodb+srv://usuario:password@cluster.mongodb.net/franquiciasdb?retryWrites=true&w=majority
#    MONGO_DATABASE=franquiciasdb

# 4. Levantar la aplicación
docker-compose up -d

# 5. Ver logs
docker-compose logs -f

# 6. La API estará disponible en:
#    http://localhost:8080
```

### Detener
```bash
docker-compose down
```

---

## 💻 Opción 2 — Correr desde IntelliJ IDEA

### Pre-requisitos
- Java 17+
- Maven 3.9+
- URI de conexión a MongoDB Atlas

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/franchises.git
cd franchises
```

**Configurar variables de entorno en IntelliJ:**

1. Menú superior → **Edit Configurations...**
2. Selecciona la configuración de `FranchisesApplication`
3. Campo **Environment variables**, agregar:
```
MONGO_URI=mongodb+srv://usuario:password@cluster.mongodb.net/franquiciasdb?retryWrites=true&w=majority;MONGO_DATABASE=franquiciasdb
```
4. Clic en **OK** y darle **Run**

---

## ⚙️ Configuración — Variables de entorno

Crea un archivo `.env` en la raíz del proyecto (nunca lo subas a GitHub):

```env
MONGO_URI=mongodb+srv://<usuario>:<password>@<cluster>.mongodb.net/franquiciasdb?retryWrites=true&w=majority
MONGO_DATABASE=accenture_test
```



## 📋 Endpoints de la API

Base URL: `http://localhost:8080/api/v1`

### Franquicias

| Método | URL | Descripción | HTTP |
|--------|-----|-------------|------|
| `POST` | `/franchises` | Crear franquicia | 201 |
| `GET` | `/franchises` | Listar todas las franquicias | 200 |
| `GET` | `/franchises/{franchiseId}` | Obtener franquicia por ID | 200 |
| `PATCH` | `/franchises/{franchiseId}/name` | Actualizar nombre ⭐ | 200 |
| `GET` | `/franchises/{franchiseId}/products/max-stock` | Producto con más stock por sucursal | 200 |

### Sucursales

| Método | URL | Descripción | HTTP |
|--------|-----|-------------|------|
| `POST` | `/franchises/{franchiseId}/branches` | Agregar sucursal | 201 |
| `PATCH` | `/franchises/{franchiseId}/branches/{branchId}/name` | Actualizar nombre ⭐ | 200 |

### Productos

| Método | URL | Descripción | HTTP |
|--------|-----|-------------|------|
| `POST` | `/franchises/{franchiseId}/branches/{branchId}/products` | Agregar producto | 201 |
| `DELETE` | `/franchises/{franchiseId}/branches/{branchId}/products/{productId}` | Eliminar producto | 204 |
| `PATCH` | `/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Actualizar stock | 200 |
| `PATCH` | `/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` | Actualizar nombre ⭐ | 200 |

---

## 📬 Ejemplos de Peticiones

### Crear franquicia
```bash
curl -X POST http://localhost:8080/api/v1/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Colombia"}'
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Franchise created!",
  "data": {
    "id": "664abc123...",
    "name": "McDonald's Colombia",
    "branches": []
  },
  "timestamp": "2024-05-01T10:00:00"
}
```

### Agregar sucursal
```bash
curl -X POST http://localhost:8080/api/v1/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Chapinero Branch"}'
```

### Agregar producto
```bash
curl -X POST http://localhost:8080/api/v1/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Big Mac", "stock": 150}'
```

### Actualizar stock
```bash
curl -X PATCH http://localhost:8080/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 200}'
```

### Eliminar producto
```bash
curl -X DELETE http://localhost:8080/api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}
```

### Producto con más stock por sucursal
```bash
curl http://localhost:8080/api/v1/franchises/{franchiseId}/products/max-stock
```

**Respuesta:**
```json
[
  {
    "branchId": "abc123",
    "branchName": "Chapinero Branch",
    "productId": "xyz789",
    "productName": "Big Mac",
    "stock": 200
  },
  {
    "branchId": "def456",
    "branchName": "Salitre Branch",
    "productId": "uvw321",
    "productName": "McFlurry",
    "stock": 80
  }
]
```

### Ejemplo de error de validación
```json
{
  "success": false,
  "message": "Error de validación en los datos de entrada",
  "data": {
    "name": "El nombre es obligatorio",
    "stock": "El stock no puede ser negativo"
  },
  "timestamp": "2024-05-01T10:00:00"
}
```

---

## 🧪 Ejecutar Tests

```bash
# Ejecutar todos los tests (usa MongoDB embebido automáticamente)
mvn test
```

---

## 📝 Decisiones de diseño

**¿Por qué MongoDB Atlas y no una instancia local?**
Atlas es un servicio administrado en la nube que elimina la necesidad de gestionar infraestructura de base de datos. Ofrece alta disponibilidad, backups automáticos y escalado sin configuración adicional. Para esta prueba se usa el tier gratuito M0.

**¿Por qué documentos embebidos?**
Sucursales y productos viven dentro del documento de Franquicia. MongoDB garantiza atomicidad a nivel de documento, por lo que todas las operaciones (agregar, modificar, eliminar) son una sola escritura atómica sin necesidad de transacciones multi-documento.

**¿Por qué Spring WebFlux?**
Permite manejar más peticiones concurrentes con menos hilos, usando un modelo de I/O no bloqueante. Es especialmente eficiente cuando la aplicación pasa mucho tiempo esperando respuestas de la base de datos, como ocurre con MongoDB Atlas en la nube.