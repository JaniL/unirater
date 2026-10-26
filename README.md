# UniRater

Go rewrite of the original Spring Boot service (the Java tree is kept on
`master`): rate Unicafe restaurants and their daily menus, find the
restaurant nearest to you, and browse menus per restaurant.

Runs on Scaleway as a serverless container backed by a Serverless SQL
Database (PostgreSQL), deployable with Terraform. All container work uses
[podman](https://podman.io/).

## Configuration

| Variable            | Default    | Description                                                          |
| ------------------- | ---------- | -------------------------------------------------------------------- |
| `PORT`              | `8080`     | HTTP listen port.                                                    |
| `DATABASE_URL`      | required   | PostgreSQL connection string.                                         |
| `SESSION_SECRET`    | random     | Session cookie signing key; set it in production.                     |
| `SYNC_ON_STARTUP`  | `false`    | Fetch all UniCafe menus at startup (old dev-profile behavior).       |
| `GEOFENCE_ENABLED` | `false`    | Restrict auth and write operations to Finnish IP addresses.          |
| `TRUST_PROXY`      | `true`     | Read the client address from `X-Real-IP` (overwritten by Scaleway).  |

Menus are synced from the UniCafe API
(`https://unicafe.fi/wp-json/swiss/v1/restaurants/?lang=fi`): lazily on
first use, and manually via `GET /unicafe/saveall`.

## Routes

| Route                                    | Description                                        |
| ---------------------------------------- | -------------------------------------------------- |
| `GET /`                                  | Geolocation landing page.                          |
| `GET /{restaurantUri}`                   | Restaurant menu page (e.g. `/exactum`).            |
| `GET /json/closest/{lon}/{lat}`           | Nearest restaurant, JSON `{"uri": ...}`.           |
| `GET /json/etsiUri/{name}`                | Restaurant URI by name.                             |
| `GET /rate/{foodId}/{vote}`               | Vote +1 (`vote=1`) or -1 (`vote=0`), per user.     |
| `GET /health`                            | Health check, answers `ok`.                        |
| `GET /user/login`, `POST /user/login/authenticate` | Login form / form login.                  |
| `GET`+`POST /user/register`             | Registration (bcrypt passwords, sessions, CSRF).    |
| `GET /user/logout`                       | Logout.                                             |
| `GET /unicafe/saveall`, `GET /unicafe/stats` | Manual menu sync / menu count.                  |

### Geofence (feature flag)

With `GEOFENCE_ENABLED=true`, login, registration, voting and
`/unicafe/saveall` answer `403` unless the client address is Finnish.
The check uses embedded aggregated CIDR snapshots of the Finnish address
space from [ipverse/country-ip-blocks] and the `X-Real-IP` header, which
the Scaleway gateway overwrites on every request. Loopback and private
addresses always pass, so local development is unaffected. Refresh the
snapshots with `make gen-fi`.

## Local development (podman)

```bash
podman compose up --build   # app + postgres:16
curl localhost:8080/health   # -> ok
```

Or without containers: `DATABASE_URL=postgres://... go run ./cmd/unirater`.

```bash
make test    # unit tests
make fmt     # gofmt
make vet     # go vet
```

## Deploying on Scaleway (Terraform)

State is local (`terraform/terraform.tfstate`, gitignored). Credentials
come from the `scw` CLI config or `SCW_*` environment variables.

First apply creates the registry namespace, IAM application + policy and
the Serverless SQL database (the container deploy will report an error
until the image exists):

```bash
make tf-init
make tf-apply TAG=v1            # infra + db (container may error: image missing)
make image TAG=v1               # podman build
make push  TAG=v1               # podman login + push to the registry
make tf-apply TAG=v1            # container deploys for real
curl "$(terraform -chdir=terraform output -raw container_endpoint)/health"
```

Provisioned:

- `scaleway_registry_namespace` — image storage
- `scaleway_iam_application` + `scaleway_iam_api_key` + `scaleway_iam_policy`
  (`ServerlessSQLDatabaseReadWrite`) — database credentials, IAM-based
  (no SQL users)
- `scaleway_sdb_sql_database` — PostgreSQL-compatible serverless database,
  `min_cpu = 0`, `max_cpu = 8`
- `scaleway_container` — scale-to-zero (`min_scale = 0`, `max_scale = 2`),
  128 MiB, port 8080, `GEOFENCE_ENABLED=true`

[ipverse/country-ip-blocks]: https://github.com/ipverse/country-ip-blocks