# oracle-crud-service

Spring Boot 4.1 REST CRUD service backed by Oracle Database, built with Maven and targeting JDK 21.

Layers: `entity` → `repository` (Spring Data JPA) → `service` (interface + impl) → `controller` (REST). The
sample domain is a `Product` (id, name, description, price, quantity, timestamps) - swap it for your own
entity by following the same four-file pattern.

## Requirements

- JDK 21
- Maven 3.9+
- An Oracle Database instance reachable from wherever you run the app (Oracle XE, Autonomous DB, or a
  company instance)

## Configure the database connection

No credentials are hard-coded. `src/main/resources/application.properties` reads everything from
environment variables, with local-dev fallbacks:

| Variable | Purpose | Example |
|---|---|---|
| `DB_URL` | JDBC URL | `jdbc:oracle:thin:@//myhost:1521/ORCLPDB1` |
| `DB_USERNAME` | DB user | `app_user` |
| `DB_PASSWORD` | DB password | `********` |
| `SERVER_PORT` | HTTP port (optional) | `8080` |

Set them before starting the app, e.g. on macOS/Linux:

```bash
export DB_URL="jdbc:oracle:thin:@//localhost:1521/ORCLPDB1"
export DB_USERNAME="app_user"
export DB_PASSWORD="change_me"
```

On Windows PowerShell:

```powershell
$env:DB_URL="jdbc:oracle:thin:@//localhost:1521/ORCLPDB1"
$env:DB_USERNAME="app_user"
$env:DB_PASSWORD="change_me"
```

Or pass them as `-D` system properties / your IDE's run configuration / a `.env` file loaded by your
container orchestrator - whatever fits your workflow. Just don't commit real credentials.

## Run it

```bash
mvn spring-boot:run
```

With `spring.jpa.hibernate.ddl-auto=update` (the default in this project), Hibernate creates the
`PRODUCTS` table and `PRODUCT_SEQ` sequence for you on first boot - handy for local dev. For anything
shared or production, switch that to `validate`/`none` and manage the schema yourself (see
`src/main/resources/sql/schema-oracle.sql` for the equivalent DDL, or adopt Flyway/Liquibase).

## Endpoints

Base path: `/api/v1/products`

| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/api/v1/products` | `ProductRequestDTO` | Create a product |
| GET | `/api/v1/products` | – | List all products |
| GET | `/api/v1/products/{id}` | – | Get one product |
| PUT | `/api/v1/products/{id}` | `ProductRequestDTO` | Update a product |
| DELETE | `/api/v1/products/{id}` | – | Delete a product |

Example create request:

```json
{
  "name": "Wireless Mouse",
  "description": "2.4GHz wireless mouse",
  "price": 29.90,
  "quantity": 100
}
```

Validation errors (400) and not-found errors (404) are returned as a consistent JSON error body via
`GlobalExceptionHandler`.

## Notes / things to revisit before production

- **Spring Boot 4.x starter rename**: the web starter is `spring-boot-starter-webmvc` here, not the
  `spring-boot-starter-web` name used on Spring Boot 3.x - that's intentional, not a typo.
- **Lombok**: install the Lombok IDE plugin (IntelliJ/Eclipse) so `@Getter`/`@Setter`/`@Builder` etc.
  resolve in your editor; Maven itself needs no extra setup.
- **Security**: no authentication/authorization is included. Add `spring-boot-starter-security` (or your
  gateway's auth) before exposing this beyond local dev.
- **ddl-auto**: see the JPA/Hibernate note above - fine for dev, not for shared environments.
- **Oracle driver version**: pinned to `ojdbc11:23.5.0.24.07`; bump it to match whatever your DBA team
  certifies against your Oracle version.
