package org.afecam.convention.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class Participant {

    private String id;
    private @NonNull String type; //individual or business
    private @NonNull String name;
    private @NonNull String phoneNumber;
    private String email;
    private @NonNull String region; //NW, SW, LT, CE,
    private String occupation;
    private String expectation;
    private @NonNull Date date;
    private @NonNull String status;// paid, advanced, unconfirmed, canceled
    private @NonNull String language; //en, fr

}
