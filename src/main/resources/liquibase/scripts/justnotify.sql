-- liquibase formatted sql

-- changeset pavelignatev:1
CREATE TABLE notification (
    Date DATE,
    Time TIME,
    text_notification TEXT
)