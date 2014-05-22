package org.bonitasoft.shell.completer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import jline.console.ConsoleReader;
import jline.console.completer.CandidateListCompletionHandler;

public class ReflectCandidateListCompletionHandler extends CandidateListCompletionHandler {

    @Override
    public boolean complete(final ConsoleReader reader, final List<CharSequence> candidates, final int pos) throws IOException {
        Iterator<CharSequence> it = candidates.iterator();
        String help = null;
        while (it.hasNext()) {
            String asString = "" + it.next();
            if (asString.startsWith("**HELP")) {
                it.remove();
                help = asString.substring(6);
            }
        }
        if (candidates.isEmpty() && help != null) {
            reader.println();
            reader.println(help);
        }
        return super.complete(reader, candidates, pos);
    }

}
