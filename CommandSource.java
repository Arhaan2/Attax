package ataxx;

/** Provides command input, one line at a time.
 *  @author P. N. Hilfinger
 */
interface CommandSource {

    /** Returns one command string, trimmed of preceding and following
     *  whitespace and converted to upper case.  If the CommandSource
     *  prompts for input, use PROMPT, if not null, to do so. */
    String getCommand(String prompt);

}
