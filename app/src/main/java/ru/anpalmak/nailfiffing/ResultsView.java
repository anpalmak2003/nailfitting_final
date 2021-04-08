package ru.anpalmak.nailfiffing;


import java.util.List;
import ru.anpalmak.nailfiffing.Detector.Recognition;

public interface ResultsView {
    public void setResults(final List<Recognition> results);
}