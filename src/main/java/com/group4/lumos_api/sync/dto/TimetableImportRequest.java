package com.group4.lumos_api.sync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 브라우저 확장이 EDWARD에서 가져온 MML 시간표를 Lumos에 저장할 때 사용한다.
 */
@Getter
@Setter
public class TimetableImportRequest {

    private String mml;

    /**
     * 브라우저 확장이 EDWARD SSV 응답(DS_COUR530M01)을 직접 전달할 때 사용한다.
     */
    private String ssv;

    /**
     * SSV에 학점 컬럼이 없을 때 수강신청확인서 MML로 학점을 보강한다.
     */
    private String confirmationMml;

    /**
     * 확장이 SSV에서 직접 추출한 과목명-학점 맵.
     */
    private Map<String, Short> creditByTitle;

    @NotBlank
    @Pattern(regexp = "\\d{7}")
    private String studentNumber;

    private Integer year;

    @Pattern(regexp = "[1-4]")
    private String termCode;
}
