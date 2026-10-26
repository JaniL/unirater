TAG ?= latest

IPVERSE_URL = https://raw.githubusercontent.com/ipverse/country-ip-blocks/master/country/fi

.PHONY: test fmt fmt-check vet dev image push gen-fi tf-init tf-plan tf-apply

test: ## run unit tests
	go test ./...

fmt: ## format all Go sources
	gofmt -w .

fmt-check: ## check formatting
	@files=$$(gofmt -l .); if [ -n "$$files" ]; then echo "unformatted:"; echo "$$files"; exit 1; fi

vet: ## run go vet
	go vet ./...

dev: ## run the app + postgres locally with podman
	podman compose up --build

image: ## build the container image with podman
	podman build --format docker -t unirater:$(TAG) .

REGISTRY ?= $(shell terraform -chdir=terraform output -raw registry_endpoint 2>/dev/null)

push: image ## push the image to the Scaleway registry (terraform output must exist)
	podman login $(REGISTRY) || (echo "run 'podman login $(REGISTRY)' with your Scaleway credentials first" && exit 1)
	podman push unirater:$(TAG) $(REGISTRY)/unirater:$(TAG)

gen-fi: ## refresh the embedded Finland IP snapshots
	curl -sL $(IPVERSE_URL)/ipv4-aggregated.txt -o internal/geofence/data/fi-ipv4.txt
	curl -sL $(IPVERSE_URL)/ipv6-aggregated.txt -o internal/geofence/data/fi-ipv6.txt
	go test ./internal/geofence/

tf-init: ## terraform init
	terraform -chdir=terraform init

tf-plan: ## terraform plan
	terraform -chdir=terraform plan -var="image_tag=$(TAG)"

tf-apply: ## terraform apply
	terraform -chdir=terraform apply -var="image_tag=$(TAG)"

.DEFAULT_GOAL := fmt-check