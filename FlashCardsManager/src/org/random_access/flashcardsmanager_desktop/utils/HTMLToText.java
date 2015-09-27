package org.random_access.flashcardsmanager_desktop.utils;

import java.io.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class HTMLToText extends HTMLEditorKit.ParserCallback {
    StringBuffer s;

    public void parse(Reader in) throws IOException {
        s = new StringBuffer();
        ParserDelegator delegator = new ParserDelegator();
        delegator.parse(in, this, Boolean.TRUE);
    }

    public void handleText(char[] text, int pos) {
        s.append(text);
        s.append("\n");
    }

    public String getText() {
        return s.toString();
    }

}