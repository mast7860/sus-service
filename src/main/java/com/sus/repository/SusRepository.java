package com.sus.repository;

import com.sus.domain.GradeStats;
import com.sus.domain.ResponseTimes;
import com.sus.domain.SessionDetails;
import com.sus.domain.Stats;
import com.sus.error.ErrorMessage;
import com.sus.error.SusException;
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
            statement.setString(1, token.getSessionId());
            statement.setTimestamp(2, Timestamp.valueOf(token.getStartDateTime()));
            statement.executeUpdate();
            return TXN_SUCCESS_STATUS;
        });
    }

    @Transactional
    @ReadOnly
    public SessionDetails getSession(String sessionId) {

        var selectSql = """
                SELECT * FROM session_details
                WHERE session_id=?;
                """;

        return jdbcOperations.prepareStatement(selectSql, statement -> {
            statement.setString(1, sessionId);
            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return jdbcOperations.readEntity(resultSet, SessionDetails.class);
            } else {
                throw new SusException(ErrorMessage.builder().code("SUS001").message("session not valid").build());
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
    public void saveScores(String sessionId, Double score, String grade, String answers) {

        var insertSql = """
                INSERT INTO scores_details (session_id, answers, score, grade, score_time)
                VALUES (?,?,?,?,?);
                """;

        jdbcOperations.prepareStatement(insertSql, statement -> {
            statement.setString(1, sessionId);
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
    public ResponseTimes getResponseTimes(LocalDate fromDate, LocalDate toDate) {
        var sql = """
                SELECT
                MAX(time_spent_in_sec) AS max_time,
                AVG(time_spent_in_sec) AS avg_time,
                MIN(time_spent_in_sec) AS min_time
                FROM session_details
                WHERE time_spent_in_sec is not null
                AND end_time >= ?
                AND end_time < ?;
                """;

        return jdbcOperations.prepareStatement(sql, statement -> {

            statement.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(toDate.atStartOfDay().plusDays(1)));

            var resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return jdbcOperations.readEntity(resultSet, ResponseTimes.class);
            } else {
                return ResponseTimes.builder().build();
            }
        });
    }

    @Transactional
    @ReadOnly
    public Stats getAveragePercentile(LocalDate fromDate, LocalDate toDate) {
        var sql = """
               SELECT
               COUNT(*) AS count, AVG(score) AS avg_score
               FROM scores_details
               WHERE score_time >= ?
               AND score_time < ?;
               """;

        return jdbcOperations.prepareStatement(sql, statement -> {
            statement.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(toDate.atStartOfDay().plusDays(1)));
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return jdbcOperations.readEntity(resultSet, Stats.class);
            } else {
                return Stats.builder().build();
            }
        });
    }

    @Transactional
    @ReadOnly
    public List<GradeStats> getGradeCount(LocalDate fromDate, LocalDate toDate) {
        var sql = """
                SELECT grade, COUNT(*) AS count, AVG(score) AS avg_grade_score
                FROM scores_details
                WHERE score_time >= ?
                AND score_time < ?
                GROUP BY grade;
                """;

        return jdbcOperations.prepareStatement(sql, statement -> {
            statement.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            statement.setTimestamp(2, Timestamp.valueOf(toDate.atStartOfDay().plusDays(1)));
            var resultSet = statement.executeQuery();
            return jdbcOperations.entityStream(resultSet, GradeStats.class).collect(Collectors.toList());
        });
    }

    @Transactional
    public void deleteUnusedSessions() {

        var insertSql = """
                DELETE FROM session_details
                WHERE end_time is null
                AND start_time < ?
                """;

        jdbcOperations.prepareStatement(insertSql, statement -> {
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
            int cleanedRows = statement.executeUpdate();
            log.info("Cleaned Sessions="+cleanedRows);
            return TXN_SUCCESS_STATUS;
        });
    }

}
