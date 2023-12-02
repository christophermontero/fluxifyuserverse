package co.com.giocom.model.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldBeReturnModelToString() {
        User user = User.builder().id(1L).firstName("Kevin").lastName("Mitnick")
                .email("mitnick@example.com").avatar("avatar_url").build();

        String expectedToString = "User(id=1, firstName=Kevin, lastName=Mitnick, email=mitnick@example.com, avatar=avatar_url)";

        String actualToString = user.toString();

        Assertions.assertEquals(expectedToString, actualToString);
    }
}
