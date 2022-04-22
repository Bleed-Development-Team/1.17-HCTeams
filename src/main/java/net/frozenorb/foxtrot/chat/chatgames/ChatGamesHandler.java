package net.frozenorb.foxtrot.chat.chatgames;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.chatgames.listener.ChatGamesListener;
import net.frozenorb.foxtrot.chat.chatgames.tasks.ChatGamesTask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatGamesHandler {

    @Getter @Setter public String question;
    @Getter @Setter public String answer;

    @Getter public List<String> questions = new ArrayList<>();
    @Getter public List<String> answers = new ArrayList<>();

    public ChatGamesHandler(){
        init();
    }

    public void init(){
        // Questions
        questions.add("What is the name of the owner?");
        questions.add("What day is SOTW hosted on?");
        questions.add("How long are kit cooldowns?");
        questions.add("What is the name of the server you're currently playing?");

        // Answers - We make these lowercase for when we handle them in the chat.
        answers.add("alebab");
        answers.add("friday");
        answers.add("24h");
        answers.add("bleedhcf");

        // Listeners
        Bukkit.getPluginManager().registerEvents(new ChatGamesListener(), Foxtrot.getInstance());

        // Tasks
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Foxtrot.getInstance(), new ChatGamesTask(), 0L, 20 * 60 * 5);

    }

    public void startNewGame(){
        if (isGoingOn()){
            return;
        }
        pickRandomQuestion();

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&4&lChat Games &7| &fStarting"));
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&fThe question is:"));
        Bukkit.broadcastMessage(CC.translate(question));
        Bukkit.broadcastMessage("");

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (isGoingOn()){
                endGame(true, null);
            }
        }, 15 * 20);

    }

    public void endGame(boolean lost, Player player){
        question = null;

        if (lost){
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(CC.translate("&4&lChat Games &7| &fEnding"));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(CC.translate("&fYou ran out of time!"));
            Bukkit.broadcastMessage(CC.translate("&fThe answer was: " + answer.substring(0, 1).toUpperCase() + answer.substring(1).replace("hcf", "HCF")));
            Bukkit.broadcastMessage("");

        } else {
            int keys = Foxtrot.RANDOM.nextInt(1, 2);
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(CC.translate("&4&lChat Games &7| &fEnding"));
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(CC.translate("&fCongratulations to &c" + player.getName() + " &ffor getting the word correct!"));
            Bukkit.broadcastMessage(CC.translate("&fThe answer was: &c" + answer.substring(0, 1).toUpperCase() + answer.substring(1)));
            Bukkit.broadcastMessage(CC.translate("&c" + player.getName() + " &fhas been awarded &f" + keys + " &6Legendary Keys!"));
            Bukkit.broadcastMessage("");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crate givekey " + player.getName() + "legendary " + keys);
        }

        answer = null;
    }

    public boolean isCorrect(String answer){
        return answers.get(questions.indexOf(question)).equalsIgnoreCase(answer);
    }

    public boolean isGoingOn(){
        return question != null || answer != null;
    }


    public void pickRandomQuestion(){
        question = questions.get(Foxtrot.RANDOM.nextInt(questions.size()));

        answer = answers.get(questions.indexOf(question));
    }

}
