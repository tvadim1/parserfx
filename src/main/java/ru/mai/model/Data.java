package ru.mai.model;

public class Data {
    private String word;
    private String dep;

    public Data (String word, String dep){
        this.word = word;
        this.dep = dep;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }
}
