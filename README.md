# Spring WebFlux Response Wrapper
This library allows you to wrap the content of REST responses in the format:
```json
{
    "ok": true,
    "data": "String/Number/Object/List"
}
```
...or, in case of an error:
```json
{
    "ok": false,
    "error": {
        "value": 500,
        "message": "..."
    }
}
```

> It will also replace not found paths with a 404 error response.

## Add dependency
### Maven
**1.** Add the JitPack repository to your _pom.xml_ file:
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
**2.** Add dependency:
```xml
<dependency>
    <groupId>com.github.giuseppe998e</groupId>
    <artifactId>spring-webflux-response-wrapper</artifactId>
    <version>v0.2.1</version>
</dependency>
```

### Gradle
**1.** Add the JitPack repository to your _build.gradle_ file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
**2.** Add dependency:
```groovy
dependencies {
    implementation 'com.github.giuseppe998e:spring-webflux-response-wrapper:v0.2.1'
}
```

## Usage
### Kotlin
**1.** Import the configuration class:
```kotlin
package com.example.spring

import brc.webflux.response.wrapper.ResponseWrapper
//...

@SpringBootApplication
@Import(ResponseWrapper::class)
class ExampleMicroserviceApplication

fun main(args: Array<String>) {
    runApplication<ExampleMicroserviceApplication>(*args)
}
```
**2.** Create a REST controller:
```kotlin
package com.example.spring

import brc.webflux.response.wrapper.model.Response
// ...

@Controller
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
class ExampleRestController {
    /*
     * Returns:
     * ```
     * {"ok":true,"data":"Example Response"}
     * ```
     */
    @GetMapping
    fun defaultHandler(): Mono<String> = Mono.just("Example Response")
    
    /*
     * Returns:
     * ```
     * {"ok":true,"data":["Example Response #1","Example Response #2","Example Response #3"]}
     * ```
     */
    @GetMapping("/flux")
    fun fluxHandler(): Flux<String> = Flux.fromArray(
        arrayOf(
            "Example Response #1",
            "Example Response #2",
            "Example Response #3"
        )
    )
    
    /*
     * Returns:
     * ```
     * {"ok":false,"error":{"value":500,"message":"Internal Server Error"}}
     * ```
     */
    @GetMapping("/error")
    fun errorHandler(): Mono<String> =
        Mono.error(Exception("This error will be logged but not returned (Code: 500 - INTERNAL SERVER ERROR)"))
    
    /*
     * Returns:
     * ```
     * {"ok":false,"error":{"value":400,"message":"This error will NOT be logged, but returned"}}
     * ```
     */
    @GetMapping("/error/custom")
    fun customErrorHandler(): Mono<String> =
        Mono.error(Response.Error(HttpStatus.BAD_REQUEST, "This error wont be logged but returned"))
}
```
