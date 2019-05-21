package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class User {

    private  String _id;
    private @NonNull String name;
    private String email;
    private String phoneNumber;
    private @NonNull String role;
    private @NonNull String status;// active, deactivated
    private @NonNull String language;

}
