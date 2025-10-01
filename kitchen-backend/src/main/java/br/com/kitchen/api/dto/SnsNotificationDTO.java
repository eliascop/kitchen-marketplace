package br.com.kitchen.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SnsNotificationDTO {

    @JsonProperty("Type")
    private String type;

    @JsonProperty("MessageId")
    private String messageId;

    @JsonProperty("TopicArn")
    private String topicArn;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("UnsubscribeURL")
    private String unsubscribeURL;

    @JsonProperty("SignatureVersion")
    private String signatureVersion;

    @JsonProperty("Signature")
    private String signature;

    @JsonProperty("SigningCertURL")
    private String signingCertURL;
}

