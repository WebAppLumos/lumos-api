CREATE TABLE user_dashboard_widgets (
    user_id     VARCHAR(255) NOT NULL,
    widget_key  VARCHAR(50)  NOT NULL,
    sort_order  INT          NOT NULL,
    visible     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_user_dashboard_widgets PRIMARY KEY (user_id, widget_key),
    CONSTRAINT fk_user_dashboard_widgets_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);
