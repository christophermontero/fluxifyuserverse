package co.com.giocom.redis.template.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserCacheDAO {

    Long id;
    String firstName;
    String lastName;
    String email;
    String avatar;
}
