package org.upperlevel.corrida.phase.game.playing;

import org.upperlevel.corrida.command.Command;

import lombok.Getter;

public class ExerciseStartCommand extends Command {
    @Getter
    private String theme;

    @Getter
    private String questionedPlayerName;

    public ExerciseStartCommand() {
    }

    public ExerciseStartCommand(String theme, String questionedPlayerName) {
        this.theme = theme;
        this.questionedPlayerName = questionedPlayerName;
    }

    @Override
    public String encode() {
        return "exercise_start \"" + theme + "\" \"" + questionedPlayerName + "\"\n";
    }

    @Override
    public ExerciseStartCommand decode(String[] split) {
        theme = split[1];
        questionedPlayerName = split[2];
        return this;
    }
}
