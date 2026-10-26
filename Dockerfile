FROM golang:1.27-alpine AS build

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY cmd cmd
COPY internal internal

RUN CGO_ENABLED=0 go build -trimpath -ldflags="-s -w" -o /out/unirater ./cmd/unirater

FROM alpine:3.20

RUN apk add --no-cache ca-certificates wget

COPY --from=build /out/unirater /app/unirater

USER nobody:nobody

ENV PORT=8080

EXPOSE 8080

# Kept for local podman runs; the serverless platform ignores it.
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=5 \
  CMD wget -q -O /dev/null http://127.0.0.1:8080/health || exit 1

ENTRYPOINT ["/app/unirater"]