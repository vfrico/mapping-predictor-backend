# Mapping predictor backend

*This project is part of my work done during my Google 
Summer of Code 2018 in collaboration with DBpedia*

This project contains all the API calls that can be done
with from the API and some others only accesible with an
external HTTP client, like [Insomnia](https://insomnia.rest/).

## API documentation
The API has been documented using Sphynx. Can be consulted
on the `/docs` folder on this repo or online on 
[Github Pages](https://vfrico.github.io/mapping-predictor-backend/)

## Develop environment
This is a Java EE project, so it is recommended to use an 
advanced IDE, as Intellij IDEA. To run the application, 
as a Java EE implementation is required, I recommend to use
Glassfish 5.0.1 or higher.

## Docker environment
To ease the development process, as well as the deploying step,
I've containerized this application with Docker.

This container depends from a SQL database. I recommend to use
MySQL. It supports version 8 or higher. Also, other MySQL 
implementations like MariaDB have been also tested successfully.

Docker compose is used to deploy the full web application, 
including both backend and frontend containers.

```  
$ docker-compose up -d
```

To build the standalone docker image:
```
$ docker build -t vfrico/dbpedia-mappings-backend:latest .
```

The image is hosted on the docker registry as: 
[vfrico/dbpedia-mappings-backend](https://hub.docker.com/r/vfrico/dbpedia-mappings-backend/)

## Installation of the backend
In order to the backend to work properly, it is needed to 
install on the database the tables that the system is going
to need. Is also possible to load a set of sample annotations.

Both tasks can be done after the backend container is ready
on this URI. Click first on *Create tables* button and after
a few seconds, on *add CSV*.
```
http://ip:8080/predictor
```

Alternatively, you can use an HTTP client to submit the 
following requests:

* POST /installation/createtables
* POST /installation/addAllCSV

  
## HTTP Client
It is recommended to use an HTTP client like [Insomnia](https://insomnia.rest/).
It is licensed under the GPLv3 license and has a very handful
way to manage environment variables and management of
HTTP requests by organizing them on folders.

You can download the Insomnia environment with all API
calls loaded and organised in folders on this repository.

