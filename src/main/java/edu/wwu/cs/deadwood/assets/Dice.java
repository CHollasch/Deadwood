/*
 * Copyright (C) 2017 Deadwood - All Rights Reserved
 *
 * Unauthorized copying of this file, via any median is strictly prohibited
 * proprietary and confidential. For more information, please contact me at
 * connor@hollasch.net
 *
 * Written by Yichuan Yin <yiny@wwu.edu>, November 2017
 */

package edu.wwu.cs.deadwood;

import edu.wwu.cs.deadwood.assets.Card;
import edu.wwu.cs.deadwood.assets.Role;
import edu.wwu.cs.deadwood.assets.Room;
import lombok.Getter;
import lombok.Setter;
import java.util.Random;

/**
 * @author Yichuan Yin
 * @since November 9, 10:40 PM
 */

public class Dice
{
    private final int numOfDice;
    private final int numOfValues;

    public Dice(int diceCount, int valueCount)
    {
        numOfDice = diceCount;
        numOfValues = valueCount;
    }

    public int roll(int diceCount)
    {
        Random r = new Random();
        int value = 0;
        for (int i = 0; i < diceCount; i++)
        {
            value += r.nextInt(numOfValues) + 1;
        }
        return value;
    }

    public int[] detailedRoll(int diceCount)
    {
        Random r = new Random();
        int[] result = new int[diceCount];
        for (int i = 0; i < diceCount; i++) {
            result[i] = r.nextInt(numOfValues) + 1;
        }
        return result;
    }
}
