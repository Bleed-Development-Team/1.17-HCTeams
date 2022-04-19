package net.frozenorb.foxtrot.util;

import java.util.Random;

public class RandomUtil {


    public static boolean chance(int chance){
        Random rnd = new Random();
        int i = rnd.nextInt(100);

        return i <= chance;
    }
}
