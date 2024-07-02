## Library Management System

- In the library-management-service we combine Scala with technologies such as 
- PostgreSQL, Flyway, Doobie, Ciris, Cats IO, HTTP4S, ZIO-JSON, and Cake Pattern.

Library Domain Model(UML Class Diagram Example):
- https://www.uml-diagrams.org/library-domain-uml-class-diagram-example.html

Run the docker image and create the topics before starting the application:
- docker compose up

### API
Create book:
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

Get book:
curl --location 'http://localhost:8080/api/v1/books/6bcbca1e-efd3-411d-9f7c-14b872444fce/Fantasy' \
--data ''

Create book item:
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

Get book item:
curl --location 'http://localhost:8080/api/v1/items/6bcbca1e-efd3-411d-9f7c-14b872444fcd/6baa3493-0564-40b7-9751-b2658b141a61'

### Kafka
Create "library-management-item-extern-topic" topic:
- docker-compose exec kafka kafka-topics.sh --create --topic library-management-item-extern-topic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092

Create "library-management-item-intern-topic" topic:
- docker-compose exec kafka kafka-topics.sh --create --topic library-management-item-intern-topic --partitions 1 --replication-factor 1 --bootstrap-server localhost:9092

Stop docker containers:
- docker stop library-management-system-kafka-1 library-management-system-zookeeper-1 db

Remove docker containers:
- docker rm library-management-system-kafka-1 library-management-system-zookeeper-1 db

Add the messages to the "library-management-item-extern-topic" topic:
- docker-compose exec kafka kafka-console-producer.sh --topic library-management-item-extern-topic --broker-list localhost:9092

"""
{
"barcode" : "6bcbca1e-efd3-411d-9f7c-14b872444fce",
"tag" : "6baa3493-0564-40b7-9751-b2658b141a61",
"isbn" : "6bcbca1e-efd3-411d-9f7c-14b872444fce",
"subject" : "Fantasy",
"title" : "Zack Snyder's Justice League",
"isReferenceOnly" : false,
"lang" : "French",
"numberOfPages" : 370,
"format" : "Paperback",
"borrowed" : "2024-05-01",
"loanPeriod" : 15,
"dueDate" : "2024-05-16",
"isOverdue" : true
}
"""

Read the messages from the "library-management-item-extern-topic" topic: 
- docker-compose exec kafka kafka-console-consumer.sh --topic library-management-item-extern-topic --from-beginning --bootstrap-server localhost:9092

Read the messages from the "library-management-item-intern-topic" topic:
- docker-compose exec kafka kafka-console-consumer.sh --topic library-management-item-intern-topic --from-beginning --bootstrap-server localhost:9092
