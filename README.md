Ktor Http clients starter

[![build status](https://img.shields.io/github/actions/workflow/status/sobelek/ktor-clients-spring-boot-starter/ci.yml?branch=main)](https://github.com/sobelek/ktor-clients-spring-boot-starter/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sobelek/ktor-clients-spring-boot-starter)](https://mvnrepository.com/artifact/io.github.sobelek)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

==============================

This starter contains auto-configuration which enables seamless configuration of multiple
ktor http client.

Clients are created using ktor plugin system.

Usage
==============================
Add dependency to your project(For now it needs to be pushed to local maven repository):
```
    implementation("io.github.sobelek:ktor-clients-spring-boot-starter:0.1.4")
```

### Define clients using configuration
Sample `application.yml`

```yaml
http-clients:
  some-awesome-service:
    expectSuccess: true
    followRedirects: false
    requestTimeout: 2000
    connectionTimeout: 2000
    socketTimeout: 2000
    retryOn: server-error
    maxRetries: 3
    logLevel: all
```

This configuration will create a bean of type `HttpClient` named `http-client-some-awesome-service`.
It later be used with:

```
@Qualifier("http-client-some-awesome-service") awesomeServiceClient: HttpClient
```
