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
