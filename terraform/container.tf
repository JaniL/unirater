resource "scaleway_container_namespace" "unirater" {
  name        = "unirater"
  description = "Namespace for the UniRater serverless container"
}

resource "random_password" "session_secret" {
  length  = 32
  special = false
}

resource "scaleway_container" "unirater" {
  namespace_id = scaleway_container_namespace.unirater.id
  name         = "unirater"
  description  = "UniRater: rate Unicafe restaurants and their daily menus"

  # Built and pushed with: make image TAG=<tag> && make push TAG=<tag>
  image = "${scaleway_registry_namespace.unirater.endpoint}/unirater:${var.image_tag}"

  port               = 8080
  protocol           = "http1"
  privacy            = "public"
  min_scale          = 0
  max_scale          = 2
  memory_limit_bytes = 134217728 # 128 MiB

  environment_variables = {
    PORT             = "8080"
    GEOFENCE_ENABLED = "true"
  }

  secret_environment_variables = {
    DATABASE_URL   = local.database_url
    SESSION_SECRET = random_password.session_secret.result
  }
}