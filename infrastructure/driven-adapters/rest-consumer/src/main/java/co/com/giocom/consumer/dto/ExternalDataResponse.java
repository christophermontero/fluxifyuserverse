package co.com.giocom.consumer.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class ExternalDataResponse {

    ExternalUserResponse data;
}