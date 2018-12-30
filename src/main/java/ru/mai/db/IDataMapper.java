package ru.mai.db;

import javafx.scene.control.TextArea;
import java.util.Optional;

public interface IDataMapper {
    Optional<IOData> find(String x);
    void insert(IOData xy_object, TextArea ta) throws DataMapperException;
    void update(IOData xy_object) throws DataMapperException;
    void delete(IOData xy_object) throws DataMapperException;
    void selectAll(TextArea ta) throws DataMapperException;
}
