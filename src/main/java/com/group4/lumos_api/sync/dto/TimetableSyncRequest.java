package com.group4.lumos_api.sync.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * EDWARD 일회성 로그인 자격증명. 저장·로깅 대상이 아니다.
 */
@Getter
@Setter
@ToString(exclude = "edwardPassword")
public class TimetableSyncRequest {

    @NotBlank(message = "edwardLoginName은 필수입니다.")
    @Pattern(regexp = "\\d{7}", message = "edwardLoginName은 7자리 학번이어야 합니다.")
    private String edwardLoginName;

    @NotBlank(message = "edwardPassword는 필수입니다.")
    private String edwardPassword;

    /** null이면 EDWARD에서 현재 학년도를 조회한다. */
    private Integer year;

    /** null이면 1(1학기)/2(2학기) 등을 학기 시작 시점 기준으로 추정한다. */
    private String termCode;
}
