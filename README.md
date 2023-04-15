Ktor Http clients starter
==============================

This starter contains auto-configuration which enables seamless configuration of multiple
ktor http client.

Clients are created using ktor plugin system.

Usage
==============================
Add dependency to your project(For now it needs to be pushed to local maven repository):
```
    implementation("io.github.sobelek:ktor-clients-spring-boot-starter:0.1.2")
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
