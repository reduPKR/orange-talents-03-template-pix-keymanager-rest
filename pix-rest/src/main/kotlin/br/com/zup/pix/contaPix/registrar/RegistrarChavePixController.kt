package br.com.zup.pix.contaPix.registrar

import br.com.zup.pix.PixServerRegistrarServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/registrar/cliente/{clienteId}")
class RegistrarChavePixController(
    @Inject val grpcClient: PixServerRegistrarServiceGrpc.PixServerRegistrarServiceBlockingStub
    ) {
    @Post
    fun registar(@PathVariable clienteId: String, @Body @Valid request: NovaChaveRequest)
    : HttpResponse<Any> {
        val requestGrpc = request.toModel(clienteId)

        try {
            val response = grpcClient.registrar(requestGrpc)
            return HttpResponse.created(
                HttpResponse.uri("/registrar/pix/$clienteId/pix/${response.pixId}")
            )
        }catch (e: Exception){
            return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        }



    }
}