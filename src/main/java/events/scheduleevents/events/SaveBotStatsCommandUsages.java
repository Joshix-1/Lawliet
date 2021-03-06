package events.scheduleevents.events;

import core.Bot;
import events.scheduleevents.ScheduleEventDaily;
import events.scheduleevents.ScheduleEventInterface;
import mysql.modules.botstats.DBBotStats;

@ScheduleEventDaily
public class SaveBotStatsCommandUsages implements ScheduleEventInterface {

    @Override
    public void run() throws Throwable {
        if (Bot.isProductionMode()) {
            DBBotStats.saveStatsCommandUsages();
        }
    }

}
