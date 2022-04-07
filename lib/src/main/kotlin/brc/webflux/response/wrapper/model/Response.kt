package brc.webflux.response.wrapper.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonIncludeProperties
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Response<T>(
    @JsonProperty("ok")
    val ok: Boolean,

    @JsonProperty("data")
    val data: T?,

    @JsonProperty("error")
    val error: Error? = null
) {
    @JsonIncludeProperties("value", "message")
    data class Error(
        @JsonProperty("value")
        val value: HttpStatus,

        @JsonProperty("message")
        override val message: String = value.reasonPhrase
    ) : Exception(message) {
        // Overrides Jackson default "value" variable getter
        fun getValue() = this.value.value()
    }
}