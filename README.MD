# Library Management System

## In the library-management-service we combine Scala with technologies such as 
## PostgreSQL, Flyway, Doobie, Ciris, Cats IO, HTTPS, ZIO-JSON, and Cake Pattern.

### Library Domain Model(UML Class Diagram Example) 
https://www.uml-diagrams.org/library-domain-uml-class-diagram-example.html

### Run the postgres docker image(from the library-management-system/docker-compose.yml path) before starting the application
docker compose up

#### Create book:
curl --location 'http://localhost:8080/api/v1/books' \
--header 'Content-Type: application/json' \
--data '{
"isbn":"6bcbca1e-efd3-411d-9f7c-14b872444fce",
"name":"Zack Snyder'\''s Justice League",
"subject":"Fantasy",
"overview":"",
"publisher":"Zack Snyder",
"publicationDate":"2020-01-08",
"lang":"English"
}'

### Get book:
curl --location 'http://localhost:8080/api/v1/books/6bcbca1e-efd3-411d-9f7c-14b872444fce/Fantasy' \
--data ''

### Create book item:
curl --location 'http://localhost:8080/api/v1/items' \
--header 'Content-Type: application/json' \
--data '{
"barcode": "6bcbca1e-efd3-411d-9f7c-14b872444fcd",
"tag": "6baa3493-0564-40b7-9751-b2658b141a61",
"isbn": "6bcbca1e-efd3-411d-9f7c-14b872444fce",
"subject": "Fantasy",
"title": "Zack Snyder'\''s Justice League",
"isReferenceOnly": false,
"lang": "English",
"numberOfPages": 370,
"format": "Paperback",
"borrowed": "2024-05-01",
"dueDate": "2024-05-16"
}'

### Get book item:
curl --location 'http://localhost:8080/api/v1/items/6bcbca1e-efd3-411d-9f7c-14b872444fcd/6baa3493-0564-40b7-9751-b2658b141a61'