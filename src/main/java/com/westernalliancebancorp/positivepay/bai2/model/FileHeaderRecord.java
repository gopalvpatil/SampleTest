package com.westernalliancebancorp.positivepay.bai2.model;

import com.westernalliancebancorp.positivepay.log.Loggable;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 01 - FILE HEADER
 * The file header marks the beginning of a file. It identifies the sender and the receiver of the transmission, and it
 * describes the structure of the file. All fields are required except those labeled optional.
 * <p/>
 * Field Name                                                                 Field description
 * Record Code                                                                01
 * Sender Identification Transmitter of file.                                 Usually the ABA of the sending instutution. Alphanumeric.
 * Receiver Identification                                                    Next recipient of the file. Usually the ABA of the next receiving
 * institution. Alphanumeric.
 * File Creation Date                                                         Date the sender created the file. YYMMDD format.
 * File Creation Time                                                         Time the sender created the file. Military format (2400); sender time zone.
 * File Identification Number                                                 Identification number defined by the sender. The number must be
 * new for each file with the same file creation date. Used to identify
 * uniquely those files transmitted between a sender and a receiver on
 * a given date.
 * Physical Record Length Optional.                                           Number of characters in a physical record. Default is variable length records.
 * Block Size                                                                 Optional. Number of physical records in a block. Default is variable block size.
 * Version Number                                                             2
 * <p/>
 * Delimiters: Comma "," delimits fields.
 * Slash "/" delimits the end of the logical record. Adjacent delimiters, ",," or ",/"
 * identify defaulted or unspecified fields. All defaulted or unspecified fields
 * must be identified.
 * Example #2 - Sample 01 Record:
 * <p/>
 * 01,122099999,123456789,940621,0200,1,55,,2/
 * Last National Bank (122099999) is sending data to its third-party intermediary, Data Corp. (123456789). The
 * file was created June 21, 1994 (940621) at 2:00 a.m. (0200). This is the first file created on this date and is
 * identified as number 1. The physical records in this file are (55) characters in length. No block size is
 * specified. This default is noted by adjacent delimiters (,,). The file is in Version (2) of the Specifications. The
 * slash (/) indicates that the preceding character was the last significant character in this physical record
 *
 * @author Giridhar Duggirala
 */
public class FileHeaderRecord {
    public static final String recordCode = "01";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(9);

    public enum FieldInfo {
        recordCode(0, false),
        senderIdentification(1, false),
        receiverIdentification(2, false),
        fileCreationDate(3, false),
        fileCreationTime(4, false),
        fileIdentificationNumber(5, false),
        physicalRecordLength(6, true),
        blockSize(7, true),
        versionNumber(8, true);
        private int feildIndex = 0;
        private boolean isOptional = false;

        private FieldInfo(int feildIndex, boolean isOptional) {
            this.feildIndex = feildIndex;
            this.isOptional = isOptional;
        }

        public int getFeildIndex() {
            return feildIndex;
        }

        public boolean isOptional() {
            return isOptional;
        }
    }

    private static int totalRecordsAvailable = 9;//Excluding zero

    public FileHeaderRecord() {
        recordValuesMap.put(FieldInfo.recordCode.getFeildIndex(), recordCode);
        for (int i = 1; i <= 8; i++) {
            recordValuesMap.put(i, "");
        }
        recordValuesMap.put(8, 2 + "");

    }

    public String getRecordCode() {
        return recordValuesMap.get(FieldInfo.recordCode.getFeildIndex());
    }

    public String getSenderIdentification() {
        return recordValuesMap.get(FieldInfo.senderIdentification.getFeildIndex());
    }

    public String getReceiverIdentification() {
        return recordValuesMap.get(FieldInfo.receiverIdentification.getFeildIndex());
    }

    //Date the sender created the file. YYMMDD format.
    public String getFileCreationDate() {
        return recordValuesMap.get(FieldInfo.fileCreationDate.getFeildIndex());
    }

    //Time the sender created the file. Military format (2400); sender time zone
    public String getFileCreationTime() {
        return recordValuesMap.get(FieldInfo.fileCreationTime.getFeildIndex());
    }

    public String getFileIdentificationNumber() {
        return recordValuesMap.get(FieldInfo.fileIdentificationNumber.getFeildIndex());
    }

    public String getPhysicalRecordLength() {
        return recordValuesMap.get(FieldInfo.physicalRecordLength.getFeildIndex());
    }

    public String getBlockSize() {
        return recordValuesMap.get(FieldInfo.blockSize.getFeildIndex());
    }

    public String getVersionNumber() {
        return recordValuesMap.get(FieldInfo.versionNumber.getFeildIndex());
    }

    static int indexChar;
    static int currentFieldIndex=0;
    public void process(Map<Integer, String> bai2File, int startIndex) {
        System.out.println("Invoked with a map and startIndex as " + startIndex);
        String line = bai2File.get(startIndex);
        System.out.println("Considering the line '" + line + "'");
        StringBuffer stringBuffer = new StringBuffer();
        int indexChar = 0;
        int fieldIndex = 0; //Total 9 records including 0 it will be 8.
        if (line != null && !line.isEmpty()) {
            char[] charsInLine = line.toCharArray();
            //First read two chars in line to get the code.
            stringBuffer.append(charsInLine[indexChar]).append(++indexChar);
            String code = stringBuffer.toString();
            if (code.equals(recordCode)) {
                stringBuffer.delete(0, stringBuffer.length());
                System.out.println("Code found is " + code + " which is matching with my code so proceeding further.");
                //Start reading from 4th char as 0=0,1=1,2=','. Read till next ',' is encountered.
                indexChar = 3;//which is actually 4 as we start counting from 0 it will be 3
                char charToRead = charsInLine[indexChar]; //To Skip ','
                if (charToRead == RecordController.LINE_DELIMETER) {
                    System.out.println("Looks like the line got ended, let me see if the next record is continuation record");
                    // String thirdRecord = checkNextRecord(bai2File, startIndex);
                }
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'SenderIdentification' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.senderIdentification.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'ReceiverIdentification' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.receiverIdentification.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'fileCreationDateIndex' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.fileCreationDate.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'fileCreationTimeIndex' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.fileCreationTime.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'fileIdentificationNumberIndex' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.fileIdentificationNumber.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'physicalRecordLengthIndex' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.physicalRecordLength.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'blockSizeIndex' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.blockSize.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                charToRead = charsInLine[++indexChar]; //To Skip ','
                while (charToRead != RecordController.FIELD_DELIMITER && charToRead != RecordController.LINE_DELIMETER) {
                    stringBuffer.append(charToRead);
                    charToRead = charsInLine[++indexChar];
                }
                fieldIndex++;
                System.out.println("Completed reading next field 'versionNumberIndex' and the value is '" + stringBuffer.toString() + "'");
                recordValuesMap.put(FieldInfo.versionNumber.getFeildIndex(), stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length());
                System.out.println("Make sure that the line got ended");
                charToRead=charsInLine[indexChar];
                if(charToRead != RecordController.LINE_DELIMETER) {
                    System.out.println("File header record should be terminated at the end of the line with "+RecordController.LINE_DELIMETER+" but found "+charToRead);
                }else{
                    System.out.println("Sucessfully completed reading the line");
                }
            }
        }
    }
}
