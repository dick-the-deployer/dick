[![][travis img]][travis]

# Dick the Deployer
Continuous Delivery Service

Dick the Deployer enables scalable and easy Continuous Delivery.

## Quick Start
You will need machine with Docker and about three minutes.

There is a deploy script provided on the [Web Repository](https://github.com/dick-the-deployer/dickthedeployer.com) meant
for quick installation.

> Note: you must already have a Docker available.

```
curl https://raw.githubusercontent.com/dick-the-deployer/dickthedeployer.com/master/deploy | bash -s
```
After installation of database and web module follow printed instructions for worker installation.

For full options:

```
curl -s https://raw.githubusercontent.com/dick-the-deployer/dickthedeployer.com/master/deploy | bash -s -- -h
```

## Why another CD tool?

Nowadays there exists several good, fully-blown tools for Continous Integration. Unfortunately these tools are build in a way that makes it really hard to build Continuous Delivery process aroud them, lacking scalability, pipelines or quick and easy configuration. 

Dick the Deployer address these issues, providing relatively simple on-premises service. Main features delivered by Dick are:

* High Scallablity through distributed architecture.
* Reactive interface.
* Familliar concepts like project's and group's.
* Configuration stored inside the project repository.


## Building

### Basic Compile and Test

To build the source you will need to install JDK 1.8.

Dick the Deployer uses maven for most buil-related tasks, so you should be able to start by cloning the project and
typing

```
./mvnw install
```

### How to develop 

As Dick the Deployer uses database you should have PostgresDB up and running. You can configure connection settings
in `dick/dick-we/src/main/resources/application.yml` file. 

The easiest way to run the project locally though is using debugger. This setup requires breakpoint in any context-based test,
changing breakpoint mode to `Suspend: thread` and simply debugging that test. In this case Dick will be using H2 database.

Front-end is based on node and grunt, and uses proxy server to communicate with Dick the Deployer. To start 
the front-end project you need to have Dick the Deployer working (normally or suspended on debugger), and then start 
`grunt serve` task in `dick-ui/nodejs/` directory. Please note that during the build node, npm and grunt are downloaded 
from the web, and located in `dick-ui/nodejs/node` and `dick-ui/nodejs/node-modules/grunt-cli`.

## Components

Dick the Deployer consist of three modules:

### Web
`Web` is a java-based application used to present and manage pipelines, assign job's and presenting results.

### Worker
`Worker` is a java-based application responsible for building jobs fetched from `Web`

### Database
`Web` uses postgres database.

## High Availability on AWS
To run Dick The Deployer in High Availability mode you need:
* AWS RDS Postgres database
* Replicated ec2 instances with docker serving `Web`
* Share private key used by `Web`, manually or via AWS EFS
* Load Balancer for `Web`
* As many as you need instances with docker for `Worker`s

[travis]:https://travis-ci.org/dick-the-deployer/dick
[travis img]:https://travis-ci.org/dick-the-deployer/dick.svg?branch=master
