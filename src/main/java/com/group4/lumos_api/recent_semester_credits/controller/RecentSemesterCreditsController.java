package com.group4.lumos_api.recent_semester_credits.controller;

import com.group4.lumos_api.common.security.CurrentUser;
import com.group4.lumos_api.recent_semester_credits.service.RecentSemesterCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecentSemesterCreditsController {

    private final RecentSemesterCreditsService recentSemesterCreditsService;

    /**
     * 가장 최근 학기의 모든 수업 학점 합계를 반환한다.
     * GET /api/recent-semester/total-credits
     */
    @GetMapping("/api/recent-semester/total-credits")
    public ResponseEntity<Integer> getRecentSemesterTotalCredits(@CurrentUser String userId) {
        Integer totalCredits = recentSemesterCreditsService.getRecentSemesterTotalCredits(userId);
        return ResponseEntity.ok(totalCredits);
    }
}
