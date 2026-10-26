# Serverless SQL Database (PostgreSQL wire compatible).
#
# Connections authenticate with an IAM application instead of a SQL user:
# the application id is the username and the API key secret is the
# password (see the scaleway_sdb_sql_database docs).

resource "scaleway_iam_application" "unirater_db" {
  name        = "unirater-db"
  description = "Connects the unirater container to its Serverless SQL database"
}

resource "scaleway_iam_api_key" "unirater_db" {
  application_id = scaleway_iam_application.unirater_db.id
  description    = "Database credentials for unirater"
}

resource "scaleway_iam_policy" "unirater_db" {
  name           = "unirater-db-access"
  description    = "Allow the unirater IAM application to read and write its Serverless SQL database"
  application_id = scaleway_iam_application.unirater_db.id

  rule {
    permission_set_names = ["ServerlessSQLDatabaseReadWrite"]
  }
}

resource "scaleway_sdb_sql_database" "unirater" {
  name    = "unirater"
  min_cpu = 0
  max_cpu = 8
}

locals {
  # The sdb endpoint looks like postgres://host:port/unirater.
  database_url = "postgres://${scaleway_iam_application.unirater_db.id}:${scaleway_iam_api_key.unirater_db.secret_key}@${trimprefix(scaleway_sdb_sql_database.unirater.endpoint, "postgres://")}"
}