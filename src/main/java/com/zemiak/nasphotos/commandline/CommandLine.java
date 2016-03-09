package com.zemiak.nasphotos.commandline;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandLine {
    private static final Logger LOG = Logger.getLogger(CommandLine.class.getName());
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    private static final Integer TIMEOUT = 300; // 5 minutes

    private CommandLine() {
    }

    public static List<String> execCmd(final String cmd, final List<String> arguments)
            throws IOException, InterruptedException, IllegalStateException {
        CommandLineResult result = new CommandLineResult();
        Callable<CommandLineResult> callable = getCallable(cmd, arguments);

        LOG.log(Level.INFO, "run:{0} {1}", new Object[]{cmd, null == arguments ? "" : Joiner.join(arguments, "|")});

        try {
            result = timedCall(callable, TIMEOUT, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            final List<String> lines = new ArrayList<>();
            lines.add("Timeout " + TIMEOUT + " seconds");

            result.setExitValue(-1);
            result.setOutput(lines);
        } catch (ExecutionException ex) {
            final List<String> lines = new ArrayList<>();
            lines.add("Execution: " + ex.getMessage());

            result.setExitValue(-2);
            result.setOutput(lines);
        }

        if (result.isError()) {
            LOG.log(Level.SEVERE, "... execCmd: error code is {0}, arguments {1}, output is {2}",
                    new Object[]{result.getExitValue(), null == arguments ? "" : Joiner.join(arguments, "|"),
                        Joiner.join(result.getOutput(), "|")});
            throw new IllegalStateException("Exit code " + result.getExitValue() + " instead of success");
        }

        return result.getOutput();
    }

    private static String streamToString(final InputStream stream) throws IOException {
        char[] buff = new char[1024];
        Writer stringWriter = new StringWriter();

        try {
            Reader bReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            int n;
            while ((n = bReader.read(buff)) != -1) {
                stringWriter.write(buff, 0, n);
            }
        } finally {
            stringWriter.close();
        }

        return stringWriter.toString();
    }

    private static <T> T timedCall(final Callable<T> c, final long timeout, final TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException
    {
        final FutureTask<T> task = new FutureTask<>(c);
        THREAD_POOL.execute(task);
        return task.get(timeout, timeUnit);
    }

    private static Callable<CommandLineResult> getCallable(final String cmd, final List<String> arguments) {
        final List<String> command = new ArrayList<>(arguments);
        command.add(0, cmd);

        return () -> {
            Process process = Runtime.getRuntime().exec(command.toArray(new String[]{}));

            int exitValue = process.waitFor();

            List<String> lines = new ArrayList<>();
            try (InputStream stream = process.getInputStream();) {
                lines.addAll(Arrays.asList(streamToString(stream).split(System.getProperty("line.separator"))));
            }

            CommandLineResult result = new CommandLineResult();
            result.setExitValue(exitValue);
            result.setOutput(lines);

            return result;
        };
    }
}
