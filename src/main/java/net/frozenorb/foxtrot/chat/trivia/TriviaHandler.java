package net.frozenorb.foxtrot.chat.trivia;

import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.chat.trivia.listener.TriviaListener;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TriviaHandler {

    private final Map<String, String> questions = new HashMap<>(); // question -> answer

    public String currentQuestion = null;
    public String currentAnswer = null;

    public TriviaHandler(){
        questions.put("What is the name of the server?", "Blizzard");
        questions.put("Who owns the server?", "Embry");
        questions.put("What version is this server on?", "1.16");
        questions.put("What's 829+219?", "1048");
        questions.put("What's 9 cubed?", "729");
        questions.put("In what year was Minecraft released?", "2011");
        questions.put("What is the current top rank on FrozenHCF?", "Glacial");
        questions.put("What country has the 2nd largest GDP?", "China");
        questions.put("What country is largest in population?", "India");
        questions.put("What country is largest in land size?", "Russia");
        questions.put("What's the first 5 numbers of PI?", "3.1415");
        questions.put("What is the 8th letter of the alphabet?", "H");
        questions.put("What is the 12th letter of the alphabet?", "L");
        questions.put("What is the 18th letter of the alphabet?", "R");
        questions.put("What is the 24th letter of the alphabet?", "W");
        questions.put("What's the final boss in Minecraft?", "Enderdragon");
        questions.put("Who built all the KOTHs?", "Pumpkin");

        Bukkit.getScheduler().runTaskTimer(HCF.getInstance(), this::startTrivia, 20 * 20 * 60, 20 * 20 * 60); // 10 minutes
        Bukkit.getPluginManager().registerEvents(new TriviaListener(), HCF.getInstance());
    }

    public void startTrivia(){
        final List<String> questionList = new ArrayList<>(questions.keySet());

        final String question = questionList.get(ThreadLocalRandom.current().nextInt(questionList.size()-1));
        final String answer = questions.get(question);
        this.currentQuestion = question;
        this.currentAnswer = answer;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(CC.translate("&3&lTrivia"));
        Bukkit.broadcastMessage(CC.translate(question));
        Bukkit.broadcastMessage(CC.translate("&7First person to get the answer correct will get a &brandom prize&7!"));
        Bukkit.broadcastMessage("");

        Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
            if (!this.currentAnswer.equalsIgnoreCase(answer)) {
                return;
            }

            endTrivia(null);
        }, 20*30);
    }

    public void endTrivia(Player player){
        if (player == null){
            Bukkit.broadcastMessage(CC.translate(""));
            Bukkit.broadcastMessage(CC.translate("&3&lTriva"));
            Bukkit.broadcastMessage(CC.translate("&7No one got the answer in time!"));
            Bukkit.broadcastMessage(CC.translate(""));

            this.currentAnswer = null;
            this.currentQuestion = null;

            return;
        }

        Bukkit.broadcastMessage(CC.translate(""));
        Bukkit.broadcastMessage(CC.translate("&3&lTriva"));
        Bukkit.broadcastMessage(CC.translate("&r" + player.getDisplayName() + " &fgot the answer!"));
        Bukkit.broadcastMessage(CC.translate("&fAnswer: &b" + currentAnswer));
        Bukkit.broadcastMessage(CC.translate("&fPrize: &b5 Partner Packages"));
        Bukkit.broadcastMessage(CC.translate(""));

        player.getInventory().addItem(ItemBuilder.copyOf(HCF.getInstance().getPartnerPackageHandler().PACKAGE_ITEM).amount(5).build());


        this.currentAnswer = null;
        this.currentQuestion = null;
    }


    public boolean isActive(){
        return currentAnswer != null;
    }

}
