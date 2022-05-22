package com.injent.miscalls.ui.pdfviewer;

public class PdfBundle {

    public PdfBundle(String html, String filePath) {
        this.html = html;
        this.filePath = filePath;
    }

    private String html;

    private String filePath;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
