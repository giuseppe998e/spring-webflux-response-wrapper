package brc.webflux.response.wrapper.handler

import brc.webflux.response.wrapper.model.Response
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.web.WebProperties.Resources
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.result.view.ViewResolver
import reactor.core.publisher.Mono
import java.util.stream.Collectors

// Author: Matteo Brunello (@Ollenurb)
internal class WebExceptionHandler(
    viewResolvers: ObjectProvider<ViewResolver>, serverCodecConfigurer: ServerCodecConfigurer,
    applicationContext: ApplicationContext, errorAttributes: ErrorAttributes, resources: Resources
) : AbstractErrorWebExceptionHandler(errorAttributes, resources, applicationContext) {
    init {
        super.setMessageWriters(serverCodecConfigurer.writers)
        super.setMessageReaders(serverCodecConfigurer.readers)
        super.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()))
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes?): RouterFunction<ServerResponse> =
        RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse)

    private fun renderErrorResponse(serverRequest: ServerRequest): Mono<ServerResponse> {
        val errorCode = with(getErrorAttributes(serverRequest, ErrorAttributeOptions.defaults())) {
            HttpStatus.valueOf(this["status"] as Int)
        }

        return ServerResponse
            .status(errorCode)
            .json()
            .bodyValue(
                Response(false, null, Response.Error(errorCode))
            )
    }
}