package co.com.giocom.api.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class UserResponse {

    Long id;
    String firstName;
    String lastName;
    String email;
    String avatar;
}
