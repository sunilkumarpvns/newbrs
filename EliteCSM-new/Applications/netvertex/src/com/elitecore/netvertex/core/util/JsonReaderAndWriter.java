package com.elitecore.netvertex.core.util;

import java.io.FileWriter;
import java.io.IOException;

public interface JsonReaderAndWriter {
    void write(FileWriter fw, Object obj) throws IOException;
}
