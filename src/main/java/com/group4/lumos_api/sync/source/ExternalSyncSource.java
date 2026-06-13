package com.group4.lumos_api.sync.source;

/**
 * 외부 학교 시스템(EDWARD, LMS 등) 데이터 동기화 확장점.
 *
 * @param <T> 동기화 결과 타입
 */
public interface ExternalSyncSource<T> {

    String sourceId();

    T sync(String userId, SyncCredentials credentials);

    /**
     * 일회성 자격증명. 저장하지 않는다.
     */
    record SyncCredentials(String loginName, char[] password) implements AutoCloseable {

        public SyncCredentials(String loginName, String password) {
            this(loginName, password.toCharArray());
        }

        @Override
        public void close() {
            java.util.Arrays.fill(password, '\0');
        }
    }
}
