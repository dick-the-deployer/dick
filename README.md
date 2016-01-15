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
curl https://raw.githubusercontent.com/dick-the-deployer/dickthedeployer.com/master/static/deploy | bash -s
```
After installation of database and web module follow printed instructions for worker installation.

For full options:

```
curl -s https://raw.githubusercontent.com/dick-the-deployer/dickthedeployer.com/master/static/deploy | bash -s -- -h
```
## Documentation

Full docs are available at [Dick the Deployer](http://dickthedeployer.com).

## Why another CD tool?

Nowadays there exists several good, fully-blown tools for Continuous Integration. Unfortunately these tools are built in a way that makes it really hard to build Continuous Delivery process around them, lacking scalability, pipelines or quick and easy configuration. 

Dick the Deployer addresses these issues, providing relatively simple on-premises service. Main features delivered by Dick are:

* High Scalability through distributed architecture.
* Reactive interface.
* Familiar concepts like projects and groups.
* Configuration stored inside the project repository.

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
