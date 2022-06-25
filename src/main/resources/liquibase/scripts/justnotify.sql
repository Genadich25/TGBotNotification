-- liquibase formatted sql

-- changeset pavelignatev:1
CREATE TABLE notification_task (
    id bigserial,
    chat_id BIGINT,
    text_notification TEXT,
    date DATE,
    time TIME
)