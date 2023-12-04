package co.com.giocom.dynamodb.helper;

import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class TemplateAdapterOperations<E, K, V> {
    private final Class<V> dataClass;
    private final Function<V, E> toEntityFn;
    private final DynamoDbAsyncTable<V> customerTable;
    private final DynamoDbAsyncIndex<V> customerTableByIndex;
    protected ObjectMapper mapper;

    protected TemplateAdapterOperations(
            DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
            ObjectMapper mapper, Function<V, E> toEntityFn, String tableName,
            String... index) {
        this.toEntityFn = toEntityFn;
        this.mapper = mapper;
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass()
                .getGenericSuperclass();
        this.dataClass = (Class<V>) genericSuperclass.getActualTypeArguments()[2];
        customerTable = dynamoDbEnhancedAsyncClient.table(tableName,
                TableSchema.fromBean(dataClass));
        customerTableByIndex = index.length > 0 ?
                customerTable.index(index[0]) :
                null;
    }

    public Mono<Void> save(E model) {
        return Mono.fromFuture(customerTable.putItem(toEntity(model)));
    }

    public Mono<E> getById(K id) {
        return Mono.fromFuture(customerTable.getItem(Key.builder()
                .partitionValue(AttributeValue.builder().s((String) id).build())
                .build())).map(this::toModel);
    }

    public Mono<E> delete(E model) {
        return Mono.fromFuture(customerTable.deleteItem(toEntity(model)))
                .map(this::toModel);
    }

    public Mono<List<E>> query(QueryEnhancedRequest queryExpression) {
        PagePublisher<V> pagePublisher = customerTable.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    public Mono<List<E>> queryByIndex(QueryEnhancedRequest queryExpression,
            String... index) {
        DynamoDbAsyncIndex<V> queryIndex = index.length > 0 ?
                customerTable.index(index[0]) :
                customerTableByIndex;

        SdkPublisher<Page<V>> pagePublisher = queryIndex.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    @Deprecated
    public Mono<List<E>> scan() {
        PagePublisher<V> pagePublisher = customerTable.scan();
        return listOfModel(pagePublisher);
    }

    private Mono<List<E>> listOfModel(PagePublisher<V> pagePublisher) {
        return Mono.from(pagePublisher)
                .map(page -> page.items().stream().map(this::toModel)
                        .collect(Collectors.toList()));
    }

    private Mono<List<E>> listOfModel(SdkPublisher<Page<V>> pagePublisher) {
        return Mono.from(pagePublisher)
                .map(page -> page.items().stream().map(this::toModel)
                        .collect(Collectors.toList()));
    }

    protected V toEntity(E model) {
        return mapper.map(model, dataClass);
    }

    protected E toModel(V data) {
        return data != null ? toEntityFn.apply(data) : null;
    }
}