package br.com.zup.pix.contaPix.detalhar

import br.com.zup.pix.*
import br.com.zup.pix.contaPix.TipoChave
import br.com.zup.pix.contaPix.TipoConta.*
import br.com.zup.pix.contaPix.registrar.NovaChaveRequest
import br.com.zup.pix.contaPix.remover.RemoverChaveRequest
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ConsultarChaveControllerTest{
    @Inject
    lateinit var grpcClient: PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub

    @field:Client("/")
    @Inject
    lateinit var client: HttpClient

    @Test
    internal fun `deve retornar dados pelo clienteId e pixId`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        val responseGrpc = ConsultarChavePixResponse.newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .setChave(
                ConsultarChavePixResponse.ChavePix.newBuilder()
                    .setTipoChaveValue(1)
                    .setChave("64370752019")
                    .setConta(
                        ConsultarChavePixResponse.ChavePix.ContaInfo.newBuilder()
                            .setTipoConta(TipoConta.CONTA_CORRENTE)
                            .setInstituicao("ITAÚ UNIBANCO S.A.")
                            .setNomeTitular("Rafael")
                            .setCpfTitular("64370752019")
                            .setAgencia("0001")
                            .setNumeroConta("889976")
                            .build()
                    )
                        .setCriadoEm(Timestamp.newBuilder()
                        .setSeconds(1621868286)
                        .setNanos(52526000)
                        .build())
                    .build()
            ).build()

        Mockito.doReturn(responseGrpc).`when`(grpcClient).consultar(Mockito.any())

        val request = HttpRequest.GET<Any>("/consultar/cliente/$clientId/pix/$pixId")
        val response = client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    internal fun `nao deve retornar dados caso a chave pix nao seja encontrada pelo clienteId e pixId`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.consultar(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.NOT_FOUND)::class.java)

        val request = HttpRequest.GET<Any>("/consultar/cliente/$clientId/pix/$pixId")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun `nao deve retornar dados caso os clienteId e pixId sejam argumantos invalidos`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.consultar(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT)::class.java)

        val request = HttpRequest.GET<Any>("/consultar/cliente/$clientId/pix/$pixId")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve retornar dados caso ocorra um erro no servidor por clienteId e pixId`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.consultar(Mockito.any()))
            .thenThrow(StatusRuntimeException::class.java)

        val request = HttpRequest.GET<Any>("/consultar/cliente/$clientId/pix/$pixId")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
        }

        with(error){
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, status.code)
        }
    }

    @Test
    internal fun `deve retornar dados pela chave`(){
        val clientId = UUID.randomUUID().toString()
        val pixId = UUID.randomUUID().toString()
        val chave = "64370752019"

        val responseGrpc = ConsultarChavePixResponse.newBuilder()
            .setClienteId(clientId)
            .setPixId(pixId)
            .setChave(
                ConsultarChavePixResponse.ChavePix.newBuilder()
                    .setTipoChaveValue(1)
                    .setChave(chave)
                    .setConta(
                        ConsultarChavePixResponse.ChavePix.ContaInfo.newBuilder()
                            .setTipoConta(TipoConta.CONTA_CORRENTE)
                            .setInstituicao("ITAÚ UNIBANCO S.A.")
                            .setNomeTitular("Rafael")
                            .setCpfTitular(chave)
                            .setAgencia("0001")
                            .setNumeroConta("889976")
                            .build()
                    )
                    .setCriadoEm(Timestamp.newBuilder()
                        .setSeconds(1621868286)
                        .setNanos(52526000)
                        .build())
                    .build()
            ).build()

        Mockito.doReturn(responseGrpc).`when`(grpcClient).consultar(Mockito.any())

        val request = HttpRequest.GET<Any>("/consultar/chave/$chave")
        val response = client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    internal fun `nao deve retornar dados caso a chave pix nao seja encontrada`(){
        val chave = "64370752019"

        Mockito.`when`(grpcClient.consultar(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.NOT_FOUND)::class.java)

        val request = HttpRequest.GET<Any>("/consultar/chave/$chave")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    internal fun `nao deve retornar dados caso a chave seja um argumanto invalido`(){
        val chave = "64370752019"

        Mockito.`when`(grpcClient.consultar(Mockito.any()))
            .thenThrow(StatusRuntimeException(Status.INVALID_ARGUMENT)::class.java)

        val request = HttpRequest.GET<Any>("/consultar/chave/$chave")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
        }

        with(error){
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    @Test
    internal fun `nao deve retornar dados caso ocorra um erro no servidor pela chave`(){
        val chave = "64370752019"

        Mockito.`when`(grpcClient.consultar(Mockito.any()))
            .thenThrow(StatusRuntimeException::class.java)

        val request = HttpRequest.GET<Any>("/consultar/chave/$chave")
        val error = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, ConsultarChaveRequest::class.java)
        }

        with(error){
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.code, status.code)
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class Clients{
        @Singleton
        fun blockingStub() = Mockito.mock(PixServerConsultarServiceGrpc.PixServerConsultarServiceBlockingStub::class.java)
    }
}