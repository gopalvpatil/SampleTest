package com.westernalliancebancorp.positivepay.service.model;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * PositivePayFtpFile is
 *
 * @author Giridhar Duggirala
 */

public class PositivePayFtpFile implements MultipartFile {
    private String fileName;
    private String path;
    private long fileSize;
    private byte[] contents;
    private Date modifiedDate;
    private String userName;

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "PositivePayFtpFile{" +
                "fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", fileSize=" + fileSize +
                ", userName=" + userName +
                ", modifiedDate=" + modifiedDate +
                '}';
    }

    @Override
    public String getName() {
        return this.getFileName();
    }

    @Override
    public String getOriginalFilename() {
        return this.getFileName();
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return Boolean.FALSE;
    }

    @Override
    public long getSize() {
        return this.getFileSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.getContents();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.getContents() != null) {
            return new ByteArrayInputStream(this.getContents());
        }
        return null;
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        throw new RuntimeException("PositivePayFtpFile Implementation.. I am not sure why you called me");
    }
}
