package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class Message {

    private String _id;
    private @NonNull String topic; //individual or business
    private @NonNull String body;
    private String senderEmail;
    private String phoneNumber;
    private @NonNull Date date;
    private @NonNull String status;// new, read, archived
    private @NonNull String language; //en, fr

}
