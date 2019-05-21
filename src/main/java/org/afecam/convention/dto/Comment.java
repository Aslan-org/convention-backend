package org.afecam.convention.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class Comment {

    private  String _id;
    private @NonNull String userId;
    private @NonNull String body;
    private Comment[] comments;
    private @NonNull Date date;
    private @NonNull String status;// new, deleted
    private @NonNull String language;

}
