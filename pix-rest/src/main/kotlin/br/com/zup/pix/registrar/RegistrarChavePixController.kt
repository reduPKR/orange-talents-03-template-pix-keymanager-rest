package br.com.zup.pix.registrar

import br.com.zup.PixRestRegistrarServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.created
import io.micronaut.http.HttpResponse.uri
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/registrar/pix/{clienteId}")
class RegistrarChavePixController(
    @Inject val registrarChavePix: PixRestRegistrarServiceGrpc.PixRestRegistrarServiceBlockingStub
    ) {
    @Post
    fun registar(@PathVariable clienteId: String, @Body @Valid request: NovaChaveRequest)
    : HttpResponse<Any> {
        val requestGrpc = request.toModel(clienteId)
        val response = registrarChavePix.registrar(requestGrpc)

        return HttpResponse.created(
            HttpResponse.uri("/registrar/pix/$clienteId/pix/${response.pixId}")
        )
    }
}