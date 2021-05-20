package br.com.zup

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("pixRest") val channel: ManagedChannel) {
//    @Singleton
//    fun pixClientSturb(@GrpcChannel("pixRest") channel: ManagedChannel)
//        :PixRestRegistrarServiceGrpc.PixRestRegistrarServiceBlockingStub{
//        return PixRestRegistrarServiceGrpc.newBlockingStub(channel)
//    }

    @Singleton
    fun RegistrarChave() = PixRestRegistrarServiceGrpc.newBlockingStub(channel)
}