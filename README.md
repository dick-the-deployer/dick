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
curl https://dickthedeployer.com/deploy | bash -s
```
After installation of database and web module follow printed instructions for worker installation.

For full options:

```
curl -s https://dickthedeployer.com/deploy | bash -s -- -h
```
## Documentation

Full docs are available at [http://dickthedeployer.com](http://dickthedeployer.com).

## Why another CD tool?

Nowadays there exists several good, fully-blown tools for Continuous Integration. Unfortunately these tools are built in a way that makes it really hard to build Continuous Delivery process around them, lacking scalability, pipelines or quick and easy configuration. 

Dick the Deployer addresses these issues, providing relatively simple on-premises service. Main features delivered by Dick are:

* High Scalability through distributed architecture.
* Reactive interface.
* Familiar concepts like projects and groups.
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

Dick the Deployer consists of three modules:

### Web
`Web` is a java-based application used to present and manage pipelines, assign jobs and present results.

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
