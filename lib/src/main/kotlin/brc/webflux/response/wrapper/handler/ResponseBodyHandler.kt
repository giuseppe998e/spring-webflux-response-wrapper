package brc.webflux.response.wrapper.handler

import brc.webflux.response.wrapper.model.Response
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.web.reactive.HandlerResult
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class ResponseBodyHandler(
    writers: List<HttpMessageWriter<*>>, resolver: RequestedContentTypeResolver
) : ResponseBodyResultHandler(writers, resolver) {
    override fun supports(result: HandlerResult): Boolean =
        result.returnType.resolve() == Mono::class.java || result.returnType.resolve() == Flux::class.java

    override fun handleResult(exchange: ServerWebExchange, result: HandlerResult): Mono<Void> {
        val returnValue = Mono.justOrEmpty(result.returnValue)
            .defaultIfEmpty(Response(true, null, null))
            .flatMap { v ->
                (v as? Flux<*>)?.collectList()
                    ?: (v as? Mono<*>)
                    ?: Mono.error(ClassCastException("The response body must be wrapped in Mono or Flux!"))
            }
            .map { d -> Response(true, d, null) }
            .onErrorMap { e ->
                when (e) {
                    !is Response.Error -> {
                        logger.error(e)
                        Response.Error(HttpStatus.INTERNAL_SERVER_ERROR)
                    }
                    else -> e
                }
            }
            .onErrorResume { e ->
                exchange.response.statusCode = (e as Response.Error).value
                Mono.just(Response(false, null, e))
            }

        return writeBody(returnValue, returnType, exchange)
    }

    companion object {
        @JvmStatic
        private fun methodForReturnType(): Mono<Response<Any>> = throw NotImplementedError()

        private val returnType: MethodParameter = MethodParameter(
            this::class.java.getDeclaredMethod("methodForReturnType"), -1
        )
    }
}