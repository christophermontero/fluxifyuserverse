<p align="center"><img src="assets/fluxifyuserverse.png" alt="logo"></p>

# FluxifyUserverse API

- [Architecture](#architecture)
    - [Domain](#domain)
    - [Use cases](#use-cases)
    - [Infrastructure](#infrastructure)
        - [Helpers](#helpers)
        - [Driven adpaters](#driven-adapters)
        - [Entry points](#entry-points)
    - [Application](#application)
    - [Requirements](#requirements)
- [Run locally](#run-locally)
    - [Install the project](#install-the-project)
    - [Create the infrastructure](#create-the-infrastructure)
    - [Set up the environment variables](#set-up-the-environment-variables)
    - [Compile](#compile-the-project)
    - [Run the server](#run-the-server)
    - [Access the server](#access-the-server)
- [Support](#support)
- [License](#license)
- [Author](#author)

## Architecture

### Domain

It is the most internal module of the architecture, it belongs to the domain layer and encapsulates the business logic
and rules. Through domain models and entities.

### Use cases

This gradle module belonging to the domain layer implements the system use cases, defines application and reacts to
invocations from the entry points module, orchestrating the flows to the entities.

### Infrastructure

#### Helpers

In the helpers section we will have general utilities for Driven Adapters and Entry Points.

These utilities are not rooted in specific objects; generics are used to model behaviors. generics of the different
persistence objects that may exist, this type of implementations are carried out based on the pattern of
design [Unit of Work and Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

These classes cannot exist alone and their sharing in **Driven Adapters** must be inherited.

#### Driven adapters

The driven adapters represent implementations external to our system, such as connections to rest services, soap,
databases, reading flat files, and specifically any origin and data source with which we must interact.

#### Entry points

Entry points represent the entry points of the application or the beginning of business flows.

### Application

This module is the most external of the architecture, it is responsible for assembling the different modules, resolving
the dependencies and create the use case beans (UseCases) automatically, injecting these instances specificities of the
declared dependencies. It also starts the application. It is the only module in the project where we will find the
function "public static void main(String[ ] args)".

**The use case beans are automatically made available thanks to an '@ComponentScan' located in this layer.**

Read the complete
article [Clean Architecture - Isolating the details](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

## Requirements

- **Java:** v17.0.9

- **Gradle:** v7.4.2

- **Docker:** v24.0.7

- **Docker compose:** v2.23.1

- **AWS cli:** v2.13.38

# Run locally

To run this Spring Boot project locally on your development machine, you can follow these steps:

## Install the project

To install the project simply unzip the zip file:

**shell**

```
git clone https://github.com/christophermontero/fluxifyuserverse.git
```

## Create the infrastructure

Navigate to the project infrastructure directory using your terminal or command prompt:

**shell**

```
cd deployment/fluxifyuserverse
```

Create the infrastructure using the following command:

**shell**

```
sudo docker compose -f infra.yml up -d
```

You can destroy the containers with the following command:

**shell**

```
sudo docker compose -f infra.yml down
```

To stop the containers use the command:

**shell**

```
sudo docker compose -f infra.yml stop
```

To start the containers use the command:

**shell**

```
sudo docker compose -f infra.yml start
```

When the infrastructure services are created use the aws cli to create the following resources

### SQS

**shell**

```
aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name users-queue
```

### DynamoDB Table

```
aws --endpoint-url=http://localhost:4566 dynamodb create-table --table-name Users --attribute-definitions AttributeName=Id,AttributeType=N --key-schema AttributeName=Id,KeyType=HASH -- provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --region us-east-1
```

To verify the creation of the table you can use the following command:

```
aws --endpoint-url=http://localhost:4566 dynamodb describe-table --table-name Users
```

## Set up the environment variables

In application.yml file, edit the following environment variables:

- **adapter.postgres.database** Database name
- **adapter.postgres.schema** Database schema
- **adapter.postgres.username** Database username
- **adapter.postgres.password** Database password
- **adapter.sqs.region** AWS region
- **adapter.sqs.queueUrl** AWS SQS url
- **adapter.sqs.endpoint** AWS endpoint

## Compile the project

Navigate to the root directory of the project using your terminal or command prompt:

**shell**

```
./gradlew build
```

## Run the server

You can run the Spring Boot application using the following Gradle command:

**shell**

```
./gradlew bootRun
```

## Access the server

Once the application is running, you can make API requests to
the specified _endpoints_. The default port for Spring Boot applications is usually 8080. Please note that the actual
URL may vary depending on your project configuration.

## Endpoints

```
* Users:
  * GET /api/v1/users
  * GET /api/v1/users/{id}
  * GET /api/v1/users?name={username}
  * POST /api/v1/users
```

# License

This project is under [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

# Support

If you have any feedback, please reach out at _cgortizm21@gmail.com_.

# Aknowledgements

- [Bancolombia scaffolding](https://github.com/bancolombia/scaffold-clean-architecture)

# Author

- [@christophermontero](https://github.com/christophermontero)