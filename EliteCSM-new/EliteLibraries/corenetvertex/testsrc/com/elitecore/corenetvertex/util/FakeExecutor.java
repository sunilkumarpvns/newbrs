package com.elitecore.corenetvertex.util;

import java.util.concurrent.Executor;

public class FakeExecutor implements Executor {

    private Runnable command;

    @Override
    public void execute(Runnable command) {
        this.command = command;
    }

    public void tick(){
        command.run();
    }

}
