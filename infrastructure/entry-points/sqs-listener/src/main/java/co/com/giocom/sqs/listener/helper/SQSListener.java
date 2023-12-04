package co.com.giocom.sqs.listener.helper;

import co.com.giocom.sqs.listener.config.SQSProperties;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Log4j2
@Builder
public class SQSListener {
    private final SqsAsyncClient client;
    private final SQSProperties properties;
    private final Function<Message, Mono<Void>> processor;
    private String operation;

    public SQSListener start() {
        this.operation = "MessageFrom:" + properties.getQueueUrl();
        ExecutorService service = Executors.newFixedThreadPool(
                properties.getNumberOfThreads());
        Flux<Void> flow = listenRetryRepeat().publishOn(
                Schedulers.fromExecutorService(service));
        for (int i = 0; i < properties.getNumberOfThreads(); i++) {
            flow.subscribe();
        }
        return this;
    }

    private Flux<Void> listenRetryRepeat() {
        return listen().doOnError(
                e -> log.error("Error listening sqs queue", e)).repeat();
    }

    private Flux<Void> listen() {
        return getMessages().flatMap(
                message -> processor.apply(message).name("async_operation")
                        .tag("operation", operation).metrics()
                        .then(confirm(message))).onErrorContinue(
                (e, o) -> log.error("Error listening sqs message", e));
    }

    private Mono<Void> confirm(Message message) {
        return Mono.fromCallable(
                        () -> getDeleteMessageRequest(message.receiptHandle())).flatMap(
                        request -> Mono.fromFuture(client.deleteMessage(request)))
                .then();
    }

    private Flux<Message> getMessages() {
        return Mono.fromCallable(this::getReceiveMessageRequest).flatMap(
                        request -> Mono.fromFuture(client.receiveMessage(request)))
                .doOnNext(response -> log.debug("{} received messages from sqs",
                        response.messages().size())).flatMapMany(
                        response -> Flux.fromIterable(response.messages()));
    }

    private ReceiveMessageRequest getReceiveMessageRequest() {
        return ReceiveMessageRequest.builder()
                .queueUrl(properties.getQueueUrl())
                .maxNumberOfMessages(properties.getMaxNumberOfMessages())
                .waitTimeSeconds(properties.getWaitTimeSeconds())
                .visibilityTimeout(properties.getVisibilityTimeout()).build();
    }

    private DeleteMessageRequest getDeleteMessageRequest(String receiptHandle) {
        return DeleteMessageRequest.builder().queueUrl(properties.getQueueUrl())
                .receiptHandle(receiptHandle).build();
    }
}
