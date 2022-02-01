CREATE TABLE session_details
(
    session_id        VARCHAR(36) PRIMARY KEY,
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    time_spent_in_sec INT
);

CREATE TABLE scores_details
(
    session_id VARCHAR(36) PRIMARY KEY,
    answers    VARCHAR(50) NOT NULL,
    score      NUMERIC(6,4),
    grade      VARCHAR(1)  NOT NULL,
    score_time TIMESTAMP,
    CONSTRAINT fk_file_type_id
        FOREIGN KEY (session_id)
            REFERENCES session_details (session_id),
    UNIQUE (session_id, answers, score, score_time)

);


CREATE INDEX ix_start_time
    ON session_details(start_time );

CREATE INDEX ix_session_score
    ON scores_details(session_id, score_time );