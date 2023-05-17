package io.github.vdaburon.jmeter.utils.comparekpi;

/**
 * Find the line number (start 0) with the same label in the file1 and the file2
 */
public class MatchLabelsFiles {
    private String label;
    private int lineFile1;
    private int lineFile2;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getLineFile1() {
        return lineFile1;
    }

    public void setLineFile1(int lineFile1) {
        this.lineFile1 = lineFile1;
    }

    public int getLineFile2() {
        return lineFile2;
    }

    public void setLineFile2(int lineFile2) {
        this.lineFile2 = lineFile2;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MatchLabelsFiles{");
        sb.append("label='").append(label).append('\'');
        sb.append(", lineFile1=").append(lineFile1);
        sb.append(", lineFile2=").append(lineFile2);
        sb.append('}');
        return sb.toString();
    }
}
