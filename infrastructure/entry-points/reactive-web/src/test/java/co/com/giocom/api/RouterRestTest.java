package co.com.giocom.api;

import co.com.giocom.usecase.user.UserUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @InjectMocks
    private RouterRest userRouterRest;

    @Mock
    private UserUseCase userUseCase;

    @Test
    void routerFunctionSuccess() {
        Assertions.assertNotNull(
                this.userRouterRest.routerFunction(new Handler(userUseCase)));
    }
}
