package com.group4.lumos_api.sync.client;



import com.group4.lumos_api.sync.exception.ExternalSyncException;

import com.group4.lumos_api.sync.model.EdwardSession;

import org.springframework.stereotype.Component;



import java.io.IOException;

import java.net.HttpCookie;

import java.net.URI;

import java.net.URLEncoder;

import java.net.http.HttpRequest;

import java.net.http.HttpResponse;

import java.nio.charset.Charset;

import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;

import java.time.Duration;

import java.util.LinkedHashMap;

import java.util.List;

import java.util.Map;



/**

 * EDWARD Nexacro 로그인 및 SSV API 호출.

 */

@Component

public class EdwardSessionClient {



    private static final SecureRandom RANDOM = new SecureRandom();



    private static final String USER_AGENT =

            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";



    private static final String EDWARD_BASE = "https://edward.kmu.ac.kr";

    private static final String J_LOGIN_PATH = "/com/SsoCtr/j_login.do";



    private static final String MENU_ID = "M505718";

    private static final String PGM_ID = "P505747";

    private static final String BOOTSTRAP_MENU_ID = "edward";

    private static final String BOOTSTRAP_PGM_ID = "edward";



    public EdwardSession login(String loginName, char[] password) {

        EdwardSession session = new EdwardSession();

        try {

            openNx(session);

            ensureSessionIds(session);

            authenticateWithEdward(session, loginName, password);

            bootstrapSession(session);

            return session;

        } catch (ExternalSyncException e) {

            session.close();

            throw e;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            session.close();

            throw new ExternalSyncException("EDWARD 로그인이 중단되었습니다.", e);

        } catch (IOException e) {

            session.close();

            throw new ExternalSyncException("EDWARD 로그인 중 네트워크 오류가 발생했습니다.", e);

        }

    }



    public String postSsv(EdwardSession session, String path, Map<String, String> extraFields) {

        return postSsv(session, path, extraFields, MENU_ID, PGM_ID);

    }



    public String postSsv(EdwardSession session, String path, Map<String, String> extraFields,

                          String menuId, String pgmId) {

        try {

            ensureSessionIds(session);



            Map<String, String> fields = new LinkedHashMap<>();

            fields.put("WMONID", session.wmonId());

            fields.put("IBMID", session.ibmId());

            fields.putAll(extraFields);

            fields.put("requestTimeStr", String.valueOf(System.currentTimeMillis()));



            String body = SsvCodec.buildRequest(fields);

            String url = EDWARD_BASE + path;

            if (menuId != null && pgmId != null) {

                url += "?menuId=" + menuId + "&pgmId=" + pgmId;

            }



            HttpRequest request = HttpRequest.newBuilder(URI.create(url))

                    .timeout(Duration.ofSeconds(30))

                    .header("Content-Type", "text/xml")

                    .header("JCF_Channel_Type", "nexacroplatform/ssv;charset=UTF-8")

                    .header("X-Requested-With", "XMLHttpRequest")

                    .header("User-Agent", USER_AGENT)

                    .header("Origin", EDWARD_BASE)

                    .header("Referer", EDWARD_BASE + "/nx/")

                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))

                    .build();



            HttpResponse<byte[]> response = session.httpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {

                throw new ExternalSyncException("EDWARD API 호출 실패: " + path + " (HTTP " + response.statusCode() + ")");

            }



            String responseBody = decodeBody(response);

            int errorCode = SsvCodec.parseErrorCode(responseBody);

            if (errorCode != 0 && errorCode != -1) {

                String msg = SsvCodec.parseErrorMessage(responseBody);
                if (msg.isBlank()) {
                    msg = "UNKNOWN";
                }
                throw new ExternalSyncException("EDWARD API 오류 (" + path + "): " + msg);

            }

            return responseBody;

        } catch (ExternalSyncException e) {

            throw e;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new ExternalSyncException("EDWARD API 호출이 중단되었습니다.", e);

        } catch (IOException e) {

            throw new ExternalSyncException("EDWARD API 호출 중 네트워크 오류가 발생했습니다.", e);

        }

    }



    public YearTerm resolveYearTerm(EdwardSession session, Integer yearOverride, String termCodeOverride) {

        if (yearOverride != null && termCodeOverride != null) {

            return new YearTerm(yearOverride, termCodeOverride, termLabel(termCodeOverride));

        }



        String body = postSsv(session, "/uni/UniCmmnCtr/findYyTmgbn.do", Map.of(

                "unitBussCd", "04",

                "detaUnitBussCd", "0404",

                "scheCd", "040401",

                "seq", "1"

        ));



        Map<String, String> row = SsvCodec.parseDatasetFirstRowMap(body, "DS_YY_TMGBN");

        String yy = row.get("yy");

        if (yy == null || yy.isBlank()) {

            yy = String.valueOf(java.time.Year.now().getValue());

        }



        int year = yearOverride != null ? yearOverride : Integer.parseInt(yy);

        String termCode = termCodeOverride != null ? termCodeOverride : guessRegularTermCode();

        return new YearTerm(year, termCode, termLabel(termCode));

    }



    public void warmUpGlioSession(EdwardSession session) {

        postSsv(session, "/com/SsoCtr/findMyGLIOList.do", Map.of(

                "columnList", "gaeinNo|userNm"

        ));

    }



    public String fetchLogNo(EdwardSession session) {

        String body = postSsv(session, "/com/SsoCtr/findMyGLIOList.do", Map.of(

                "columnList", "logNo|gaeinNo"

        ));

        Map<String, String> row = SsvCodec.parseDatasetFirstRowMap(body, "DS_GLIO");

        String logNo = row.get("logNo");

        if (logNo == null || logNo.isBlank()) {

            String columns = row.isEmpty() ? "응답 없음" : String.join(", ", row.keySet());

            throw new ExternalSyncException(

                    "EDWARD logNo를 가져오지 못했습니다. (수신 필드: " + columns + ")");

        }

        return logNo.trim();

    }



    public void recordPersonalDataAccess(EdwardSession session, int year, String termCode,

                                         String studentNumber, String termLabel) {

        String qryNm = "unst0040_prn(/rhwpfixsize[1]/rxlsxdefault[1]/rp[" + year + "][" + termCode + "]["

                + studentNumber + "][" + termLabel + "])";

        postSsv(session, "/com/SlogCtr/findPersonalDataLog.do", Map.of(

                "qryNm", qryNm,

                "qryColumnNm", "RD출력",

                "pgmGbn", "6",

                "qryCnt", "1",

                "qryFileUrl", "/rd/uni/cour/unst/unst0040_prn.mrd",

                "findResn", "",

                "excelDownResn", ""

        ));

    }



    public List<Map<String, String>> fetchCourseRegistrationList(EdwardSession session, int year,

                                                                 String termCode, String studentNumber) {

        Map<String, String> cond = new LinkedHashMap<>();

        cond.put("yy", String.valueOf(year));

        cond.put("tmGbn", termCode);

        cond.put("stuno", studentNumber);

        cond.put("tmGbnNm", termLabel(termCode));

        String body = postSsvWithDataset(

                session,

                "/uni/cour/UnstCtr/findTlsnGvupAplyList.do",

                "DS_COND",

                List.of("yy", "tmGbn", "stuno", "tmGbnNm"),

                cond,

                MENU_ID,

                PGM_ID

        );

        return SsvCodec.parseDatasetAllRows(body, "DS_COUR530M01");

    }



    private String postSsvWithDataset(EdwardSession session, String path, String datasetName,

                                      List<String> columnNames, Map<String, String> rowValues,

                                      String menuId, String pgmId) {

        try {

            ensureSessionIds(session);

            Map<String, String> headers = new LinkedHashMap<>();

            headers.put("WMONID", session.wmonId());

            headers.put("IBMID", session.ibmId());

            headers.put("requestTimeStr", String.valueOf(System.currentTimeMillis()));

            String body = SsvCodec.buildRequestWithDataset(headers, datasetName, columnNames, rowValues);

            String url = EDWARD_BASE + path;

            if (menuId != null && pgmId != null) {

                url += "?menuId=" + menuId + "&pgmId=" + pgmId;

            }

            return postRawSsv(session, url, body);

        } catch (ExternalSyncException e) {

            throw e;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new ExternalSyncException("EDWARD API 호출이 중단되었습니다.", e);

        } catch (IOException e) {

            throw new ExternalSyncException("EDWARD API 호출 중 네트워크 오류가 발생했습니다.", e);

        }

    }



    public String fetchTimetableMml(EdwardSession session, int year, String termCode,

                                    String studentNumber, String termLabel, String logNo) {

        try {

            ensureSessionIds(session);



            String mrdParam = "/rhwpfixsize [1] /rxlsxdefault [1] /rp [" + year + "] [" + termCode + "] ["

                    + studentNumber + "] [" + termLabel + "] "

                    + "/rv g_menuId[M505719] g_gaeinNo[" + studentNumber + "] g_logNo[" + logNo + "] "

                    + "/rf [http://127.0.0.1:8084/DataServer/rdagent.jsp] /rprestmtex";



            String form = "opcode=700"

                    + "&mrd_path=" + URLEncoder.encode("http://210.125.16.51:8081/rd/uni/cour/unst/unst0050_prn.mrd", StandardCharsets.UTF_8)

                    + "&mrd_param=" + URLEncoder.encode(mrdParam, StandardCharsets.UTF_8)

                    + "&mrd_data="

                    + "&runtime_param="

                    + "&mmlVersion=0"

                    + "&protocol=sync";



            HttpRequest request = HttpRequest.newBuilder(URI.create("https://report.kmu.ac.kr/ReportingServer/service"))

                    .timeout(Duration.ofSeconds(60))

                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")

                    .header("User-Agent", USER_AGENT)

                    .header("Origin", EDWARD_BASE)

                    .header("Referer", EDWARD_BASE + "/")

                    .POST(HttpRequest.BodyPublishers.ofString(form))

                    .build();



            HttpResponse<byte[]> response = session.httpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() >= 400) {

                throw new ExternalSyncException("시간표 리포트 조회 실패 (HTTP " + response.statusCode() + ")");

            }

            String mml = decodeBody(response);

            if (!mml.contains("<MML") && !mml.contains("<DOCUMENT")) {

                throw new ExternalSyncException("시간표 리포트 응답 형식이 올바르지 않습니다.");

            }

            return mml;

        } catch (ExternalSyncException e) {

            throw e;

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new ExternalSyncException("시간표 리포트 조회가 중단되었습니다.", e);

        } catch (IOException e) {

            throw new ExternalSyncException("시간표 리포트 조회 중 네트워크 오류가 발생했습니다.", e);

        }

    }



    private void authenticateWithEdward(EdwardSession session, String loginName, char[] password)

            throws IOException, InterruptedException {

        Map<String, String> headers = new LinkedHashMap<>();

        headers.put("WMONID", session.wmonId());

        headers.put("IBMID", session.ibmId());

        headers.put("requestTimeStr", String.valueOf(System.currentTimeMillis()));



        Map<String, String> loginRow = new LinkedHashMap<>();

        loginRow.put("j_id", loginName);

        loginRow.put("j_password", new String(password));

        loginRow.put("locale", "ko");



        String body = SsvCodec.buildRequestWithDataset(

                headers,

                "DS_COND",

                List.of("j_id", "j_password", "locale"),

                loginRow

        );



        String responseBody = postRawSsv(session, EDWARD_BASE + J_LOGIN_PATH, body);

        int errorCode = SsvCodec.parseErrorCode(responseBody);

        if (errorCode != 0 && errorCode != -1) {

            throw new ExternalSyncException("EDWARD 로그인에 실패했습니다. 학번/비밀번호를 확인해 주세요.");

        }



        Map<String, String> sessionInfo = SsvCodec.parseDatasetFirstRowMap(responseBody, "DS_SESSIONINFO");

        String msg = sessionInfo.get("msg");

        if (!"success".equalsIgnoreCase(msg)) {

            throw new ExternalSyncException("EDWARD 로그인에 실패했습니다. 학번/비밀번호를 확인해 주세요.");

        }



        ensureSessionIds(session);

    }



    private String postRawSsv(EdwardSession session, String url, String body)

            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))

                .timeout(Duration.ofSeconds(30))

                .header("Content-Type", "text/xml")

                .header("JCF_Channel_Type", "nexacroplatform/ssv;charset=UTF-8")

                .header("X-Requested-With", "XMLHttpRequest")

                .header("User-Agent", USER_AGENT)

                .header("Origin", EDWARD_BASE)

                .header("Referer", EDWARD_BASE + "/nx/")

                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))

                .build();



        HttpResponse<byte[]> response = session.httpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() >= 400) {

            throw new ExternalSyncException("EDWARD API 호출 실패 (HTTP " + response.statusCode() + ")");

        }

        return decodeBody(response);

    }



    private void openNx(EdwardSession session) throws IOException, InterruptedException {

        HttpRequest nxRequest = HttpRequest.newBuilder(URI.create(EDWARD_BASE + "/nx/"))

                .timeout(Duration.ofSeconds(30))

                .header("User-Agent", USER_AGENT)

                .header("Referer", EDWARD_BASE + "/com/SsoCtr/sso_login.do")

                .GET()

                .build();

        session.httpClient().send(nxRequest, HttpResponse.BodyHandlers.discarding());

    }



    private void bootstrapSession(EdwardSession session) {

        postSsv(session, "/com/SsoCtr/isLogin.do", Map.of(), null, null);

        postSsv(session, "/com/MainCtr/findTopMenuList.do", Map.of(),

                BOOTSTRAP_MENU_ID, BOOTSTRAP_PGM_ID);

    }



    private void ensureSessionIds(EdwardSession session) {

        refreshSessionIds(session);



        if (session.wmonId() == null || session.wmonId().isBlank()) {

            session.setWmonId(generateWmonId());

        }



        if (session.ibmId() == null || session.ibmId().isBlank()) {

            session.setIbmId(resolveIbmId(session));

        }

    }



    private void refreshSessionIds(EdwardSession session) {

        session.cookieManager().getCookieStore().getCookies().forEach(cookie -> {

            if ("WMONID".equalsIgnoreCase(cookie.getName())) {

                session.setWmonId(cookie.getValue());

            }

            if ("IBMID".equalsIgnoreCase(cookie.getName())) {

                session.setIbmId(normalizeIbmId(cookie.getValue()));

            }

        });

    }



    private static String resolveIbmId(EdwardSession session) {

        List<HttpCookie> cookies = session.cookieManager().getCookieStore().getCookies();

        for (HttpCookie cookie : cookies) {

            if ("IBMID".equalsIgnoreCase(cookie.getName())) {

                return normalizeIbmId(cookie.getValue());

            }

        }

        for (HttpCookie cookie : cookies) {

            if ("JSESSIONID".equalsIgnoreCase(cookie.getName())) {

                return normalizeIbmId(cookie.getValue());

            }

        }

        return normalizeIbmId(generateWmonId());

    }



    private static String normalizeIbmId(String value) {

        if (value == null || value.isBlank()) {

            return generateWmonId() + ":1";

        }

        return value.contains(":") ? value : value + ":1";

    }



    private static String generateWmonId() {

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(11);

        for (int i = 0; i < 11; i++) {

            sb.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));

        }

        return sb.toString();

    }



    private static String resolveUrl(String url) {

        if (url.startsWith("http://") || url.startsWith("https://")) {

            return url;

        }

        if (url.startsWith("//")) {

            return "https:" + url;

        }

        if (url.startsWith("/")) {

            return EDWARD_BASE + url;

        }

        return EDWARD_BASE + "/" + url;

    }



    private static String decodeBody(HttpResponse<byte[]> response) {

        Charset charset = response.headers().firstValue("Content-Type")

                .filter(v -> v.toLowerCase().contains("euc-kr"))

                .map(v -> Charset.forName("EUC-KR"))

                .orElse(StandardCharsets.UTF_8);

        return new String(response.body(), charset);

    }



    private static String guessRegularTermCode() {

        int month = java.time.LocalDate.now().getMonthValue();

        if (month >= 3 && month <= 8) {

            return "1";

        }

        return "2";

    }



    private static String termLabel(String termCode) {

        return switch (termCode) {

            case "1" -> "1학기";

            case "2" -> "2학기";

            case "3" -> "하계학기";

            case "4" -> "동계학기";

            default -> termCode + "학기";

        };

    }



    public record YearTerm(int year, String termCode, String termLabel) {

    }

}


