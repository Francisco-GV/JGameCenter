package com.frank.jgamecenter.games;

import com.frank.jgamecenter.games.resources.Game;

public class Memory extends Game {
    @Override
    protected void init() {

    }

    @Override
    protected void run() {

    }

    public Memory() {
        super("Memory", """
                A board full of overturned cards.
                There is a pair for each card.
                The player needs to overturn all 
                the cards.""",
                null);
    }
}
