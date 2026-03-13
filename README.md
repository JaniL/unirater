UniRater 29.10.2014
----------------------------------------------------------------------------

[![Build Status](https://travis-ci.org/JaniL/wepaharkka.svg?branch=master)](https://travis-ci.org/JaniL/wepaharkka)
[![Coverage Status](https://img.shields.io/coveralls/JaniL/wepaharkka.svg)](https://coveralls.io/r/JaniL/wepaharkka)

UniRater on webpalvelu, jossa käyttäjät voivat arvostella ja katsoa
arvosteluja kyseisenä päivänä tarjolla oleviiin ruokiin eri Unicafe ravintoloissa.
Käyttäjät saavat ravintolan valittuaan tarjolle kyseisen päivänä ruuat ja
ruokien arvosteluiden keskiarvot. Arvostelut kirjataan asteikolla 0-5.

Tietokannasta on myös mahdollista tehdä kootusti hakuja etsien parhaiten
arvosteltuja ravintoloita ja parhaiten arvosteltuja ruokia. 

Run locally at `http://localhost:8080/` (see Podman instructions below).

----------------------------------------------------------------------------

UniRater ota yhteyttä:

IRCnet:
Lesandre, Janluu, Coolness, neodyymi

----------------------------------------------------------------------------

## Running with Podman

The UniCafe integration now uses the current API endpoint:

- `https://unicafe.fi/wp-json/swiss/v1/restaurants/?lang=fi`

Container runtime defaults to `dev` profile so menus are fetched on startup.

### Build image

```bash
podman build --format docker -t unirater:latest .
```

### Run container

```bash
podman run --rm -p 8080:8080 --name unirater unirater:latest
```

### Run with podman-compose

```bash
podman-compose up --build
```

### Health check

- Endpoint: `http://localhost:8080/health`
- Expected response: `ok`
