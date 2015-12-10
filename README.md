[![][travis img]][travis]

# Dick The Deployer
Continuous Delivery Tool

Dick The Deployer enables scalable and easy Continuous Delivery.

# Quick Start
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

# Components

Dick the Deployer consist of three modules:

## Web
Web is a java-based application used to present and manage pipelines, assign job's and presenting results.

## Worker
Worker is a java-based application responsible for building jobs fetched from `Web`.

## Database
Web uses postgres database.

# High Availability on AWS
To run Dick The Deployer in High Availability mode you need:
* AWS RDS Postgres database
* Replicated ec2 instances with docker serving `Web``
* Load Balancer for Web
* As many as you need instances with docker for `Workers`

[travis]:https://travis-ci.org/dick-the-deployer/dick
[travis img]:https://travis-ci.org/dick-the-deployer/dick.svg?branch=master
