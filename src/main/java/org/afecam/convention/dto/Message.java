package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Message {

    private String _id;
    private @NonNull String topic; //individual or business
    private @NonNull String body;
    private String senderEmail;
    private String phoneNumber;
    private @NonNull String status;// new, read, archived
    private @NonNull String language; //en, fr

}
