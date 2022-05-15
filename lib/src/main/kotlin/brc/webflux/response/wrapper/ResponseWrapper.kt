package brc.webflux.response.wrapper

import brc.webflux.response.wrapper.handler.ResponseBodyHandler
import brc.webflux.response.wrapper.handler.WebExceptionHandler
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.accept.RequestedContentTypeResolver
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler
import org.springframework.web.reactive.result.view.ViewResolver

@ComponentScan
@Configuration
class ResponseWrapper {
    @Autowired
    private lateinit var serverCodecConfigurer: ServerCodecConfigurer

    @Autowired
    private lateinit var requestedContentTypeResolver: RequestedContentTypeResolver

    @Autowired
    private lateinit var viewResolvers: ObjectProvider<ViewResolver>

    @Autowired
    private lateinit var errorAttributes: ErrorAttributes

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    private val resources: WebProperties.Resources = WebProperties.Resources()

    @Bean
    fun responseBodyWrapper(): ResponseBodyResultHandler = ResponseBodyHandler(
        serverCodecConfigurer.writers, requestedContentTypeResolver
    )

    @Bean
    @Order(-10)
    fun webExceptionWrapper(): AbstractErrorWebExceptionHandler = WebExceptionHandler(
        viewResolvers, serverCodecConfigurer, applicationContext, errorAttributes, resources
    )
}