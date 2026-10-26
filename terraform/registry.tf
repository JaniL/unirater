# Container registry for the unirater image.
resource "scaleway_registry_namespace" "unirater" {
  name        = "unirater"
  description = "Registry for the UniRater Go image"
  is_public   = false
}