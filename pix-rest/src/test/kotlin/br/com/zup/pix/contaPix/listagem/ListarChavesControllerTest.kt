package br.com.zup.pix.contaPix.listagem

import br.com.zup.pix.*
import br.com.zup.pix.contaPix.detalhar.ConsultarChaveRequest
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListarChavesControllerTest{
    @Inject
    lateinit var grpcClient: PixServerListarServiceGrpc.PixServerListarServiceBlockingStub

    @field:Client("/")
    @Inject
    lateinit var client: HttpClient

    @Test
    internal fun `deve listar as chaves apartir do cliente id`(){
        val clienteId = UUID.randomUUID().toString()

        val clienteRequest = ListarChavePixRequest.newBuilder().
        setClienteId(clienteId).build()

        val responseGrpc = ListarChavePixResponse.newBuilder()
            .setClienteId(clienteId)
            .addAllListaChaves(montarListaChave())
            .build()

        Mockito.doReturn(responseGrpc).`when`(grpcClient).listar(clienteRequest)

        val request = HttpRequest.GET<ListaResponse>("/listar/chaves/cliente/$clienteId")
        val response = client.toBlocking().exchange(request, List::class.java)

        assertEquals(HttpStatus.OK, response.status)
        assertNotNull(response.body())
        assertEquals(2, response.body().size)
    }

    private fun montarListaChave(): List<ListarChavePixResponse.ChavePix> {
        val chave1 = ListarChavePixResponse.ChavePix.newBuilder()
            .setPixId("50dcad6a-98e0-4e1e-83d1-fde3e45e94cd")
            .setTipoChave(TipoChave.CPF)
            .setChave("64370752019")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadoEm(Timestamp.newBuilder()
                .setSeconds(1621868286)
                .setNanos(52526000)
                .build())
            .build()

        val chave2 = ListarChavePixResponse.ChavePix.newBuilder()
            .setPixId("50dcad6a-98e0-4e1e-83d1-fde3e45e9111")
            .setTipoChave(TipoChave.EMAIL)
            .setChave("rafael@email.com")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .setCriadoEm(Timestamp.newBuilder()
                .setSeconds(1621868286)
                .setNanos(52526000)
                .build())
            .build()

        return listOf<ListarChavePixResponse.ChavePix>(chave1, chave2)
    }

    @Test
    internal fun `nao deve retornar dados caso clienteId nao seja encontrado`(){
        val clienteId = UUID.randomUUID().toString()
        val clienteRequest = ListarChavePixRequest.newBuilder().
        setClienteId(clienteId).build()

        Mockito.`when`(grpcClient.listar(clienteRequest))
            .thenThrow(StatusRuntimeException(Status.NOT_FOUND)::class.java)

        val request = HttpRequest.GET<ListaResponse>("/listar/chaves/cliente/$clienteId")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, List::class.java)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun `nao deve retornar dados caso clienteId como argumento invalido`(){
        val clienteId = UUID.randomUUID().toString()
        val clienteRequest = ListarChavePixRequest.newBuilder().
        setClienteId(clienteId).build()

        Mockito.`when`(grpcClient.listar(clienteRequest))
            .thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT)::class.java)

        val request = HttpRequest.GET<ListaResponse>("/listar/chaves/cliente/$clienteId")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, List::class.java)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve retornar dados caso de um erro interno`(){
        val clienteId = UUID.randomUUID().toString()
        val clienteRequest = ListarChavePixRequest.newBuilder().
        setClienteId(clienteId).build()

        Mockito.`when`(grpcClient.listar(clienteRequest))
            .thenThrow(StatusRuntimeException::class.java)

        val request = HttpRequest.GET<ListaResponse>("/listar/chaves/cliente/$clienteId")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, List::class.java)
        }

        with(error){
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, status.code)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class Clients{
        @Singleton
        fun blockingStub() = Mockito.mock(PixServerListarServiceGrpc.PixServerListarServiceBlockingStub::class.java)
    }
}