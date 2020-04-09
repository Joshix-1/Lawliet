package CommandSupporters;

import CommandListeners.*;
import Commands.CasinoCategory.CoinFlipCommand;
import Commands.InformationCategory.*;
import Commands.CasinoCategory.*;
import Commands.EmotesCategory.*;
import Commands.ExternalCategory.*;
import Commands.GimmicksCategory.*;
import Commands.InteractionsCategory.*;
import Commands.ModerationCategory.*;
import Commands.NSFWCategory.*;
import Commands.FisheryCategory.*;
import Commands.ManagementCategory.*;
import Commands.Splatoon2Category.*;
import Core.ExceptionHandler;
import org.javacord.api.DiscordApi;

import java.util.*;

public class CommandContainer {
    private static CommandContainer ourInstance = new CommandContainer();
    public static CommandContainer getInstance() {
        return ourInstance;
    }

    private HashMap<String, Class<? extends Command>> commands;
    private ArrayList<OnReactionAddStaticListener> staticReactionAddCommands;
    private ArrayList<OnReactionRemoveStaticListener> staticReactionRemoveCommands;
    private ArrayList<OnTrackerRequestListener> trackerCommands;
    private ArrayList<Command> commandsReaction;
    private ArrayList<Command> commandsMessageForward;
    private ArrayList<Class<? extends Command>> commandList;

    private CommandContainer() {
        commands = new HashMap<>();
        staticReactionAddCommands = new ArrayList<>();
        staticReactionRemoveCommands = new ArrayList<>();
        trackerCommands = new ArrayList<>();
        commandsReaction = new ArrayList<>();
        commandsMessageForward = new ArrayList<>();
        commandList = new ArrayList<>();

        //GIMMICKS
        commandList.add(RollCommand.class);
        commandList.add(FortuneCommand.class);
        commandList.add(KiraCommand.class);
        commandList.add(TriggerCommand.class);
        commandList.add(RainbowCommand.class);
        commandList.add(ShipCommand.class);
        commandList.add(QuoteCommand.class);
        commandList.add(AvatarCommand.class);
        commandList.add(SayCommand.class);
        commandList.add(VoteCommand.class);

        //MANAGEMENT
        commandList.add(WhiteListCommand.class);
        commandList.add(LanguageCommand.class);
        commandList.add(PrefixCommand.class);
        commandList.add(TrackerCommand.class);
        commandList.add(ReactionRolesCommand.class);
        commandList.add(WelcomeCommand.class);
        commandList.add(AutoRolesCommand.class);
        commandList.add(AutoChannelCommand.class);
        commandList.add(AutoQuoteCommand.class);
        commandList.add(NSFWFilterCommand.class);
        commandList.add(MemberCountDisplayCommand.class);
        //commandList.add(ReportCommand.class);

        //MODERATION
        commandList.add(ModSettingsCommand.class);
        commandList.add(WarnCommand.class);
        commandList.add(KickCommand.class);
        commandList.add(BanCommand.class);
        commandList.add(WarnLogCommand.class);
        commandList.add(WarnRemoveCommand.class);
        commandList.add(ChannelMuteCommand.class);
        commandList.add(ChannelUnmuteCommand.class);
        commandList.add(SelfPromotionBlockCommand.class);
        commandList.add(BannedWordsCommand.class);
        //commandList.add(AutoKickCommand.class);
        commandList.add(ClearCommand.class);
        commandList.add(FullClearCommand.class);

        //INFORMATION
        commandList.add(HelpCommand.class);
        commandList.add(FAQCommand.class);
        commandList.add(ServerInfoCommand.class);
        commandList.add(ChannelInfoCommand.class);
        commandList.add(UserInfoCommand.class);
        //commandList.add(SignUpCommand.class);
        commandList.add(PingCommand.class);
        commandList.add(NewCommand.class);
        commandList.add(StatsCommand.class);
        commandList.add(InviteCommand.class);
        commandList.add(UpvoteCommand.class);
        commandList.add(DonateCommand.class);

        //FISHERY
        commandList.add(FisheryCommand.class);
        commandList.add(ExchangeRateCommand.class);
        commandList.add(SellCommand.class);
        commandList.add(BuyCommand.class);
        commandList.add(DailyCommand.class);
        commandList.add(ClaimCommand.class);
        commandList.add(AccountCommand.class);
        commandList.add(GearCommand.class);
        commandList.add(TopCommand.class);
        commandList.add(GiveCommand.class);
        commandList.add(SurveyCommand.class);

        //CASINO
        commandList.add(CoinFlipCommand.class);
        commandList.add(HangmanCommand.class);
        commandList.add(SlotCommand.class);
        commandList.add(BlackjackCommand.class);
        commandList.add(QuizCommand.class);
        commandList.add(AnimeQuizCommand.class);

        //EMOTES
        commandList.add(DabCommand.class);
        commandList.add(AwkwardCommand.class);
        commandList.add(YesCommand.class);
        commandList.add(NoCommand.class);
        commandList.add(WaveCommand.class);
        commandList.add(CryCommand.class);
        commandList.add(DanceCommand.class);
        commandList.add(SmileCommand.class);
        commandList.add(AngryCommand.class);
        commandList.add(RunCommand.class);
        commandList.add(BlushCommand.class);
        commandList.add(StareCommand.class);
        commandList.add(SleepCommand.class);
        commandList.add(NoseBleedCommand.class);
        commandList.add(LaughCommand.class);

        //INTERACTIONS
        commandList.add(MerkelCommand.class);
        commandList.add(KillCommand.class);
        commandList.add(PunchCommand.class);
        commandList.add(SlapCommand.class);
        commandList.add(BakaCommand.class);
        commandList.add(StealCommand.class);
        commandList.add(ThrowCommand.class);
        commandList.add(BullyCommand.class);
        //commandList.add(NotWorkCommand.class);
        commandList.add(EveryoneCommand.class);
        commandList.add(BiteCommand.class);
        commandList.add(NomCommand.class);
        commandList.add(PokeCommand.class);
        commandList.add(FishCommand.class);
        commandList.add(TickleCommand.class);
        commandList.add(HighfiveCommand.class);
        commandList.add(PatCommand.class);
        commandList.add(RewardCommand.class);
        commandList.add(MassageCommand.class);
        commandList.add(LickCommand.class);
        commandList.add(LoveCommand.class);
        commandList.add(HugCommand.class);
        commandList.add(YaoiHugCommand.class);
        commandList.add(CuddleCommand.class);
        commandList.add(YaoiCuddleCommand.class);
        commandList.add(KissCommand.class);
        commandList.add(YaoiKissCommand.class);
        commandList.add(YuriKissCommand.class);
        commandList.add(MarryCommand.class);
        commandList.add(FuckCommand.class);
        commandList.add(YaoiFuckCommand.class);
        commandList.add(YuriFuckCommand.class);
        commandList.add(YiffCommand.class);

        //EXTERNAL
        commandList.add(RedditCommand.class);
        commandList.add(MemeCommand.class);
        commandList.add(WholesomeCommand.class);
        commandList.add(DadJokeCommand.class);
        commandList.add(AnimeNewsCommand.class);
        commandList.add(AnimeReleasesCommand.class);
        commandList.add(IncreaseResolutionCommand.class);
        //commandList.add(YouTubeMP3Command.class);
        commandList.add(SafebooruCommand.class);
        commandList.add(SoftYaoiCommand.class);
        commandList.add(SoftYuriCommand.class);

        //NSFW
        commandList.add(Rule34Command.class);
        commandList.add(GelbooruCommand.class);
        commandList.add(RealbooruCommand.class);
        commandList.add(RealLifePornCommand.class);
        commandList.add(HentaiCommand.class);
        commandList.add(TrapCommand.class);
        commandList.add(FutaCommand.class);
        commandList.add(NekoCommand.class);
        commandList.add(YaoiCommand.class);
        commandList.add(YuriCommand.class);
        commandList.add(FurryCommand.class);
        commandList.add(GimmeHentaiCommand.class);

        //SPLATOON
        commandList.add(MapsCommand.class);
        commandList.add(SalmonCommand.class);
        commandList.add(SplatnetCommand.class);

        for(Class<? extends Command> clazz: commandList) {
            try {
                Command command = CommandManager.createCommandByClass(clazz);
                addCommand(command.getTrigger(), command);
                for(String str: command.getAliases()) addCommand(str, command);
                if (command instanceof OnReactionAddStaticListener) staticReactionAddCommands.add((OnReactionAddStaticListener)command);
                if (command instanceof OnReactionRemoveStaticListener) staticReactionRemoveCommands.add((OnReactionRemoveStaticListener)command);
                if (command instanceof OnTrackerRequestListener) trackerCommands.add((OnTrackerRequestListener)command);
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearShard(int shardId) {
        for(Command command: new ArrayList<>(commandsReaction)) {
            DiscordApi api;
            if (command instanceof OnReactionAddListener) api = ((OnReactionAddListener)command).getReactionMessage().getApi();
            else api = command.getNavigationMessage().getApi();
            if (api.getCurrentShard() == shardId) {
                command.stopCountdown();
                commandsReaction.remove(command);
            }
        }
        for(Command command: new ArrayList<>(commandsMessageForward)) {
            DiscordApi api;
            if (command instanceof OnForwardedRecievedListener) api = ((OnForwardedRecievedListener)command).getForwardedMessage().getApi();
            else api = command.getNavigationMessage().getApi();
            if (api.getCurrentShard() == shardId) {
                command.stopCountdown();
                commandsReaction.remove(command);
            }
        }
    }

    private void addCommand(String trigger, Command command) {
        if (commands.containsKey(trigger)) ExceptionHandler.showErrorLog("Dupicate key for \"" + command.getTrigger() + "\"");
        else commands.put(trigger, command.getClass());
    }


    public HashMap<String, Class<? extends Command>> getCommands() {
        return commands;
    }

    public ArrayList<OnReactionAddStaticListener> getStaticReactionAddCommands() {
        return staticReactionAddCommands;
    }

    public ArrayList<OnReactionRemoveStaticListener> getStaticReactionRemoveCommands() {
        return staticReactionRemoveCommands;
    }

    public int getActivitiesSize() {
        ArrayList<Command> commandList = new ArrayList<>();

        for(Command command: getReactionInstances()) {
            if (!commandList.contains(command)) commandList.add(command);
        }
        for(Command command: getMessageForwardInstances()) {
            if (!commandList.contains(command)) commandList.add(command);
        }

        return commandList.size();
    }

    public ArrayList<Command> getReactionInstances() {
        return new ArrayList<>(commandsReaction);
    }

    public ArrayList<Command> getMessageForwardInstances() {
        return new ArrayList<>(commandsMessageForward);
    }

    public void addReactionListener(Command commandParent) {
        if (!commandsReaction.contains(commandParent))
            commandsReaction.add(commandParent);
    }

    public void addMessageForwardListener(Command commandParent) {
        if (!commandsMessageForward.contains(commandParent))
            commandsMessageForward.add(commandParent);
    }

    public void removeReactionListener(Command commandParent) {
        commandsReaction.remove(commandParent);
    }

    public void removeForwarder(Command commandParent) {
        commandsMessageForward.remove(commandParent);
    }

    public boolean reactionListenerContains(Command commandParent) {
        return commandsReaction.contains(commandParent);
    }

    public boolean forwarderContains(Command commandParent) {
        return commandsMessageForward.contains(commandParent);
    }

    public ArrayList<OnTrackerRequestListener> getTrackerCommands() {
        return trackerCommands;
    }

    public ArrayList<Class<? extends Command>> getCommandList() {
        return commandList;
    }

}
