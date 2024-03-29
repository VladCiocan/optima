package com.hartehanks;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Largely GNU-compatible command-line options parser. Has short (-v) and
 * long-form (--verbose) option support, and also allows options with
 * associated values (-d 2, --debug 2, --debug=2). Option processing
 * can be explicitly terminated by the argument '--'.
 *
 * @author Steve Purcell
 * @version $Id: Optarg.java,v 1.3 2001/08/21 08:47:36 purcell Exp $
 * @see jargs.examples.gnu.OptionTest
 */

/**
 * Base class for exceptions that may be thrown when options are parsed
 */
public class Optarg {
    public static abstract class OptionException extends Exception {
        OptionException(String msg) {
            super(msg);
        }
    }

    /**
     * Thrown when the parsed command-line contains an option that is not
     * recognised. <code>getMessage()</code> returns
     * an error string suitable for reporting the error to the user (in
     * English).
     */
    public static class UnknownOptionException extends OptionException {
        private String optionName = null;

        UnknownOptionException(String optionName) {
            super("unknown option '" + optionName + "'");
            this.optionName = optionName;
        }

        /**
         * @return the name of the option that was unknown (e.g. "-u")
         */
        public String getOptionName() {
            return this.optionName;
        }
    }

    /**
     * Thrown when an illegal or missing value is given by the user for
     * an option that takes a value. <code>getMessage()</code> returns
     * an error string suitable for reporting the error to the user (in
     * English).
     */
    public static class IllegalOptionValueException extends OptionException {
        private Option option;
        private String value;

        IllegalOptionValueException(Option opt, String value) {
            super("illegal value '" + value + "' for option -" +
                    opt.shortForm() + "/--" + opt.longForm());
            this.option = opt;
            this.value = value;
        }

        /**
         * @return the name of the option whose value was illegal (e.g. "-u")
         */
        public Option getOption() {
            return this.option;
        }

        /**
         * @return the illegal value
         */
        public String getValue() {
            return this.value;
        }
    }

    /**
     * Representation of a command-line option
     */
    public static abstract class Option {
        protected Option(char shortForm, String longForm,
                         boolean wantsValue) {
            if (longForm == null) {
                throw new IllegalArgumentException(
                        "null arg forms not allowed");
            }
            this.shortForm = new String(new char[]{shortForm});
            this.longForm = longForm;
            this.wantsValue = wantsValue;
        }

        public String shortForm() {
            return this.shortForm;
        }

        public String longForm() {
            return this.longForm;
        }

        /**
         * Tells whether or not this option wants a value
         */
        public boolean wantsValue() {
            return this.wantsValue;
        }

        public final Object getValue(String arg)
                throws IllegalOptionValueException {
            if (this.wantsValue) {
                if (arg == null) {
                    throw new IllegalOptionValueException(this, "");
                }
                return this.parseValue(arg);
            } else {
                return Boolean.TRUE;
            }
        }

        /**
         * Override to extract and convert an option value passed on the
         * command-line
         */
        protected Object parseValue(String arg)
                throws IllegalOptionValueException {
            return null;
        }

        private String shortForm = null;
        private String longForm = null;
        private boolean wantsValue = false;

        public static class BooleanOption extends Option {
            public BooleanOption(char shortForm, String longForm) {
                super(shortForm, longForm, false);
            }
        }

        /**
         * An option that expects an integer value
         */
        public static class IntegerOption extends Option {
            public IntegerOption(char shortForm, String longForm) {
                super(shortForm, longForm, true);
            }

            protected Object parseValue(String arg)
                    throws IllegalOptionValueException {
                try {
                    return new Integer(arg);
                } catch (NumberFormatException e) {
                    throw new IllegalOptionValueException(this, arg);
                }
            }
        }

        /**
         * An option that expects a string value
         */
        public static class StringOption extends Option {
            public StringOption(char shortForm, String longForm) {
                super(shortForm, longForm, true);
            }

            protected Object parseValue(String arg) {
                return arg;
            }
        }
    }

    /**
     * Add the specified Option to the list of accepted options
     */
    public final void addOption(Option opt) {
        this.options.put("-" + opt.shortForm(), opt);
        this.options.put("-" + opt.longForm(), opt);
        this.options.put("--" + opt.longForm(), opt);
    }

    /**
     * Convenience method for adding a string option.
     * @return the new Option
     */
    public final Option addStringOption(char shortForm, String longForm) {
        Option opt = new Option.StringOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * Convenience method for adding an integer option.
     * @return the new Option
     */
    public final Option addIntegerOption(char shortForm, String longForm) {
        Option opt = new Option.IntegerOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * Convenience method for adding a boolean option.
     * @return the new Option
     */
    public final Option addBooleanOption(char shortForm, String longForm) {
        Option opt = new Option.BooleanOption(shortForm, longForm);
        addOption(opt);
        return opt;
    }

    /**
     * @return the parsed value of the given Option, or null if the
     * option was not set.
     * For Boolean type will return true(set) or null(not set)
     * For String type will return String(set) or null(not set)
     * For Integer type will return Integer(set) or null(not set)
     */
    public final Object getOptionValue(Option o) {
        return values.get(o.longForm());
    }

    /**
     * @return the non-option arguments
     */
    public final String[] getRemainingArgs() {
        return this.remainingArgs;
    }

    /**
     * Extract the options and non-option arguments from the given
     * list of command-line arguments.
     */
    public void parse(String[] argv)
            throws IllegalOptionValueException, UnknownOptionException {
        Vector otherArgs = new Vector();
        int position = 0;
        while (position < argv.length) {
            String curArg = argv[position];
            if (curArg.startsWith("-")) {
                if (curArg.equals("--")) { // end of options
                    position += 1;
                    break;
                }
                String valueArg = null;
                if (curArg.startsWith("--")) { // handle --arg=value
                    int equalsPos = curArg.indexOf("=");
                    if (equalsPos != -1) {
                        valueArg = curArg.substring(equalsPos + 1);
                        curArg = curArg.substring(0, equalsPos);
                    }
                }
                Option opt = (Option) this.options.get(curArg);
                if (opt == null) {
                    throw new UnknownOptionException(curArg);
                }
                Object value = null;
                if (opt.wantsValue()) {
                    if (valueArg == null) {
                        position += 1;
                        valueArg = null;

                        if (position < argv.length) {
                            valueArg = argv[position];
                        }
                    }

                    value = opt.getValue(valueArg);
                } else {
                    value = opt.getValue(null);
                }

                this.values.put(opt.longForm(), value);
                position += 1;
            } else {
                break;
            }
        }
        for (; position < argv.length; ++position) {
            otherArgs.addElement(argv[position]);
        }

        this.remainingArgs = new String[otherArgs.size()];
        int i = 0;
        for (Enumeration e = otherArgs.elements(); e.hasMoreElements(); ++i) {
            this.remainingArgs[i] = (String) e.nextElement();
        }
    }

    private String[] remainingArgs = null;
    private Hashtable options = new Hashtable(10);
    private Hashtable values = new Hashtable(10);
}
