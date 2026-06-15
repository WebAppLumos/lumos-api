package com.group4.lumos_api.sync.service;

import com.group4.lumos_api.sync.dto.TimetableImportRequest;
import com.group4.lumos_api.sync.model.ParsedTimetableSlot;
import com.group4.lumos_api.sync.parser.ConfirmationMmlParser;
import com.group4.lumos_api.sync.parser.CourseRegistrationParser;
import com.group4.lumos_api.sync.parser.MmlTimetableParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TimetableSyncServiceImportFallbackTest {

    @Spy
    private ConfirmationMmlParser confirmationMmlParser = new ConfirmationMmlParser();

    @Spy
    private CourseRegistrationParser courseRegistrationParser = new CourseRegistrationParser();

    @Spy
    private MmlTimetableParser mmlTimetableParser = new MmlTimetableParser();

    @InjectMocks
    private TimetableSyncService timetableSyncService;

    private Method resolveImportSlots;
    private Class<?> confirmationMetadataClass;

    @BeforeEach
    void setUp() throws Exception {
        for (Class<?> nested : TimetableSyncService.class.getDeclaredClasses()) {
            if ("ConfirmationMetadata".equals(nested.getSimpleName())) {
                confirmationMetadataClass = nested;
                break;
            }
        }
        resolveImportSlots = TimetableSyncService.class.getDeclaredMethod(
                "resolveImportSlots",
                TimetableImportRequest.class,
                confirmationMetadataClass
        );
        resolveImportSlots.setAccessible(true);
    }

    @Test
    void fallsBackToConfirmationMmlWhenSsvHasNoRows() throws Exception {
        String mml = Files.readString(Path.of("src/test/resources/edward-user-confirmation.mml"));
        TimetableImportRequest request = new TimetableImportRequest();
        request.setSsv("""
                SSV:utf-8\u001e
                Dataset:DS_COUR530M01\u001e
                _RowType_\u001fscNm:STRING(256)\u001fpnt:STRING(256)\u001flsnTmtablFormaSmryCtnt:STRING(256)\u001e
                """);
        request.setConfirmationMml(mml);
        request.setStudentNumber("5763834");
        request.setYear(2026);
        request.setTermCode("1");

        Method buildMetadata = TimetableSyncService.class.getDeclaredMethod(
                "buildConfirmationMetadata",
                String.class
        );
        buildMetadata.setAccessible(true);
        Object metadata = buildMetadata.invoke(timetableSyncService, mml);

        @SuppressWarnings("unchecked")
        List<ParsedTimetableSlot> slots = (List<ParsedTimetableSlot>) resolveImportSlots.invoke(
                timetableSyncService,
                request,
                metadata
        );

        assertFalse(slots.isEmpty());
        assertTrue(slots.stream().anyMatch(slot -> slot.title().contains("운영체제")));
    }
}
