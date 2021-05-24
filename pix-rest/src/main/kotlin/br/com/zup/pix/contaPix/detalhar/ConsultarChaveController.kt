package br.com.zup.pix.contaPix.detalhar

import br.com.zup.pix.PixServerConsultarServiceGrpc
import br.com.zup.pix.contaPix.ErrorResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import javax.inject.Inject

@Validated
@Controller("/consultar/")
class ConsultarChaveController(
    @Inject val grpcClient: PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub
) {
    @Get("/cliente/{clienteId}/pix/{pixId}")
    fun consultar(@PathVariable clienteId: String, @PathVariable pixId: String): HttpResponse<Any> {
        val requestGrpc = ConsultarChaveRequest(null, FiltroPorPixId(clienteId,pixId))
                                .filtroPorPixId()

        try {
            val response = grpcClient.consultar(requestGrpc)
            return HttpResponse.ok(ConsultaResponse(response))
        }catch (e: Exception){
            val statusCode = (e as StatusRuntimeException).status.code

            return when (statusCode) {
                Status.NOT_FOUND.code -> HttpResponse.notFound(ErrorResponse("Chave PIX não encontrado"))
                Status.INVALID_ARGUMENT.code -> HttpResponse.badRequest(ErrorResponse("Argumentos invalidos"))
                else -> HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}