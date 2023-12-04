package co.com.giocom.dynamodb;

import co.com.giocom.dynamodb.helper.TemplateAdapterOperations;
import co.com.giocom.dynamodb.models.UserDynamoDAO;
import co.com.giocom.model.user.User;
import co.com.giocom.model.user.gateways.UserEventGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class DynamoDBTemplateAdapter
        extends TemplateAdapterOperations<User, Long, UserDynamoDAO>
        implements UserEventGateway {

    public DynamoDBTemplateAdapter(
            DynamoDbEnhancedAsyncClient connectionFactory,
            ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class),
                "Users");
    }
}
