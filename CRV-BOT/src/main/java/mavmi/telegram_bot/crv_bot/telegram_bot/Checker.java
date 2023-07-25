package mavmi.telegram_bot.crv_bot.telegram_bot;

import mavmi.telegram_bot.crv_bot.user.CrvProfile;

public class Checker extends Thread {
    private boolean exit = false;

    private final long id;
    private final Bot bot;

    public Checker(Bot bot, long id){
        this.id = id;
        this.bot = bot;
    }

    @Override
    public void run() {
        msg("new checker started");
        while (true) {
            if (exit) {
                msg("checker exit (" + bot.getCheckerList().size() + " left)");
                return;
            }
            CrvProfile crvProfile = CrvProfile.getCrvProfile(bot.getCrvRepository(), id);
            if (crvProfile == null) return;
            bot.checkCrvCount(crvProfile);
            long ms = (long)(Math.random() * (bot.getRequestOptions().getWaitTo() - bot.getRequestOptions().getWaitFrom())) +
                    bot.getRequestOptions().getWaitFrom();
            bot.sendMsg(id, readableWaitingTime(ms));
            try {
                sleep(ms);
            } catch (InterruptedException e) {
                bot.getLogger().err(e.getMessage());
                return;
            }
        }
    }

    public void exit(boolean value){
        exit = value;
    }

    private String readableWaitingTime(long ms){
        long s = ms / 1000;
        return "Next try in " + (s / 60) + "m " + (s % 60) + "s";
    }
    private void msg(String msg){
        bot.getLogger().log(
                "[" +
                        id +
                        "] " +
                        msg
        );
    }
}
