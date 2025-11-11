# Cucumber Generator Web

Aplicación full-stack para gestionar casos de prueba con atributos dinámicos, plantillas Gherkin y generación de archivos `.feature`.

## Estructura

```
project-root/
├── backend/            # Aplicación Spring Boot
├── frontend/           # Aplicación Vue 3 + Vite
├── scripts/            # Utilidades para iniciar los servicios
└── pom.xml             # Proyecto raíz Maven (agrega el módulo backend)
```

## Requisitos

- Java 21
- Maven 3.9+
- Node.js 18+
- npm 9+

## Arrancar y detener la aplicación

1. **Backend (API Spring Boot)**
   ```bash
   ./scripts/start_backend.sh
   ```
   El script compila y levanta la API en `http://localhost:8080`. Para detenerla, regresa a la terminal donde se está ejecutando y presiona `Ctrl + C`.

2. **Frontend (Vue 3 + Vite)**
   ```bash
   ./scripts/start_frontend.sh
   ```
   El script instala dependencias (si es necesario) y levanta la UI en `http://localhost:5173`. Igual que con el backend, detén la ejecución con `Ctrl + C`.

> **Consejo:** Inicia primero el backend y después el frontend para que la interfaz pueda consumir la API.

## Backend

La API REST se expone en `http://localhost:8080/api/v1` y utiliza SQLite por defecto.

### Comandos útiles

```bash
cd backend
mvn spring-boot:run             # Ejecutar la API
mvn test                        # Ejecutar pruebas
```

### Perfiles de base de datos

- **SQLite (default):** `jdbc:sqlite:./data/app.db`
- **MySQL:** activar con `spring.profiles.active=mysql`

## Frontend

La interfaz Vue consume la API del backend y proporciona CRUD de tests, atributos y plantillas, además del generador de archivos `.feature`.

### Comandos útiles

```bash
cd frontend
npm install
npm run dev       # Desarrollo
npm run build     # Build de producción
npm run test:unit # Tests unitarios (Vitest)
```

## Scripts

```bash
./scripts/start_backend.sh   # Inicia la API
./scripts/start_frontend.sh  # Instala dependencias e inicia el frontend
```

## Funcionalidades principales

- Gestión de casos de prueba con atributos dinámicos.
- Definición de atributos reutilizables con validaciones.
- Editor de plantillas Gherkin con placeholders (`<remitente>`, `<destinatario>`, etc.).
- Generación de preview y descarga en ZIP de archivos `.feature` con políticas para valores faltantes.

## Tests

El backend incluye pruebas con JUnit 5 (ver `backend/src/test/java`).
