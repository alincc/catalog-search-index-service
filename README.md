# catalog-search-index-service

# build
mvn clean package docker:build

# run
docker run -p 8080 -e spring.profiles.active=docker -i -t nb/catalog-search-index-rest
