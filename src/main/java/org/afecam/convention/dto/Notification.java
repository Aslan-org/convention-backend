package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class Notification {

    private String id;
    private @NonNull String[] userId;
    private @NonNull String type;// email, sms, push
    private @NonNull String body;
    private @NonNull Date date;
    private String senderEmail;
    private String phoneNumber;
    private @NonNull String status;// new, sent
    private @NonNull String language; //en, fr

}
