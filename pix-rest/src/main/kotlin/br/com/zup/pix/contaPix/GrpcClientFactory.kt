package br.com.zup.pix.contaPix

import br.com.zup.pix.PixServer
import br.com.zup.pix.PixServerRegistrarServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("pix") val channel: ManagedChannel) {
    @Singleton
    fun RegistrarChave() = PixServerRegistrarServiceGrpc.newBlockingStub(channel)
}