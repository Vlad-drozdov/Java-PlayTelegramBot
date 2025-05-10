package org.example;

public class Countdown10S extends Thread {

    private final TelegramBot bot;
    private Room r;
    private User player;
    private User opponent;
    private MessageManager msg;

    private volatile boolean running = true;

    public Countdown10S(TelegramBot bot,MessageManager msg,Room r,User player, User opponent) {
        this.bot = bot;
        this.msg = msg;
        this.r = r;
        this.player = player;
        this.opponent = opponent;
    }

    public void kill() {
        running = false;
    }

    @Override
    public void run() {
        for (int i = 10; i > 0 && running; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }
        if (!running) return;

        if (opponent.isOnlineGameAgain()){
            msg.editMessage(opponent.getId(),opponent.getOnlineGameChoiceMessage().getMessageId(),"Опонент покинув гру");
            r.removePlayer(opponent.getId());
            r.clearPlayerIn();
            bot.getUsers().get(opponent.getId()).setChoice(null);
            bot.getUsers().get(opponent.getId()).setOnlineGame(false);
            bot.getUsers().get(opponent.getId()).setRoom(null);

        }

        if (player.isOnlineGame()&&opponent.isOnlineGame()){
            r.exitOnlineGame(bot,msg,player.getId());
            r.exitOnlineGame(bot,msg,opponent.getId());
        }else {
            if (!player.isOnlineGame()){
                r.exitOnlineGame(bot,msg,opponent.getId());
            }else if (!opponent.isOnlineGame()){
                r.exitOnlineGame(bot,msg,player.getId());
            }
        }


    }
}