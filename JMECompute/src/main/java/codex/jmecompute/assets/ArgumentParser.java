/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.jmecompute.assets;

import java.util.HashMap;
import java.util.function.Function;

/**
 *
 * @author codex
 */
public class ArgumentParser {
    
    private final HashMap<String, Argument> arguments = new HashMap<>();
    private String[] values;
    
    public <T> ArgumentParser add(Function<String, T> parser, String... commands) {
        Argument arg = new Argument(parser);
        for (String c : commands) {
            if (arguments.put(c, arg) != null) {
                throw new IllegalArgumentException("Command \"" + c + "\" is already used.");
            }
        }
        return this;
    }
    
    public void parse(String[] args) {
        if ((args.length & 1) == 1) {
            throw new IllegalArgumentException("Expected an even number of arguments to parse.");
        }
        for (int i = 0; i < args.length;) {
            String command = args[i++].trim();
            String value = args[i++].trim();
            if (command.startsWith("--") && command.length() > 2) {
                command = command.substring(2);
            } else if (command.startsWith("-") && command.length() == 2) {
                command = command.substring(1);
            } else {
                throw new IllegalArgumentException("Expected \"" + command + "\" in command syntax.");
            }
            Argument arg = arguments.get(command);
            if (arg == null) {
                continue;
            }
            if (!value.equals(arg.getLastParsedSource())) {
                arg.parse(value);
            }
        }
    }
    
    public <T> T get(String command) {
        Argument<T> arg = arguments.get(command);
        return arg.getLastParsedValue();
    }
    
    private static class Argument <T> {
        
        private final Function<String, T> parser;
        private String source;
        private T value;

        public Argument(Function<String, T> parser) {
            this.parser = parser;
        }
        
        public T parse(String source) {
            this.source = source;
            return (value = parser.apply(this.source));
        }
        
        public String getLastParsedSource() {
            return source;
        }
        
        public T getLastParsedValue() {
            return value;
        }
        
    }
    
}
