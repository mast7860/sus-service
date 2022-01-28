package com.sus.repository;

import com.sus.domain.GradeStat;
import com.sus.domain.ResponseTimes;
import com.sus.domain.SessionDetails;
import com.sus.model.SusRequest;
import com.sus.model.Token;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.transaction.annotation.ReadOnly;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JdbcRepository(dialect = Dialect.MYSQL)
@Slf4j
public class SusRepository {

    private static final String TXN_SUCCESS_STATUS = "SUCCESS";

    private final JdbcOperations jdbcOperations;

    public SusRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Transactional
    public void createSession(Token token) {

        var insertSql = """
                INSERT INTO session_details(session_id, start_time)
                VALUES (?,?);
                """;

        jdbcOperations.prepareStatement(insertSql, statement -> {
            statement.setString(1, token.getSessionId().toString());
            statement.setTimestamp(2, Timestamp.valueOf(token.getStartDateTime()));
            statement.executeUpdate();
            return TXN_SUCCESS_STATUS;
        });
    }

    @Transactional
    @ReadOnly
    public SessionDetails getSession(Token token) {

        var selectSql = """
                SELECT * FROM session_details
                WHERE session_id=?;
                """;

        return jdbcOperations.prepareStatement(selectSql, statement -> {
            statement.setString(1, token.getSessionId().toString());
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return jdbcOperations.readEntity(resultSet, SessionDetails.class);
            } else {
                throw new RuntimeException("session not found");
            }
        });
    }


    @Transactional
    public void updateSession(SessionDetails sessionDetails) {

        var updateSql = """
                UPDATE session_details SET
                end_time=?,
                time_spent_in_sec=?
                WHERE session_id=?;
                """;

        var endTime = LocalDateTime.now();

        var timeSpent = Duration.between(sessionDetails.getStartTime(), endTime);

        jdbcOperations.prepareStatement(updateSql, statement -> {
            statement.setTimestamp(1, Timestamp.valueOf(endTime));
            statement.setLong(2, timeSpent.getSeconds());
            statement.setString(3, sessionDetails.getSessionId());
            statement.executeUpdate();
            return TXN_SUCCESS_STATUS;
        });
    }


    @Transactional
    @ReadOnly
    public ResponseTimes getResponseTimes(LocalDate fromDate, LocalDate toDate) {
        var sql = """
                SELECT
                MAX(time_spent_in_sec) AS max_time,
                AVG(time_spent_in_sec) AS avg_time,
                MIN(time_spent_in_sec) AS min_time
                FROM session_details;
                """;

        return jdbcOperations.prepareStatement(sql, statement -> {

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return jdbcOperations.readEntity(resultSet, ResponseTimes.class);
            } else {
                return ResponseTimes.builder().build();
            }
        });
    }

    @Transactional
    public void saveScores(SusRequest susRequest, Double score, String grade, String answers) {

        var insertSql = """
                INSERT INTO scores_details (session_id, answers, score, grade, score_time)
                VALUES (?,?,?,?,?);
                """;

        jdbcOperations.prepareStatement(insertSql, statement -> {
            statement.setString(1, susRequest.getToken().getSessionId().toString());
            statement.setString(2, answers);

            statement.setDouble(3, score);
            statement.setString(4, grade);
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();
            return TXN_SUCCESS_STATUS;
        });
    }

    @Transactional
    @ReadOnly
    public Double getAveragePercentile(LocalDate fromDate, LocalDate toDate) {
        var sql = """
                SELECT
                AVG(score) AS avg_score
                FROM scores_details;
                """;

        return jdbcOperations.prepareStatement(sql, statement -> {

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble(1);
            } else {
                return 0.0;
            }
        });
    }

    @Transactional
    @ReadOnly
    public List<GradeStat> getGradeCount(LocalDate fromDate, LocalDate toDate) {
        var sql = """
                SELECT grade, COUNT(*) AS count
                FROM scores_details
                GROUP BY grade;
                """;

        return jdbcOperations.prepareStatement(sql, statement -> {
            var resultSet = statement.executeQuery();
            return jdbcOperations.entityStream(resultSet, GradeStat.class).collect(Collectors.toList());
        });
    }


}
