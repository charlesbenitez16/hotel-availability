# Hotel Availability Search API

Servicio backend que registra búsquedas de disponibilidad hotelera y responde
cuántas búsquedas idénticas se han hecho hasta el momento. Cada búsqueda que
entra por la API se publica en Kafka y un consumidor la persiste de forma
asíncrona en PostgreSQL; la consulta de conteo lee siempre desde la base de
datos.

## Stack tecnológico

- Java 21 · Spring Boot 3.5.16
- PostgreSQL 16 + Flyway (migraciones de esquema)
- Apache Kafka 3.8 (modo KRaft, sin ZooKeeper)
- springdoc-openapi / Swagger UI
- JUnit 5 · Mockito · AssertJ · Spring Kafka Test · Awaitility
- JaCoCo (gate de cobertura ≥ 80 %)
- Docker · Docker Compose

## Arquitectura

Arquitectura hexagonal en un único módulo Maven, con tres capas:

```
domain/           Modelo y reglas de negocio puras, sin frameworks.
                  Records inmutables: HotelSearchQuery, RegisteredSearch, SearchCount.
                  Puertos de entrada (casos de uso) y de salida (repositorio,
                  publicador de eventos, generador de id).

application/      Implementación de los casos de uso. Solo orquesta: no contiene
                  reglas de negocio.

infrastructure/   Adaptadores que conectan el dominio con el exterior:
                    in/web           controlador REST, DTOs, validación, manejo de errores
                    in/messaging     consumidor de Kafka
                    out/messaging    productor de Kafka + DTO de transporte
                    out/persistence  entidad JPA, repositorio Spring Data, mapper
                    out/id           generación de searchId (UUID)
                    config           configuración de Spring
```

Las dependencias apuntan siempre hacia el dominio: `application` solo conoce a
`domain`, e `infrastructure` conoce a ambos a través de los puertos. El dominio
no depende de nada del proyecto.

### Flujo de una búsqueda

1. `POST /search` valida la petición, construye el objeto de dominio y lo
   publica en el topic `hotel_availability_searches`. Devuelve el `searchId` al
   instante, sin tocar la base de datos.
2. El consumidor de Kafka recibe el mensaje y guarda la búsqueda en PostgreSQL
   (la escritura corre sobre un hilo virtual).
3. `GET /count` lee desde la base de datos y cuenta cuántas búsquedas comparten
   exactamente el mismo hotel, las mismas fechas y las mismas edades **en el
   mismo orden**.

Como la persistencia es asíncrona, una llamada a `GET /count` hecha justo
después del `POST /search` puede devolver `404` durante una fracción de
segundo, hasta que el consumidor procesa el mensaje. Es el comportamiento
esperado.

## Endpoints

Base URL: `http://localhost:8080`

### `POST /search`

Registra una nueva búsqueda.

Petición:
```json
{
  "hotelId": "1234aBc",
  "checkIn": "29/12/2023",
  "checkOut": "31/12/2023",
  "ages": [30, 29, 1, 3]
}
```

Respuesta `201 Created`:
```json
{ "searchId": "3f29b6b0-df7b-4e3a-9c9a-2f7e9a6a9a11" }
```

Responde `400 Bad Request` si `hotelId` está vacío, si `checkIn` no es anterior
a `checkOut`, o si las fechas no llegan en formato `dd/MM/yyyy`.

### `GET /count?searchId={id}`

Devuelve cuántas búsquedas idénticas se han registrado.

Respuesta `200 OK`:
```json
{
  "searchId": "3f29b6b0-df7b-4e3a-9c9a-2f7e9a6a9a11",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "29/12/2023",
    "checkOut": "31/12/2023",
    "ages": [30, 29, 1, 3]
  },
  "count": 100
}
```

Responde `404 Not Found` si el `searchId` no existe o todavía no ha sido
consumido.

## Cómo levantar la aplicación

Solo hace falta Docker.

```bash
docker-compose up --build -d --force-recreate
```

Compila el proyecto dentro de Docker y arranca tres contenedores —PostgreSQL,
Kafka y la aplicación— con healthchecks para que la app espere a que sus
dependencias estén listas. Al terminar, la API responde en
`http://localhost:8080`.

Para detener todo y borrar los datos de PostgreSQL:

```bash
docker-compose down -v
```

## Probar los endpoints con Swagger UI

Con la aplicación corriendo, abrir:

**http://localhost:8080/swagger-ui.html**

Desde ahí se ejecutan los dos endpoints con el botón **"Try it out"**: se edita
el cuerpo de la petición y se ve la respuesta, los códigos de estado y los
esquemas documentados. La especificación OpenAPI en crudo está en
`http://localhost:8080/v3/api-docs`.

Flujo mínimo de prueba:

1. En `POST /search`, enviar el ejemplo y copiar el `searchId` de la respuesta.
2. En `GET /count`, pegar ese `searchId`. Si responde `404`, esperar un segundo
   y repetir: el mensaje aún se está consumiendo.
3. Repetir el mismo `POST /search` varias veces y volver a consultar
   `GET /count`: el contador sube de a uno por cada búsqueda idéntica.
4. Enviar las mismas edades en otro orden (`[3, 29, 30, 1]`): esa búsqueda
   cuenta por separado.

## Ejecutar las pruebas

Con Docker, sin instalar Java ni Maven:

```bash
docker run --rm -v "$(pwd)":/workspace -w /workspace maven:3.9-eclipse-temurin-21 mvn clean verify
```

Con JDK 21 y Maven instalados en local:

```bash
mvn test      # solo las pruebas unitarias
mvn verify    # suite completa + reporte de cobertura + gate del 80 %
```

Qué cubre la suite:

- Pruebas unitarias del modelo de dominio, los servicios de aplicación y cada
  adaptador (mapper, validación, controlador con `@WebMvcTest`, productor y
  consumidor de Kafka).
- Un `@DataJpaTest` sobre H2 en memoria que verifica la lógica de conteo de
  `/count`, incluida la sensibilidad al orden de las edades.
- Una prueba de punta a punta (`SearchFlowIntegrationTest`) con `@EmbeddedKafka`
  que lanza `POST /search` por HTTP, espera a que el consumidor persista el
  mensaje y comprueba que `GET /count` lo refleja.

## Reporte de cobertura (JaCoCo)

El reporte se genera al ejecutar `mvn verify`:

```bash
mvn verify
# o, con Docker:
docker run --rm -v "$(pwd)":/workspace -w /workspace maven:3.9-eclipse-temurin-21 mvn clean verify
```

Al terminar, abrir el informe HTML:

```
target/site/jacoco/index.html
```

`mvn verify` **hace fallar el build si la cobertura de líneas, ramas, métodos o
instrucciones baja del 80 %** (reglas del `jacoco-maven-plugin` en `pom.xml`).
El gate está fuera del build de la imagen Docker (`docker-compose up`), así que
una cobertura al límite nunca bloquea el arranque de la aplicación.

Quedan excluidas del cálculo las clases sin lógica: DTOs web, cuerpo de error,
clases `@Configuration` y el punto de entrada de Spring Boot.

## Desarrollo local (sin Docker)

Requiere un PostgreSQL y un Kafka accesibles (por ejemplo
`docker-compose up postgres kafka`) más JDK 21 y Maven:

```bash
mvn spring-boot:run
```

Los datos de conexión se pueden sobrescribir con variables de entorno
(`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`,
`KAFKA_BOOTSTRAP_SERVERS`). Los valores por defecto están en
`src/main/resources/application.properties`.
