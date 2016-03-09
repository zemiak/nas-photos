package com.zemiak.nasphotos.commandline;

import java.util.Collections;
import java.util.List;

public class CommandLineResult {
    private int exitValue;
    private List<String> output;

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public List<String> getOutput() {
        return Collections.unmodifiableList(output);
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public boolean isError() {
        return exitValue != 0;
    }
}
