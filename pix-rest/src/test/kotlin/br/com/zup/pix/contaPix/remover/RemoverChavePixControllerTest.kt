package br.com.zup.pix.contaPix.remover

import br.com.zup.pix.ChaveRegistradaResponse
import br.com.zup.pix.GrpcClientFactory
import br.com.zup.pix.PixServerRegistrarServiceGrpc
import br.com.zup.pix.PixServerRemoveServiceGrpc
import br.com.zup.pix.contaPix.ErrorResponse
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta
import br.com.zup.pix.contaPix.registrar.NovaChaveRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemoverChavePixControllerTest{
    @Inject
    lateinit var grpcClient: PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub

    @field:Client("/")
    @Inject
    lateinit var client: HttpClient

    @Test
    internal fun `deve remover uma chave`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val responseGrpc = ChaveRegistradaResponse.newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .build()

        Mockito.doReturn(responseGrpc).`when`(grpcClient).remover(Mockito.any())

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)
        val response = client.toBlocking().exchange(request, RemoverChaveRequest::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    internal fun `nao deve remover uma chave quando não encontrar a chave`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito.doReturn(Status.NOT_FOUND).`when`(grpcClient).remover(Mockito.any())

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, RemoverChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não localizada", status.description)
        }
    }

    @Test
    internal fun `nao deve remover uma chave quando os argumentos forem invalidos`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito.doReturn(Status.INVALID_ARGUMENT).`when`(grpcClient).remover(Mockito.any())

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, RemoverChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados invalidos", status.description)
        }
    }

    @Test
    internal fun `nao deve remover uma chave quandoocorrer um erro no servidor`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        Mockito.doReturn(HttpResponse.serverError(ErrorResponse("Erro interno"))).`when`(grpcClient).remover(Mockito.any())

        val chaveRequest = RemoverChaveRequest(clientId, pixId)
        val request = HttpRequest.DELETE("/remover/chave",chaveRequest)

        val error = assertThrows<StatusRuntimeException> {
            client.toBlocking().exchange(request, RemoverChaveRequest::class.java)
        }

        with(error){
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status.code)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class Clients{
        @Singleton
        fun blockingStub() = Mockito.mock(PixServerRemoveServiceGrpc.PixServerRemoveServiceBlockingStub::class.java)
    }
}