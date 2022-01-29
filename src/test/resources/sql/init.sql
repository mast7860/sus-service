CREATE TABLE session_details
(
    session_id        VARCHAR(36) PRIMARY KEY,
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    time_spent_in_sec INT,
    INDEX (start_time
)
    );


CREATE TABLE scores_details
(
    session_id VARCHAR(36) PRIMARY KEY,
    answers    VARCHAR(50) NOT NULL,
    score      DECIMAL(6, 4),
    grade      VARCHAR(1)  NOT NULL,
    score_time TIMESTAMP,
    INDEX (session_id,
    score_time
) ,
    CONSTRAINT fk_file_type_id
        FOREIGN KEY (session_id)
            REFERENCES session_details (session_id),
    UNIQUE (session_id, answers, score, score_time)

);

insert into session_details (session_id, start_time, end_time, time_spent_in_sec)
values ('df2deb5f-c9a8-4777-bb77-049bbd0c84eb', '2022-01-29 12:28:00', '2022-01-29 12:38:00', 10);