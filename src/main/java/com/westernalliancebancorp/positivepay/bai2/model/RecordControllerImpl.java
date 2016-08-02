package com.westernalliancebancorp.positivepay.bai2.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * RecordControllerImpl is
 *
 * @author Giridhar Duggirala
 */

public class RecordControllerImpl implements RecordController {
    private Map<Integer, String> bai2File = new HashMap<Integer, String>();

    /**
     * Read the file. Read the lines and put it in a Map<Integer, String> where Integer the line number of the file and String the line content.
     */
    public RecordControllerImpl(File fileToRead) {
        try {
            BufferedReader bufferReader = new BufferedReader(new FileReader(fileToRead));
            int i = 0;
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                bai2File.put(i, line);
                i++;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void process() {
        FileHeaderRecord fileHeaderRecord = new FileHeaderRecord();
        fileHeaderRecord.process(bai2File, 0);
    }
}
