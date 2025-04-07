docker network create --driver bridge --attachable api_user_network
docker network create --driver bridge --attachable api_box_network
docker network create --driver bridge --attachable api_flat_network
docker network create --driver bridge --attachable gateway_network

docker network create --driver bridge --attachable service_grafana
docker network create --driver bridge --attachable service_prometheus
docker network create --driver bridge --attachable service_keycloak_network
docker network create --driver bridge --attachable service_postgres_network
docker network create --driver bridge --attachable service_fss
docker network create --driver bridge --attachable service_elk

mkdir -p ~/docker/volumes/minio
mkdir -p ~/docker/volumes/kibana
mkdir -p ~/docker/volumes/logstash
mkdir -p ~/docker/volumes/postgres
mkdir -p ~/docker/volumes/elasticsearch
