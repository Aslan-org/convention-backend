package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class PassCode {

    private  String _id;
    private @NonNull String passCode;
    private @NonNull User user;
    private @NonNull Date date;
    private @NonNull String status;// claimed, unclaimed

}
