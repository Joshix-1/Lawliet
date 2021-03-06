package core;

import commands.CommandContainer;
import commands.runningchecker.RunningCheckerManager;
import constants.AssetIds;
import core.internet.HttpProperty;
import core.internet.HttpRequest;
import core.internet.HttpResponse;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.KnownCustomEmoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DiscordApiCollection {

    private static final DiscordApiCollection ourInstance = new DiscordApiCollection();
    public static DiscordApiCollection getInstance() { return ourInstance; }

    private final static Logger LOGGER = LoggerFactory.getLogger(DiscordApiCollection.class);

    private DiscordApi[] apiList = new DiscordApi[0];
    private int[] errorCounter;
    private boolean[] isAlive;
    private boolean started = false;
    private final Instant startingTime = Instant.now();

    private DiscordApiCollection() {
        Thread t = new CustomThread(() -> {
            try {
                Thread.sleep(12 * 60 * 1000);
                if (!allShardsConnected()) {
                    LOGGER.error("EXIT - Could not boot up");
                    System.exit(-1);
                }
            } catch (InterruptedException e) {
                LOGGER.error("EXIT - Interrupted", e);
                System.exit(-1);
            }
        }, "bootup_timebomb", 1);
        t.start();
    }

    public void init(int shardNumber) {
        apiList = new DiscordApi[shardNumber];
        errorCounter = new int[shardNumber];
        isAlive = new boolean[shardNumber];
    }

    public void insertApi(DiscordApi api) {
        apiList[api.getCurrentShard()] = api;
        if (Bot.isProductionMode()) {
            new CustomThread(() -> keepApiAlive(api), "keep_alive_shard" + api.getCurrentShard(), 1)
                    .start();
        }
    }

    public void setStarted() {
        if (allShardsConnected())
            started = true;
    }

    private void keepApiAlive(DiscordApi api) {
        api.addMessageCreateListener(event -> isAlive[event.getApi().getCurrentShard()] = true);
        try {
            while (Bot.isRunning()) {
                Thread.sleep(10 * 1000);
                int n = api.getCurrentShard();
                if (shardIsConnected(n) && isAlive[n]) {
                    errorCounter[n] = 0;
                    isAlive[n] = false;
                } else {
                    LOGGER.debug("No data from shard {}", n);

                    errorCounter[n]++;
                    if (errorCounter[n] >= 6) {
                        LOGGER.warn("Shard {} temporarely offline", n);
                        reconnectShard(n);
                        return;
                    }
                }
            }
        } catch (InterruptedException e) {
            LOGGER.error("EXIT - Interrupted", e);
            System.exit(-1);
        }
    }

    public boolean shardIsConnected(int n) {
        return n < apiList.length && apiList[n] != null;
    }

    public void reconnectShard(int n) {
        if (Bot.isRunning() && apiList[n] != null) {
            DiscordApi api = apiList[n];
            apiList[n] = null;
            try {
                CommandContainer.getInstance().clearShard(n);
            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
            RunningCheckerManager.getInstance().clearShard(n);
            api.disconnect();
            DiscordConnector.getInstance().reconnectApi(api.getCurrentShard());
            errorCounter[n] = 0;
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void stop() {
        started = false;
        for(DiscordApi api: apiList) {
            if (api != null) {
                try {
                    api.disconnect();
                } catch (Throwable e) {
                    LOGGER.error("Error while disconnecting api with shard {}", api.getCurrentShard());
                }
            }
        }
    }

    public Optional<Server> getServerById(long serverId) {
        if (apiList[getResponsibleShard(serverId)] == null) return Optional.empty();
        return apiList[getResponsibleShard(serverId)].getServerById(serverId);
    }

    public Optional<User> getUserById(long serverId, long userId) {
        int shardId = getResponsibleShard(serverId);
        if (apiList[shardId] != null) {
            try {
                return Optional.of(apiList[shardId].getUserById(userId).get());
            } catch (InterruptedException | ExecutionException e) {
                //Ignore
            }
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(long userId) {
        for(DiscordApi api: apiList) {
            if (api != null) {
                try {
                    return Optional.of(api.getUserById(userId).get());
                } catch (InterruptedException | ExecutionException e) {
                    //Ignore
                }
            }
        }
        return Optional.empty();
    }

    public Optional<User> fetchUserById(Server server, long userId) {
        try {
            return Optional.of(server.getApi().getUserById(userId).get());
        } catch (InterruptedException | ExecutionException e) {
            //Ignore
        }
        return Optional.empty();
    }

    public List<Server> getMutualServers(User user) {
        return getServers().stream().filter((server) -> server.isMember(user)).collect(Collectors.toList());
    }

    public Optional<ServerTextChannel> getServerTextChannelById(long serverId, long channelId) {
        Optional<Server> server = getServerById(serverId);
        if (server.isPresent()) {
            return server.get().getTextChannelById(channelId);
        } else {
            return Optional.empty();
        }
    }

    public Optional<ServerVoiceChannel> getServerVoiceChannelById(long serverId, long channelId) {
        Optional<Server> server = getServerById(serverId);
        if (server.isPresent()) {
            return server.get().getVoiceChannelById(channelId);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Message> getMessageById(long serverId, long channelId, long messageId) {
        Optional<Server> server = getServerById(serverId);
        if (server.isPresent()) {
            return getMessageById(server.get(), channelId, messageId);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Message> getMessageById(Server server, long channelId, long messageId) {
        Optional<ServerTextChannel> channel = server.getTextChannelById(channelId);
        if (channel.isPresent()) {
            return getMessageById(channel.get(), messageId);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Message> getMessageById(ServerTextChannel channel, long messageId) {
        try {
            return Optional.of(channel.getMessageById(messageId).get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

    public Optional<KnownCustomEmoji> getCustomEmojiById(long emojiId) {
        waitForStartup();

        for(DiscordApi api: apiList) {
            Optional<KnownCustomEmoji> emojiOptional = api.getCustomEmojiById(emojiId);
            if (emojiOptional.isPresent()) return emojiOptional;
        }
        return Optional.empty();
    }

    public Optional<KnownCustomEmoji> getCustomEmojiById(String emojiId) {
        try {
            return getCustomEmojiById(Long.parseLong(emojiId));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public boolean customEmojiIsKnown(CustomEmoji customEmoji) {
        return getCustomEmojiById(customEmoji.getId()).isPresent();
    }

    public Server getHomeServer() {
        long serverId = AssetIds.HOME_SERVER_ID;
        Optional<Server> serverOptional = getServerById(serverId);
        if (!serverOptional.isPresent()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
            throw new RuntimeException("Home server not connected");
        }
        return serverOptional.get();
    }

    public boolean apiHasHomeServer(DiscordApi api) {
        long serverId = AssetIds.HOME_SERVER_ID;
        return getResponsibleShard(serverId) == api.getCurrentShard();
    }

    public int getResponsibleShard(long serverId) {
        return Math.abs((int) ((serverId >> 22) % apiList.length));
    }

    public int size() {
        return apiList.length;
    }

    public boolean allShardsConnected() {
        for (DiscordApi discordApi : apiList)
            if (discordApi == null) return false;
        return true;
    }

    public Collection<Server> getServers() {
        waitForStartup();

        ArrayList<Server> serverList = new ArrayList<>();
        for(DiscordApi api: apiList) {
            serverList.addAll(api.getServers());
        }

        return serverList;
    }

    public HashSet<Long> getUserIds() {
        HashSet<Long> userSet = new HashSet<>();
        getServers().forEach(server -> {
            server.getMembers().forEach(user -> {
                if (!user.isBot()) userSet.add(user.getId());
            });
        });

        return userSet;
    }

    public int getServerTotalSize() {
        waitForStartup();

        int n = 0;
        for(DiscordApi api: apiList) {
            n += api.getServers().size();
        }

        return n;
    }

    public void waitForStartup() {
        while(!allShardsConnected()) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted", e);
            }
        }
    }

    public User getOwner() {
        for(DiscordApi api: apiList) {
            try {
                return api.getOwner().get();
            } catch (InterruptedException | ExecutionException e) {
                //Ignore
            }
        }

        throw new NullPointerException();
    }

    public long getOwnerId() {
        return apiList[0].getOwnerId();
    }

    public User getYourself() {
        waitForStartup();
        return apiList[0].getYourself();
    }

    public Collection<DiscordApi> getApis() {
        return Arrays.asList(apiList);
    }

    public KnownCustomEmoji getHomeEmojiById(long emojiId) {
        try {
            return EmojiCache.getInstance().getHomeEmojiById(emojiId);
        } catch (ExecutionException e) {
            LOGGER.error("Emoji with id {} not found", emojiId);
            return null;
        }
    }

    public KnownCustomEmoji getHomeEmojiByName(String emojiName) {
        try {
            return EmojiCache.getInstance().getHomeEmojiByName(emojiName);
        } catch (ExecutionException e) {
            LOGGER.error("Emoji with name {} not found", emojiName);
            return null;
        }
    }

    public CustomEmoji getBackEmojiCustom() {
        return getHomeEmojiById(511165137202446346L);
    }

    public Optional<ServerTextChannel> getFirstWritableChannel(Server server) {
        if (server.getSystemChannel().isPresent() && server.getSystemChannel().get().canYouSee() && server.getSystemChannel().get().canYouWrite() && server.getSystemChannel().get().canYouEmbedLinks()) {
            return server.getSystemChannel();
        } else {
            for(ServerTextChannel channel: server.getTextChannels()) {
                if (channel.canYouSee() && channel.canYouWrite() && channel.canYouEmbedLinks()) {
                    return Optional.of(channel);
                }
            }
        }

        return Optional.empty();
    }

    public CompletableFuture<HttpResponse> removeWebhook(String webhookUrl) throws IOException {
        String[] segments = webhookUrl.split("/");
        String webhookId = segments[segments.length - 2];
        String token = segments[segments.length - 1];

        return HttpRequest.getData(String.format("https://discordapp.com/api/v6/webhooks/%s/%s", webhookId, token), "DELETE", 0, "");
    }

    public CompletableFuture<HttpResponse> sendToWebhook(Server server, String webhookUrl, String content) throws IOException {
        User yourself = getYourself();

        HttpProperty contentType = new HttpProperty("Content-type", "application/json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", server.getDisplayName(yourself));
        jsonObject.put("avatar_url", yourself.getAvatar().getUrl());
        jsonObject.put("content", content);

        return HttpRequest.getData(webhookUrl, "POST", jsonObject.toString(), contentType);
    }

    public Instant getStartingTime() {
        return startingTime;
    }

}