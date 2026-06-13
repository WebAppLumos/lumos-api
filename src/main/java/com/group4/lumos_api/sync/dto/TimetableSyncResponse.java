package com.group4.lumos_api.sync.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimetableSyncResponse {

    private final Long semesterId;
    private final Long timetableId;
    private final String semesterTitle;
    private final int courseCount;
    private final int entryCount;
}
