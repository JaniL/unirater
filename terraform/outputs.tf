output "container_endpoint" {
  description = "Public HTTPS endpoint of the unirater container."
  value       = scaleway_container.unirater.public_endpoint
}

output "database_endpoint" {
  description = "Endpoint of the Serverless SQL database."
  value       = scaleway_sdb_sql_database.unirater.endpoint
}

output "registry_endpoint" {
  description = "Registry endpoint to push images to."
  value       = scaleway_registry_namespace.unirater.endpoint
}

output "image_reference" {
  description = "Full image reference used by the container."
  value       = "${scaleway_registry_namespace.unirater.endpoint}/unirater:${var.image_tag}"
}

output "database_url" {
  description = "Connection string for the database (secret)."
  value       = local.database_url
  sensitive   = true
}