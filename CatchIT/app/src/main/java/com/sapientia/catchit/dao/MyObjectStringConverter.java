package com.sapientia.catchit.dao;

import com.sapientia.catchit.models.Group;
import com.sapientia.catchit.models.Player;
import com.sapientia.catchit.models.Trainer;

import java.util.Arrays;
import java.util.List;

public class MyObjectStringConverter
{
    public MyObjectStringConverter()
    {
    }

    public String playerToCsv(Player player)
    {
        String csvPlayer = "";
        csvPlayer += player.getUserName() + ",";
        csvPlayer += player.getPassword() + ",";
        csvPlayer += player.getBirthYear() + ",";
        csvPlayer += player.getBirthMonth() + ",";
        csvPlayer += player.getBirthDay() + ",";
        csvPlayer += player.getGender() + ",";
        csvPlayer += player.getAutomaticLogin();
        return csvPlayer;
    }

    public String trainerToCsv(Trainer trainer)
    {
        String csvTrainer = "";
        csvTrainer += trainer.getUserName() + ",";
        csvTrainer += trainer.getPassword() + ",";
        csvTrainer += trainer.getBirthYear() + ",";
        csvTrainer += trainer.getBirthMonth() + ",";
        csvTrainer += trainer.getBirthDay() + ",";
        csvTrainer += trainer.getGender() + ",";
        csvTrainer += trainer.getAutomaticLogin() ;
        return csvTrainer;
    }

    public String groupToCsv(Group group)
    {
        String csvGroup = "";
        csvGroup += group.getGroupName() + ",";
        csvGroup += group.getOwner() + ",";
        csvGroup += group.getNumberOfPlayers();
        return  csvGroup;
    }

//    public Player fromCsvPlayer(String csvPlayer)
//    {
//        List<String> objFieldList = Arrays.asList(csvPlayer.split(","));
//        int index = 0;
//        Integer id = objFieldList
//
//    }

}
