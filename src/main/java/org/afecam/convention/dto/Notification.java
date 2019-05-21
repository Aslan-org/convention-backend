package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Notification {

    private String id;
    private @NonNull String[] userId;
    private @NonNull String type;// email, sms, push
    private @NonNull String body;
    private String senderEmail;
    private String phoneNumber;
    private @NonNull String status;// new, sent
    private @NonNull String language; //en, fr

}
