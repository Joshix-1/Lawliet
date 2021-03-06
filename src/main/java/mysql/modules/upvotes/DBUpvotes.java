package mysql.modules.upvotes;

import mysql.DBBeanGenerator;
import mysql.DBMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DBUpvotes extends DBBeanGenerator<Long, UpvotesBean> {

    private final static Logger LOGGER = LoggerFactory.getLogger(DBUpvotes.class);

    private static final DBUpvotes ourInstance = new DBUpvotes();
    public static DBUpvotes getInstance() { return ourInstance; }
    private DBUpvotes() {}

    @Override
    protected UpvotesBean loadBean(Long userId) throws Exception {
        UpvotesBean upvotesBean;

        PreparedStatement preparedStatement = DBMain.getInstance().preparedStatement("SELECT lastDate FROM Upvotes WHERE userId = ?;");
        preparedStatement.setLong(1, userId);
        preparedStatement.execute();

        ResultSet resultSet = preparedStatement.getResultSet();
        if (resultSet.next()) {
            upvotesBean = new UpvotesBean(
                    userId,
                    resultSet.getTimestamp(1).toInstant()
            );
        } else {
            upvotesBean = new UpvotesBean(
                    userId,
                    Instant.now().minus(24, ChronoUnit.HOURS)
            );
        }

        resultSet.close();
        preparedStatement.close();

        return upvotesBean;
    }

    @Override
    protected void saveBean(UpvotesBean upvotesBean) {
        DBMain.getInstance().asyncUpdate("REPLACE INTO Upvotes (userId, lastDate) VALUES (?, ?);", preparedStatement -> {
            preparedStatement.setLong(1, upvotesBean.getUserId());
            preparedStatement.setString(2, DBMain.instantToDateTimeString(upvotesBean.getLastUpvote()));
        });
    }

    public void cleanUp() {
        try {
            Statement statement = DBMain.getInstance().statementExecuted("DELETE FROM Upvotes WHERE DATE_ADD(lastDate, INTERVAL 12 HOUR) < NOW();");
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Could not remove Upvote", e);
        }
    }

}
