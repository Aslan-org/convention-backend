package org.afecam.convention.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Article {

    private String _id;
    private @NonNull String authorId;
    private @NonNull String title; //individual or business
    private @NonNull String body;
    private @NonNull String date;
    private @NonNull Comment[] comments;
    private @NonNull String tags;
    private @NonNull String status; //published, draft, archived
    private @NonNull String language;
    private Long viewCount;


}
