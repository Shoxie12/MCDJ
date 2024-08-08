package com.shoxie.mcdj.misc;

import java.nio.file.Path;

public class discRegData{
    private int Number;
    private String fileName;
    private String DisplayName;
    private Path FilePath;

    public discRegData(int _Number, String _fileName, String _DisplayName, Path _FilePath) {
        this.Number = _Number;
        this.fileName = _fileName;
        this.DisplayName = _DisplayName;
        this.FilePath = _FilePath;
    }

    public int getNumber() { return this.Number; }
    public String getFileName() { return this.fileName; }
    public String getName() { return ("record_"+this.fileName); }
    public String getDisplayName() { return this.DisplayName; }
    public Path getFilePath() { return this.FilePath; }
}